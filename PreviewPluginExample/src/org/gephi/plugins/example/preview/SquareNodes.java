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
package org.gephi.plugins.example.preview;

import java.awt.Color;
import org.gephi.preview.api.*;
import org.gephi.preview.plugin.items.NodeItem;
import org.gephi.preview.plugin.renderers.NodeRenderer;
import org.gephi.preview.spi.Renderer;
import org.gephi.preview.types.DependantColor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import processing.core.PGraphics;

/**
 * Extends and replaces default nodes renderer and implements square shaped nodes.
 * <p>
 * By specifically extending a preview default renderer, this replaces it and the old one is no longer available.
 * A default renderer can be extended in order to add more features to it, or simply replace it completely.
 * Default renderers are contained in <code>org.gephi.preview.plugin.renderers</code> package.
 * <p>
 * Note that we need to set the position parameter even this replaces other renderer.
 * In this case and normally, the position has been set the same that the replaced renderer has (300).
 * @author Eduardo Ramos<eduramiba@gmail.com>
 */
@ServiceProvider(service = Renderer.class, position=300)
public class SquareNodes extends NodeRenderer {

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(SquareNodes.class, "SquareNodes.name");
    }

    @Override
    public void render(Item item, RenderTarget target, PreviewProperties properties) {
        if (properties.getBooleanValue("SquareNodes.property.enable")) {
            if (target instanceof ProcessingTarget) {
                renderSquaresProcessing(item, (ProcessingTarget) target, properties);
            } else if (target instanceof SVGTarget) {
                renderSquaresSVG(item, (SVGTarget) target, properties);
            } else if (target instanceof PDFTarget) {
                renderSquaresPDF(item, (PDFTarget) target, properties);
            }
        } else {
            super.render(item, target, properties);
        }
    }

    public void renderSquaresProcessing(Item item, ProcessingTarget target, PreviewProperties properties) {
        //Params
        Float x = item.getData(NodeItem.X);
        Float y = item.getData(NodeItem.Y);
        Float size = item.getData(NodeItem.SIZE);
        Color color = item.getData(NodeItem.COLOR);
        Color borderColor = ((DependantColor) properties.getValue(PreviewProperty.NODE_BORDER_COLOR)).getColor(color);
        float borderSize = properties.getFloatValue(PreviewProperty.NODE_BORDER_WIDTH);
        int alpha = (int) ((properties.getFloatValue(PreviewProperty.NODE_OPACITY) / 100f) * 255f);
        if (alpha > 255) {
            alpha = 255;
        }

        //Graphics
        PGraphics graphics = target.getGraphics();

        if (borderSize > 0) {
            graphics.stroke(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), alpha);
            graphics.strokeWeight(borderSize);
        } else {
            graphics.noStroke();
        }
        graphics.fill(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        graphics.rect(x, y, size, size);
    }

    public void renderSquaresPDF(Item item, PDFTarget target, PreviewProperties properties) {
        //TODO Not implemented
    }

    public void renderSquaresSVG(Item item, SVGTarget target, PreviewProperties properties) {
        //TODO Not implemented
    }

    @Override
    public PreviewProperty[] getProperties() {
        //Creates the same properties as the default renderer 
        //but adds a new one to control square shaped nodes rendering
        PreviewProperty[] props = super.getProperties();
        PreviewProperty[] newProps = new PreviewProperty[props.length + 1];

        for (int i = 0; i < props.length; i++) {
            newProps[i] = props[i];
        }

        newProps[newProps.length - 1] = PreviewProperty.createProperty(this, "SquareNodes.property.enable", Boolean.class,
                NbBundle.getMessage(SquareNodes.class, "SquareNodes.property.name"),
                NbBundle.getMessage(SquareNodes.class, "SquareNodes.property.description"),
                PreviewProperty.CATEGORY_NODES).setValue(false);
        return newProps;
    }
}
