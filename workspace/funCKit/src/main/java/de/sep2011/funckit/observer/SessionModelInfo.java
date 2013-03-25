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

import de.sep2011.funckit.controller.Tool;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.view.NewBrickList;

/**
 * This Class stores Infos for the {@link SessionModelObserver}s notify method.
 * Create an empty Instance with {@link #getInfo()}
 */
public class SessionModelInfo extends Info<SessionModelInfo> {
    private boolean toolChanged = false;

    private boolean prepareExit = false;

    private boolean cursorChanged = false;

    private boolean projectAdded = false;

    private boolean projectRemoved = false;

    private boolean currentProjectChanged = false;

    private Project changedProject = null;

    private boolean fullScreenModeChanged = false;

    private boolean currentBrickChanged = false;

    private boolean copyBufferChanged = false;

    private boolean newBrickListChanged = false;

    private int[] newBrickListAddedIndices;

    private int[] newBrickListRemovedIndices;

    private SessionModelInfo() {
    }

    /**
     * Factory Method to get a new instance of this Info where every property is
     * false or unset.
     * 
     * @return the new instance
     */
    public static SessionModelInfo getInfo() {
        return new SessionModelInfo();
    }

    /**
     * Return true if the current {@link Tool} changed.
     * 
     * @return true if the current {@link Tool} changed
     */
    public boolean isToolChanged() {
        return toolChanged;
    }

    /**
     * Mark that the {@link Tool} has changed in the Model.
     * 
     * @param toolChanged
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setToolChanged(boolean toolChanged) {
        this.toolChanged = toolChanged;
        return this;
    }

    /**
     * Return true if the user indicated that he wants to exit the application.
     * 
     * @return true if the user indicated that he wants to exit the application
     */
    public boolean isPrepareExit() {
        return this.prepareExit;
    }

    /**
     * Mark that the model is prepared to exit the application.
     * 
     * @param prepareExit
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setPrepareExit(boolean prepareExit) {
        this.prepareExit = prepareExit;
        return this;
    }

    /**
     * Returns true if the currentBrick has changed.
     * 
     * @return true if the currentBrick has changed.
     */
    public boolean isCurrentBrickChanged() {
        return this.currentBrickChanged;
    }

    /**
     * Mark that the currentBrick has changed.
     * 
     * @param currentBrickChanged
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setCurrentBrickChanged(boolean currentBrickChanged) {
        this.currentBrickChanged = currentBrickChanged;
        return this;
    }

    /**
     * Return true if the cursor changed.
     * 
     * @return true if the cursor changed
     */
    public boolean hasCursorChanged() {
        return cursorChanged;
    }

    /**
     * Mark that the Coursor has changed.
     * 
     * @param coursorChanged
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setCursorChanged(boolean coursorChanged) {
        this.cursorChanged = coursorChanged;
        return this;
    }

    /**
     * Set the value indicating that the fullscreen mode changed.
     * 
     * @param b
     *            the value indicating that the fullscreen mode changed
     * @return this for convenience
     */
    public SessionModelInfo setFullScreenModeChanged(boolean b) {
        fullScreenModeChanged = b;
        return this;
    }

    /**
     * Get the value indicating that the fullscreen mode changed.
     * 
     * @return the value indicating that the fullscreen mode changed.
     */
    public boolean isFullScreenModeChanged() {
        return fullScreenModeChanged;
    }

    @Override
    public SessionModelInfo getNewInstance() {
        return getInfo();
    }

    /**
     * Set this to true if a {@link Project} has been added to the Model.
     * 
     * @param projectAdded
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setProjectAdded(boolean projectAdded) {
        this.projectAdded = projectAdded;
        return this;
    }

    /**
     * Returns the value set by {@link #setProjectAdded(boolean)}.
     * 
     * @return the value set by {@link #setProjectAdded(boolean)}
     */
    public boolean hasProjectAdded() {
        return projectAdded;
    }

    /**
     * Set this to true if a {@link Project} has been removed from the Model.
     * 
     * @param projectRemoved
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setProjectRemoved(boolean projectRemoved) {
        this.projectRemoved = projectRemoved;
        return this;
    }

    /**
     * Returns the value set by {@link #setCurrentProjectChanged(boolean)}.
     * 
     * @return the value set by {@link #setCurrentProjectChanged(boolean)}
     */
    public boolean hasCurrentProjectChanged() {
        return currentProjectChanged;
    }

    /**
     * Set this to true if the current active Project has beeen changed.
     * 
     * @param projectChanged
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setCurrentProjectChanged(boolean projectChanged) {
        this.currentProjectChanged = projectChanged;
        return this;
    }

    /**
     * Returns the value set by {@link #setProjectRemoved(boolean)}.
     * 
     * @return the value set by {@link #setProjectRemoved(boolean)}
     */
    public boolean hasProjectRemoved() {
        return projectRemoved;
    }

    /**
     * Set the {@link Project} that has changed.
     * 
     * @param project
     *            the {@link Project} that has changed.
     * @return this for convenience
     */
    public SessionModelInfo setChangedProject(Project project) {
        this.changedProject = project;
        return this;
    }

    /**
     * Returns the value set by {@link #setChangedProject(Project)}.
     * 
     * @return the value set by {@link #setChangedProject(Project)}
     */
    public Project getChangedProject() {
        return changedProject;
    }

    /**
     * Set this to true if the contents of the copy buffer have changed.
     * 
     * @param b
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setCopyBufferChanged(boolean b) {
        copyBufferChanged = b;
        return this;
    }

    /**
     * Returns the value set by {@link #setCopyBufferChanged(boolean)}.
     * 
     * @return the value set by {@link #setCopyBufferChanged(boolean)}
     */
    public boolean isCopyBufferChanged() {
        return copyBufferChanged;
    }

    /**
     * Returns the value set by {@link #setNewBrickListChanged(boolean)}.
     * 
     * @return the value set by {@link #setNewBrickListChanged(boolean)}
     */
    public boolean isNewBrickListChanged() {
        return newBrickListChanged;
    }

    /**
     * Set this to true if the {@link NewBrickList} changed.
     * 
     * @param newBrickListChanged
     *            true or false
     * @return this for convenience
     */
    public SessionModelInfo setNewBrickListChanged(boolean newBrickListChanged) {
        this.newBrickListChanged = newBrickListChanged;
        return this;
    }

    /**
     * Set the removed indices from the new Brick list, if any.
     * 
     * @param indices
     *            the removed indices, null otherwise
     * @return this for convenience
     */
    public SessionModelInfo setNewBrickListRemovedIndices(int[] indices) {
        newBrickListRemovedIndices = indices;
        return this;
    }

    /**
     * Set the added indices from the new Brick list, if any.
     * 
     * @param indices
     *            the added indices, null otherwise
     * @return this for convenience
     */
    public SessionModelInfo setNewBrickListAddedIndices(int[] indices) {
        newBrickListAddedIndices = indices;
        return this;
    }

    /**
     * Returns the value set by {@link #setNewBrickListAddedIndices(int[])}.
     * 
     * @return the value set by {@link #setNewBrickListAddedIndices(int[])}
     */
    public int[] getNewBrickListAddedIndices() {
        return newBrickListAddedIndices;
    }

    /**
     * Returns the value set by {@link #setNewBrickListRemovedIndices(int[])}.
     * 
     * @return the value set by {@link #setNewBrickListRemovedIndices(int[])}
     */
    public int[] getNewBrickListRemovedIndices() {
        return newBrickListRemovedIndices;
    }
}
