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
package org.gephi.plugins.example.filter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;

/**
 * Filter that keeps the top N nodes. Nodes are sorted using an attribute column.
 * <p>
 * This example shows how to use the <code>init()</code> method to initialize
 * a filter and how to deal with filter properties. The <code>init()</code> is
 * called once before the filter is applied with the <code>execute()</code>
 * method. In this example we collect and sort all numerical values for the
 * selected attribute column. We simply keep in a set all valid values and use
 * it in the <code>evaluate()</code> method. Note that the <code>finish()</code>
 * method resets the <code>topSet()</code> variable.
 * <p>
 * Filter properties are simply all the parameters the user can change for the
 * filter. Each pripery should have a name and the proper getters and setters.
 * For example if your filter has a 'top' of the type <code>Integer</code> the
 * class must have the <code>getTop()</code> and <code>setTop()</code> accessors.
 * Make sure to use <code>Integer</code>, <code>Double</code> etc. and not the
 * default types.
 * 
 * @see TopNodesBuilder
 * @author Mathieu Bastian
 */
public class TopNodesFilter implements NodeFilter {

    private AttributeColumn column;
    private FilterProperty[] filterProperties;
    private Integer top = 1;
    //Flag
    private Set<Node> topSet;

    @Override
    public boolean init(Graph graph) {
        Node[] nodes = graph.getNodes().toArray();
        Arrays.sort(nodes, new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                Comparable a1 = (Comparable) o1.getAttributes().getValue(column.getIndex());
                Comparable a2 = (Comparable) o2.getAttributes().getValue(column.getIndex());
                if (a1 == null && a2 != null) {
                    return 1;
                } else if (a1 != null && a2 == null) {
                    return -1;
                } else if (a1 == null && a2 == null) {
                    return 0;
                } else {
                    return a2.compareTo(a1);
                }
            }
        });
        topSet = new HashSet<Node>(top);
        for (int i = 0; i < top && i < nodes.length; i++) {
            topSet.add(nodes[i]);
        }
        return true;
    }

    @Override
    public boolean evaluate(Graph graph, Node node) {
        return topSet.contains(node);
    }

    @Override
    public void finish() {
        topSet = null;
    }

    @Override
    public String getName() {
        return "Keep the top N nodes for this column";
    }

    @Override
    public FilterProperty[] getProperties() {
        if (filterProperties == null) {
            filterProperties = new FilterProperty[0];
            try {
                filterProperties = new FilterProperty[]{
                    FilterProperty.createProperty(this, AttributeColumn.class, "column"),
                    FilterProperty.createProperty(this, Integer.class, "top"),};
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return filterProperties;
    }

    public void setColumn(AttributeColumn column) {
        this.column = column;
    }

    public AttributeColumn getColumn() {
        return column;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }
}
