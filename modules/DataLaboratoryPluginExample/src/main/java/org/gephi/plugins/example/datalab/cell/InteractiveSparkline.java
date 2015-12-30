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
package org.gephi.plugins.example.datalab.cell;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.Icon;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.values.AttributeValueManipulator;
import org.gephi.graph.api.AttributeUtils;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Interval;
import org.gephi.graph.api.types.IntervalMap;
import org.gephi.utils.sparklines.SparklineComponent;
import org.gephi.utils.sparklines.SparklineParameters;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * AttributeValueManipulator (table cell) that shows an interactive sparkline of
 * a number list or dynamic number.
 *
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class InteractiveSparkline implements AttributeValueManipulator {
    //Sparkline data:

    private Number[] xValues = null;
    private Number[] yValues = null;
    private Column column;

    @Override
    public void setup(Element row, Column column) {
        this.column = column;
        //Prepare data for sparkline and validation:
        Object value = row.getAttribute(column);
        if (value != null) {
            if (AttributeUtils.isDynamicType(column.getTypeClass())) {
                Number[][] values = getDynamicNumberNumbers((IntervalMap) value);
                xValues = values[0];
                yValues = values[1];
            } else if (column.isArray() && AttributeUtils.isNumberType(column.getTypeClass().getComponentType())) {
                yValues = getNumberListNumbers(value);
            }//else: no valid column type for a sparkline at all
        }
    }

    @Override
    public void execute() {
        //Show interactive sparkline window:
        SparklineParameters parameters = new SparklineParameters(200, 20, SparklineParameters.DEFAULT_LINE_COLOR, new Color(225, 255, 255), Color.RED, Color.GREEN);//Dimension is automatically updated by SparklineComponent
        parameters.setHighlightTextColor(SparklineParameters.DEFAULT_TEXT_COLOR);
        parameters.setHighlightTextBoxColor(SparklineParameters.DEFAULT_TEXT_BOX_COLOR);
        SparklineComponent sparklineUI = new SparklineComponent(xValues, yValues, parameters, true);//True makes it interactive to mouse
        sparklineUI.setPreferredSize(new Dimension(200, 20));//Initial size
        //Using Netbeans RCP Dialogs API:        
        DialogDescriptor dd = new DialogDescriptor(sparklineUI, column.getTitle());
        dd.setModal(false);
        dd.setOptions(new Object[0]);//No buttons
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(InteractiveSparkline.class, "InteractiveSparkline.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(InteractiveSparkline.class, "InteractiveSparkline.description");
    }

    @Override
    public boolean canExecute() {
        //Make sure we are able to show a sparkline with this table cell data:
        return yValues != null;
    }

    @Override
    public ManipulatorUI getUI() {
        return null;//We don't use UI support here because we don't want to create a Ok-cancel dialog that would do some action
    }

    @Override
    public int getType() {
        return 100;//Group of cell actions
    }

    @Override
    public int getPosition() {
        return 100;//Position in group. We let enough space to be able to add new actions between old ones
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/plugins/example/datalab/resources/application-wave.png", true);
    }

    /**
     * For getting y values from a number list. X values can be null,
     * SparklineGraph will create them automatically.
     */
    private Number[] getNumberListNumbers(Object numberList) {
        int l = Array.getLength(numberList);
        ArrayList<Number> numbers = new ArrayList<Number>(l);
        Number n;
        for (int i = 0; i < l; i++) {
            n = (Number) Array.get(numberList, i);
            if (n != null) {
                numbers.add(n);
            }
        }
        return numbers.toArray(new Number[0]);
    }

    /**
     * For getting x and y values from a dynamic number Use the intervals start
     * time as X values
     */
    private Number[][] getDynamicNumberNumbers(IntervalMap dynamicNumber) {
        ArrayList<Number> xValuesList = new ArrayList<Number>();
        ArrayList<Number> yValuesList = new ArrayList<Number>();
        if (dynamicNumber == null) {
            return new Number[2][0];
        }

        Number n;
        for (Interval interval : dynamicNumber.toKeysArray()) {
            n = (Number) dynamicNumber.get(interval, null);
            if (n != null) {
                xValuesList.add(interval.getLow());
                yValuesList.add(n);
            }
        }
        return new Number[][]{xValuesList.toArray(new Number[0]), yValuesList.toArray(new Number[0])};
    }
}
