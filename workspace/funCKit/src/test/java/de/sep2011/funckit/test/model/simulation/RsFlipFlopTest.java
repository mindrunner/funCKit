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

package de.sep2011.funckit.test.model.simulation;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.simulationmodel.LightSimulationState;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.LinkedList;

import static de.sep2011.funckit.util.Log.gl;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * This test creates a Circuit with two Switches connected to set/reset
 * {@link Input} of a Fliplflop {@link Component} and one {@link Light}
 * connected to the {@link Output} of the Flipflop. Tests if it behaves like the
 * specified Flipflop.
 */
public class RsFlipFlopTest {

    /**
     * Performs the Test.
     */
    @Test
    public void testCreateFlipFlop() {
        gl().info("testSimulateSimpleCircuit");

        /* Build circuit & inputs & lamp */
        Circuit circuit = new CircuitImpl();
        Switch set = new SwitchImpl(new Rectangle(0, 0, 20, 20), "Set");
        Switch reset = new SwitchImpl(new Rectangle(0, 0, 20, 20), "Reset");
        circuit.addBrick(set);
        circuit.addBrick(reset);
        Light lamp = new Light(new Rectangle(0, 0, 20, 20), "Q");
        Light lamp2 = new Light(new Rectangle(0, 0, 20, 20), "Q_quer");
        circuit.addBrick(lamp);
        circuit.addBrick(lamp2);

        /* build Ands and Nots */
        Brick andBrick1 = new And(new Rectangle(0, 0, 20, 20), "myAnd1");
        circuit.addBrick(andBrick1);
        Brick andBrick2 = new And(new Rectangle(0, 0, 20, 20), "myAnd2");
        circuit.addBrick(andBrick2);

        Brick and1Not = new Not(new Rectangle(0, 0, 20, 20), "and1Not");
        circuit.addBrick(and1Not);
        Brick and2Not = new Not(new Rectangle(0, 0, 20, 20), "and2Not");
        circuit.addBrick(and2Not);

        /* connect */
        circuit.connect(set.getOutput("o"), andBrick1.getInput("a"));
        circuit.connect(reset.getOutput("o"), andBrick2.getInput("b"));

        circuit.connect(andBrick1.getOutput("o"), and1Not.getInput("a"));
        circuit.connect(andBrick2.getOutput("o"), and2Not.getInput("a"));

        circuit.connect(and1Not.getOutput("o"), andBrick2.getInput("a"));
        circuit.connect(and2Not.getOutput("o"), andBrick1.getInput("b"));

        circuit.connect(and1Not.getOutput("o"), lamp.getInput("a"));
        circuit.connect(and2Not.getOutput("o"), lamp2.getInput("a"));

        /* set delay */
        andBrick1.setDelay(1);
        andBrick2.setDelay(1);

        /* First simulation.... */
        Simulation simulation = new SimulationImpl(circuit);

        SimulationBrick simSet = new SimulationBrick(set,
                new LinkedList<Component>());
        SimulationBrick simReset = new SimulationBrick(reset,
                new LinkedList<Component>());
        SimulationBrick simLamp = new SimulationBrick(lamp,
                new LinkedList<Component>());
        SimulationBrick simLamp2 = new SimulationBrick(lamp2,
                new LinkedList<Component>());
        LightSimulationState lightState = (LightSimulationState) simulation
                .getSimulationState(simLamp);
        LightSimulationState light2State = (LightSimulationState) simulation
                .getSimulationState(simLamp2);

        // starting with meta stable state => at first set stable state (set
        // state)
        SwitchSimulationState setState = (SwitchSimulationState) simulation
                .getSimulationState(simSet);
        SwitchSimulationState resetState = (SwitchSimulationState) simulation
                .getSimulationState(simReset);
        setState.setValue(false);
        resetState.setValue(true);
        simulation.nextStep();
        simulation.nextStep();
        simulation.nextStep();
        assertTrue(lightState.getValue(lamp.getInput("a")));
        assertFalse(light2State.getValue(lamp2.getInput("a")));

        // check if the value holds (hold state)
        setState.setValue(true);
        resetState.setValue(true);
        for (int i = 0; i < 10; i++) {
            simulation.nextStep();
            assertTrue(lightState.getValue(lamp.getInput("a")));
            assertFalse(light2State.getValue(lamp2.getInput("a")));
        }

        /* change (reset state), run 10 steps and try again */
        resetState.setValue(false);
        for (int i = 0; i < 10; i++) {
            simulation.nextStep();
        }
        assertFalse(lightState.getValue(lamp.getInput("a")));
        assertTrue(light2State.getValue(lamp2.getInput("a")));

        // check if the value holds (hold state)
        setState.setValue(true);
        resetState.setValue(true);
        for (int i = 0; i < 10; i++) {
            simulation.nextStep();
            assertFalse(lightState.getValue(lamp.getInput("a")));
            assertTrue(light2State.getValue(lamp2.getInput("a")));
        }
    }
}
