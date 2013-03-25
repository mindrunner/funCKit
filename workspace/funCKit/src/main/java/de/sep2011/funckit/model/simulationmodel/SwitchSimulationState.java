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
import de.sep2011.funckit.model.graphmodel.Switch;

/**
 * SwitchSimulationState stores simulation specific information for a
 * {@link Switch}.
 */
public class SwitchSimulationState implements SimulationState, Cloneable {

    /**
     * The Switch this SimulationState belongs to.
     */
    private final Switch parentSwitch;

    /**
     * The current value of this switch.
     */
    private boolean value;

    /**
     * Constructs a new empty SimulationState for the given Switch.
     * 
     * @param parentSwitch
     *            Switch to construct the state for. Has to be non null.
     */
    public SwitchSimulationState(Switch parentSwitch) {
        this.parentSwitch = parentSwitch;
        value = parentSwitch.getValue();
    }

    /**
     * This operation is not supported for Switches and should never be used. If
     * so an exception is thrown.
     */
    @Override
    public boolean hasAllInputs() {
        throw new UnsupportedOperationException(
                "Error! A switch has no inputs.");
    }

    /**
     * {@inheritDoc} In this case just the value of this switch is returned, no
     * matter what {@link Output} is given.
     */
    @Override
    public Boolean getNextValue(Output output) {
        return value;
    }

    /**
     * This operation is not supported for Switches and should never be used. If
     * so an exception is thrown.
     */
    @Override
    public void setInput(Input input, Boolean value) {
        throw new UnsupportedOperationException("Error! A switch has no input.");
    }

    /**
     * This operation is not supported for Switches and should never be used. If
     * so an exception is thrown.
     */
    @Override
    public void calculate() {
        throw new UnsupportedOperationException(
                "Error! A switch cannot calculate");
    }

    /**
     * {@inheritDoc} In this case just the value of this switch is returned, no
     * matter what {@link Output} is given.
     */
    @Override
    public Boolean getValue(Output output) {
        return value;
    }

    /**
     * Gets the value of this switch.
     * 
     * @return the value of this switch.
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * Set the value of this switch.
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * {@inheritDoc} In this case only {@link Switch} Objects are allowed.
     */
    @Override
    public SimulationState create(Brick b) {
        if (b instanceof Switch) {
            return new SwitchSimulationState((Switch) b);
        }
        throw new IllegalArgumentException(
                "Error! Cannot create SwitchSimulationState from a non Switch.");
    }

    @Override
    public SimulationState clone() {
        SwitchSimulationState clone = new SwitchSimulationState(parentSwitch);
        clone.value = this.value;
        return clone;
    }

    @Override
    public int hashCode() {
        return parentSwitch.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SwitchSimulationState)) {
            return false;
        }
        SwitchSimulationState other = (SwitchSimulationState) obj;
        return this.parentSwitch.equals(other.parentSwitch)
                && this.value == other.value;
    }

}
