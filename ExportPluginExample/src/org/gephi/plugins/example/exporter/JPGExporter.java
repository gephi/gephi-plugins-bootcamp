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

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.gephi.io.exporter.spi.ByteExporter;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import processing.core.PGraphicsJava2D;

/**
 * JPG export inspired from the PNG exporter. It uses Preview's ability to export
 * <code>BufferedImage</code> from the Processing canvas.
 * 
 * @author Mathieu Bastian
 */
public class JPGExporter implements VectorExporter, ByteExporter {

    private Workspace workspace;
    private OutputStream stream;
    private int width = 1024;
    private int height = 1024;
    private ProcessingTarget target;

    @Override
    public boolean execute() {

        PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
        controller.getModel(workspace).getProperties().putValue(PreviewProperty.VISIBILITY_RATIO, 1.0);
        controller.refreshPreview(workspace);

        PreviewProperties props = controller.getModel(workspace).getProperties();
        props.putValue("width", width);
        props.putValue("height", height);
        target = (ProcessingTarget) controller.getRenderTarget(RenderTarget.PROCESSING_TARGET, workspace);

        try {
            target.refresh();

            PGraphicsJava2D pg2 = (PGraphicsJava2D) target.getGraphics();
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            img.setRGB(0, 0, width, height, pg2.pixels, 0, width);
            ImageIO.write(img, "jpg", stream);
            stream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setOutputStream(OutputStream stream) {
        this.stream = stream;
    }
}
