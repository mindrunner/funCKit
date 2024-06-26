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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.ZeroDelayLoopCheck;
import org.junit.Test;

import java.awt.Rectangle;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertFalse;

/**
 * This class contains tests for {@link ZeroDelayLoopCheck}.
 */
public class OneNotLoopTest {

    /**
     * This test creates a Circuit with one Not Gate with no delay. The
     * {@link de.sep2011.funckit.model.graphmodel.Output} of the Not is
     * connected to its {@link Input}. The test runs a ZeroDelayLoopCheck to
     * test if it recognizes this non-simulatable Circuit.
     */
    @Test
    public void testZeroDelayLoopOneNotLoop() {
        gl().info("testSimulateSimpleCircuit");

        /* Build simple circuit */
        Circuit circuit = new CircuitImpl();
        Brick notBrick = new Not(new Rectangle(0, 0, 20, 20), "myNot");
        circuit.addBrick(notBrick);
        circuit.connect(notBrick.getOutput("o"), notBrick.getInput("a"));

        /* Checks.... */
        ZeroDelayLoopCheck zdlCheck = new ZeroDelayLoopCheck();
        Result result = zdlCheck.perform(circuit);
        boolean value = result.isPassed();
        assertFalse(value);
        assertFalse(false);

    }
}
