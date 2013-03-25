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
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * GateSimulationState stores simulation specific information for a {@link Gate}
 * .
 */
public class GateSimulationState implements SimulationState, Cloneable {

    /**
     * The value with which the outputQueues of new states are getting filled.
     */
    private static final boolean DEFAULT_OUTPUT_VALUE = false;

    /**
     * Assigns each {@link Output} its queue of {@link Output} values, used for
     * delaying those values.
     */
    private final Map<Output, Queue<Boolean>> outputQueueMap;

    /**
     * Assigns each {@link Input} value to an {@link Input}.
     */
    private final Map<Input, Boolean> inputMap;

    /**
     * The {@link Gate} this state belongs to.
     */
    private final Gate parent;

    /**
     * Creates a new empty {@link SimulationState} for the given {@link Gate}.
     * Therefore the queues for the {@link Output} values are filled with
     * {@link GateSimulationState#DEFAULT_OUTPUT_VALUE}.
     * 
     * @param parent
     *            The {@link Gate} this state belongs to. Has to be non null.
     */
    public GateSimulationState(Gate parent) {
        this.parent = parent;
        outputQueueMap = new LinkedHashMap<Output, Queue<Boolean>>();
        inputMap = new LinkedHashMap<Input, Boolean>();
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
     * <p/>
     * For this to work correctly the {@link GateSimulationState#calculate()}
     * and this method have to be called alternating because here the values are
     * dumped and in {@link GateSimulationState#calculate()} new values are
     * provided. This means this state is only valid after exactly one call to
     * each method.
     */
    @Override
    public Boolean getNextValue(Output output) {
        Queue<Boolean> queue = outputQueueMap.get(output);
        if (queue != null) {
            if (queue.size() <= parent.getDelay()) {
                throw new IllegalStateException(
                        "Output queue is not allowed to shrunk beyond delay("
                                + parent.getDelay()
                                + ")! To avoid this a call to calculate() is needed before.");
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

    @Override
    public Boolean getValue(Output output) {
        Queue<Boolean> queue = outputQueueMap.get(output);
        if (queue != null) {
            return queue.peek();
        }
        return null;
    }

    /**
     * {@inheritDoc} In this case the {@link Gate} this state belongs to is used
     * to execute the calculation. Then the result is appended to the queues of
     * {@link Output} values. This method and
     * {@link GateSimulationState#getNextValue(Output)} have to be called
     * alternating because here the values are provided and in
     * {@link GateSimulationState#getNextValue(Output)} old values are dumped.
     * This means this state is only valid after exactly one call to each
     * method.
     */
    @Override
    public void calculate() {
        if (parent != null) {
            Map<Output, Boolean> results = parent.calculate(inputMap);
            for (Map.Entry<Output, Boolean> result : results.entrySet()) {
                outputQueueMap.get(result.getKey()).offer(result.getValue());
            }
            inputMap.clear();
        } else {
            throw new UnsupportedOperationException(
                    "Calculation not supported for class ");
        }
    }

    @Override
    public boolean hasAllInputs() {
        return inputMap.size() == parent.getInputs().size();
    }

    @Override
    public void setInput(Input input, Boolean value) {
        inputMap.put(input, value);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * In this case the {@link Brick} has to be a {@link Gate}.
     */
    @Override
    public SimulationState create(Brick b) {
        if (b instanceof Gate) {
            return new GateSimulationState((Gate) b);
        }
        throw new IllegalArgumentException(
                "Error! Cannot create GateSimulationState from a non Gate.");
    }

    @Override
    public SimulationState clone() {
        GateSimulationState clone = new GateSimulationState(parent);
        for (Input i : this.inputMap.keySet()) {
            clone.inputMap.put(i, this.inputMap.get(i));
        }
        for (Output o : this.outputQueueMap.keySet()) {
            Queue<Boolean> outputQueue = new LinkedList<Boolean>();
            for (Boolean value : this.outputQueueMap.get(o)) {
                outputQueue.add(value);
            }
            clone.outputQueueMap.put(o, outputQueue);
        }
        return clone;
    }

    @Override
    public int hashCode() {
        return parent.hashCode() + inputMap.hashCode()
                + outputQueueMap.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GateSimulationState)) {
            return false;
        }
        GateSimulationState other = (GateSimulationState) obj;
        return this.parent.equals(other.parent)
                && this.inputMap.equals(other.inputMap)
                && this.outputQueueMap.equals(other.outputQueueMap);
    }

}