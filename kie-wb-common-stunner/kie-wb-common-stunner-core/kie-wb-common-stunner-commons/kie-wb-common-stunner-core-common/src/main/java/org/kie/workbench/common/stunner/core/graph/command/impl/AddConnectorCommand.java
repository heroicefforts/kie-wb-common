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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command which adds an edge into a graph and sets its target node.
 */
@Portable
public final class AddConnectorCommand extends AbstractGraphCompositeCommand {

    private final String nodeUUID;
    private final Edge edge;
    private final Integer magnetIndex;
    private transient Node<?, Edge> node;

    public AddConnectorCommand( @MapsTo( "nodeUUID" ) String nodeUUID,
                                @MapsTo( "edge" ) Edge edge,
                                @MapsTo( "magnetIndex" ) Integer magnetIndex ) {
        this.nodeUUID = PortablePreconditions.checkNotNull( "nodeUUID",
                nodeUUID );
        this.edge = PortablePreconditions.checkNotNull( "edge",
                edge );
        this.magnetIndex = PortablePreconditions.checkNotNull( "magnetIndex", magnetIndex );
    }

    public AddConnectorCommand( Node<?, Edge> sourceNode,
                                Edge edge,
                                Integer magnetIndex ) {
        this( sourceNode.getUUID(), edge, magnetIndex );
        this.node = sourceNode;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void initialize( final GraphCommandExecutionContext context ) {
        super.initialize( context );
        final Node<? extends View<?>, Edge> source = ( Node<? extends View<?>, Edge> ) getNode( context );
        commands.add( new SetConnectionSourceNodeCommand( source, edge, magnetIndex ) );
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context ) {
        // Add the edge into index, so child commands can find it.
        getMutableIndex( context ).addEdge( edge );
        final CommandResult<RuleViolation> results = super.allow( context );
        if ( CommandUtils.isError( results ) ) {
            // Remove the transient edge after the error.
            getMutableIndex( context ).removeEdge( edge );
        }
        return results;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        // Add the edge into index, so child commands can find it.
        getMutableIndex( context ).addEdge( edge );
        final CommandResult<RuleViolation> results = super.execute( context );
        if ( CommandUtils.isError( results ) ) {
            // Remove the transient edge after the error.
            getMutableIndex( context ).removeEdge( edge );
        }
        return results;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> undo( GraphCommandExecutionContext context ) {
        final DeleteConnectorCommand undoCommand = new DeleteConnectorCommand( edge );
        return undoCommand.execute( context );
    }

    @SuppressWarnings( "unchecked" )
    private Node<?, Edge> getNode( final GraphCommandExecutionContext context ) {
        if ( null == node ) {
            node = getNode( context, nodeUUID );
        }
        return node;
    }

    public Edge getEdge() {
        return edge;
    }

    public Integer getMagnetIndex() {
        return magnetIndex;
    }

    public Node<?, Edge> getSourceNode() {
        return node;
    }

    @Override
    public String toString() {
        return "AddEdgeCommand [target=" + nodeUUID + ", edge=" + edge.getUUID() + "]";
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }

}
