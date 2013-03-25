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

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.observer.AbstractObservable;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.CommandDispatcher;
import de.sep2011.funckit.validator.Result;

/**
 * This class represents a project in this application. It has all the data that
 * a belongs only to one project, such as the {@link Circuit}, the current
 * {@link Simulation} of the {@link Circuit}, the opened views (
 * {@link EditPanelModel}s) of the {@link Circuit}, the dispatchers for the
 * {@link Command}s etc.
 */
public class Project extends AbstractObservable<ProjectObserver, ProjectInfo>
        implements Serializable, GraphModelObserver {

    /**
     * Used for serialization.
     */
    private static final long serialVersionUID = 3658148621266426656L;

    /**
     * Name of this project.
     */
    private String name;

    /**
     * Path to project on hard disk for persisting circuit and project
     * information.
     */
    private String path;

    /**
     * The current active simulation for this project.
     */
    transient private Simulation simulation;

    /**
     * The models for the currently opened EditPanels.
     */
    transient private Set<EditPanelModel> openedEditPanelModels;

    /**
     * The currently selected {@link EditPanelModel}.
     */
    transient private EditPanelModel selectedEditPanelModel;

    /**
     * The circuit belonging to this project.
     */
    transient private Circuit circuit;

    /**
     * Set of results from a validator or set of checks to display information
     * on GUI.
     */
    transient private List<Result> checkResults;

    /**
     * Ghosts that should marked erroneous.
     */
    transient private Set<Element> errorGhosts;

    /**
     * Command dispatcher object for dispatching or undoing commands performed
     * on graph model.
     */
    transient private CommandDispatcher graphCommandDispatcher;

    /**
     * Command dispatcher object to dispatch or undo simulation commands.
     */
    transient private CommandDispatcher simulationCommandDispatcher;

    /**
     * Was the project modified since last save?
     */
    transient private boolean modified;

    /**
     * Specifies the delay of the simulation timer in miliseconds for this
     * project.
     */
    private transient int timerDelay;

    /**
     * true if simulation is in paused state.
     */
    private transient boolean simulationPaused;

    /**
     * Returns the timer delay.
     * 
     * @return the timer delay
     */
    public int getTimerDelay() {
        return timerDelay;
    }

    /**
     * Sets the delay of the simulation timer in milliseconds.
     * 
     * @param timerDelay
     *            should be >= 0
     */
    public void setTimerDelay(int timerDelay) {
        assert timerDelay >= 0;
        this.timerDelay = timerDelay;
        setChanged();
        getInfo().setSimulationControlStateModified(true);
        notifyObserversIfAuto();
    }

    /**
     * Creates a new {@link Project} with the given {@link Circuit}, the given
     * name and the given application {@link Settings}.
     * 
     * @param circuit
     *            the {@link Circuit} of this {@link Project}.
     * @param name
     *            the name of this {@link Project}. Has to be non null.
     * @param settings
     *            the {@link Settings} of the application to use. Has to be non
     *            null.
     */
    public Project(Circuit circuit, String name, Settings settings) {
        assert name != null;
        this.name = name;
        this.circuit = circuit;
        if (circuit != null) {
            circuit.addObserver(this);
        }
        initializeTransient(settings);
    }

    /**
     * Initializes all the transient attributes of this {@link Project} with the
     * given {@link Settings} of the application.
     * 
     * @param settings
     *            the {@link Settings} of the application.
     */
    public void initializeTransient(Settings settings) {
        assert settings != null;
        initInfo(ProjectInfo.getInfo());
        graphCommandDispatcher =
                new CommandDispatcher(
                        settings.getInt(Settings.MAXIMUM_COMMAND_QUEUE_SIZE));
        simulationCommandDispatcher =
                new CommandDispatcher(
                        settings.getInt(Settings.MAXIMUM_COMMAND_QUEUE_SIZE));
        openedEditPanelModels = new HashSet<EditPanelModel>();
        checkResults = new LinkedList<Result>();
        timerDelay = settings.getInt(Settings.DEFAULT_TIMER_DELAY);
        simulationPaused = false;
    }

    /**
     * Return true if the simulation is in paused state.
     * 
     * @return true if the simulation is in paused state
     */
    public boolean isSimulationPaused() {
        return simulationPaused;
    }

    /**
     * true means simulation paused.
     * 
     * @param simulationPaused
     *            true means simualtion paused
     */
    public void setSimulationPaused(boolean simulationPaused) {
        this.simulationPaused = simulationPaused;
        setChanged();
        getInfo().setSimulationControlStateModified(true);
        notifyObserversIfAuto();
    }

    @Override
    public void notifyObserver(ProjectInfo i, ProjectObserver obs) {
        obs.projectChanged(this, i);
    }

    /**
     * Getter method for project name.
     * 
     * @return Project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Specifies new project name. Notifies all observers.
     * 
     * @param name
     */
    public void setName(String name) {
        assert name != null;
        if (!this.name.equals(name)) {
            modified = true;
        }

        this.name = name;
        setChanged();
        getInfo().setNameChanged(true).setModified(true);
        notifyObserversIfAuto();
    }

    /**
     * Getter method for project hard disk path.
     * 
     * @return Hard disk path as string. Can be null.
     */
    public String getAbsolutePath() {
        return path;
    }

    /**
     * Specifies project path as string. Notifies all observers.
     * 
     * @param path
     *            project path as string.
     */
    public void setPath(String path) {
        this.path = path;
        setChanged();
        getInfo().setPathChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Get the current {@link Simulation} for this project.
     * 
     * @return the current {@link Simulation} for this project. Can be null.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * Set the current {@link Simulation} for this project. Notifies all
     * observers.
     * 
     * @param simulation
     *            {@link Simulation} to set as current.
     */
    public void setSimulation(Simulation simulation) {
        getInfo().setOldSimulation(this.simulation);
        this.simulation = simulation;
        setChanged();
        getInfo().setSimulationChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Returns true if this project is currently simulated.
     * 
     * @return true if this project is currently simulated.
     */
    public boolean hasSimulation() {
        return simulation != null;
    }

    /**
     * Returns the currently opened {@link EditPanelModel}s. This a reference to
     * the internally used {@link Set} - do not modify!.
     * 
     * @return the currently opened {@link EditPanelModel}s.
     */
    public Set<EditPanelModel> getOpenedEditPanelModels() {
        return openedEditPanelModels;
    }

    /**
     * Gets the current selected {@link EditPanelModel}. Returns null if noting
     * is selected.
     * 
     * @return the current selected {@link EditPanelModel}. Can be null.
     */
    public EditPanelModel getSelectedEditPanelModel() {
        return selectedEditPanelModel;
    }

    /**
     * Sets the currently selected {@link EditPanelModel}. Notifies all
     * observers.
     * 
     * @param panelModel
     *            the new selected {@link EditPanelModel}. Use null to unset.
     */
    public void setSelectedEditPanelModel(EditPanelModel panelModel) {
        selectedEditPanelModel = panelModel;
        setChanged();
        getInfo().setChangedModel(panelModel).setActiveEditPanelModelChanged(
                true);
        notifyObserversIfAuto();
    }

    /**
     * Returns set of results from latest performed set of checks.
     * 
     * @return set of results from latest performed set of checks. Is null if no
     *         check was performed.
     */
    public List<Result> getCheckResults() {
        return checkResults;
    }

    /**
     * Sets the set of results from latest performed set of checks. Notifies all
     * observers.
     * 
     * @param checkResults
     *            the new set of results. Use null to unset.
     */
    public void setCheckResults(List<Result> checkResults) {
        this.checkResults = checkResults;
        setChanged();
        getInfo().setResultsChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Returns the circuit that belongs to this project.
     * 
     * @return the circuit that belongs to this project.
     */
    public Circuit getCircuit() {
        return circuit;
    }

    /**
     * Sets the {@link Circuit} that belongs to this project. Notifies all
     * observers. Warning! This does just set the new circuit. Nothing is done
     * to ensure consistent {@link EditPanelModel}s etc.
     * 
     * @param circuit
     *            the new {@link Circuit} of this project.
     */
    public void setCircuit(Circuit circuit) {
        if (this.circuit != null) {
            this.circuit.deleteObserver(this);
        }
        this.circuit = circuit;
        if (circuit == null) {
            setSimulation(null);
        } else {
            circuit.addObserver(this);
        }
        setChanged();
        getInfo().setCircuitChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * Adds a new {@link EditPanelModel} to the opened {@link EditPanelModel}s.
     * Notifies all observers.
     * 
     * @param panelModel
     *            the new {@link EditPanelModel}. Has to be non null.
     */
    public void addEditPanelModel(EditPanelModel panelModel) {
        assert panelModel != null;
        openedEditPanelModels.add(panelModel);
        setChanged();
        getInfo().setChangedModel(panelModel).setEditPanelModelAdded(true);
        notifyObserversIfAuto();
    }

    /**
     * Removes the given {@link EditPanelModel} from the opened models and
     * notifies the observers.
     * 
     * @param model
     *            {@link EditPanelModel} to remove.
     */
    public void removeEditPanelModel(EditPanelModel model) {
        if (openedEditPanelModels.contains(model)) {
            openedEditPanelModels.remove(model);
            setChanged();
            getInfo().setChangedModel(model).setEditPanelModelRemoved(true);
            if (selectedEditPanelModel == model) {
                selectedEditPanelModel = null;
                getInfo().setChangedModel(model)
                        .setActiveEditPanelModelChanged(true);
            }
            notifyObserversIfAuto();
        }
    }

    /**
     * Returns the simulation command dispatcher of this project.
     * 
     * @return the simulation command dispatcher of this project.
     */
    public CommandDispatcher getSimulationCommandDispatcher() {
        return simulationCommandDispatcher;
    }

    /**
     * Returns the graph command dispatcher of this project.
     * 
     * @return the graph command dispatcher of this project.
     */
    public CommandDispatcher getGraphCommandDispatcher() {
        return graphCommandDispatcher;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Sets the ghosts which should be marked erroneous.
     * 
     * @param ghosts the error ghosts, can be null
     */
    public void setErrorGhosts(Set<Element> ghosts) {
        this.errorGhosts = ghosts;
    }

    /**
     * Get the ghosts set with {@link #setErrorGhosts(Set)}.
     * 
     * @return the error ghosts
     */
    public Set<Element> getErrorGhosts() {
        return errorGhosts;
    }

    /**
     * Returns the value set by {@link #setModified(boolean)}.
     * 
     * @return true or false
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Set to true if the Project is modified. Will lead to save questions, etc.
     * 
     * @param modified true or false.
     */
    public void setModified(boolean modified) {
        this.modified = modified;
        setChanged();
        getInfo().setModified(true);
        notifyObserversIfAuto();
    }

    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {
        setModified(true);
    }
}
