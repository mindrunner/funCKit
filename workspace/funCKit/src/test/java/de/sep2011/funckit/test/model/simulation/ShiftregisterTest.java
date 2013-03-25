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

import de.sep2011.funckit.circuitfactory.ShiftRegisterFactory;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import org.junit.Test;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests if the Shiftregister produced by the {@link ShiftRegisterFactory} is
 * correctly simulated by triggering its inputs and comparing the outputs with
 * the expected values. This is done with tolerance for the setup time of the
 * Shiftregister.
 */
public class ShiftregisterTest {
    private static final int REGISTER_LENGTH = 10;
    private static final int INITIAL_SETUP_TIME = 10 * REGISTER_LENGTH;
    private static final int VALUE_SETUP_TIME = 5;
    private static final int RESULT_DELAY = 5;

    /**
     * Performs the test.
     */
    @Test
    public void test() {
        ComponentType shiftregisterType = new ShiftRegisterFactory(
                REGISTER_LENGTH).getComponentTypeForCircuit();
        Component shiftregister = new ComponentImpl(shiftregisterType);

        // build circuit
        List<Light> lights = new LinkedList<Light>();
        List<Switch> switches = new LinkedList<Switch>();

        Circuit circuit = new CircuitImpl();
        circuit.addBrick(shiftregister);

        SwitchImpl data = new SwitchImpl(new Point());
        SwitchImpl clock = new SwitchImpl(new Point());
        circuit.addBrick(data);
        circuit.addBrick(clock);
        circuit.connect(data.getOutputO(), shiftregister.getInput("d"));
        circuit.connect(clock.getOutputO(), shiftregister.getInput("clk"));
        switches.add(data);
        switches.add(clock);

        for (int i = 1; i <= REGISTER_LENGTH; i++) {
            Light qX = new Light(new Point());
            circuit.addBrick(qX);
            circuit.connect(shiftregister.getOutput("q" + i), qX.getInputA());
            lights.add(qX);
        }

        // test it
        CircuitSimulator combTester = new CircuitSimulator(circuit, switches,
                lights);

        // wait until the register is all set to zero
        for (int i = 0; i <= INITIAL_SETUP_TIME; i++) {
            setValue(combTester, 0);
        }

        // now everything should be zero
        String outputs = "0";
        for (int i = 1; i < REGISTER_LENGTH; i++) {
            outputs += " 0";
        }
        combTester.addTestRow("0 0", outputs);

        // let a 1 get shifted through
        setValue(combTester, 1);
        outputs = "1";
        for (int i = 1; i < REGISTER_LENGTH; i++) {
            outputs += " 0";
        }
        combTester.addTestRow("0 0", outputs);
        for (int position = 1; position < REGISTER_LENGTH; position++) {
            setValue(combTester, 0);
            outputs = "0";
            for (int i = 1; i < position; i++) {
                outputs += " 0";
            }
            outputs += " 1";
            for (int i = position + 1; i < REGISTER_LENGTH; i++) {
                outputs += " 0";
            }
            System.out.println(outputs);
            combTester.addTestRow("0 0", outputs);
        }

        assertTrue(combTester.simulate());

    }

    private void setValue(CircuitSimulator combTester, int value) {
        testRowMultipleTimes(combTester, value + " 1", "", VALUE_SETUP_TIME);
        testRowMultipleTimes(combTester, value + " 0", "", RESULT_DELAY);
    }

    private void testRowMultipleTimes(CircuitSimulator combTester,
            String inputs, String outputs, int times) {
        for (int i = 0; i < times; i++) {
            combTester.addTestRow(inputs, outputs);
        }
    }
}
