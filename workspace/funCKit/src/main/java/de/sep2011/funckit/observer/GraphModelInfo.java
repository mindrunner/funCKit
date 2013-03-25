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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Wire;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This Class stores information about changes for {@link GraphModelObserver}s.
 * Create an empty Instance with {@link #getInfo()}
 */
public class GraphModelInfo extends Info<GraphModelInfo> {
    private final Set<Wire> changedWires = new LinkedHashSet<Wire>();
    private final Set<Brick> changedBricks = new LinkedHashSet<Brick>();

    private final Set<Wire> addedWires = new LinkedHashSet<Wire>();
    private final Set<Brick> addedBricks = new LinkedHashSet<Brick>();

    private final Set<Wire> removedWires = new LinkedHashSet<Wire>();
    private final Set<Brick> removedBricks = new LinkedHashSet<Brick>();

    private boolean elementBoundsChanged = false;
    private boolean brickDelayChanged = false;
    private boolean elementNameChanged = false;
    private boolean switchValueChanged = false;

    private GraphModelInfo() {
    }

    /**
     * Factory Method to get a new instance of this Info where every property is
     * false or unset.
     * 
     * @return the new instance
     */
    public static GraphModelInfo getInfo() {
        return new GraphModelInfo();
    }

    /**
     * Get the set of Wires which are added.
     * 
     * @return the set of Wires which are added
     */
    public Set<Wire> getAddedWires() {
        return addedWires;
    }

    /**
     * Get the set of Bricks which are added.
     * 
     * @return the set of Bricks which are added
     */
    public Set<Brick> getAddedBricks() {
        return addedBricks;
    }

    /**
     * Get the set of Wires which are removed.
     * 
     * @return the set of Wires which are removed
     */
    public Set<Wire> getRemovedWires() {
        return removedWires;
    }

    /**
     * Get the set of Bricks which are removed.
     * 
     * @return the set of Bricks which are removed
     */
    public Set<Brick> getRemovedBricks() {
        return removedBricks;
    }

    /**
     * Returns the Set of Changed Wires.
     * 
     * @return the Set of Changed Wires
     */
    public Set<Wire> getChangedWires() {
        return changedWires;
    }

    /**
     * Returns the Set of Changed Bricks.
     * 
     * @return the Set of Changed Bricks
     */
    public Set<Brick> getChangedBricks() {
        return changedBricks;
    }

    /**
     * Indicates that the bounding Rectangle of an Element may have changed.
     * 
     * @return true it it maybe changed
     */
    public boolean isElementBoundsChanged() {
        return elementBoundsChanged;
    }

    /**
     * Set indicator that the bounding Rectangle of an Element may have changed.
     * 
     * @param elementBoundsChanged
     *            true or false
     * @see #isElementBoundsChanged()
     * @return this for convenience
     */
    public GraphModelInfo setElementBoundsChanged(boolean elementBoundsChanged) {
        this.elementBoundsChanged = elementBoundsChanged;
        return this;
    }

    @Override
    public GraphModelInfo getNewInstance() {
        return getInfo();
    }

    /**
     * Set this true if the Delay of a Brick in the {@link Circuit} has changed.
     * 
     * @param b
     *            true or false
     * @return this for convenience
     */
    public GraphModelInfo setBrickDelayChanged(boolean b) {
        brickDelayChanged = b;
        return this;

    }

    /**
     * Returns the value set by {@link #setBrickDelayChanged(boolean)}.
     * 
     * @return the value set by {@link #setBrickDelayChanged(boolean)}
     */
    public boolean isBrickDelayChanged() {
        return brickDelayChanged;
    }

    /**
     * Set this true if the Name of an Element in the {@link Circuit} has
     * changed.
     * 
     * @param b
     *            true or false
     * @return this for convenience
     */
    public GraphModelInfo setElementNameChanged(boolean b) {
        elementNameChanged = b;
        return this;

    }

    /**
     * Returns the value set by {@link #setElementNameChanged(boolean)}.
     * 
     * @return the value set by {@link #setElementNameChanged(boolean)}
     */
    public boolean isElementNameChanged() {
        return elementNameChanged;
    }

    /**
     * Set this true if the Value of a Switch in the {@link Circuit} has
     * changed.
     * 
     * @param b
     *            true or false
     * @return this for convenience
     */
    public GraphModelInfo setSwitchValueChanged(boolean b) {
        switchValueChanged = b;
        return this;

    }

    /**
     * Returns the value set by {@link #setSwitchValueChanged(boolean)}.
     * 
     * @return the value set by {@link #setSwitchValueChanged(boolean)}
     */
    public boolean isSwitchValueChanged() {
        return switchValueChanged;
    }
}
