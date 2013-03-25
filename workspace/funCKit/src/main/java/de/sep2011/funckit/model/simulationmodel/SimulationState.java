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
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;

/**
 * SimulationState represents state of a {@link Brick} in a certain
 * {@link Simulation}. A state therefore extends information weight of a
 * {@link Brick} by its {@link Input} values, its {@link Output} values and
 * determines if all {@link Input}s are available to calculate new
 * {@link Output} values.
 */
public interface SimulationState {

    /**
     * Returns the next value (in queue) of the currently simulated
     * {@link Brick} for a certain {@link Output}. The old current value is
     * discarded. The {@link Output} has to be from the {@link Brick} this
     * SimulationState belongs to.
     * 
     * @param output
     *            The {@link Output} for which the next value has to be
     *            returned.
     * @return the new current value of the give {@link Output}.
     */
    Boolean getNextValue(Output output);

    /**
     * Returns the current value for a certain {@link Output} of the currently
     * simulated {@link Brick}. The {@link Output} has to be from the
     * {@link Brick} this SimulationState belongs to.
     * 
     * @param output
     *            The {@link Output} for which the current value has to be
     *            returned.
     * @return the current value of the give {@link Output}.
     */
    Boolean getValue(Output output);

    /**
     * Assign the given value to the given {@link Input}. The {@link Input} has
     * to be from the {@link Brick} this StimulationState belongs to.
     * 
     * @param input
     *            {@link Input} to assign the value to.
     * @param value
     *            Value to be assigned to the given {@link Input}.
     */
    void setInput(Input input, Boolean value);

    /**
     * Checks if values for all {@link Input} access points are given.
     * 
     * @return true iff all values are given, false otherwise.
     */
    boolean hasAllInputs();

    /**
     * Calculates new {@link Output} values for the formerly given {@link Input}
     * values and stores them internally. After that the formerly given
     * {@link Input} values are erased. This method requires that all
     * {@link Input} values are given (see
     * {@link SimulationState#hasAllInputs()}).
     */
    void calculate();

    /**
     * Creates a new initial specialized SimulationState for the given
     * {@link Brick}. The given {@link Brick} needs to match the expected type
     * of the specialized SimulationState.
     * 
     * @param parentBrick
     *            {@link Brick} for which the SimulationState will be created.
     * @return the new initial specialized SimulationState for the given
     *         {@link Brick}.
     */
    SimulationState create(Brick parentBrick);

    /**
     * Creates a deep copy of this state.
     * 
     * @return a deep copy of this state.
     */
    SimulationState clone();

}