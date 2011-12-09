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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.filters.api.FilterLibrary;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.CategoryBuilder;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Builder for the {@link TopNodesFilter} filter.
 * <p>
 * This example shows how to create <code>CategoryBuilder</code> implementations, 
 * which are used for attributes-based filters. Category builders are one level
 * above <code>FilterBuilder</code> implementations. Indeed, category builders
 * create filter builders. For filters which work on attribute columns (e.g. name,
 * age, gender etc.) a category builder builds one <code>FilterBuilder</code> per
 * column.
 * <p>
 * The created <code>TopNodesColumnFilterBuilder</code> builders store
 * the column they are assigned and pass it on to the created filters.
 * <p>
 * This example also shows how to define an UI for the filter.
 * 
 * @see TopNodesFilterPanel
 * @author Mathieu Bastian
 */
@ServiceProvider(service = CategoryBuilder.class)
public class TopNodesBuilder implements CategoryBuilder {

    //Create a new category (i.e. folder) which inherits from 'Attributes'
    private final static Category TOP_CATEGORY = new Category(
            "Top Nodes",
            null,
            FilterLibrary.ATTRIBUTES);

    @Override
    public FilterBuilder[] getBuilders() {
        List<FilterBuilder> builders = new ArrayList<FilterBuilder>();
        
        //Get all the current attribute columns
        AttributeModel am = Lookup.getDefault().lookup(AttributeController.class).getModel();
        for (AttributeColumn c : am.getNodeTable().getColumns()) {
            //Keep only numerical columns
            if (AttributeUtils.getDefault().isNumberColumn(c)) {
                TopNodesColumnFilterBuilder b = new TopNodesColumnFilterBuilder(c);
                builders.add(b);
            }
        }
        return builders.toArray(new FilterBuilder[0]);
    }

    @Override
    public Category getCategory() {
        return TOP_CATEGORY;
    }

    /**
     * Inner class for the Filter builder. One of those is created for each columns.
     */
    private static class TopNodesColumnFilterBuilder implements FilterBuilder {

        private final AttributeColumn column;

        public TopNodesColumnFilterBuilder(AttributeColumn column) {
            this.column = column;
        }

        @Override
        public Category getCategory() {
            return TOP_CATEGORY;
        }

        @Override
        public String getName() {
            //Nice formatting to have the type of the column in addition of the name
            return "<font color='#000000'>" + column.getTitle() + "</font> "
                    + "<font color='#999999'><i>" + column.getType().toString() + " "
                    + "(Node)</i></font>";
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public String getDescription() {
            return "Keep the top N nodes for this column";
        }

        @Override
        public Filter getFilter() {
            //Create the filter and set the column
            TopNodesFilter filter = new TopNodesFilter();
            filter.setColumn(column);
            return filter;
        }

        @Override
        public JPanel getPanel(Filter filter) {
            //Create the panel
            TopNodesFilterPanel panel = new TopNodesFilterPanel();
            panel.setup((TopNodesFilter) filter);
            return panel;
        }

        @Override
        public void destroy(Filter filter) {
        }
    }
}
