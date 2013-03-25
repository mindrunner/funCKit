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
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * ComponentSimulationState stores simulation specific information for a
 * {@link Component}.
 */
public class ComponentSimulationState implements SimulationState, Cloneable {

    /**
     * The value with which the outputQueues of new states are getting filled.
     */
    private static final boolean DEFAULT_OUTPUT_VALUE = false;

    /**
     * Assigns each {@link Output} its queue of {@link Output} values, used for
     * delaying those values.
     */
    private Map<Output, Queue<Boolean>> outputQueueMap;

    /**
     * The {@link Component} this state belongs to.
     */
    private Component parent;

    /**
     * The amount of already set values for the outputs.
     */
    private int amountValuesSet;

    /**
     * Creates a new empty {@link SimulationState} for the given
     * {@link Component}. Therefore the queues for the {@link Output} values are
     * filled with {@link ComponentSimulationState#DEFAULT_OUTPUT_VALUE}.
     * 
     * @param parent
     *            The {@link Component} this state belongs to. Has to be non
     *            null.
     */
    public ComponentSimulationState(Component parent) {
        assert parent != null;
        amountValuesSet = 0;
        this.parent = parent;
        outputQueueMap = new LinkedHashMap<Output, Queue<Boolean>>();
        for (Output output : parent.getOutputs()) {
            Queue<Boolean> queue = new LinkedList<Boolean>();
            for (int i = 0; i <= parent.getDelay(); i++) {
                queue.offer(DEFAULT_OUTPUT_VALUE);
            }
            outputQueueMap.put(output, queue);
        }
    }

    /**
     * To access the current output queue map (only access!), here is a getter
     * method for it. No abuse, only reading. For performance reasons this does
     * not copy the map and thus enables access to internal functionality of
     * this implementation of {@link SimulationState}!
     * 
     * @return Map of outputs and their according queue of simulation values.
     */
    public Map<Output, Queue<Boolean>> getOutputQueueMap() {
        return outputQueueMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getValue(Output output) {
        Queue<Boolean> queue = outputQueueMap.get(output);
        if (queue != null) {
            return queue.peek();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * In this case the {@link Brick} has to be a {@link Component}.
     */
    @Override
    public SimulationState create(Brick b) {
        if (b instanceof Component) {
            return new ComponentSimulationState((Component) b);
        }

        throw new IllegalArgumentException(
                "Error! Cannot create ComponentSimulationState from a non Component.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimulationState clone() {
        ComponentSimulationState clone = new ComponentSimulationState(parent);
        for (Output o : this.outputQueueMap.keySet()) {
            Queue<Boolean> outputQueue = new LinkedList<Boolean>();
            for (Boolean value : this.outputQueueMap.get(o)) {
                outputQueue.add(value);
            }
            clone.outputQueueMap.put(o, outputQueue);
        }
        clone.amountValuesSet = this.amountValuesSet;
        return clone;
    }

    @Override
    public int hashCode() {
        return parent.hashCode() + outputQueueMap.hashCode() + amountValuesSet
                % 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ComponentSimulationState)) {
            return false;
        }
        ComponentSimulationState other = (ComponentSimulationState) obj;
        return this.parent.equals(other.parent)
                && this.amountValuesSet == other.amountValuesSet
                && this.outputQueueMap.equals(other.outputQueueMap);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * For this to work correctly the all {@link Output}s have to have received
     * a new value via {@link #addOutputValue(Output, Boolean)} before.
     */
    @Override
    public Boolean getNextValue(Output output) {
        Queue<Boolean> queue = outputQueueMap.get(output);
        if (queue != null) {
            if (queue.size() <= parent.getDelay()) {
                throw new IllegalStateException(
                        "Output queue of " + parent.getName() + " is not allowed to shrunk beyond delay("
                                + parent.getDelay()
                                + ")! To avoid this a call to addOutputValue(Output, Boolean) is needed before for each Output.");
            }
            if (!parent.hasDelay() && queue.size() != 2) {
            	throw new IllegalStateException(
                        "Output queue of " + parent.getName() + " with no delay is not allowed to shrunk beyond 2! To avoid this a call to addOutputValue(Output, Boolean) is needed before for each Output.");
            }
            queue.poll();
            return queue.peek();
        }
        return null;
    }

    /**
     * Adds the given value to the queue of the given {@link Output}.
     * 
     * @param output
     *            {@link Output} to who's queue the value is added.
     * @param value
     *            the value to add.
     */
    public void addOutputValue(Output output, Boolean value) {
    	Queue<Boolean> queue = outputQueueMap.get(output);
    	assert queue != null;
    	if (!parent.hasDelay() && queue.size() != 1) {
    		throw new IllegalStateException("Queue size of component "+parent.getName()+" with no delay has to match 1, but was "+queue.size()+"!");
    	}
    	if (parent.hasDelay() && !(queue.size() == parent.getDelay() || queue.size() == parent.getDelay() + 1)) {
    		throw new IllegalStateException("Queue size of component "+parent.getName()+" with delay has to match delay("+parent.getDelay()+")+(1/0), but was "+queue.size()+"!");
    	}
        queue.offer(value);
        amountValuesSet++;
    }

    /**
     * This operation is not supported for {@link Component}s and should never
     * be used. If so an exception is thrown.
     * 
     * @param input
     *            irrelevant
     * @param value
     *            irrelevant
     */
    @Override
    public void setInput(Input input, Boolean value) {
        throw new UnsupportedOperationException(
                "Error! Components do not calculate directly.");
    }

    /**
     * This operation is not supported for {@link Component}s and should never
     * be used. If so an exception is thrown.
     * 
     * @return nothing as it throws exception
     */
    @Override
    public boolean hasAllInputs() {
        throw new UnsupportedOperationException(
                "Error! Components do not calculate directly.");
    }

    /**
     * This operation is not supported for {@link Component}s and should never
     * be used. If so an exception is thrown.
     * 
     */
    @Override
    public void calculate() {
        throw new UnsupportedOperationException(
                "Error! Components do not calculate directly.");
    }

}