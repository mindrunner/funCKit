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

package de.sep2011.funckit.model.simulationmodel;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;

import java.util.Deque;

/**
 * This class represents a Brick in the Simulation. Because of the nature of
 * Components and the referenced circuit through its ComponentType Bricks alone
 * are not enough to identify a Instance in the Simulation. Therefore a Brick
 * and its position in the tree of components within a circuit are stored here
 * together so that this class can identify a Brick in the Simulation.
 */
public class SimulationBrick {

    /**
     * The {@link Brick} this SimulationBrick belongs to.
     */
    private Brick brick;

    /**
     * The path in the tree of {@link Component}s for this Brick.
     */
    private Deque<Component> stack;

    /**
     * This constructor creates a new SimulationBrick with the given parameters.
     * Both parameters have to be non null.
     * 
     * @param brick
     *            The {@link Brick} the new SimulationBrick belongs to.
     * @param stack
     *            The path in the tree of {@link Component}s for this
     *            SimulationBrick.
     */
    public SimulationBrick(Brick brick, Deque<Component> stack) {
        assert brick != null;
        assert stack != null;
        this.brick = brick;
        this.stack = stack;
    }

    /**
     * Returns the {@link Brick} this SimulationBrick belongs to.
     * 
     * @return the {@link Brick} this SimulationBrick belongs to.
     */
    public Brick getBrick() {
        return brick;
    }

    /**
     * Returns the path in the tree of {@link Component}s for this
     * SimlationBrick.
     * 
     * @return The path in the tree of {@link Component}s for this
     *         SimlationBrick.
     */
    public Deque<Component> getStack() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {

        // NOTE: the equals method of linked list does already an elementary
        // comparison!
        if (obj instanceof SimulationBrick) {
            SimulationBrick b = (SimulationBrick) obj;
            return this.brick.equals(b.brick) && this.stack.equals(b.stack);
        }
        return false;
    }

    @Override
    public String toString() {
        return "SimulationBrick [brick=" + brick + ", stack=" + stack + "]";
    }

    @Override
    public int hashCode() {
        return brick.hashCode() + stack.hashCode();
    }

}
