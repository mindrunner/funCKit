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

import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static de.sep2011.funckit.test.model.simulation.SimulationTestUtil.getLightByName;
import static de.sep2011.funckit.test.model.simulation.SimulationTestUtil.getSwitchByName;
import static org.junit.Assert.assertTrue;

/**
 * This test runs the simulation on a D-FlipFlop based on the example from
 * Andreas (see /example-d-FlipFlop/example-dflipflop.fck). Test sequence: <br>
 * 1) set the switch to 1, wait 10 steps to stabilize, expect 1 on the active
 * high Output and 0 on the active low output. <br>
 * 2) set the switch to 0, wait 10 steps to stabilize, expect 0 on the active
 * high Output and 1 on the active low output.<br>
 * 3) set the switch to 1, wait 10 steps to stabilize, expect 1 on the active
 * high Output and 0 on the active low output.
 */
public class DFlipFlopTest {

    /**
     * Performs the test.
     * 
     * @throws SEPFormatImportException
     */
    @Test
    public void test() throws SEPFormatImportException {
        Circuit circuit = new SEPFormatConverter("", Mode.FUNCKITFORMAT)
                .doImport(getClass().getResourceAsStream(
                        "/example-d-FlipFlop/example-dflipflop.fck"));

        List<Light> lights = new LinkedList<Light>();
        List<Switch> switches = new LinkedList<Switch>();

        lights.add(getLightByName(circuit, "lQ"));
        lights.add(getLightByName(circuit, "lQ_"));

        switches.add(getSwitchByName(circuit, "s0"));

        CircuitSimulator combTester = new CircuitSimulator(circuit, switches,
                lights);

        /* switch on and wait for stabilize */
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "1 0");
        /* switch off and wait for stabilize */
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "");
        combTester.addTestRow("0", "0 1");
        /* switch on and wait for stabilize */
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "");
        combTester.addTestRow("1", "1 0");

        assertTrue(combTester.simulate());

    }

}
