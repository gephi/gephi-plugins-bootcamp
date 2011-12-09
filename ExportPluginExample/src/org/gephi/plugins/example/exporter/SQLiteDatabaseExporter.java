/*
Copyright 2008-2011 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.plugins.example.exporter;

import com.sun.org.apache.bcel.internal.generic.SALOAD;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.database.drivers.SQLUtils;
import org.gephi.io.database.drivers.SQLiteDriver;
import org.gephi.io.exporter.spi.ByteExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;

/**
 * Example of a custom exporter exporting the graph to a SQLite database file.
 * <p>
 * File exporters are normally implementing {@link ByteExporter}
 * or {@link CharacterExporter} and write to a {@link Writer} or {@link InputStream}.
 * In this case that won't work because the SQLite driver manages the file writing
 * by itself so we simply have to execute UPDATE queries.
 * <p>
 * The exporter writes only the basic graph structure. It also shows how to use
 * the progress and cancel management because it implements {@link LongTask}.
 * <p>
 * This exporter's <code>execute()</code> method will be called from the 
 * {@link SQLiteDatabaseExporterUI} class which controls the execution.
 * 
 * @see SQLiteDatabaseExporterUI
 * @author Mathieu Bastian
 */
public class SQLiteDatabaseExporter implements Exporter, LongTask {

    private File path;
    private Workspace workspace;
    private ProgressTicket progress;
    private boolean cancel = false;

    @Override
    public boolean execute() {
        Connection connection = null;
        try {
            if (path.getParentFile().exists()) {
                //Create connection
                SQLiteDriver sQLiteDriver = Lookup.getDefault().lookup(SQLiteDriver.class);
                String connectionUrl = SQLUtils.getUrl(sQLiteDriver, path.getAbsolutePath(), 0, "");
                connection = sQLiteDriver.getConnection(connectionUrl, "", "");

                //Create statement and create nodes and egdes table
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                statement.executeUpdate("drop table if exists nodes");
                statement.executeUpdate("drop table if exists edges");
                statement.executeUpdate("create table nodes (id string, label string)");
                statement.executeUpdate("create table edges (source string, target string, weight real)");

                //Get the current graph in the defined workspace
                GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
                GraphModel graphModel = graphController.getModel(workspace);
                Graph graph = graphModel.getGraphVisible();

                //Count the number of tasks (nodes + edges) and start the progress
                int tasks = graph.getNodeCount() + graph.getEdgeCount();
                Progress.start(progress, tasks);

                //Export nodes. Progress is incremented at each step.
                for (Node n : graph.getNodes().toArray()) {
                    String id = n.getNodeData().getId();
                    String label = n.getNodeData().getLabel();
                    statement.executeUpdate("insert into nodes values('" + id + "', '" + label + "')");
                    if (cancel) {
                        return false;
                    }
                    Progress.progress(progress);
                }

                //Export edges. Progress is incremented at each step.
                for (Edge e : graph.getEdges().toArray()) {
                    String sourceId = e.getSource().getNodeData().getId();
                    String targetId = e.getTarget().getNodeData().getId();
                    String weight = String.valueOf(e.getWeight());
                    statement.executeUpdate("insert into edges values('" + sourceId + "', '" + targetId + "', '" + weight + "')");
                    if (cancel) {
                        return false;
                    }
                    Progress.progress(progress);
                }

                //Finish progress
                Progress.finish(progress);
                return true;
            } else {
                throw new FileNotFoundException(path.getAbsolutePath() + " does not exist");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    @Override
    public void setWorkspace(Workspace wrkspc) {
        this.workspace = wrkspc;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket pt) {
        this.progress = pt;
    }
}
