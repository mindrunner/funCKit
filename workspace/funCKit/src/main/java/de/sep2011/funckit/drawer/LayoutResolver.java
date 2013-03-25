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

package de.sep2011.funckit.drawer;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.simulationmodel.ComponentSimulationState;
import de.sep2011.funckit.model.simulationmodel.GateSimulationState;
import de.sep2011.funckit.model.simulationmodel.LightSimulationState;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SimulationState;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;

import java.util.Deque;

public class LayoutResolver implements ElementDispatcher {
    private Deque<Component> elementComponentStack;
    private Simulation simulation;
    private Layout layout;

    /**
     * Injection for layout object.
     *
     * @param layout Layout object to inject further information.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * Injects component stack of associated {@link Element} of {@link Layout},
     * that was injected by {@link LayoutResolver#setLayout}. Specifies path of
     * element in graph.
     *
     * @param stack Stack of parent components.
     */
    public void setComponentStack(Deque<Component> stack) {
        this.elementComponentStack = stack;
    }

    /**
     * Setter injection method for current simulation object to initialize
     * layout object with simulation state information and output queue maps.
     *
     * @param simulation Current simulation object.
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Element element) {
        throw new UnsupportedOperationException("Unknown element dispatched.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Wire wire) {
        assert elementComponentStack != null;
        assert layout != null;

        if (simulation != null) {
            /*
             * Basically we can assume, that in simulation mode inputs are only
             * connected to outputs. But for safety reasons, we first check for
             * the outputs correct type, before we cast it. In some cases it
             * occurred, that ghosts were drawn in simulation mode, too. As we
             * can not assume only-input-to-output-connections with ghosts, we
             * have to check this cast here and ignore wires, that don't support
             * that assumption.
             */
            AccessPoint accessPoint = (wire.getFirstAccessPoint() instanceof Output ? wire
                    .getFirstAccessPoint() : wire.getSecondAccessPoint());

            if (accessPoint instanceof Output) {
                /* Now we have our output to receive the current simulation state. */
                Output o = (Output) accessPoint;
                SimulationBrick simBrick = new SimulationBrick(o.getBrick(),
                        elementComponentStack);

                SimulationState state = simulation.getSimulationState(simBrick);
                boolean value = state.getValue(o);
                layout.setSimulationState(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Component component) {
        resolve(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Switch s) {
        assert elementComponentStack != null;
        assert layout != null;

        /* Resolve simulation state information */
        if (simulation != null) {
            SimulationBrick simBrick = new SimulationBrick(s,
                    elementComponentStack);
            SwitchSimulationState state = (SwitchSimulationState) simulation
                    .getSimulationState(simBrick);
            boolean value = state.getValue();
            layout.setSimulationState(value);
        }

        /* Resolve common layout information */
        resolve(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Light light) {
        assert elementComponentStack != null;
        assert layout != null;

        if (simulation != null) {
            Input firstInput = light.getInputs().iterator().next();
            SimulationBrick simBrick = new SimulationBrick(light,
                    elementComponentStack);
            LightSimulationState state = (LightSimulationState) simulation
                    .getSimulationState(simBrick);
            boolean value = state.getValue(firstInput);
            layout.setSimulationState(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(And and) {
        resolve(and);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Or or) {
        resolve(or);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Not not) {
        resolve(not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdPoint idPoint) {
        resolve(idPoint);
    }

    public void resolve(Input input) {
        assert layout != null;
        if (simulation != null && !input.getWires().isEmpty()) {
            Wire wire = input.getWires().iterator().next();
            /*
             * Usually getOther(input) has to be an Output, but we ensure that
             * with an instanceof-operator to ignore cases with ghost elements,
             * that still exist in simulation mode.
             */
            if (wire.getOther(input) instanceof Output) {
                Output other = (Output) wire.getOther(input);
                resolve(other);
            }
        }
    }

    public void resolve(Output output) {
        assert layout != null;
        if (simulation != null) {
            SimulationBrick simBrick = new SimulationBrick(output.getBrick(),
                    elementComponentStack);
            SimulationState state = simulation.getSimulationState(simBrick);
            layout.setSimulationState(state.getValue(output));
        }
    }

    private void resolve(Brick brick) {
        if (simulation != null) {
            /*
             * If brick has outputs and is no switch, it must have an output
             * queue
             */
            if (brick.getOutputs().size() != 0 && !(brick instanceof Switch)) {
                SimulationBrick simBrick = new SimulationBrick(brick, elementComponentStack);

                SimulationState state = simulation.getSimulationState(simBrick);
                if (state instanceof GateSimulationState) {
                    layout.setOutputQueueMap(((GateSimulationState) state).getOutputQueueMap());
                } else if (state instanceof ComponentSimulationState) {
                    layout.setOutputQueueMap(((ComponentSimulationState) state).getOutputQueueMap());
                }
            }
        }
    }
}
