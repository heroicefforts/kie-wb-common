/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.drag;

import org.kie.workbench.common.stunner.client.lienzo.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.GlyphDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.PrimitiveDragProxy;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GlyphDragProxyImpl implements GlyphDragProxy<AbstractCanvas> {

    PrimitiveDragProxy primitiveDragProxyFactory;

    @Inject
    public GlyphDragProxyImpl( final PrimitiveDragProxy primitiveDragProxyFactory ) {
        this.primitiveDragProxyFactory = primitiveDragProxyFactory;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public DragProxy<AbstractCanvas, Glyph, DragProxyCallback> proxyFor( final AbstractCanvas context ) {
        final LienzoLayer layer = ( LienzoLayer ) context.getLayer();
        this.primitiveDragProxyFactory.proxyFor( layer.getLienzoLayer() );
        return this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public DragProxy<AbstractCanvas, Glyph, DragProxyCallback> show( final Glyph item,
                                                                     final int x,
                                                                     final int y,
                                                                     final DragProxyCallback callback ) {
        primitiveDragProxyFactory.show( item.getGroup(), x, y, callback );
        return this;
    }

    @Override
    public void clear() {
        if ( null != this.primitiveDragProxyFactory ) {
            this.primitiveDragProxyFactory.clear();
        }
    }

    @Override
    public void destroy() {
        if ( null != this.primitiveDragProxyFactory ) {
            this.primitiveDragProxyFactory.destroy();
        }
        this.primitiveDragProxyFactory = null;

    }

}
