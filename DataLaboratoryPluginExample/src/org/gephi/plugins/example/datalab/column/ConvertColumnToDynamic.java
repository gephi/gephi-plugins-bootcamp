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
package org.gephi.plugins.example.datalab.column;

import java.awt.Image;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.gephi.graph.api.Attributes;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * AttributeColumnsManipulator that replaces a column with its dynamic equivalent with a defined interval.
 *
 * @author Eduardo Ramos<eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)//AttributeColumnsManipulators do not need builders
public class ConvertColumnToDynamic implements AttributeColumnsManipulator {

    private static String start = "-Infinity";
    private static String end = "Infinity";

    @Override
    public void execute(AttributeTable table, AttributeColumn column) {
        AttributeType dynamicType = getEquivalentDynamicType(column.getType());

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        Attributes[] rows = ac.getTableAttributeRows(table);
        Object[] values = new Object[rows.length];

        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = dynamicType.parse(String.format("[%s,%s,%s]", start, end, rows[i].getValue(column.getIndex()).toString()));
            } catch (Exception e) {
            }
        }

        AttributeColumn dynamicColumn = table.replaceColumn(column, column.getId(), column.getTitle(), dynamicType, column.getOrigin(), null);

        for (int i = 0; i < values.length; i++) {
            rows[i].setValue(dynamicColumn.getIndex(), values[i]);
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ConvertColumnToDynamic.class, "ConvertColumnToDynamic.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ConvertColumnToDynamic.class, "ConvertColumnToDynamic.description");
    }

    @Override
    public boolean canManipulateColumn(AttributeTable at, AttributeColumn c) {
        if (AttributeUtils.getDefault().isNodeColumn(c)) {
            if (c.getIndex() != PropertiesColumn.NODE_ID.getIndex() && c.getIndex() != PropertiesColumn.NODE_LABEL.getIndex()) {
                //Can't replace id or label column
                return getEquivalentDynamicType(c.getType()) != null;//Is convertible to some dynamic type?
            } else {
                return false;
            }
        } else if (AttributeUtils.getDefault().isEdgeColumn(c)) {
            if (c.getIndex() != PropertiesColumn.EDGE_ID.getIndex() && c.getIndex() != PropertiesColumn.EDGE_LABEL.getIndex()) {
                //Can't replace id or label column, but we can replace weight column with dynamic weight
                return getEquivalentDynamicType(c.getType()) != null;//Is convertible to some dynamic type?
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public AttributeColumnsManipulatorUI getUI(AttributeTable at, AttributeColumn ac) {
        return new ConvertColumnToDynamicUI();
    }

    private AttributeType getEquivalentDynamicType(AttributeType type) {
        switch (type) {//What types can be converted
            case BYTE:
                return AttributeType.DYNAMIC_BYTE;
            case SHORT:
                return AttributeType.DYNAMIC_SHORT;
            case INT:
                return AttributeType.DYNAMIC_INT;
            case LONG:
                return AttributeType.DYNAMIC_LONG;
            case FLOAT:
                return AttributeType.DYNAMIC_FLOAT;
            case DOUBLE:
                return AttributeType.DYNAMIC_DOUBLE;
            case BOOLEAN:
                return AttributeType.DYNAMIC_BOOLEAN;
            case CHAR:
                return AttributeType.DYNAMIC_CHAR;
            case STRING:
                return AttributeType.DYNAMIC_STRING;
            case BIGINTEGER:
                return AttributeType.DYNAMIC_BIGINTEGER;
            case BIGDECIMAL:
                return AttributeType.DYNAMIC_BIGDECIMAL;
            default:
                return null;//Not convertible to dynamic
        }
    }

    @Override
    public int getType() {
        return 500;//Group of column actions
    }

    @Override
    public int getPosition() {
        return 0;//Position in group. We let enough space to be able to add new actions between old ones
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/plugins/example/datalab/resources/table-select-column.png", true);
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}
