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
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.observer.AbstractObservable;
import de.sep2011.funckit.observer.SimulationModelInfo;
import de.sep2011.funckit.observer.SimulationModelObserver;
import de.sep2011.funckit.util.Pair;
import de.sep2011.funckit.util.Profiler;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static de.sep2011.funckit.util.Log.gl;

/**
 * Default implementation of {@link Simulation}.
 */
public class SimulationImpl extends
        AbstractObservable<SimulationModelObserver, SimulationModelInfo>
        implements Simulation {

    /**
     * Used as default value for {@link Input}s which are not connected.
     */
    private static final boolean DEFAULT_INPUT_VALUE = false;

    /**
     * With these {@link Brick}s the simulation will begin to calculate because
     * those are providing values at the beginning. This means specifically all
     * {@link Switch}es and all {@link Brick}s with a delay greater zero.
     */
    private Set<SimulationBrick> rootBricks;

    /**
     * These {@link Input}s belonging to the corresponding
     * {@link SimulationBrick} the simulation will give a default input value at
     * the beginning of each simulation step, because they don't get values,
     * because they are not connected to anything.
     */
    private Set<Pair<Input, SimulationBrick>> freeInputs;

    /**
     * This assigns every {@link Brick} in the {@link Simulation} a
     * corresponding {@link SimulationState}.
     */
    private Map<SimulationBrick, SimulationState> simulationStateMap;

    /**
     * Used for debugging (to verify that all bricks are simulated in one step).
     */
    private final Set<SimulationBrick> simulatedBricks;

    /**
     * Used for debugging (to check the bricks against the simulated bricks).
     */
    private final Circuit circuit;

    /**
     * This constructor creates a new Simulation based on the given
     * {@link Circuit}.
     * 
     * @param circuit
     *            the {@link Circuit} the Simulation will simulate. This has to
     *            be non null.
     */
    public SimulationImpl(Circuit circuit) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        this.circuit = circuit;
        simulatedBricks = new LinkedHashSet<SimulationBrick>();
        rootBricks = new LinkedHashSet<SimulationBrick>();
        simulationStateMap = new LinkedHashMap<SimulationBrick, SimulationState>();
        doBuildRootSet(new LinkedList<Component>(), circuit);
        Deque<Component> stack = new LinkedList<Component>();
        Set<Input> connectedInputs = new LinkedHashSet<Input>();
        freeInputs = getUnconnectedInputs(stack, circuit, connectedInputs);
        initInfo(SimulationModelInfo.getInfo());

        if (Profiler.ON) {
            Profiler.simulation(Profiler.CREATE_SIMULATION,
                    System.currentTimeMillis() - time);
        }
    }

    /**
     * Copy constructor. Does a deep copy of the given {@link SimulationImpl}.
     * 
     * @param other
     *            {@link SimulationImpl} to copy from.
     */
    private SimulationImpl(SimulationImpl other) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        simulatedBricks = new LinkedHashSet<SimulationBrick>();
        this.circuit = other.circuit;

        // deep copy of the rootBricks
        rootBricks = new LinkedHashSet<SimulationBrick>();
        for (SimulationBrick rootBrick : other.rootBricks) {
            Deque<Component> stack = new LinkedList<Component>(
                    rootBrick.getStack());
            rootBricks.add(new SimulationBrick(rootBrick.getBrick(), stack));
        }

        // deep copy of the freeInputs
        freeInputs = new LinkedHashSet<Pair<Input, SimulationBrick>>();
        for (Pair<Input, SimulationBrick> freeInput : other.freeInputs) {

            // copy the simulation brick
            Brick brick = freeInput.getRight().getBrick();
            Deque<Component> otherStack = freeInput.getRight().getStack();
            Deque<Component> stack = new LinkedList<Component>(otherStack);
            SimulationBrick simBrick = new SimulationBrick(brick, stack);

            // copy the pair
            freeInputs.add(new Pair<Input, SimulationBrick>(
                    freeInput.getLeft(), simBrick));
        }

        // deep copy of the simulationStateMap
        simulationStateMap = new LinkedHashMap<SimulationBrick, SimulationState>();
        for (SimulationBrick otherKey : other.simulationStateMap.keySet()) {
            SimulationState otherState = other.simulationStateMap.get(otherKey);
            assert otherState != null;
            Deque<Component> stack = new LinkedList<Component>(
                    otherKey.getStack());
            SimulationBrick key = new SimulationBrick(otherKey.getBrick(),
                    stack);
            simulationStateMap.put(key, otherState.clone());
        }

        initInfo(SimulationModelInfo.getInfo());

        if (Profiler.ON) {
            Profiler.simulation(Profiler.COPY_SIMULATION,
                    System.currentTimeMillis() - time);
        }
    }

    /**
     * Constructs the {@link SimulationImpl#rootBricks} by finding all
     * {@link Switch}es and non {@link Component}s with delay and creating a
     * {@link SimulationBrick} for them.
     * 
     * @param stack
     *            The current path to the {@link Component} in which the
     *            {@link Circuit} is in.
     * @param circuit
     *            The {@link Circuit} from which the
     *            {@link SimulationImpl#rootBricks} set is built.
     */
    private void doBuildRootSet(Deque<Component> stack, Circuit circuit) {
        for (Element element : circuit.getElements()) {
            if (element instanceof Brick) {
                Brick brick = (Brick) element;

                // Add brick which have delay or are switches to the root set.
                if (brick instanceof Switch || brick.hasDelay()) {
                    Deque<Component> newStack = new LinkedList<Component>(stack);
                    rootBricks.add(new SimulationBrick(brick, newStack));
                }

                // go recursively into components and continue building the set
                if (brick instanceof Component) {
                    Component component = (Component) brick;
                    stack.push(component);
                    doBuildRootSet(stack, component.getType().getCircuit());
                    stack.pop();
                }
            }
        }
    }

    /**
     * Calculates a {@link Set} of unconnected {@link Input}s in the given
     * {@link Circuit} with their corresponding {@link SimulationBrick}. This is
     * done recursively within {@link Component}s and their
     * {@link ComponentType}s and their {@link Circuit}s while ignoring
     * unconnected {@link Input}s in the {@link Circuit} if they belong to the
     * {@link ComponentType}.
     * 
     * @param stack
     *            The current path to the {@link Component} in which the
     *            {@link Circuit} is in.
     * @param c
     *            The {@link Circuit} for which the unconnected {@link Input}s
     *            are calculated.
     * @param ignoredInputs
     *            A {@link Set} of {@link Input}s to ignore even if they are
     *            unconnected.
     * @return
     */
    private Set<Pair<Input, SimulationBrick>> getUnconnectedInputs(
            Deque<Component> stack, Circuit c, Set<Input> ignoredInputs) {
        Set<Pair<Input, SimulationBrick>> inputs = new LinkedHashSet<Pair<Input, SimulationBrick>>();
        for (Element e : c.getElements()) {
            if (e instanceof Brick) {
                Brick brick = (Brick) e;
                if (brick instanceof Component) {

                    /*
                     * go recursively into components and add those unconnected
                     * inputs while ignoring inputs connected through the
                     * component
                     */
                    Component component = (Component) brick;
                    stack.push(component);
                    Set<Input> newIgnoredInputs = getConnectedInputsOfComponentType(
                            component, ignoredInputs);
                    inputs.addAll(getUnconnectedInputs(stack, component
                            .getType().getCircuit(), newIgnoredInputs));
                    stack.pop();
                } else {

                    /*
                     * look for unconnected inputs which are not ignored and add
                     * them
                     */
                    for (Input i : brick.getInputs()) {
                        if (i.getWires().isEmpty()
                                && !ignoredInputs.contains(i)) {
                            inputs.add(new Pair<Input, SimulationBrick>(i,
                                    new SimulationBrick(brick,
                                            new LinkedList<Component>(stack))));
                        }
                    }
                }
            }
        }
        return inputs;
    }

    /**
     * Calculates a {@link Set} of {@link Input}s of the {@link ComponentType}
     * of the given {@link Component} which are connected through the
     * {@link Component} because their corresponding {@link Input} at the
     * {@link Component} is connected.
     * 
     * @param component
     *            The {@link Component} where the resulting {@link Input}s
     *            belong to its {@link ComponentType}
     * @param alreadyConnectedInputs
     *            A {@link Set} of {@link Input}s of the component which are
     *            somehow already connected.
     * @return A {@link Set} of {@link Input}s of the {@link ComponentType} of
     *         the given {@link Component} which are connected through the
     *         {@link Component} because their corresponding {@link Input} at
     *         the {@link Component} is connected.
     */
    private static Set<Input> getConnectedInputsOfComponentType(Component component,
            Set<Input> alreadyConnectedInputs) {
        Set<Input> connectedInputs = new HashSet<Input>();
        for (Input i : component.getType().getInputs()) {
            Input outer = component.getOuterInput(i);

            // look if one of the inner inputs has wires on its outer inputs or
            // the outer input is otherwise connected => it is connected
            if (alreadyConnectedInputs.contains(outer)
                    || !outer.getWires().isEmpty()) {
                connectedInputs.add(i);
            }
        }
        return connectedInputs;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This is done by beginning with the {@link SimulationBrick}s in
     * {@link SimulationImpl#rootBricks}, taking their {@link Output} values and
     * propagating them through the connected {@link Wire}s to the connected
     * {@link Input}s. {@link Input}s which are not connected will receive the
     * {@link SimulationImpl#DEFAULT_INPUT_VALUE}.
     */
    @Override
    public void nextStep() {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        simulatedBricks.clear();
        for (Pair<Input, SimulationBrick> inputTupel : freeInputs) {
            receiveValue(inputTupel, DEFAULT_INPUT_VALUE);
        }
        for (SimulationBrick identifier : rootBricks) {
            doSim(identifier);
        }

        if (Profiler.ON) {
            Profiler.simulation(Profiler.SIMULATION_STEP,
                    System.currentTimeMillis() - time);
        }

        setChanged();
        getInfo().setSimulationChanged(true);
        notifyObserversIfAuto();

        // test simulation only with assertions enabled, but don't stop on
        // failure
        assert (testAllBricksSimulated(circuit, new LinkedList<Component>()) || Boolean
                .valueOf("true"));
    }

    /**
     * Checks if all {@link Brick}s in the given {@link Circuit} in the given
     * path of {@link Component}s were simulated at the last simulation step. If
     * not simulated {@link Brick}s are found a Warning is logged.
     * 
     * @param circuit
     *            {@link Circuit} to test.
     * @param stack
     *            The current path to the {@link Component} in which the
     *            {@link Circuit} is in.
     * @return test successful? (all {@link Brick}s were simulated)
     */
    private boolean testAllBricksSimulated(Circuit circuit,
            Deque<Component> stack) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        boolean allSimulated = true;
        for (Element e : circuit.getElements()) {
            if (e instanceof Brick) {
                Brick b = (Brick) e;
                SimulationBrick s = new SimulationBrick(b,
                        new LinkedList<Component>(stack));

                // Component? => check if the circuit in it was simulated
                if (b instanceof Component) {
                    Component c = (Component) b;
                    stack.push(c);
                    testAllBricksSimulated(c.getType().getCircuit(), stack);
                    stack.pop();
                } else if (!simulatedBricks.contains(s) && !b.getOutputs().isEmpty()) {

                    // warn if brick with outputs was not simulated
                    gl().warn(
                            "WARNING! The following SimulationBrick was not"
                                    + "simulated during this step: " + s);
                    allSimulated = false;
                }
            }
        }

        if (Profiler.ON) {
            Profiler.simulation(Profiler.SIMULATION_TEST,
                    System.currentTimeMillis() - time);
        }

        return allSimulated;
    }

    /**
     * Calculates the simulation for the given {@link SimulationBrick} by
     * propagating all the {@link Output} values through the connected
     * {@link Wire}s.
     * 
     * @param identifier
     *            the {@link SimulationBrick} to simulate.
     */
    private void doSim(SimulationBrick identifier) {
        Brick brick = identifier.getBrick();
        Deque<Component> stack = identifier.getStack();
        SimulationState state = getSimulationState(identifier);

        for (Output o : brick.getOutputs()) {
            Boolean value = state.getNextValue(o);
            propagateValue(o, stack, value);
            propagateUpwards(o, stack, value);
        }

        // only add if assertions enabled
        assert (simulatedBricks.add(identifier) || Boolean.valueOf("true"));
    }

    /**
     * If the given {@link Output} belongs to an {@link Output} of the given
     * {@link Component} the value is propagated to this {@link Output} and
     * onwards from there.
     * 
     * @param o
     *            The {@link Output} where the value comes from.
     * @param stack
     *            The path to the current {@link Component} in which we are at
     *            this step in the Simulation.
     * @param value
     *            The value to propagate.
     */
    private void propagateUpwards(Output o, Deque<Component> stack,
            boolean value) {

        // propagate value to the outer component outputs if appropriate
        if (!stack.isEmpty()) {
            Component component = stack.peek();
            Output outer = component.getOuterOutput(o);
            if (outer != null) {
                Deque<Component> stack2 = new LinkedList<Component>(stack);
                stack2.pop();

                // delay it
                SimulationBrick identifier = new SimulationBrick(component,
                        stack2);
                SimulationState s = getSimulationState(identifier);
                assert s instanceof ComponentSimulationState;
                ComponentSimulationState state = (ComponentSimulationState) s;
                state.addOutputValue(outer, value);
				if (!component.hasDelay()) {
					value = state.getNextValue(outer);
					propagateValue(outer, stack2, value);
		            propagateUpwards(outer, stack2, value);
				}
            }
        }
    }

    /**
     * Propagates the given value from the given {@link Output} through all the
     * connected {@link Wire}s to the {@link SimulationBrick} the connected
     * {@link Input} belongs to. The calculation of the new {@link Output}
     * values is started if the {@link SimulationBrick} then has all
     * {@link Input} values it needs. If the {@link SimulationBrick} has no
     * delay it is simulated immediately after the calculation.
     * 
     * @param source
     *            The {@link Output} where the value comes from.
     * @param stack
     *            The path to the current {@link Component} in which we are at
     *            this step in the Simulation.
     * @param value
     *            The value to propagate.
     */
    private void propagateValue(Output source, Deque<Component> stack,
            boolean value) {
        for (Wire w : source.getWires()) {

            // other has to be an input. This is the case in a simulation ready
            // circuit
            Input input = (Input) w.getOther(source);
            Pair<Input, SimulationBrick> inputTupel = getInputTupel(input,
                    stack);
            receiveValue(inputTupel, value);
        }
    }

    /**
     * Adds the given value to the {@link SimulationState} of the given
     * {@link SimulationBrick} at the given {@link Input}. The calculation of
     * the new {@link Output} values is started if the {@link SimulationBrick}
     * then has all {@link Input} values it needs. If the
     * {@link SimulationBrick} has no delay it is simulated immediately after
     * the calculation.
     * 
     * @param inputTupel
     *            The {@link SimulationBrick} and the {@link Input} which will
     *            receive the given value.
     * @param value
     *            The value the {@link Input} on the {@link SimulationBrick}
     *            receives.
     */
    private void receiveValue(Pair<Input, SimulationBrick> inputTupel,
            boolean value) {
        SimulationBrick identifier = inputTupel.getRight();
        Input input = inputTupel.getLeft();
        Brick brick = identifier.getBrick();
        SimulationState state = getSimulationState(identifier);
        state.setInput(input, value);
        if (state.hasAllInputs()) {
            state.calculate();
            if (!brick.hasDelay()) {
                doSim(identifier);
            }
        }
    }

    /**
     * Translates a given {@link Input} with its current path of
     * {@link Component}s where it is in to an {@link Input} of a non
     * {@link Component} and the corresponding {@link SimulatonBrick} of the
     * {@link Input}. Therefore it "goes down into Components".
     * 
     * @param input
     *            The {@link Input} to translate.
     * @param stack
     *            The path to the current {@link Component} in which the given
     *            {@link Input} is at this moment.
     * @return The resulting {@link Input} of a non {@link Component} and the
     *         corresponding {@link SimulationBrick}.
     */
    private Pair<Input, SimulationBrick> getInputTupel(Input input,
            Deque<Component> stack) {
        Pair<Input, SimulationBrick> inputTupel;
        Brick brick = input.getBrick();

        // Component? => go deeper into the component
        if (brick instanceof Component) {
            Component component = (Component) brick;
            Input innerInput = component.getInnerInput(input);
            Deque<Component> stack2 = new LinkedList<Component>(stack);
            stack2.push(component);
            inputTupel = getInputTupel(innerInput, stack2);
        } else { // Non component => leave it as is
            inputTupel = new Pair<Input, SimulationBrick>(input,
                    new SimulationBrick(brick, stack));
        }
        return inputTupel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimulationState getSimulationState(SimulationBrick identifier) {
        SimulationState state = simulationStateMap.get(identifier);
        if (state == null) {
            Brick brick = identifier.getBrick();
            state = SimulationStateFactory.getSimulationState(brick);
            assert state != null;
            simulationStateMap.put(identifier, state);
        }
        return state;
    }

    @Override
    public Simulation createSnapshot() {
        return new SimulationImpl(this);
    }

    @Override
    public void restoreFromSnapshot(Simulation snapshot) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        if (snapshot instanceof SimulationImpl) {
            SimulationImpl snapshotCopy = (SimulationImpl) (snapshot
                    .createSnapshot());
            this.freeInputs = snapshotCopy.freeInputs;
            this.rootBricks = snapshotCopy.rootBricks;
            this.simulationStateMap = snapshotCopy.simulationStateMap;

            setChanged();
            getInfo().setSimulationChanged(true);
            notifyObserversIfAuto();
        } else {
            throw new IllegalArgumentException(
                    "Restore is only supported for instances of SimulationImpl.");
        }

        if (Profiler.ON) {
            Profiler.simulation(Profiler.RESTORE_SIMULATION,
                    System.currentTimeMillis() - time);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<SimulationBrick, SimulationState> entries : simulationStateMap
                .entrySet()) {
            Brick brick = entries.getKey().getBrick();
            Deque<Component> stack = entries.getKey().getStack();
            SimulationState state = entries.getValue();
            builder.append("Name: ").append(brick.getName());
            builder.append("; Stack: ");
            for (Component component : stack) {
                builder.append(component.getName()).append(", ");
            }
            builder.append("; OutputValues: ");
            for (Output output : brick.getOutputs()) {
                builder.append(state.getValue(output)).append(", ");
            }
        }
        return builder.toString();
    }

    @Override
    public void notifyObserver(SimulationModelInfo i,
            SimulationModelObserver obs) {
        obs.simulationModelChanged(this, i);
    }

    @Override
    public int hashCode() {
        return rootBricks.hashCode() + freeInputs.hashCode()
                + simulationStateMap.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SimulationImpl)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        SimulationImpl other = (SimulationImpl) obj;
        boolean a = rootBricks.equals(other.rootBricks);
        boolean b = freeInputs.equals(other.freeInputs);
        boolean c = simulationStateMap.equals(other.simulationStateMap);
        return a && b && c;
    }
}