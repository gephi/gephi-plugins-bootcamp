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
package org.gephi.plugins.example.exporter;

import javax.swing.JPanel;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.ExporterUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * Settings user interface implementation for the JPG export.
 * <p>
 * It provides the name and the panel for configuring the file export. The
 * <code>setup()</code> method is called first to set the exporter which is
 * going to be configured. Then the <code>getPanel()</code> method is called, 
 * the panel is shown and closed and finally the <code>unsetup()</code> method
 * is called to write settings back to the exporter.
 * <p>
 * This example also shows a convenient way to save settings between export
 * execution. Because this class is a singleton (it implements ServiceProvider)
 * the <code>settings</code> variable will stay as long as the program runs.
 * <p>
 * Make sure to unset <code>panel</code> and <code>exporter</code> in the
 * <code>unsetup()</code> method so it can be GCed.
 * 
 * @see JPGExporter
 * @see JPGExporterPanel
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ExporterUI.class)
public class JPGExporterUI implements ExporterUI {

    private JPGExporterPanel panel;
    private JPGExporter exporter;
    private final ExporterJPGSettings settings = new ExporterJPGSettings();

    @Override
    public JPanel getPanel() {
        panel = new JPGExporterPanel();
        return panel;
    }

    @Override
    public void setup(Exporter exporter) {
        this.exporter = (JPGExporter) exporter;
        settings.load(this.exporter);
        panel.setup(this.exporter);
    }

    @Override
    public void unsetup(boolean update) {
        if (update) {
            panel.unsetup(exporter);
            settings.save(exporter);
        }

        panel = null;
        exporter = null;
    }

    @Override
    public boolean isUIForExporter(Exporter exporter) {
        return exporter instanceof JPGExporter;
    }

    @Override
    public String getDisplayName() {
        return "JPG";
    }

    private static class ExporterJPGSettings {

        private int width = 1024;
        private int height = 1024;

        void load(JPGExporter exporter) {
            exporter.setHeight(height);
            exporter.setWidth(width);
        }

        void save(JPGExporter exporter) {
            height = exporter.getHeight();
            width = exporter.getWidth();
        }
    }
}
