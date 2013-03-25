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

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.model.simulationmodel.SimulationState;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;
import de.sep2011.funckit.util.Pair;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.LinkedList;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains some simple tests for the simulation.
 */
public class SimpleSimulationTests {

    /**
     * Tests the Simulation of a simple Circuit containing 2 Switches connected
     * to an AND.
     */
    @Test
    public void testSimulateSimpleCircuit() {
        gl().info("testSimulateSimpleCircuit");

        /* Build simple circuit */
        Circuit circuit = new CircuitImpl();
        Switch switchBrick1 = new SwitchImpl(new Rectangle(0, 0, 20, 20),
                "mySwitch1");
        Switch switchBrick2 = new SwitchImpl(new Rectangle(0, 0, 20, 20),
                "mySwitch2");
        circuit.addBrick(switchBrick1);
        circuit.addBrick(switchBrick2);
        Brick andBrick = new And(new Rectangle(0, 0, 20, 20), "myAnd");
        circuit.addBrick(andBrick);
        circuit.connect(switchBrick1.getOutput("o"), andBrick.getInput("a"));
        circuit.connect(switchBrick2.getOutput("o"), andBrick.getInput("b"));
        SimulationBrick realBrick = new SimulationBrick(andBrick,
                new LinkedList<Component>());
        Output output = andBrick.getOutput("o");

        /* First simulation must be false as switch is not activated. */
        Simulation simulation = new SimulationImpl(circuit);
        simulation.nextStep();
        SimulationState state = simulation.getSimulationState(realBrick);
        boolean value = state.getValue(output);
        assertFalse(value);

        /* Now toggle switch and simulate next step. */
        SimulationBrick simSwitch1 = new SimulationBrick(switchBrick1,
                new LinkedList<Component>());
        SimulationBrick simSwitch2 = new SimulationBrick(switchBrick2,
                new LinkedList<Component>());
        SwitchSimulationState switch1State = (SwitchSimulationState) simulation
                .getSimulationState(simSwitch1);
        SwitchSimulationState switch2State = (SwitchSimulationState) simulation
                .getSimulationState(simSwitch2);

        switch1State.setValue(true);
        simulation.nextStep();
        state = simulation.getSimulationState(realBrick);
        value = state.getValue(output);
        assertFalse(value);

        /* Now toggle other switch and simulate next step. */
        switch2State.setValue(true);
        simulation.nextStep();
        state = simulation.getSimulationState(realBrick);
        value = state.getValue(output);
        assertTrue(value);
    }

    /**
     * This test tests the simulation of a simple Combinatorial Circuit with 3
     * switches, 2 NOT and 2 OR bricks.
     */
    @Test
    public void testSimulateCombinatorialCircuit() {
        Pair<Circuit, Brick> pair = getCombinatorialCircuit(false, false, true);
        Circuit testCircuit = pair.getLeft();
        Brick lastBrick = pair.getRight();
        Simulation simulation = new SimulationImpl(testCircuit);
        simulation.nextStep();
        SimulationBrick realBrick = new SimulationBrick(lastBrick,
                new LinkedList<Component>());
        SimulationState state = simulation.getSimulationState(realBrick);
        boolean value = state.getValue(lastBrick.getOutput("o"));
        assertTrue(value);
    }

    private static Pair<Circuit, Brick> getCombinatorialCircuit(boolean switch1,
            boolean switch2, boolean switch3) {
        Circuit circuit = new CircuitImpl();

        /* Using three input switches. */
        Switch switchBrickA = new SwitchImpl(new Rectangle(0, 0, 20, 20), "A");
        Switch switchBrickB = new SwitchImpl(new Rectangle(0, 0, 40, 20), "B");
        Switch switchBrickC = new SwitchImpl(new Rectangle(0, 0, 60, 20), "C");
        circuit.addBrick(switchBrickA);
        circuit.addBrick(switchBrickB);
        circuit.addBrick(switchBrickC);
        AccessPoint switchBrickAOutput = switchBrickA.getOutput("o");
        AccessPoint switchBrickBOutput = switchBrickB.getOutput("o");
        AccessPoint switchBrickCOutput = switchBrickC.getOutput("o");

        /* Toggle switches. */
        if (switch1) {
            switchBrickA.toggle();
        }
        if (switch2) {
            switchBrickB.toggle();
        }
        if (switch3) {
            switchBrickC.toggle();
        }

        /* Add two not-bricks */
        Brick not1 = new Not(new Rectangle(0, 0, 30, 40), "not1");
        Brick not2 = new Not(new Rectangle(0, 0, 50, 40), "not2");
        circuit.addBrick(not1);
        circuit.addBrick(not2);
        AccessPoint not1input = not1.getInput("a");
        AccessPoint not2input = not2.getInput("a");
        AccessPoint not1output = not1.getOutput("o");
        AccessPoint not2output = not2.getOutput("o");

        /* Negate A and C switches to have two further values. */
        circuit.connect(switchBrickAOutput, not1input);
        circuit.connect(switchBrickCOutput, not2input);

        /* Add two or-bricks */
        Brick or1 = new Or(new Rectangle(0, 0, 30, 40), "or1");
        Brick or2 = new Or(new Rectangle(0, 0, 50, 40), "or2");
        circuit.addBrick(or1);
        circuit.addBrick(or2);
        AccessPoint or1inputA = or1.getInput("a");
        AccessPoint or1inputB = or1.getInput("b");
        AccessPoint or1output = or1.getOutput("o");
        AccessPoint or2inputA = or2.getInput("a");
        AccessPoint or2inputB = or2.getInput("b");
        AccessPoint or2output = or2.getOutput("o");

        /* Connect negated A and B to or1 and A and negated C to or2 */
        circuit.connect(not1output, or1inputA);
        circuit.connect(switchBrickBOutput, or1inputB);
        circuit.connect(switchBrickAOutput, or2inputA);
        circuit.connect(not2output, or2inputB);

        /* Create and brick */
        Brick and = new And(new Rectangle(0, 0, 40, 60), "and");
        circuit.addBrick(and);
        AccessPoint andInputA = and.getInput("a");
        AccessPoint andInputB = and.getInput("b");
        AccessPoint andOutput = and.getOutput("o");

        /* Connect results of or bricks to and brick. */
        circuit.connect(or1output, andInputA);
        circuit.connect(or2output, andInputB);

        /* Add negate brick */
        Brick not3 = new Not(new Rectangle(0, 0, 40, 80), "not3");
        circuit.addBrick(not3);
        AccessPoint not3input = not3.getInput("a");
        // AccessPoint not3output = not3.getOutput("o");

        /* Connect result of and brick to negate brick. */
        circuit.connect(andOutput, not3input);

        return new Pair<Circuit, Brick>(circuit, not3);
    }
}
