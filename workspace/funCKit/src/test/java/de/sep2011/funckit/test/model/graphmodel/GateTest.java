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

package de.sep2011.funckit.test.model.graphmodel;

import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.GateImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests gate interface and concrete implementation (black and white box testing
 * of {@link Gate}).
 */
public class GateTest {

    /**
     * Test setting, changing and receiving bound rectangle of several gate
     * implementations.
     */
    @Test
    public void testChangeBoundingRectangle() {

    }

    /**
     * As And represents an official interface class of graphmodel, its
     * calculation method has to be exactly a logic-and.
     */
    @Test
    public void testAndCalculation() {
        Map<Output, Boolean> outputMap;
        Map<Input, Boolean> inputMap;
        Gate andGate = new And(new Rectangle(0, 0, 20, 20), "myOr");
        inputMap = new LinkedHashMap<Input, Boolean>();
        Input inputA = andGate.getInput("a");
        Input inputB = andGate.getInput("b");
        inputMap.put(inputA, false);
        inputMap.put(inputB, false);
        outputMap = andGate.calculate(inputMap);
        boolean value = outputMap.get(andGate.getOutput("o"));
        assertFalse(value);

        inputMap.put(inputB, true);
        outputMap = andGate.calculate(inputMap);
        value = outputMap.get(andGate.getOutput("o"));
        assertFalse(value);

        inputMap.put(inputA, true);
        inputMap.put(inputB, false);
        outputMap = andGate.calculate(inputMap);
        value = outputMap.get(andGate.getOutput("o"));
        assertFalse(value);

        inputMap.put(inputB, true);
        outputMap = andGate.calculate(inputMap);
        value = outputMap.get(andGate.getOutput("o"));
        assertTrue(value);
    }

    /**
     * As Or represents an official interface class of graphmodel, its
     * calculation method has to be exactly a logic-or.
     */
    @Test
    public void testOrCalculation() {
        Map<Output, Boolean> outputMap;
        Map<Input, Boolean> inputMap;
        GateImpl orGate = new Or(new Rectangle(0, 0, 20, 20), "myOr");
        inputMap = new LinkedHashMap<Input, Boolean>();
        Input inputA = orGate.getInput("a");
        Input inputB = orGate.getInput("b");
        inputMap.put(inputA, true);
        inputMap.put(inputB, false);

        outputMap = orGate.calculate(inputMap);
        gl().debug(outputMap.get(orGate.getOutput("o")));

    }

    /**
     * As Not represents an official interface class of graphmodel, its
     * calculation method has to be exactly the inverse value of given input.
     */
    @Test
    public void testNotCalculation() {
        Map<Output, Boolean> outputMap;
        Map<Input, Boolean> inputMap;
        Gate notGate = new Not(new Rectangle(0, 0, 20, 20));
        inputMap = new LinkedHashMap<Input, Boolean>();
        Input inputA = notGate.getInput("a");
        inputMap.put(inputA, true);

        outputMap = notGate.calculate(inputMap);
        gl().debug(outputMap.get(notGate.getOutput("o")));
    }

    /**
     * As IdPoint represents an official interface class of graphmodel, its
     * calculation method has to return exactly same value as the given input.
     */
    @Test
    public void testIdPointCalculation() {

    }

}
