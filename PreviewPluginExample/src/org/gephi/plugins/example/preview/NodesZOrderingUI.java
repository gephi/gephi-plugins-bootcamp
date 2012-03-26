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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.spi.PreviewUI;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * User interface plug-in for the {@link NodesZOrdering} builder.
 * <p>
 * This example shows how to create complex UI panels for Preview plug-ins. This
 * is useful when a plug-in requires multiple parameters and/or complex components.
 * <p>
 * For each implementation of <code>PreviewUI</code> a new tab is added to the 
 * preview settings module with the panel provided by the <code>getPanel()</code>
 * method.
 * <p>
 * In this case we populate the panel with a combob box for choosing an attribute
 * column.
 * 
 * @author Mathieu Bastian
 */
@ServiceProvider(service = PreviewUI.class)
public class NodesZOrderingUI implements PreviewUI {

    final String NO_SELECTION = "---";
    private PreviewModel previewModel;

    @Override
    public void setup(PreviewModel pm) {
        this.previewModel = pm;
    }

    @Override
    public JPanel getPanel() {
        //Create a panel and add a label
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelColumn = new JLabel("Column:");
        panel.add(labelColumn);
        final JComboBox columnCombo = new JComboBox();

        //Create the combob box model with all numerical attribute columns
        ComboBoxModel comboBoxModel = createComboBoxModel();
        columnCombo.setModel(comboBoxModel);

        //Add the combo to the panel
        panel.add(columnCombo);

        //When combo selection is updated, update the preview property
        columnCombo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                if (!comboBox.getSelectedItem().equals(NO_SELECTION)) {
                    previewModel.getProperties().putValue(NodesZOrdering.SORT_COLUMN, comboBox.getSelectedItem());
                } else {
                    previewModel.getProperties().putValue(NodesZOrdering.SORT_COLUMN, null);
                }
            }
        });

        //Add a button to refresh the column
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ComboBoxModel comboBoxModel = createComboBoxModel();
                columnCombo.setModel(comboBoxModel);
            }
        });
        panel.add(refreshButton);

        return panel;
    }

    private ComboBoxModel createComboBoxModel() {
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

        //Add a element for no column selected
        comboBoxModel.addElement(NO_SELECTION);

        //Get the current attribute columns in the current workspace
        AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel attributeModel = attributeController.getModel();
        AttributeUtils utils = AttributeUtils.getDefault();
        for (AttributeColumn col : attributeModel.getNodeTable().getColumns()) {
            if (utils.isNumberColumn(col)) {
                comboBoxModel.addElement(col);
            }
        }
        return comboBoxModel;
    }

    @Override
    public void unsetup() {
        previewModel = null;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getPanelTitle() {
        return "Z-ordering";
    }
}
