/*
 * funCKit - functional Circuit Kit
 * Copyright (C) 2013  Lukas Elsner <open@mindrunner.de>
 * Copyright (C) 2013  Peter Dahlberg <catdog2@tuxzone.org>
 * Copyright (C) 2013  Julian Stier <mail@julian-stier.de>
 * Copyright (C) 2013  Sebastian Vetter <mail@b4sti.eu>
 * Copyright (C) 2013  Thomas Poxrucker <poxrucker_t@web.de>
 * Copyright (C) 2013  Alexander Treml <alex.treml@directbox.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.sep2011.funckit.model.sessionmodel;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.sep2011.funckit.util.Log.gl;

import java.awt.Cursor;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.sep2011.funckit.Application;
import de.sep2011.funckit.controller.CreateTool;
import de.sep2011.funckit.controller.Tool;
import de.sep2011.funckit.drawer.Drawer;
import de.sep2011.funckit.drawer.FancyDrawer;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel.ToolMode;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.observer.AbstractObservable;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.CommandDispatcher;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.view.View;

/**
 * Current session model representing data like project path, current assigned
 * editing tool, command dispatcher objects, used drawer object or current
 * selected brick for creating new bricks.
 */
public class SessionModel extends AbstractObservable<SessionModelObserver, SessionModelInfo> {

    public static enum ViewType {
        VIEW_TYPE_STANDALONE, VIEW_TYPE_ELEANING_SOLVE, VIEW_TYPE_PRESENTER;
    }

    /**
     * Defines the current global Cursor of the Application.
     */
    private Cursor currentCursor;

    /**
     * The currently shown projects in the project explorer.
     */
    private List<Project> projects;

    /**
     * The current active project.
     */
    private Project currentProject;

    /**
     * Application window position.
     */
    private Point windowPosition;

    /**
     * Current used tool object, specifying a strategy for mouse- or key actions
     * on edit events.
     */
    private Tool tool;

    /**
     * Currently used drawer object, specifying strategy how to accept certain
     * elements by double dispatching them.
     */
    private Drawer drawer;

    /**
     * Current selected brick for creating new bricks in create mode.
     * {@link CreateTool}
     */
    private Brick currentBrick;

    private final NewBrickListManager newBrickListManager;

    private Circuit copyBuffer;

    private Tool lastTool;

    private final Settings settings;

    private final SessionModel.ViewType currentViewType;

    /**
     * Create a new SessionModel.
     * 
     * @param application
     *            the associated {@link Application}
     * @param settings
     *            the associated {@link Settings} object
     * @param the
     *            {@link SessionModel.ViewType} this view should be of, not null
     */
    @SuppressWarnings("unchecked")
    public SessionModel(Application application, Settings settings, ViewType viewType) {
        checkNotNull(viewType);
        currentViewType = viewType;
        initInfo(SessionModelInfo.getInfo());
        this.settings = settings;
        /*
         * Note: cannot properly check if this is a list of Projects because of
         * generics
         */
        projects = settings.get(Settings.OPENED_PROJECTS, List.class);
        if (projects != null) {
            for (Project p : projects) {
                p.initializeTransient(settings);
                if (p.getName().equals("")) {
                    p.setName(Language.tr("project.unknown"));
                }
            }
        } else {
            projects = new LinkedList<Project>();
        }
        drawer = new FancyDrawer(settings);
        currentCursor = new Cursor(Cursor.DEFAULT_CURSOR);

        newBrickListManager = new NewBrickListManager(this,
                application.getComponentListDirectory(), application.LoadInternalComponentTypes());
        currentBrick = getNewBrickList().get(0);
        copyBuffer = new CircuitImpl();
    }

    /**
     * Prepare for exit and inform observers that we want exit.
     */
    public void prepareExit() {
        Log.gl().info("Clean application exit initiated");
        setChanged();
        getInfo().setPrepareExit(true);
        notifyObserversIfAuto();
    }
    
    /**
     * Returns the {@link SessionModel.ViewType} this {@link View} is of
     * 
     * @return the currentViewType
     */
    public SessionModel.ViewType getViewType() {
        return currentViewType;
    }

