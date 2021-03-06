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

package org.kie.workbench.common.stunner.client.lienzo.components.palette.impl;

import org.kie.workbench.common.stunner.client.lienzo.components.palette.AbstractLienzoGlyphItemsPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoGlyphsHoverPalette;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.LienzoHoverPaletteView;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class LienzoGlyphsHoverPaletteImpl
        extends AbstractLienzoGlyphItemsPalette<HasPaletteItems<? extends GlyphPaletteItem>, LienzoHoverPaletteView>
        implements LienzoGlyphsHoverPalette {

    protected LienzoGlyphsHoverPaletteImpl() {
        this( null, null, null );
    }

    @Inject
    public LienzoGlyphsHoverPaletteImpl( final ShapeManager shapeManager,
                                         final LienzoHoverPaletteView view,
                                         final DefinitionGlyphTooltip definitionGlyphTooltip ) {
        super( shapeManager, definitionGlyphTooltip, view );
    }

    @PostConstruct
    public void init() {
        super.doInit();
    }

}
