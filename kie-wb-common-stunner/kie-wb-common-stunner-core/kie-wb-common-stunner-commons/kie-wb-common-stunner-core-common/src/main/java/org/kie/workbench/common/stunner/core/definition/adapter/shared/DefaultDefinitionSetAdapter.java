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

package org.kie.workbench.common.stunner.core.definition.adapter.shared;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.impl.DefinitionSetImpl;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;

// TODO
@ApplicationScoped
public class DefaultDefinitionSetAdapter implements DefinitionSetAdapter<DefinitionSetImpl> {

    @Override
    public boolean accepts( final Class<?> pojo ) {
        return pojo.getName().equals( DefinitionSetImpl.class.getName() );
    }

    @Override
    public boolean isPojoModel() {
        return false;
    }

    @Override
    public String getId( final DefinitionSetImpl pojo ) {
        return null;
    }

    @Override
    public String getDomain( final DefinitionSetImpl pojo ) {
        return null;
    }

    @Override
    public String getDescription( final DefinitionSetImpl pojo ) {
        return null;
    }

    @Override
    public Set<String> getDefinitions( final DefinitionSetImpl pojo ) {
        return null;
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType( DefinitionSetImpl pojo ) {
        return null;
    }

    @Override
    public int getPriority() {
        return 1;
    }

}
