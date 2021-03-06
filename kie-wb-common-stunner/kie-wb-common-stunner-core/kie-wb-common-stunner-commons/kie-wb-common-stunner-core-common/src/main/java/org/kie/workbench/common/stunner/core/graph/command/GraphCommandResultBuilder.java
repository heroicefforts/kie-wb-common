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

package org.kie.workbench.common.stunner.core.graph.command;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.Collection;
import java.util.LinkedList;

@NonPortable
public class GraphCommandResultBuilder extends CommandResultBuilder<RuleViolation> {

    public static final CommandResult<RuleViolation> SUCCESS = new CommandResultImpl<>(
            CommandResult.Type.INFO,
            RESULT_SUCCESS,
            new LinkedList<>()
    );

    public static final CommandResult<RuleViolation> FAILED = new CommandResultImpl<>(
            CommandResult.Type.ERROR,
            RESULT_FAILED,
            new LinkedList<>()
    );


    public GraphCommandResultBuilder() {
    }

    public GraphCommandResultBuilder( final Collection<RuleViolation> violations ) {
        super( violations );
    }

    @Override
    public boolean isError( final RuleViolation violation ) {
        return RuleViolation.Type.ERROR.equals( violation.getViolationType() );
    }

    @Override
    public String getMessage( final RuleViolation violation ) {
        return violation.getMessage();
    }
}
