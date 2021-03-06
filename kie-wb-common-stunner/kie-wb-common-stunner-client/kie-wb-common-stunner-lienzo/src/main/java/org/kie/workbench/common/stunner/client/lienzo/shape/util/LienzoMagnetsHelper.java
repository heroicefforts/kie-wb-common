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

package org.kie.workbench.common.stunner.client.lienzo.shape.util;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils;
import org.kie.workbench.common.stunner.core.client.shape.util.EdgeMagnetsHelper;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LienzoMagnetsHelper implements EdgeMagnetsHelper {

    @Override
    public int[] getDefaultMagnetsIndex( final ShapeView<?> sourceView,
                                         final ShapeView<?> targetView ) {
        return LienzoShapeUtils.getDefaultMagnetsIndex( ( WiresShape ) sourceView, ( WiresShape ) targetView );
    }
}
