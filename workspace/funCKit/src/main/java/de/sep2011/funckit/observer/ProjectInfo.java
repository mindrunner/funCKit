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

package de.sep2011.funckit.observer;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.validator.Result;

/**
 * This Class stores Infos for the {@link ProjectObserver}s notify method.
 * Create an empty Instance with {@link #getInfo()}.
 */
public class ProjectInfo extends Info<ProjectInfo> {

    /**
     * The changed {@link EditPanelModel}. To see what change look at
     * {@link #editPanelModelAdded} and {@link #editPanelModelRemoved}.
     */
    private EditPanelModel changedModel = null;

    /**
     * Specifies whether the {@link #changedModel} was removed.
     */
    private boolean editPanelModelRemoved = false;

    /**
     * Specifies whether the {@link #changedModel} was added.
     */
    private boolean editPanelModelAdded = false;

    /**
     * Has the selected {@link EditPanelModel} of the {@link Project} changed?.
     */
    private boolean activeEditPanelModelChanged = false;

    /**
     * Has the {@link Simulation} of the {@link Project} changed?.
     */
    private boolean simulationChanged = false;

    /**
     * Has the set of {@link Result}s of the {@link Project} changed?.
     */
    private boolean resultsChanged = false;

    /**
     * Has the name of the {@link Project} changed?.
     */
    private boolean nameChanged = false;

    /**
     * Has the path of the {@link Project} changed?.
     */
    private boolean pathChanged = false;

    /**
     * Has the {@link Circuit} of the {@link Project} changed?.
     */
    private boolean circuitChanged = false;

    /**
     * Was the project's persistent data modified?
     */
    private boolean modified = false;

    /**
     * true if the state of the simulation control was changed (timer delay,
     * pause).
     */
    private boolean simulationControlStateModified = false;

    private Simulation oldSimulation;

    /**
     * Private constructor. Use {@link #getInfo()} to create a new Instance.
     */
    private ProjectInfo() {
    }

    /**
     * Factory Method to get a new instance of this Info where every property is
     * false or unset.
     * 
     * @return the new instance
     */
    public static ProjectInfo getInfo() {
        return new ProjectInfo();
    }

    @Override
    public ProjectInfo getNewInstance() {
        return getInfo();
    }

    /**
     * Returns whether the name of the {@link Project} changed.
     * 
     * @return has the name changed?
     */
    public boolean isNameChanged() {
        return nameChanged;
    }

    /**
     * Mark whether the name has changed.
     * 
     * @param nameChanged
     *            the name has changed?
     * @return this for convenience
     */
    public ProjectInfo setNameChanged(boolean nameChanged) {
        this.nameChanged = nameChanged;
        return this;
    }

    /**
     * Returns whether the path of the {@link Project} changed.
     * 
     * @return has the path changed?
     */
    public boolean isPathChanged() {
        return pathChanged;
    }

    /**
     * Mark whether the path has changed.
     * 
     * @param pathChanged
     *            the path has changed?
     * @return this for convenience
     */
    public ProjectInfo setPathChanged(boolean pathChanged) {
        this.pathChanged = pathChanged;
        return this;
    }

    /**
     * Returns whether the {@link Circuit} of the {@link Project} changed.
     * 
     * @return has the {@link Circuit} changed?
     */
    public boolean isCircuitChanged() {
        return circuitChanged;
    }

    /**
     * Mark whether the {@link Circuit} has changed.
     * 
     * @param circuitChanged
     *            the {@link Circuit} has changed?
     * @return this for convenience
     */
    public ProjectInfo setCircuitChanged(boolean circuitChanged) {
        this.circuitChanged = circuitChanged;
        return this;
    }

    /**
     * Return true if the EditPanelModel has been removed.
     * 
     * @return true if the EditPanelModel has been removed.
     */
    public boolean hasEditPanelModelRemoved() {
        return editPanelModelRemoved;
    }

    /**
     * Mark that the EditPanelModel has been removed.
     * 
     * @param editPanelModelRemoved
     *            true or false
     * @return this for convenience
     */
    public ProjectInfo setEditPanelModelRemoved(boolean editPanelModelRemoved) {
        this.editPanelModelRemoved = editPanelModelRemoved;
        return this;
    }

