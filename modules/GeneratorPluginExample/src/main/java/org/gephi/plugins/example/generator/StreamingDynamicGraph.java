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
package org.gephi.plugins.example.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.desktop.project.api.ProjectControllerUI;
import org.gephi.graph.api.*;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.project.api.ProjectController;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Random streaming dynamic graph generator.
 * <p>
 * Example of a generator not using the container structure and directly pushing
 * to the graph structure. It shows how to create a graph from scratch and
 * assign a time interval attribute to nodes.
 * <p>
 * The generator creates a thread that will survive after the generator method
 * ends. It is controlled by a cancel flag that becomes <code>true</code> if the
 * user cancels the task.
 * <p>
 * This example directly appends nodes and edges to the graph. A traditional
 * generator go through the container structure for adding objects. However as
 * using the container can only be done in the scope of the generator it can't
 * be used for continuous generators. Because this generator doesn't use the
 * container it makes sure a project and workspace is opened before starting to
 * append data.
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Generator.class)
public class StreamingDynamicGraph implements Generator {

    private final int delay = 500;
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    @Override
    public void generate(ContainerLoader container) {
        //Reset cancel
        cancel = false;

        //Start progress
        Progress.start(progressTicket);

        //Project
        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
        ProjectControllerUI projectControllerUI = Lookup.getDefault().lookup(ProjectControllerUI.class);
        if (projectController.getCurrentProject() == null) {
            projectControllerUI.newProject();
        }

        //Get current graph
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel graphModel = graphController.getGraphModel();
        Graph graph = graphModel.getGraph();

        //Create list of nodes and a random obj
        List<Node> nodes = new ArrayList<Node>();
        Random random = new Random(232323);

        //Create nodes and edges until cancelled
        while (!cancel) {

            //Create a new node and assign random position
            Node n = graphModel.factory().newNode();
            n.setX(random.nextInt(2000) - 1000);
            n.setY(random.nextInt(2000) - 1000);

            //Create a new random time interval and set it to the node
            double min = random.nextInt(2000) + 100;//Min value is 100
            double max = random.nextInt(2000) + 100;//Max value is 2099
            Interval timeInterval = new Interval(min < max ? min : max, max > min ? max : min);
            n.addInterval(timeInterval);

            //Add the node to the graph
            graph.addNode(n);

            //Add a random number of edges between 0 and 3
            int nbedges = random.nextInt(4);
            for (int i = 0; i < nbedges; i++) {

                //Shuffle an index in the list of nodes 
                int index = random.nextInt(nodes.size() + 1);
                if (index < nodes.size()) {

                    //Add an edge if not already exist
                    Node m = nodes.get(index);
                    if (n != m && graph.getEdge(n, m) == null) {
                        Edge e = graphModel.factory().newEdge(n, m);
                        graph.addEdge(e);
                    }
                }
            }

            //Add the node to the list of nodes
            nodes.add(n);

            //Sleep some time
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Progress.finish(progressTicket);
    }

    @Override
    public String getName() {
        return "Streaming Dynamic Graph";
    }

    @Override
    public GeneratorUI getUI() {
        return null;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
