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
package org.gephi.plugins.example.datalab.merge.columns;

import javax.swing.Icon;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.columns.merge.AttributeColumnsMergeStrategy;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * AttributeColumnsMergeStrategy that creates a new boolean column with values
 * indicating if the two given columns have the same value. This strategy will
 * appear in the Merge columns dialog of data laboratory.
 *
 * @author Eduardo Ramos<eduramiba@gmail.com>
 */
public class EqualValuesMergeStrategy implements AttributeColumnsMergeStrategy {

    private Table table;
    private Column[] columns;
    private String columnTitle;

    @Override
    public void setup(Table table, Column[] columns) {
        this.table = table;
        this.columns = columns;
    }

    @Override
    public void execute() {
        Column column1, column2;
        column1 = columns[0];
        column2 = columns[1];
        //Simplify code using data laboratory API utilities:
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        //New column:
        Column newColumn = ac.addAttributeColumn(table, columnTitle, Boolean.class);

        //Fill rows of new column:
        Element[] rows = ac.getTableAttributeRows(table);
        for (int i = 0; i < rows.length; i++) {
            rows[i].setAttribute(newColumn, valuesAreEqual(column1, column2, rows[i]));
        }
    }

    private boolean valuesAreEqual(Column column1, Column column2, Element elmt) {
        Object value1 = elmt.getAttribute(column1);
        Object value2 = elmt.getAttribute(column2);

        if (value1 == value2) {
            return true;//Both null or same object
        } else if (value1 == null || value2 == null) {
            return false;//One is null and the other is not
        } else {
            return value1.toString().equals(value2.toString());//Else use string representation so columns don't need to have the same type to be compared
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(EqualValuesMergeStrategy.class, "EqualValuesMergeStrategy.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(EqualValuesMergeStrategy.class, "EqualValuesMergeStrategy.description");
    }

    @Override
    public boolean canExecute() {
        return columns.length == 2;//Only executable with exactly 2 columns
    }

    @Override
    public ManipulatorUI getUI() {
        return new EqualValuesMergeStrategyUI();
    }

    @Override
    public int getType() {
        return 200;//Group of merge strategies
    }

    @Override
    public int getPosition() {
        return 0;//Position in group. We let enough space to be able to add new actions between old ones
    }

    @Override
    public Icon getIcon() {
        return null;//No icon
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
