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
package org.gephi.plugins.example.tool;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.tools.spi.Tool;
import org.gephi.tools.spi.ToolEventListener;
import org.gephi.tools.spi.ToolSelectionType;
import org.gephi.tools.spi.ToolUI;
import org.gephi.visualization.VizController;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * This tool creates a text field in the property bar to find nodes in the graph
 * and zoom at their location.
 * <p>
 * This example shows how to build something in the property bar. The tool doesn't
 * interact with the graph or the mouse so the <code>getListeners()</code> method
 * doesn't return anything.
 * <p>
 * The <code>select()</code> method is called when the tool is selected by the user.
 * When a node is find it uses the <code>VizController</code> to zoom on it.
 * <p>
 * The user interface shows how to associate an icon to the tool.
 * 
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Tool.class)
public class FindTool implements Tool {
    
    private Map<String, Node> data;
    private final FindToolUI ui = new FindToolUI();
    
    @Override
    public void select() {
        //Get current visible graph
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        Graph graph = graphController.getModel().getGraphVisible();
        
        //Build the autocomplete data. A simple map from node's label
        graph.readLock();
        data = new HashMap<String, Node>();
        for (Node n : graph.getNodes()) {
            String label = n.getNodeData().getLabel();
            String id = n.getNodeData().getId();
            if (label != null) {
                if (!label.isEmpty()) {
                    data.put(label, n);
                }
            } else if (id != null && !id.isEmpty()) {
                data.put(id, n);
            }
            
        }
        graph.readUnlock();
    }
    
    @Override
    public void unselect() {
        //Clean data
        data = null;
    }
    
    protected Map<String, Node> getData() {
        return data;
    }
    
    protected boolean findNode(String label) {
        Node node = data.get(label);
        if (node != null) {
            VizController.getInstance().getSelectionManager().centerOnNode(node);
            return true;
        }
        return false;
    }
    
    @Override
    public ToolEventListener[] getListeners() {
        return new ToolEventListener[0];
    }
    
    @Override
    public ToolUI getUI() {
        return ui;
    }
    
    @Override
    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION_AND_DRAGGING;
    }
    
    private static class FindToolUI implements ToolUI {
        
        @Override
        public JPanel getPropertiesBar(Tool tool) {
            //Cast
            final FindTool findTool = (FindTool) tool;

            //Build the autoComplete
            final JTextField labeltext = new JTextField(20);
            AutoCompleteDecorator.decorate(labeltext, new ArrayList(findTool.getData().keySet()), false);

            //Label
            final JLabel statusLabel = new JLabel();
            statusLabel.setFont(statusLabel.getFont().deriveFont((float) 10));

            //Button
            JButton findButton = new JButton("Find");
            findButton.setDefaultCapable(true);
            findButton.setFont(statusLabel.getFont().deriveFont((float) 10));
            ActionListener findAction = new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (findTool.findNode(labeltext.getText())) {
                        statusLabel.setText("");
                    } else {
                        statusLabel.setText("Sorry, no match found");
                    }
                }
            };
            findButton.addActionListener(findAction);

            //Also bind the action to the text field
            labeltext.addActionListener(findAction);

            //Build panel
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panel.add(labeltext);
            panel.add(findButton);
            panel.add(statusLabel);
            return panel;
        }
        
        @Override
        public Icon getIcon() {
            return new ImageIcon(getClass().getResource("/org/gephi/plugins/example/tool/resources/magnifier.png"));
        }
        
        @Override
        public String getName() {
            return "Search for nodes";
        }
        
        @Override
        public String getDescription() {
            return "Find and locate nodes using the node's label.";
        }
        
        @Override
        public int getPosition() {
            return 1200;
        }
    }
}
