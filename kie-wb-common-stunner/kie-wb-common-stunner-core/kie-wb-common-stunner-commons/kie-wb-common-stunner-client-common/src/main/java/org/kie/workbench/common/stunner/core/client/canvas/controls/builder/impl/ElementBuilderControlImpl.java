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

package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.model.ModelCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelContainmentRuleManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Build element on the canvas, either a node or an edge.
 */
@Dependent
@Element
public class ElementBuilderControlImpl extends AbstractElementBuilderControl {

    @Inject
    public ElementBuilderControlImpl( final ClientDefinitionManager clientDefinitionManager,
                                      final ClientFactoryService clientFactoryServices,
                                      final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                      final GraphUtils graphUtils,
                                      final ModelContainmentRuleManager modelContainmentRuleManager,
                                      final ModelCardinalityRuleManager modelCardinalityRuleManager,
                                      final CanvasCommandFactory canvasCommandFactory,
                                      final GraphBoundsIndexer graphBoundsIndexer,
                                      final CanvasLayoutUtils canvasLayoutUtils ) {
        super( clientDefinitionManager, clientFactoryServices, canvasCommandManager, graphUtils,
                modelContainmentRuleManager, modelCardinalityRuleManager, canvasCommandFactory,
                graphBoundsIndexer, canvasLayoutUtils );

    }

}
