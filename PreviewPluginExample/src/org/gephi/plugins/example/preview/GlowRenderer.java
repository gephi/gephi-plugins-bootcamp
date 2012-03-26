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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import org.gephi.preview.api.*;
import org.gephi.preview.plugin.builders.NodeBuilder;
import org.gephi.preview.plugin.items.NodeItem;
import org.gephi.preview.plugin.renderers.NodeRenderer;
import org.gephi.preview.spi.ItemBuilder;
import org.gephi.preview.spi.Renderer;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import processing.core.PGraphicsJava2D;

/**
 * Adds a glow effect on nodes by drawing a gradient sphere under nodes.
 * <p>
 * This example is rendering something new for an existing object (i.e. node). That's why
 * the <code>isRendererForitem()</code> returns <code>true</code> for node items and
 * the <code>render()</code> method will receive node items.
 * <p>
 * The rendering part shows how to retrieve attributes from node items such as
 * the position, the size and the color. Then it shows how to draw something using
 * Java2D instead of Processing. Processing is built on top of Java2D so it's not
 * a problem to use either is the most convenient for a task. The PDF and SVG
 * renderers are not implemented yet but doing a radial gradient should be easy.
 * <p>
 * Note that the renderer has <code>position = 10</code> parameter. That's below
 * any default renderer (edge is 100, node is 300...) and will therefore execute
 * this renderer first. This is important as we want the glow to be under the node.
 * 
 * @author Mathieu Bastian
 * @see NodeRenderer
 */
@ServiceProvider(service = Renderer.class, position = 10)
public class GlowRenderer implements Renderer {
    

    //Custom properties
    public static final String ENABLE_NODE_GLOW = "node.glow.enable";
    
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(GlowRenderer.class, "GlowRenderer.name");
    }
    
    @Override
    public void preProcess(PreviewModel pm) {
    }
    
    @Override
    public void render(Item item, RenderTarget target, PreviewProperties properties) {
        if (target instanceof ProcessingTarget) {
            renderProcessing(item, (ProcessingTarget) target, properties);
        } else if (target instanceof SVGTarget) {
            renderSVG(item, (SVGTarget) target, properties);
        } else if (target instanceof PDFTarget) {
            renderPDF(item, (PDFTarget) target, properties);
        }
    }
    
    public void renderProcessing(Item item, ProcessingTarget target, PreviewProperties properties) {
        //Params
        Float x = item.getData(NodeItem.X);
        Float y = item.getData(NodeItem.Y);
        Float size = item.getData(NodeItem.SIZE);
        Color color = item.getData(NodeItem.COLOR);
        Color startColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 32);
        Color endColor = new Color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), 0);
        float radius = size * 6;

        //Get processing Java2D canvas, as its easier to use a RadialGradientPaint
        //here but we could do a 100% processing solution as well
        PGraphicsJava2D graphics = (PGraphicsJava2D) target.getGraphics();
        Graphics2D g2 = graphics.g2;
        
        
        RadialGradientPaint p = new RadialGradientPaint(new Point2D.Double(x, y), radius,
                new float[]{
                    0.0f, 1.0f},
                new Color[]{
                    startColor,
                    endColor});
        g2.setPaint(p);
        g2.fillOval((int) (x.floatValue() - radius), (int) (y.floatValue() - radius), (int) (radius * 2), (int) (radius * 2));
    }
    
    public void renderPDF(Item item, PDFTarget target, PreviewProperties properties) {
        //TODO Not implemented
    }
    
    public void renderSVG(Item item, SVGTarget target, PreviewProperties properties) {
        //TODO Not implemented
    }
    
    @Override
    public PreviewProperty[] getProperties() {
        return new PreviewProperty[]{
                    PreviewProperty.createProperty(this, ENABLE_NODE_GLOW, Boolean.class,
                    "Show glow effect",
                    "Glow effect around the node",
                    PreviewProperty.CATEGORY_NODES).setValue(false)
                };
    }
    
    @Override
    public boolean isRendererForitem(Item item, PreviewProperties properties) {
        return item instanceof NodeItem && properties.getBooleanValue(ENABLE_NODE_GLOW);
    }
    
    @Override
    public boolean needsItemBuilder(ItemBuilder itemBuilder, PreviewProperties properties){
        return itemBuilder instanceof NodeBuilder && properties.getBooleanValue(ENABLE_NODE_GLOW);
    }
}
