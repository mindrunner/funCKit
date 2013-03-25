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

package de.sep2011.funckit.test.validator;

import de.sep2011.funckit.circuitfactory.ClockFactory;
import de.sep2011.funckit.circuitfactory.ShiftRegisterFactory;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.test.factory.circuit.ComplexComponentCircuit1Factory;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.ZeroDelayLoopCheck;
import org.junit.Test;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for {@link ZeroDelayLoopCheck}.
 */
public class ZeroDelayLoopCheckTest {

    /**
     * Tests the Check with a Circuit without a Loop.
     */
    @Test
    public void testNoLoop() {
        Circuit c = new ComplexComponentCircuit1Factory().getCircuit();
        ZeroDelayLoopCheck check = new ZeroDelayLoopCheck();
        Result result = check.perform(c);
        gl().debug(result.getFlawElements());
        assertTrue(result.isPassed());
        assertTrue(result.getFlawElements().isEmpty());
    }

    /**
     * Creates a Clock from {@link ClockFactory} with zero delay and checks if
     * the {@link Check} detects this.
     */
    @Test
    public void testSimpleZeroDelayLoop() {
        Circuit c = new ClockFactory(0).getCircuit();

        ZeroDelayLoopCheck check = new ZeroDelayLoopCheck();
        Result result = check.perform(c);
        assertFalse(result.isPassed());
        gl().debug(result.getFlawElements());
    }

    /**
     * Creates a Clock from {@link ClockFactory} with a delay of 1 and checks if
     * the {@link Check} passes.
     */
    @Test
    public void testSimpleDelayLoop() {
        Circuit c = new ClockFactory(1).getCircuit();
        ZeroDelayLoopCheck check = new ZeroDelayLoopCheck();
        Result result = check.perform(c);
        assertTrue(result.isPassed());
        assertTrue(result.getFlawElements().isEmpty());
    }

    /**
     * Tests a complex setup of a zero delay loop by using a shiftregister with
     * a loop from the last output to the data input.
     */
    @Test
    public void testComplexZeroDelayLoop() {
    	final int REGISTER_LENGTH = 10;
		ComponentType shiftregisterType = new ShiftRegisterFactory(
				REGISTER_LENGTH).getComponentTypeForCircuit();
		Component shiftregister = new ComponentImpl(shiftregisterType);
        Circuit c = new CircuitImpl();
        c.addBrick(shiftregister);
		c.connect(shiftregister.getOutput("q" + REGISTER_LENGTH),
				shiftregister.getInput("d"));
		ZeroDelayLoopCheck check = new ZeroDelayLoopCheck();
        Result result = check.perform(c);
        assertFalse(result.isPassed());
        assertFalse(result.getFlawElements().isEmpty());
    }

    /**
     * Tests a complex setup of a non zero delay loop by using a shiftregister 
     * with a loop from the last output to the data input, but the register has
     * a non zero delay.
     */
    @Test
    public void testComplexDelayLoop() {
    	final int REGISTER_LENGTH = 10;
		ComponentType shiftregisterType = new ShiftRegisterFactory(
				REGISTER_LENGTH).getComponentTypeForCircuit();
		Component shiftregister = new ComponentImpl(shiftregisterType);
		shiftregister.setDelay(1);
        Circuit c = new CircuitImpl();
        c.addBrick(shiftregister);
		c.connect(shiftregister.getOutput("q" + REGISTER_LENGTH),
				shiftregister.getInput("d"));
		ZeroDelayLoopCheck check = new ZeroDelayLoopCheck();
        Result result = check.perform(c);
        assertTrue(result.isPassed());
        assertTrue(result.getFlawElements().isEmpty());
    }
}
