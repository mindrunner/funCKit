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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.simulationmodel.LightSimulationState;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.LinkedList;

import static de.sep2011.funckit.util.Log.gl;
import static junit.framework.Assert.assertTrue;

/**
 * This test creates a Circuit with one Or Gate, connected to two Switches and
 * one Lamp. One of the Switches is set to true the other to false. Simulates
 * the Circuit and checks for the Correct result at the Input of the
 * {@link Light}.
 */
public class OneOrTest {

    /**
     * Performs the test.
     */
    @Test
    public void testSimulateOneOr() {
        gl().info("testSimulateSimpleCircuit");

        /* Build simple circuit */
        Circuit circuit = new CircuitImpl();

        /* build and init Switches */
        SwitchImpl switchBrick1 = new SwitchImpl(new Rectangle(0, 0, 20, 20),
                "mySwitch1");
        SwitchImpl switchBrick2 = new SwitchImpl(new Rectangle(0, 0, 20, 20),
                "mySwitch2");
        circuit.addBrick(switchBrick1);
        circuit.addBrick(switchBrick2);
        switchBrick1.setValue(true);
        switchBrick2.setValue(false);

        /* build Or */
        Or orBrick = new Or(new Rectangle(0, 0, 20, 20), "myOr");
        circuit.addBrick(orBrick);

        /* build Lamp */
        Light lamp = new Light(new Rectangle(0, 0, 20, 20), "myLight");
        circuit.addBrick(lamp);

        /* connect elements with Wires */
        circuit.connect(switchBrick1.getOutputO(), orBrick.getInputA());
        circuit.connect(switchBrick2.getOutputO(), orBrick.getInputB());
        circuit.connect(orBrick.getOutputO(), lamp.getInputA());

        /* First simulation.... */
        Simulation simulation = new SimulationImpl(circuit);
        simulation.nextStep(); // do a Simulation step

        SimulationBrick simLamp = new SimulationBrick(lamp,
                new LinkedList<Component>());
        LightSimulationState lightState = (LightSimulationState) simulation
                .getSimulationState(simLamp);
        boolean value = lightState.getValue(lamp.getInputA());

        assertTrue(value);

    }
}