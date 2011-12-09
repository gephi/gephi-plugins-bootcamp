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
package org.gephi.plugins.example.statistics;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;

/**
 * Calculates the average euclidean distance for every node to others.
 * <p>
 * This is an example of a statistics which calculates a per-node result (as
 * opposed as a global result like <code>CountSelfLoop</code>). The calculation
 * is looking at all nodes position and do a simple distance average.
 * <p>
 * The final result is stored in node's  attributes and the way to create a new
 * attribute column is shown.
 * <p>
 * The statistic has a parameter to use either all other nodes or just connections.
 * 
 * @see AverageEuclideanDistanceBuilder
 * @see AverageEuclideanDistanceUI
 * @author Mathieu Bastian
 */
public class AverageEuclideanDistance implements Statistics {

    //public cons for the result column name if reused by others
    public static final String AVG_EUCLIDEAN_DISTANCE = "avg_euclidean_distance";
    //Settings
    private boolean useOnlyConnections = false;

    @Override
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        Graph graph = graphModel.getGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(Graph graph, AttributeModel attributeModel) {
        //Look if the result column already exist and create it if needed
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn col = nodeTable.getColumn(AVG_EUCLIDEAN_DISTANCE);
        if (col == null) {
            col = nodeTable.addColumn(AVG_EUCLIDEAN_DISTANCE, "Average Euclidean Distance", AttributeType.DOUBLE, AttributeOrigin.COMPUTED, 0.0);
        }

        //Lock to graph. This is important to have consistent results if another
        //process is currently modifying it.
        graph.readLock();

        //Iterate on all nodes
        Node[] nodes = graph.getNodes().toArray();
        for (Node n : nodes) {
            double avg = 0;
            int count = 0;
            if (useOnlyConnections) {
                //Calculate distance with neighbors
                for (Node m : graph.getNeighbors(n)) {
                    double xDist = n.getNodeData().x() - m.getNodeData().x();
                    double yDist = n.getNodeData().y() - m.getNodeData().y();
                    double dist = Math.sqrt(xDist * xDist + yDist * yDist);
                    avg = (dist + avg) / ++count;
                }
            } else {
                //Calculate distance with all other nodes
                for (Node m : nodes) {
                    if (n != m) {
                        double xDist = n.getNodeData().x() - m.getNodeData().x();
                        double yDist = n.getNodeData().y() - m.getNodeData().y();
                        double dist = Math.sqrt(xDist * xDist + yDist * yDist);
                        avg = (dist + avg) / ++count;
                    }
                }
            }
            //Store the average in the node attribute
            n.getAttributes().setValue(col.getIndex(), avg);
        }

        graph.readUnlock();
    }

    public void setUseOnlyConnections(boolean useOnlyConnections) {
        this.useOnlyConnections = useOnlyConnections;
    }

    public boolean isUseOnlyConnections() {
        return useOnlyConnections;
    }

    @Override
    public String getReport() {
        //This is the HTML report shown when execution ends. 
        //One could add a distribution histogram for instance
        String report = "<HTML> <BODY> <h1>Average Euclidean Distance</h1> "
                + "<hr>"
                + "<br> No global results to show"
                + "<br />"
                + "</BODY></HTML>";
        return report;
    }
}
