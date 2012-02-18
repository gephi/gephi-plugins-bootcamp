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
package org.gephi.plugins.example.datalab.nodes;

import java.awt.Color;
import javax.swing.Icon;
import org.gephi.datalab.spi.ContextMenuItemManipulator;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.nodes.NodesManipulator;
import org.gephi.graph.api.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * NodesManipulator that edits the color of one or more nodes
 *
 * @author Eduardo Ramos<eduramiba@gmail.com>
 */
public class SetNodesColor implements NodesManipulator {

    private Node[] nodes;
    private Color color;

    @Override
    public void setup(Node[] nodes, Node clickedNode) {
        //Here we receive the selected nodes in the table (rows) that were right clicked
        //Data laboratory ensures that at least one node is selected
        this.nodes = nodes;
        this.color = new Color(clickedNode.getNodeData().r(), clickedNode.getNodeData().g(), clickedNode.getNodeData().b(), clickedNode.getNodeData().alpha());//We set initially selected color to the right-clicked node color for convenience
    }

    @Override
    public void execute() {
        float r,g,b,a;
        r=color.getRed()/255.0f;
        g=color.getGreen()/255.0f;
        b=color.getBlue()/255.0f;
        a=color.getAlpha()/255.0f;
        for (Node node : nodes) {
            node.getNodeData().setR(r);
            node.getNodeData().setG(g);
            node.getNodeData().setB(b);
            node.getNodeData().setAlpha(a);
        }
    }

    @Override
    public String getName() {
        if (nodes.length > 1) {
            return NbBundle.getMessage(SetNodesColor.class, "SetNodesColor.name.multiple");
        } else {
            return NbBundle.getMessage(SetNodesColor.class, "SetNodesColor.name.single");
        }
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean canExecute() {
        return true;//This says if the action is enabled in the popup menu.
    }

    @Override
    public ManipulatorUI getUI() {
        return new SetNodesColorUI();
    }

    @Override
    public int getType() {
        return 400;//Group of node actions
    }

    @Override
    public int getPosition() {
        return 400;//Position in group. We let enough space to be able to add new actions between old ones
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/plugins/example/datalab/resources/color--pencil.png", true);
    }

    @Override
    public ContextMenuItemManipulator[] getSubItems() {
        return null;//This action does not have sub-actions
    }

    @Override
    public boolean isAvailable() {
        return true;//This says if the action appears at all in the popup menu, enabled or not.
    }

    @Override
    public Integer getMnemonicKey() {
        return null;//No short-cut
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
