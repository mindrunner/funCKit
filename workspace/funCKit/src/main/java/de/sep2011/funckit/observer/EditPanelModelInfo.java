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

import java.awt.geom.AffineTransform;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;

/**
 * This Class stores infos for the {@link EditPanelModelInfo}s notify method.
 * Create an empty Instance with {@link #getInfo()}
 */
public class EditPanelModelInfo extends Info<EditPanelModelInfo> {
    private boolean ghostsChanged = false;
    private boolean selectionChanged = false;
    private boolean transformChanged = false;
    private boolean activeChanged = false;
    private boolean cursorChanged = false;

    private EditPanelModelInfo() {
    }

    /**
     * Factory Method to get a new instance of this Info where every property is
     * false or unset.
     * 
     * @return the new instance
     */
    public static EditPanelModelInfo getInfo() {
        return new EditPanelModelInfo();
    }

    /**
     * Set if the ghosts have changed.
     * 
     * @return the info object for convenience to build chains
     */
    public EditPanelModelInfo setGhostsChanged() {
        ghostsChanged = true;
        return this;
    }

    /**
     * Return if the ghosts have changed.
     * 
     * @return true if ghosts have changed
     */
    public boolean isGhostsChanged() {
        return ghostsChanged;
    }

    /**
     * Set if the selection has changed.
     * 
     * @return the info object for convinience to build chains
     */
    public EditPanelModelInfo setSelectionChanged() {
        selectionChanged = true;
        return this;
    }

    /**
     * Return if the selection has changed.
     * 
     * @return true if selection has changed
     */
    public boolean isSelectionChanged() {
        return selectionChanged;
    }

    /**
     * Return true if the {@link AffineTransform} of the {@link EditPanelModel}
     * has been changed.
     * 
     * @return true if the {@link AffineTransform} of the {@link EditPanelModel}
     *         has been changed.
     * @since implementation
     */
    public boolean isTransformChanged() {
        return transformChanged;
    }

    /**
     * Mark that the {@link AffineTransform} of the {@link EditPanelModel} has
     * been changed.
     * 
     * @param transformChanged
     *            true or false
     * @since implementation
     * @return this for convenience
     */
    public EditPanelModelInfo setTransformChanged(boolean transformChanged) {
        this.transformChanged = transformChanged;
        return this;
    }

    @Override
    public EditPanelModelInfo getNewInstance() {
        return getInfo();
    }

    /**
     * Set this to true if the active Brick has changed.
     * 
     * @param changed
     *            true or false
     * @return this for convenience
     * @see EditPanelModel#setActiveBrick(Brick)
     */
    public EditPanelModelInfo setActiveChanged(boolean changed) {
        activeChanged = true;
        return this;
    }

    /**
     * Returns the value set by {@link #setActiveChanged(boolean)}.
     * 
     * @return the value set by {@link #setActiveChanged(boolean)}
     */
    public boolean isActiveChanged() {
        return activeChanged;
    }

    /**
     * Set this to true if the Cursor has changed.
     * 
     * @param changed
     *            true or false
     * @return this for convenience
     * @see EditPanelModel#setCursor(java.awt.Cursor)
     */
    public EditPanelModelInfo setCursorChanged(boolean changed) {
        cursorChanged = changed;
        return this;
    }

    /**
     * Returns the value set by {@link #setCursorChanged(boolean)}.
     * 
     * @return the value set by {@link #setCursorChanged(boolean)}
     */
    public boolean isCursorChanged() {
        return cursorChanged;
    }
}
