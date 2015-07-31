/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;

@Portable
public class ContainerOnError {

    private String message;
    private ContainerRef container;

    public ContainerOnError() {

    }

    public ContainerOnError(final String message, final ContainerRef container) {
        this.message = message;
        this.container = container;
    }

    public ContainerRef getContainer() {
        return container;
    }

    public String getMessage() {
        return message;
    }
}
