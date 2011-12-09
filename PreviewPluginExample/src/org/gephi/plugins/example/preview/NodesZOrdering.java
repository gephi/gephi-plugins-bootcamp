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
package org.gephi.plugins.example.preview;

import java.util.Arrays;
import java.util.Comparator;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.preview.api.Item;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.plugin.builders.NodeBuilder;
import org.gephi.preview.spi.ItemBuilder;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Z-ordering of nodes based on node size or an Attribute column.
 * <p>
 * Nodes can overlap each other and it's nice to be able to decide in which order
 * nodes should be displayed. This example shows how to customize the output of
 * an existing {@link ItemBuilder}. Builders are responsible for creating items
 * (in this case <code>NodeItem</code>) and return an array of items. This class
 * replaces and extends the default {@link NodeBuilder}. It simply sorts the
 * array of items coming out of the <code>NodeBuilder</code> using a <code>sort.column</code>
 * parameter set in the {@link PreviewProperties}.
 * 
 * @see NodesZOrderingUI
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ItemBuilder.class, position = 100, supersedes = "org.gephi.preview.plugin.builders.NodeBuilder")
public class NodesZOrdering extends NodeBuilder {

    public static final String SORT_COLUMN = "sort.column";

    @Override
    public Item[] getItems(Graph graph, AttributeModel attributeModel) {
        //Get the current preview model, little hack to get the current workspace from the graph
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel(graph.getGraphModel().getWorkspace());
        
        //Get the sort column from the properties, if any
        PreviewProperties previewProperties = previewModel.getProperties();
        final AttributeColumn sortColumn = previewProperties.getValue(SORT_COLUMN);
        
        //Get the standard node items from the node builder
        Item[] nodeItems = super.getItems(graph, attributeModel);
        if (sortColumn != null) {
            //Sort by column
            Arrays.sort(nodeItems, new Comparator<Item>() {

                @Override
                public int compare(Item o1, Item o2) {
                    Node n1 = (Node) o1.getSource();
                    Node n2 = (Node) o2.getSource();
                    Number s1 = (Number) n1.getAttributes().getValue(sortColumn.getIndex());
                    Number s2 = (Number) n2.getAttributes().getValue(sortColumn.getIndex());
                    double size1 = s1 == null ? Double.NEGATIVE_INFINITY : s1.doubleValue();
                    double size2 = s2 == null ? Double.NEGATIVE_INFINITY : s2.doubleValue();
                    return size1 > size2 ? 1 : size1 < size2 ? -1 : 0;
                }
            });
        } else {
            //Sort by node size
            Arrays.sort(nodeItems, new Comparator<Item>() {

                @Override
                public int compare(Item o1, Item o2) {
                    Node n1 = (Node) o1.getSource();
                    Node n2 = (Node) o2.getSource();
                    float size1 = n1.getNodeData().getSize();
                    float size2 = n2.getNodeData().getSize();
                    return size1 > size2 ? 1 : size1 < size2 ? -1 : 0;
                }
            });

        }
        return nodeItems;
    }
}
