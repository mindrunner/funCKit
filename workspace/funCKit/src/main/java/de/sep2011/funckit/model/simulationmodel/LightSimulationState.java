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
import de.sep2011.funckit.model.graphmodel.implementations.Light;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LightSimulationState stores simulation specific information for a
 * {@link Light}.
 */
public class LightSimulationState implements SimulationState, Cloneable {

    /**
     * Used to assign {@link Input} values to {@link Input}s.
     */
    private final Map<Input, Boolean> inputMap;

    /**
     * The {@link Light} this state belongs to.
     */
    private final Light parentLight;

    /**
     * This constructor creates a new empty SimulationState for the given
     * {@link Light}.
     * 
     * @param parentLight
     *            The {@link Light} the SimulationState is created for. Has to
     *            be non null.
     */
    public LightSimulationState(Light parentLight) {
        this.parentLight = parentLight;
        inputMap = new LinkedHashMap<Input, Boolean>();
    }

    /**
     * This operation is not supported for {@link Light}s and should never be
     * used. If so an exception is thrown.
     */
    @Override
    public Boolean getNextValue(Output output) {
        throw new UnsupportedOperationException(
                "Error! Light has no next value.");
    }

    /**
     * This operation is not supported for {@link Light}s and should never be
     * used. If so an exception is thrown.
     */
    @Override
    public Boolean getValue(Output output) {
        throw new UnsupportedOperationException("Error! Light has no value.");
    }

    @Override
    public void setInput(Input input, Boolean value) {
        inputMap.put(input, value);
    }

    /**
     * Returns the currently associated value to the given {@link Input}.
     * 
     * @param input
     *            {@link Input} to get the value for. Has to be from the
     *            {@link Light} this state belongs to.
     * @return value of the given {@link Input}. If no value is associated the
     *         result is false.
     */
    public boolean getValue(Input input) {
        Boolean value = inputMap.get(input);
        value = value == null ? false : value;
        return value;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * In this case always false is returned because a {@link Light} never has
     * enough {@link Input}s to start calculation.
     */
    @Override
    public boolean hasAllInputs() {
        // never calculates, so never has enough inputs :)
        return false;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * In this case nothing is done because a {@link Light} doesn't calculate.
     */
    @Override
    public void calculate() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * <p/>
     * In this case the {@link Brick} has to be a {@link Light}.
     */
    @Override
    public SimulationState create(Brick b) {
        if (b instanceof Light) {
            return new LightSimulationState((Light) b);
        }
        throw new IllegalArgumentException(
                "Error! Cannot create LightSimulationState from a non Light.");
    }

    @Override
    public SimulationState clone() {
        LightSimulationState clone = new LightSimulationState(parentLight);
        for (Input i : this.inputMap.keySet()) {
            clone.inputMap.put(i, this.inputMap.get(i));
        }
        return clone;
    }

    @Override
    public int hashCode() {
        return parentLight.hashCode() + inputMap.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LightSimulationState)) {
            return false;
        }
        LightSimulationState other = (LightSimulationState) obj;
        return this.parentLight.equals(other.parentLight)
                && this.inputMap.equals(other.inputMap);
    }

}
