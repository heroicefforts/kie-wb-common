/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.editor;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.impl.AbstractClientSessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.*;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.util.ClientSessionUtils;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationViolation;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidatorCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

// TODO: i18n.
public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor {

    private static Logger LOGGER = Logger.getLogger( AbstractProjectDiagramEditor.class.getName() );

    public interface View extends UberView<AbstractProjectDiagramEditor>, KieEditorView, IsWidget {

        void setWidget( IsWidget widget );

    }

    private final PlaceManager placeManager;
    private final ErrorPopupPresenter errorPopupPresenter;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final R resourceType;
    private final ClientProjectDiagramService projectDiagramServices;
    private final AbstractClientSessionManager clientSessionManager;
    private final AbstractClientSessionPresenter clientSessionPresenter;
    private final BS3PaletteFactory paletteFactory;
    private final ClientSessionUtils sessionUtils;
    private final ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;

    private final ClearSelectionSessionCommand sessionClearSelectionCommand;
    private final VisitGraphSessionCommand sessionVisitGraphCommand;
    private final SwitchGridSessionCommand sessionSwitchGridCommand;
    private final ClearSessionCommand sessionClearCommand;
    private final DeleteSelectionSessionCommand sessionDeleteSelectionCommand;
    private final UndoSessionCommand sessionUndoCommand;
    private final RedoSessionCommand sessionRedoCommand;
    private final ValidateSessionCommand sessionValidateCommand;
    private final RefreshSessionCommand sessionRefreshCommand;

    private AbstractClientFullSession session;
    private BS3PaletteWidget paletteWidget;
    private String title = "Project Diagram Editor";

    @Inject
    public AbstractProjectDiagramEditor( final View view,
                                         final PlaceManager placeManager,
                                         final ErrorPopupPresenter errorPopupPresenter,
                                         final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                         final SavePopUpPresenter savePopUpPresenter,
                                         final R resourceType,
                                         final ClientProjectDiagramService projectDiagramServices,
                                         final AbstractClientSessionManager clientSessionManager,
                                         final AbstractClientSessionPresenter clientSessionPresenter,
                                         final BS3PaletteFactory paletteFactory,
                                         final ClientSessionUtils sessionUtils,
                                         final SessionCommandFactory sessionCommandFactory,
                                         final ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder ) {
        super( view );
        this.placeManager = placeManager;
        this.errorPopupPresenter = errorPopupPresenter;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;
        this.resourceType = resourceType;
        this.projectDiagramServices = projectDiagramServices;
        this.clientSessionManager = clientSessionManager;
        this.clientSessionPresenter = clientSessionPresenter;
        this.paletteFactory = paletteFactory;
        this.sessionUtils = sessionUtils;
        this.menuItemsBuilder = menuItemsBuilder;
        this.sessionClearSelectionCommand = sessionCommandFactory.newClearSelectionCommand();
        this.sessionVisitGraphCommand = sessionCommandFactory.newVisitGraphCommand();
        this.sessionSwitchGridCommand = sessionCommandFactory.newSwitchGridCommand();
        this.sessionClearCommand = sessionCommandFactory.newClearCommand();
        this.sessionDeleteSelectionCommand = sessionCommandFactory.newDeleteSelectedElementsCommand();
        this.sessionUndoCommand = sessionCommandFactory.newUndoCommand();
        this.sessionRedoCommand = sessionCommandFactory.newRedoCommand();
        this.sessionValidateCommand = sessionCommandFactory.newValidateCommand();
        this.sessionRefreshCommand = sessionCommandFactory.newRefreshSessionCommand();
    }

    protected abstract int getCanvasWidth();

    protected abstract int getCanvasHeight();

    @PostConstruct
    @SuppressWarnings( "unchecked" )
    public void init() {
        getView().init( this );
        // Create a new full control session.
        session = ( AbstractClientFullSession ) clientSessionManager.newFullSession();
        // Initialize the session presenter.
        clientSessionPresenter
                .setDisplayErrors( true )
                .initialize( session,
                        getCanvasWidth(),
                        getCanvasHeight() );
        // Use the session presenter's view.
        getView().setWidget( clientSessionPresenter.getView() );
        // Initialize toolbar's commands.
        bindCommands();
    }

    protected void doStartUp( final ObservablePath path,
                              final PlaceRequest place ) {
        init( path, place, resourceType );
    }

    @Override
    protected void loadContent() {
        projectDiagramServices.getByPath( versionRecordManager.getCurrentPath(), new ServiceCallback<ProjectDiagram>() {
            @Override
            public void onSuccess( ProjectDiagram item ) {
                open( item );
            }

            @Override
            public void onError( ClientRuntimeError error ) {
                showError( error );
            }
        } );

    }

    protected void open( final ProjectDiagram diagram ) {
        showLoadingViews();
        final Command callback = () -> {
            hideLoadingViews();
        };
        this.paletteWidget = buildPalette( diagram );
        clientSessionPresenter.getView().setPalette( this.paletteWidget.getView() );
        clientSessionPresenter.open( diagram, callback );
        updateTitle( diagram.getMetadata().getTitle() );
    }

    @Override
    protected Command onValidate() {
        showLoadingViews();
        return () -> {
            session.getCanvasValidationControl().validate();
            hideLoadingViews();
        };
    }

    @Override
    protected void save( final String commitMessage ) {
        showLoadingViews();
        session.getCanvasValidationControl().validate( new CanvasValidatorCallback() {
            @Override
            public void onSuccess() {
                doSave( commitMessage );
            }

            @Override
            public void onFail( Iterable<CanvasValidationViolation> violations ) {
                log( Level.WARNING, "Validation failed [violations=" + violations.toString() + "]." );
                hideLoadingViews();
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    protected void doSave( final String commitMessage ) {
        // Obtain diagram's image data before saving.
        final String thumbData = sessionUtils.canvasToImageData( session );
        // Update diagram's image data as thumbnail.
        final CanvasHandler canvasHandler = session.getCanvasHandler();
        final Diagram diagram = canvasHandler.getDiagram();
        diagram.getMetadata().setThumbData( thumbData );
        // Perform update operation remote call.
        projectDiagramServices.saveOrUpdate( versionRecordManager.getCurrentPath(),
                getDiagram(),
                metadata,
                commitMessage, new ServiceCallback<ProjectDiagram>() {
                    @Override
                    public void onSuccess( ProjectDiagram item ) {
                        getSaveSuccessCallback( item.hashCode() );
                        hideLoadingViews();
                    }

                    @Override
                    public void onError( ClientRuntimeError error ) {
                        showError( error );
                    }
                } );
    }

    @Override
    protected void makeMenuBar() {
        // TODO: fix - menu items not getting disabled/enabled?
        final MenuItem clearItem = menuItemsBuilder.newClearItem( AbstractProjectDiagramEditor.this::menu_clear );
        sessionClearCommand.listen( () -> clearItem.setEnabled( sessionClearCommand.isEnabled() ) );
        final MenuItem clearSelectionItem = menuItemsBuilder.newClearSelectionItem( AbstractProjectDiagramEditor.this::menu_clearSelection );
        sessionClearSelectionCommand.listen( () -> clearSelectionItem.setEnabled( sessionClearSelectionCommand.isEnabled() ) );
        final MenuItem visitGraphItem = menuItemsBuilder.newVisitGraphItem( AbstractProjectDiagramEditor.this::menu_visitGraph );
        sessionVisitGraphCommand.listen( () -> visitGraphItem.setEnabled( sessionVisitGraphCommand.isEnabled() ) );
        final MenuItem switchGridItem = menuItemsBuilder.newSwitchGridItem( AbstractProjectDiagramEditor.this::menu_switchGrid );
        sessionSwitchGridCommand.listen( () -> switchGridItem.setEnabled( sessionSwitchGridCommand.isEnabled() ) );
        final MenuItem deleteSelectionItem = menuItemsBuilder.newDeleteSelectionItem( AbstractProjectDiagramEditor.this::menu_deleteSelected );
        sessionDeleteSelectionCommand.listen( () -> deleteSelectionItem.setEnabled( sessionDeleteSelectionCommand.isEnabled() ) );
        final MenuItem undoItem = menuItemsBuilder.newUndoItem( AbstractProjectDiagramEditor.this::menu_undo );
        sessionUndoCommand.listen( () -> undoItem.setEnabled( sessionUndoCommand.isEnabled() ) );
        final MenuItem redoItem = menuItemsBuilder.newRedoItem( AbstractProjectDiagramEditor.this::menu_redo );
        sessionRedoCommand.listen( () -> redoItem.setEnabled( sessionRedoCommand.isEnabled() ) );
        final MenuItem validateItem = menuItemsBuilder.newValidateItem( AbstractProjectDiagramEditor.this::menu_validate );
        sessionValidateCommand.listen( () -> validateItem.setEnabled( sessionValidateCommand.isEnabled() ) );
        final MenuItem refreshItem = menuItemsBuilder.newRefreshItem( AbstractProjectDiagramEditor.this::menu_refresh );
        sessionRefreshCommand.listen( () -> refreshItem.setEnabled( sessionRefreshCommand.isEnabled() ) );
        // Build the menu.
        menuBuilder
                // Specific Stunner toolbar items.
                .addNewTopLevelMenu( clearItem )
                .addNewTopLevelMenu( clearSelectionItem )
                .addNewTopLevelMenu( visitGraphItem )
                .addNewTopLevelMenu( switchGridItem )
                .addNewTopLevelMenu( deleteSelectionItem )
                .addNewTopLevelMenu( undoItem )
                .addNewTopLevelMenu( redoItem )
                .addNewTopLevelMenu( validateItem )
                .addNewTopLevelMenu( refreshItem );
        if ( menuItemsBuilder.isDevItemsEnabled() ) {
            menuBuilder.addNewTopLevelMenu( menuItemsBuilder.newDevItems() );
        }
        menus = menuBuilder
                // Project editor menus.
                .addSave( versionRecordManager.newSaveMenuItem( () -> onSave() ) )
                .addCopy( versionRecordManager.getCurrentPath(), fileNameValidator )
                .addRename( versionRecordManager.getPathToLatest(), fileNameValidator )
                .addDelete( versionRecordManager.getPathToLatest() )
                .addNewTopLevelMenu( versionRecordManager.buildMenu() )
                // Build the menu.
                .build();
    }

    private void menu_clear() {
        sessionClearCommand.execute();
    }

    private void menu_clearSelection() {
        sessionClearSelectionCommand.execute();
    }

    private void menu_visitGraph() {
        sessionVisitGraphCommand.execute();
    }

    private void menu_switchGrid() {
        sessionSwitchGridCommand.execute();
    }

    private void menu_deleteSelected() {
        sessionDeleteSelectionCommand.execute();
    }

    private void menu_undo() {
        sessionUndoCommand.execute();
    }

    private void menu_redo() {
        sessionRedoCommand.execute();
    }

    private void menu_validate() {
        sessionValidateCommand.execute();
    }

    private void menu_refresh() {
        showLoadingViews();

        sessionRefreshCommand.execute( new ClientSessionCommand.Callback<Diagram>() {
            @Override
            public void onSuccess( final Diagram result ) {
                log( FINE, "Diagram refresh successful." );
                hideLoadingViews();
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                showError( error );
            }
        } );
    }

    protected void doOpen() {
        if ( null != session && session.isOpened() ) {
            clientSessionManager.resume( session );
        }
    }

    protected void showLoadingViews() {
        getView().showLoading();
        clientSessionPresenter.getView().setLoading( true );
    }

    protected void hideLoadingViews() {
        getView().hideBusyIndicator();
        clientSessionPresenter.getView().setLoading( false );
    }

    protected void doClose() {
        disposeSession();
    }

    protected void doFocus() {
        log( FINE, "Focusing Stunner Project Diagram Editor..." );
        if ( !isSameSession( clientSessionManager.getCurrentSession() ) ) {
            clientSessionManager.open( session );
        } else {
            log( FINE, "Session already active, no action." );
        }
    }

    protected void doLostFocus() {
        hidePaletteFloatingView();
    }

    void onSessionErrorEvent( @Observes OnSessionErrorEvent errorEvent ) {
        if ( isSameSession( errorEvent.getSession() ) ) {
            executeWithConfirm( "An error happened [" + errorEvent.getError() + "]. Do you want" +
                    "to refresh the diagram (Last changes can be lost)? ", this::menu_refresh );
        }
    }

    private boolean isSameSession( final ClientSession other ) {
        return null != other && null != session && other.equals( session );
    }

    public String getTitleText() {
        return title;
    }

    protected Menus getMenus() {
        if ( menus == null ) {
            makeMenuBar();
        }
        return menus;
    }

    protected boolean _onMayClose() {
        return super.mayClose( getCurrentDiagramHash() );
    }

    private void bindCommands() {
        this.sessionClearSelectionCommand.bind( session );
        this.sessionVisitGraphCommand.bind( session );
        this.sessionSwitchGridCommand.bind( session );
        this.sessionClearCommand.bind( session );
        this.sessionDeleteSelectionCommand.bind( session );
        this.sessionUndoCommand.bind( session );
        this.sessionRedoCommand.bind( session );
        this.sessionValidateCommand.bind( session );
        this.sessionRefreshCommand.bind( session );
    }

    private void unbindCommands() {
        this.sessionClearSelectionCommand.unbind();
        this.sessionVisitGraphCommand.unbind();
        this.sessionSwitchGridCommand.unbind();
        this.sessionClearCommand.unbind();
        this.sessionDeleteSelectionCommand.unbind();
        this.sessionUndoCommand.unbind();
        this.sessionRedoCommand.unbind();
        this.sessionValidateCommand.unbind();
        this.sessionRefreshCommand.unbind();
    }

    private void resume() {
        clientSessionManager.resume( session );
    }

    private void pauseSession() {
        clientSessionManager.pause();
    }

    private void disposeSession() {
        clientSessionManager.dispose();
        unbindCommands();
        if ( null != paletteWidget ) {
            destroyPalette();
        }
        this.paletteWidget = null;
        this.session = null;
    }

    private BS3PaletteWidget buildPalette( final ProjectDiagram diagram ) {
        return paletteFactory
                .forCanvasHandler( session.getCanvasHandler() )
                .newPalette( diagram.getMetadata().getShapeSetId() );
    }

    private void destroyPalette() {
        if ( null != paletteWidget ) {
            paletteWidget.unbind();
            paletteWidget.destroy();
        }
    }

    private void updateTitle( final String title ) {
        // Change editor's title.
        this.title = title;
        changeTitleNotificationEvent.fire( new ChangeTitleWidgetEvent( this.place, this.title ) );
    }

    private void hidePaletteFloatingView() {
        if ( null != paletteWidget ) {
            paletteWidget.getFloatingView().hide();
        }
    }

    private void showError( final ClientRuntimeError error ) {
        errorPopupPresenter.showMessage( error.toString() );
        hideLoadingViews();
    }

    protected int getCurrentDiagramHash() {
        if ( getDiagram() == null ) return 0;
        return getDiagram().hashCode();
    }

    protected ProjectDiagram getDiagram() {
        return null != clientSessionPresenter.getCanvasHandler() ? ( ProjectDiagram ) clientSessionPresenter.getCanvasHandler().getDiagram() : null;
    }

    private  void executeWithConfirm( final String message, final Command command ) {
        final Command yesCommand = command::execute;
        final Command noCommand = () -> {
        };
        final YesNoCancelPopup popup =
                YesNoCancelPopup.newYesNoCancelPopup( message, null, yesCommand, noCommand, noCommand );
        popup.show();
    }

    protected View getView() {
        return ( View ) baseView;
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