    /**
     * Returns the settings object associated with this session model.
     * 
     * @return the settings object associated with this session model
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Returns the Project that is currently active.
     * 
     * @return the Project that is currently active, null if no Project is
     *         Active.
     */
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Sets the current selected Project of this session.
     * 
     * @param project the current selected Project of this session.
     */
    public void setCurrentProject(Project project) {
        currentProject = project;
        setChanged();
        getInfo().setChangedProject(project).setCurrentProjectChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Returns a {@link List} of template Bricks to add to the {@link Circuit}.
     * 
     * @return a {@link List} of template Bricks to add to the {@link Circuit}
     */
    public java.util.List<Brick> getNewBrickList() {
        return newBrickListManager.getNewBrickList();
    }

    /**
     * Returns the {@link NewBrickListManager} associated with this session.
     * 
     * @return the {@link NewBrickListManager} associated with this session
     */
    public NewBrickListManager getNewBrickListManager() {
        return newBrickListManager;
    }

    /**
     * Getter method for current window position, stored in model.
     * 
     * @return Current stored window position.
     */
    public Point getWindowPosition() {
        return windowPosition;
    }

    /**
     * Specifies new window position in session model.
     * 
     * @param windowPosition
     *            the window position
     */
    public void setWindowPosition(Point windowPosition) {
        this.windowPosition = windowPosition;
    }

    /**
     * Getter method for current assigned tool object.
     * 
     * @return Tool strategy object.
     */
    public Tool getTool() {
        return tool;
    }

    public void forceTool(Tool tool) {
        this.tool = tool;
        gl().debug("Set tool:" + tool);

        /* some cleanup */
        for (Project p : projects) {
            for (EditPanelModel e : p.getOpenedEditPanelModels()) {
                e.setToolMode(ToolMode.DEFAULT_MODE);
                Set<Element> gh = e.getGhosts();
                gh.clear();
                e.setGhosts(gh);
                e.setSelectionStart(null);
                e.setSelectionEnd(null);
            }
        }

        /* set tools default cursor on all editpanel models */
        setCursorOnEditPanels(tool.getToolDefaultCursor());

        setChanged();
        getInfo().setToolChanged(true);
        notifyObserversIfAuto();
    }

    private void setCursorOnEditPanels(Cursor cursor) {
        for (Project project : getProjects()) {
            for (EditPanelModel epmodel : project.getOpenedEditPanelModels()) {
                epmodel.setCursor(cursor);
            }

        }
    }

    /**
     * Specifies a new tool object.
     * 
     * @param tool
     *            a new tool object.
     */
    public void setTool(Tool tool) {
        assert tool != null;
        if (currentProject != null) {
            if (currentViewType == ViewType.VIEW_TYPE_PRESENTER || currentProject.hasSimulation()
                    || (currentProject.getSelectedEditPanelModel() != null && !currentProject
                            .getSelectedEditPanelModel().hasMainCircuit())) {
                gl().warn("tried to set tool in non edit mode");
                return;
            }
        }
        forceTool(tool);
    }

    /**
     * Short for {@link Project#getSimulation()} on {@link #getCurrentProject()}
     * .
     * 
     * 
     * @return current simulation, null if there is none
     */
    public Simulation getCurrentSimulation() {
        return currentProject != null ? currentProject.getSimulation() : null;
    }

    /**
     * Returns currently assigned drawer object.
     * 
     * @return currently assigned drawer object.
     */
    public Drawer getDrawer() {
        return drawer;
    }

    /**
     * Specifies a new drawer strategy object.
     * 
     * @param drawer
     *            a new drawer strategy object
     */
    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public void notifyObserver(SessionModelInfo i, SessionModelObserver obs) {
        obs.sessionModelChanged(this, i);
    }

    /**
     * Returns set of results from latest performed set of checks of the current
     * project.
     * 
     * @return set of results from latest performed set of checks of the current
     *         project.
     */
    public List<Result> getCurrentCheckResults() {
        return currentProject != null ? currentProject.getCheckResults() : null;
    }

    /**
     * Sets current brick to create brick of that type in create mode.
     * 
     * @param currentBrick
     *            current brick to create brick of that type in create mode
     */
    public void setCurrentBrick(Brick currentBrick) {
        this.currentBrick = currentBrick;
        setChanged();
        getInfo().setCurrentBrickChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Returns current selected brick to copy and add it in graph model.
     * 
     * @return the current selected brick to copy and add it in graph model.
     */
    public Brick getCurrentBrick() {
        return currentBrick;
    }

    /**
     * Returns the current {@link Cursor}.
     * 
     * @return the current {@link Cursor}, not null
     */
    public Cursor getCurrentCursor() {
        return currentCursor;
    }

    /**
     * Set the {@link Cursor}.
     * 
     * @param currentCursor
     *            the {@link Cursor}, not null
     */
    public void setCurrentCursor(Cursor currentCursor) {
        if (!this.currentCursor.equals(currentCursor)) {
            this.currentCursor = currentCursor;
            setChanged();
        }
        getInfo().setCursorChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Returns the currently selected {@link EditPanelModel} of the current
     * project.
     * 
     * @return the currently selected {@link EditPanelModel}, null if none is
     *         selected
     * @since implementation
     */
    public EditPanelModel getSelectedEditPanelModel() {
        return currentProject != null ? currentProject.getSelectedEditPanelModel() : null;
    }

    /**
     * Stores a Circuit for copy and paste.
     * 
     * @return never null
     */
    public Circuit getCopyBuffer() {
        return copyBuffer;
    }

    /**
     * Set copy Buffer.
     * 
     * @param copyBuffer
     *            not null
     */
    public void setCopyBuffer(Circuit copyBuffer) {
        assert copyBuffer != null;
        gl().debug("Set copyBuffer with " + copyBuffer.getElements().size() + " Elements");
        this.copyBuffer = copyBuffer;
        setChanged();
        getInfo().setCopyBufferChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Saves the current {@link Tool} if no {@link Tool} was saved before.
     */
    public void saveTool() {
        if (lastTool == null) {
            lastTool = tool;
            gl().debug("Saved tool : " + tool);
        }
    }

    /**
     * Returns true if the {@link Tool} was saved before and not restored.
     * 
     * @return true if the {@link Tool} was saved before and not restored.
     */
    public boolean isToolSaved() {
        return lastTool != null;
    }

    /**
     * Restores the {@link Tool} to the saved {@link Tool} and deletes the saved
     * one. If no {link Tool} was saved nothing is done.
     * 
     * @return the restored {@link Tool}.
     */
    public Tool restoreTool() {
        if (currentProject != null) {
            if (currentViewType == ViewType.VIEW_TYPE_PRESENTER || currentProject.hasSimulation()
                    || (currentProject.getSelectedEditPanelModel() != null && !currentProject
                            .getSelectedEditPanelModel().hasMainCircuit())) {
                gl().warn("tried to restore tool in non edit mode");
                return tool;
            }
        }
        if (lastTool != null) {
            setTool(lastTool);
            lastTool = null;
            gl().debug("Restored tool: " + tool);
        }
        return tool;
    }

    /**
     * Adds a Project to the model.
     * 
     * @param p
     *            a Project, not null
     */
    public void addProject(Project p) {
        projects.add(p);
        persistProjects();
        setChanged();
        getInfo().setChangedProject(p).setProjectAdded(true);
        notifyObserversIfAuto();
    }

    /**
     * Removes a Project from the model.
     * 
     * @param p
     *            a Project inside the model, not null
     */
    public void removeProject(Project p) {
        projects.remove(p);
        if (p == currentProject) {
            currentProject = null;
        }
        persistProjects();
        setChanged();
        getInfo().setChangedProject(p).setProjectRemoved(true);
        notifyObserversIfAuto();
    }

    /**
     * Persists the projects into the settings.
     */
    public void persistProjects() {
        List<Project> persistentProjects = new LinkedList<Project>();
        for (Project persistentProject : projects) {
            if (persistentProject.getAbsolutePath() != null
                    && !persistentProject.getAbsolutePath().equals("")) {
                persistentProjects.add(persistentProject);
            }
        }

        settings.set(Settings.OPENED_PROJECTS, persistentProjects); // update
                                                                    // settings
    }

    /**
     * Gets the list of currently opened {@link Project}s. Warning! This is a
     * reference to the internal used list. Do not modify.
     * 
     * @return the list of currently opened {@link Project}s.
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * Returns the current Circuit {@link CommandDispatcher} from the Project.
     * 
     * @return the current Circuit {@link CommandDispatcher}, null if there is
     *         none
     */
    public CommandDispatcher getCurrentGraphCommandDispatcher() {
        return currentProject != null ? currentProject.getGraphCommandDispatcher() : null;
    }

    /**
     * Returns the current Simulation {@link CommandDispatcher} from the
     * Project.
     * 
     * @return the current Simulation {@link CommandDispatcher}, null if there
     *         is none
     */
    public CommandDispatcher getCurrentSimulationCommandDispatcher() {
        return currentProject != null ? currentProject.getSimulationCommandDispatcher() : null;
    }

    /**
     * Returns the Path of the current Project.
     * 
     * @return the Path of the current Project, null if there is none
     */
    public String getCurrentProjectPath() {
        return currentProject != null ? currentProject.getAbsolutePath() : null;
    }

}