    /**
     * Return true if the EditPanelModel has been added.
     * 
     * @return true if the EditPanelModel has been added.
     */
    public boolean hasEditPanelModelAdded() {
        return editPanelModelAdded;
    }

    /**
     * Mark that the EditPanelModel has been added.
     * 
     * @param editPanelModelAdded
     *            true or false
     * @return this for convenience
     */
    public ProjectInfo setEditPanelModelAdded(boolean editPanelModelAdded) {
        this.editPanelModelAdded = editPanelModelAdded;
        return this;
    }

    /**
     * Set the changed EditPanelModel.
     * 
     * @param editPanelModel
     *            the EditPanelModel to set.
     * @return this for convenience
     */
    public ProjectInfo setChangedModel(EditPanelModel editPanelModel) {
        changedModel = editPanelModel;
        return this;
    }

    /**
     * Returns the changed EditPanelModel.
     * 
     * @return the changed EditPanelModel.
     */
    public EditPanelModel getChangedModel() {
        return changedModel;
    }

    /**
     * Set the value indicating that the active {@link EditPanelModel} changed.
     * 
     * @param b
     *            the value indicating that the active {@link EditPanelModel}
     *            changed.
     * @return this for convenience
     */
    public ProjectInfo setActiveEditPanelModelChanged(boolean b) {
        activeEditPanelModelChanged = b;
        return this;
    }

    /**
     * Get the value indicating that the active {@link EditPanelModel} changed.
     * 
     * @return the value indicating that the active {@link EditPanelModel}
     *         changed.
     */
    public boolean isActiveEditPanelModelChanged() {
        return activeEditPanelModelChanged;
    }

    /**
     * Set the value indicating that the simulation changed.
     * 
     * @param b
     *            the value indicating that the simulation changed
     * @return this for convenience
     */
    public ProjectInfo setSimulationChanged(boolean b) {
        simulationChanged = b;
        return this;
    }

    /**
     * Get the value indicating that the simulation changed.
     * 
     * @return the value indicating that the simulation changed.
     */
    public boolean isSimulationChanged() {
        return simulationChanged;
    }

    /**
     * Set the value indicating that the results set changed.
     * 
     * @param resultsChanged
     *            the value indicating that the results set changed.
     * @return this for convenience
     */
    public ProjectInfo setResultsChanged(boolean resultsChanged) {
        this.resultsChanged = resultsChanged;
        return this;
    }

    /**
     * Get the value indicating that the results set changed.
     * 
     * @return the value indicating that the results set changed.
     */
    public boolean isResultsChanged() {
        return resultsChanged;
    }

    /**
     * Set the value indicating that projects persistent data changed.
     * 
     * @return this for convenience
     * @param modified
     *            the value indicating that projects persistent data changed.
     */
    public ProjectInfo setModified(boolean modified) {
        this.modified = modified;
        return this;
    }

    /**
     * Get the value indicating that projects persistent data changed.
     * 
     * @return the value indicating that projects persistent data changed.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * return true if the state of the simulation control was changed (timer
     * delay, pause).
     * 
     * @return true if the state of the simulation control was changed (timer
     *         delay, pause)
     */
    public boolean isSimulationControlStateModified() {
        return simulationControlStateModified;
    }

    /**
     * setter for {@link #isSimulationChanged()}.
     * 
     * @param modified
     *            see {@link #isSimulationChanged()}
     * @return this
     */
    public ProjectInfo setSimulationControlStateModified(boolean modified) {
        this.simulationControlStateModified = modified;
        return this;
    }

    /**
     * If the Current Simulation has changed set the old value of of the
     * simulation property here.
     * 
     * @param simulation
     *            the previous simulation
     * @return this for convenience
     */
    public ProjectInfo setOldSimulation(Simulation simulation) {
        this.oldSimulation = simulation;
        return this;
    }

    /**
     * Getter for the value set by {@link #setOldSimulation(Simulation)}.
     * 
     * @return the value set by {@link #setOldSimulation(Simulation)}
     */
    public Simulation getOldSimulation() {
        return oldSimulation;
    }
}
