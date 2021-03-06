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

package org.kie.workbench.common.stunner.core.registry.impl;

import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.registry.factory.TypeFactoryRegistry;

import java.util.*;

class FactoryRegistryImpl<T extends Factory<?>> implements TypeFactoryRegistry<T> {

    private final AdapterManager adapterManager;
    private final List<DefinitionFactory<?>> definitionFactories = new LinkedList<>();
    private final Map<Class<? extends ElementFactory>, ElementFactory<?, ?, ?>> graphFactories =
            new HashMap<>();
    private final List<DiagramFactory<?, ?>> diagramFactories
            = new LinkedList<>();

    FactoryRegistryImpl( final AdapterManager adapterManager ) {
        this.adapterManager = adapterManager;
    }

    @Override
    public DefinitionFactory<?> getDefinitionFactory( final String id ) {
        for ( final DefinitionFactory<?> factory : definitionFactories ) {
            if ( factory.accepts( id ) ) {
                return factory;
            }
        }
        return null;
    }

    @Override
    public ElementFactory<?, ?, ?> getElementFactory( final Class<? extends ElementFactory> type ) {
        return graphFactories.get( type );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public DiagramFactory<?, ?> getDiagramFactory( final String defSetId,
                                                   final Class<? extends Metadata> metadataType ) {
        return diagramFactories.stream()
                .filter( factory -> factory.accepts( defSetId ) && metadataType.equals( factory.getMetadataType() )  )
                .findFirst()
                .orElse( getDefaultDiagramFactory( defSetId, metadataType ) );
    }

    private DiagramFactory<?, ?> getDefaultDiagramFactory( final String defSetId,
                                                            final Class<? extends Metadata> metadataType ) {
        return diagramFactories.stream()
                .filter( factory -> !factory.accepts( defSetId ) && metadataType.equals( factory.getMetadataType() )  )
                .findFirst()
                .orElse( null );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void register( final T item ) {
        if ( item instanceof DefinitionFactory ) {
            definitionFactories.add( ( DefinitionFactory<?> ) item );
        } else if ( item instanceof ElementFactory ) {
            graphFactories.put( ( ( ElementFactory ) item ).getFactoryType(), ( ElementFactory<?, ?, ?> ) item );
        } else if ( item instanceof DiagramFactory ) {
            diagramFactories.add( ( DiagramFactory<?, ?> ) item );
        }
    }

    @Override
    public boolean remove( final T item ) {
        if ( item instanceof DefinitionFactory ) {
            return definitionFactories.remove( item );
        } else if ( item instanceof ElementFactory ) {
            return null != graphFactories.remove( ( ( ElementFactory ) item ).getFactoryType() );
        } else  if ( item instanceof DiagramFactory ) {
            return diagramFactories.remove( item );
        }
        return false;
    }

    @Override
    public void clear() {
        definitionFactories.clear();
        graphFactories.clear();
        diagramFactories.clear();
    }

    @Override
    public boolean contains( final T item ) {
        if ( item instanceof DefinitionFactory ) {
            return definitionFactories.contains( item );
        } else if ( item instanceof ElementFactory ) {
            return graphFactories.containsValue( item );
        } else if ( item instanceof DiagramFactory ) {
            return diagramFactories.contains( item );
        }
        return false;

    }

    @Override
    public boolean isEmpty() {
        return definitionFactories.isEmpty() && graphFactories.isEmpty();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<T> getAllFactories() {
        return new LinkedList<T>() {{
            addAll( ( Collection<? extends T> ) definitionFactories );
            addAll( ( Collection<? extends T> ) diagramFactories );
            addAll( ( Collection<? extends T> ) graphFactories.values() );
        }};
    }

    @Override
    public DefinitionFactory<?> getDefinitionFactory( final Class<?> type ) {
        final String id = BindableAdapterUtils.getDefinitionId( type, adapterManager.registry() );
        return getDefinitionFactory( id );
    }

}
