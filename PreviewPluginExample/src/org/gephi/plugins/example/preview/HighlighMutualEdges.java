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
import org.gephi.preview.api.*;
import org.gephi.preview.plugin.builders.NodeBuilder;
import org.gephi.preview.plugin.items.EdgeItem;
import org.gephi.preview.plugin.renderers.EdgeRenderer;
import org.gephi.preview.spi.ItemBuilder;
import org.gephi.preview.spi.Renderer;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Changes the color of mutual edges. Note you'll need to set the edge color to the
 * 'Original' mode to see that working.
 * <p>
 * This example shows how a renderer can customize the output of another renderer in the
 * <code>preProcess()</code> phase. Renderers are executed sequentially using the
 * <code>position = 150</code> parameter set to the <code>@ServiceProvider</code>
 * annotation. When no position is set the renderer is executed after all default
 * renderers (nodes, edges, labels and arrows). The default position for the edge
 * renderer is 100 and 200 for the arrow renderer. We know that the arrow renderer will
 * use whatever color is set for the edge so for this example we set a position
 * equal to 150 to be executed after the edge renderer but before the arrow renderer.
 * <p>
 * This example doesn't do anything in the <code>render()</code> method but modifies
 * the color of the edge item.
 * <p>
 * The renderer defines two new properties. Each property should have a unique name
 * so it's a good practice to set the property name as a public constant. Note that
 * the <code>MUTUALEGDE_HIGHLIGHT_COLOR</code> property depends on the
 * <code>MUTUALEGDE_HIGHLIGHT</code> property. Dependencies work only with boolean
 * properties and model the need to enable/disable features. Indeed, no need to
 * customize the highlight color if the feature is disabled by the user.
 * 
 * @author Mathieu Bastian
 * @see EdgeRenderer
 * @see EdgeItem
 */
@ServiceProvider(service = Renderer.class, position = 150)
public class HighlighMutualEdges implements Renderer {

    //Custom properties
    public static final String MUTUALEGDE_HIGHLIGHT = "mutualedge.highlight";
    public static final String MUTUALEGDE_HIGHLIGHT_COLOR = "mutualedge.highlight.color";
    //Default values
    protected boolean defaultHighlightMutualEdges = false;
    protected Color defaultHighlightColor = Color.RED;
    
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(HighlighMutualEdges.class, "HighlighMutualEdges.name");
    }

    @Override
    public void preProcess(PreviewModel previewModel) {
        PreviewProperties properties = previewModel.getProperties();

        //Check if the boolean property is set
        if (properties.getBooleanValue(MUTUALEGDE_HIGHLIGHT)) {
            Color color = properties.getColorValue(MUTUALEGDE_HIGHLIGHT_COLOR);

            // Retrieve all edge items in the model
            // As this renderer is called after the EdgeRenderer (which has a position=100, 
            // and this renderer has no specific position) we know these edge
            // items are well defined and already posses a color
            Item[] edgeItems = previewModel.getItems(Item.EDGE);
            for (Item item : edgeItems) {
                EdgeItem edgeItem = (EdgeItem) item;
                Boolean mutual = edgeItem.getData(EdgeItem.MUTUAL);
                if (mutual) {
                    //If mutual edge, change the color
                    edgeItem.setData(EdgeItem.COLOR, color);
                }
            }
        }
    }

    @Override
    public void render(Item item, RenderTarget rt, PreviewProperties pp) {
    }

    @Override
    public PreviewProperty[] getProperties() {
        return new PreviewProperty[]{
                    PreviewProperty.createProperty(this, MUTUALEGDE_HIGHLIGHT, Boolean.class,
                    "Highlight mutual edges",
                    "Color the mutual edges with a custom color",
                    PreviewProperty.CATEGORY_EDGES).setValue(defaultHighlightMutualEdges),
                    PreviewProperty.createProperty(this, MUTUALEGDE_HIGHLIGHT_COLOR, Color.class,
                    "Highlight mutual edges color",
                    "Set highlight color",
                    PreviewProperty.CATEGORY_EDGES, MUTUALEGDE_HIGHLIGHT).setValue(defaultHighlightColor)
                };
    }

    @Override
    public boolean isRendererForitem(Item item, PreviewProperties pp) {
        return false;
    }
    
    @Override
    public boolean needsItemBuilder(ItemBuilder itemBuilder, PreviewProperties properties){
        return false;
    }
}
