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

package org.kie.workbench.common.stunner.core.client.session.impl;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.api.platform.ClientPlatform;
import org.kie.workbench.common.stunner.core.client.api.platform.PlatformManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.event.*;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ClientSessionManagerImpl extends AbstractClientSessionManager {

    private static Logger LOGGER = Logger.getLogger( ClientSessionManagerImpl.class.getName() );

    private final PlatformManager platformManager;
    private final Event<SessionOpenedEvent> sessionOpenedEvent;
    private final Event<SessionPausedEvent> sessionPausedEvent;
    private final Event<SessionResumedEvent> sessionResumedEvent;
    private final Event<SessionDisposedEvent> sessionDisposedEvent;
    private final Event<OnSessionErrorEvent> sessionErrorEvent;

    protected ClientSessionManagerImpl() {
        this( null, null, null, null, null, null );
    }

    @Inject
    public ClientSessionManagerImpl( final PlatformManager platformManager,
                                     final Event<SessionOpenedEvent> sessionOpenedEvent,
                                     final Event<SessionDisposedEvent> sessionDisposedEvent,
                                     final Event<SessionPausedEvent> sessionPausedEvent,
                                     final Event<SessionResumedEvent> sessionResumedEvent,
                                     final Event<OnSessionErrorEvent> sessionErrorEvent ) {
        this.platformManager = platformManager;
        this.sessionOpenedEvent = sessionOpenedEvent;
        this.sessionPausedEvent = sessionPausedEvent;
        this.sessionResumedEvent = sessionResumedEvent;
        this.sessionDisposedEvent = sessionDisposedEvent;
        this.sessionErrorEvent = sessionErrorEvent;
    }

    protected void postOpen() {
        this.sessionOpenedEvent.fire( new SessionOpenedEvent( current ) );
    }

    protected void postPause() {
        this.sessionPausedEvent.fire( new SessionPausedEvent( current ) );
    }

    protected void postResume() {
        this.sessionResumedEvent.fire( new SessionResumedEvent( current ) );
    }

    protected void postDispose() {
        this.sessionDisposedEvent.fire( new SessionDisposedEvent( current ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> newReadOnlySession() {
        final ClientPlatform platform = getPlatform();
        ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> session = null;
        if ( platform instanceof AbstractClientSessionProducer ) {
            final AbstractClientSessionProducer sessionProducer = ( AbstractClientSessionProducer ) platform;
            session = sessionProducer.newReadOnlySession();

        }
        return session;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public ClientFullSession<AbstractCanvas, AbstractCanvasHandler> newFullSession() {
        final ClientPlatform platform = getPlatform();
        ClientFullSession<AbstractCanvas, AbstractCanvasHandler> session = null;
        if ( platform instanceof AbstractClientSessionProducer ) {
            final AbstractClientSessionProducer sessionProducer = ( AbstractClientSessionProducer ) platform;
            session = sessionProducer.newFullSession();

        }
        return session;
    }

    @Override
    public void handleCommandError( final CommandException ce ) {
        super.handleCommandError( ce );
        sessionErrorEvent.fire( new OnSessionErrorEvent( current,
                new ClientRuntimeError( "Error while executing command.", ce ) ) );
    }

    @Override
    public void handleClientError( final ClientRuntimeError error ) {
        super.handleClientError( error );
        sessionErrorEvent.fire( new OnSessionErrorEvent( current, error ) );
    }

    protected ClientPlatform getPlatform() {
        return platformManager.getCurrentPlatform();

    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
