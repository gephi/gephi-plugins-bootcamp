/*
 Copyright 2008-2011 Gephi
 Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.plugins.example.datalab.general;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Icon;
import org.gephi.datalab.api.datatables.DataTablesController;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.general.PluginGeneralActionsManipulator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * General action that inverts the current table row selection.
 * Note that we implement PluginGeneralActionsManipulator instead of GeneralActionsManipulator in order to specify that this action has to
 * appear in the 'More actions' dialog to not fill the toolbar.
 *
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service=PluginGeneralActionsManipulator.class)
public class InvertRowSelection implements PluginGeneralActionsManipulator {

    @Override
    public void execute() {
        //Note that a function to inverse selection directly in the table with DataTablesController
        //would be more efficient than calculating it here, but this example demonstrates some table selection features.
        
        DataTablesController dtc = Lookup.getDefault().lookup(DataTablesController.class);
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getModel().getGraph();
        if (dtc.isNodeTableMode()) {
            //Get currently selected nodes and calculate inverse set.
            Node[] selected = dtc.getNodeTableSelection();

            ArrayList<Node> nodes = new ArrayList<Node>();
            nodes.addAll(Arrays.asList(graph.getNodes().toArray()));
            for (Node node : selected) {
                nodes.remove(node);
            }

            dtc.setNodeTableSelection(nodes.toArray(new Node[0]));
        } else if (dtc.isEdgeTableMode()) {
            //Get currently selected edges and calculate inverse set.
            Edge[] selected = dtc.getEdgeTableSelection();

            ArrayList<Edge> edges = new ArrayList<Edge>();
            edges.addAll(Arrays.asList(graph.getEdges().toArray()));
            for (Edge edge : selected) {
                edges.remove(edge);
            }
            
            dtc.setEdgeTableSelection(edges.toArray(new Edge[0]));
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(InvertRowSelection.class, "InvertRowSelection.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(InvertRowSelection.class, "InvertRowSelection.description");
    }

    @Override
    public boolean canExecute() {
        return true;//Always executable
    }

    @Override
    public ManipulatorUI getUI() {
        return null;
    }

    @Override
    public int getType() {
        return -100;//Group of general actions. Place in a group before existing groups (they use positive numbers)
    }

    @Override
    public int getPosition() {
        return 0;//Position in group. We let enough space to be able to add new actions between old ones
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/plugins/example/datalab/resources/tables.png", true);
    }
}
