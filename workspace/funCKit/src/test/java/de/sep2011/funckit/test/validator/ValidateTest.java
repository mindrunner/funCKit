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
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.TwoInputsConnectedCheck;
import de.sep2011.funckit.validator.TwoOutputsConnectedCheck;
import de.sep2011.funckit.validator.ZeroDelayLoopCheck;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for various {@link Check}s.
 */
public class ValidateTest {

    private Circuit circuit;
    private Brick andBrick;
    private Brick notBrick;

    @Before
    public void setUp() {
        gl().info("testSimulateSimpleCircuit");

        /* Build simple circuit */
        circuit = new CircuitImpl();
        notBrick = new Not(new Rectangle(0, 0, 20, 20), "myNot");
        circuit.addBrick(notBrick);
        andBrick = new And(new Rectangle(0, 0, 20, 20), "myAnd");
        circuit.addBrick(andBrick);
    }

    /**
     * connects 2 outputs of 2 different bricks and expects that the 2
     * outputs-connected-check won't work. then disconnect.
     */
    @Test
    public void testConnectDifBrickOut() {
        /* Connect Outputs of different gates and check and disconnect */
        circuit.connect(notBrick.getOutput("o"), andBrick.getOutput("o"));
        TwoOutputsConnectedCheck tocCheck = new TwoOutputsConnectedCheck();
        Result result = tocCheck.perform(circuit);
        boolean value = result.isPassed();
        assertFalse(value);
    }

    /**
     * connects the 2 inputs of the same bricks and expects that the 2
     * inputs-connected-check won't work. then disconnect.
     */
    @Test
    public void testConnectSameBrickIn() {
        circuit.connect(notBrick.getInput("a"), notBrick.getInput("a"));

        TwoInputsConnectedCheck ticCheck = new TwoInputsConnectedCheck();
        Result result = ticCheck.perform(circuit);
        boolean value = result.isPassed();
        assertFalse(value);
    }

    /**
     * This tests connects two inputs and checks for the correct result of
     * {@link TwoInputsConnectedCheck}.
     */
    @Test
    public void testConnectDifBrickIn() {
        /* Connect Inputs of different gates and check and disconnect */
        circuit.connect(notBrick.getInput("a"), andBrick.getInput("a"));
        TwoInputsConnectedCheck ticCheck = new TwoInputsConnectedCheck();
        Result result = ticCheck.perform(circuit);
        boolean value = result.isPassed();
        assertFalse(value);
    }

    /**
     * Creates a circuit where {@link TwoInputsConnectedCheck} and
     * {@link TwoOutputsConnectedCheck} should pass. Checks if they do.
     */
    @Test
    public void testConnectDifBrickOutIn() {
        /* Connect Input and Output of 2 gates and check and disconnect */
        circuit.connect(notBrick.getOutput("o"), andBrick.getInput("a"));
        TwoInputsConnectedCheck ticCheck = new TwoInputsConnectedCheck();
        Result result = ticCheck.perform(circuit);
        boolean value = result.isPassed();
        assertTrue(value);
        TwoOutputsConnectedCheck tocCheck = new TwoOutputsConnectedCheck();
        result = tocCheck.perform(circuit);
        value = result.isPassed();
        assertTrue(value);

        circuit.disconnect(notBrick.getOutput("o"), andBrick.getInput("a"));
    }

    /**
     * Tests {@link ZeroDelayLoopCheck} with a simple circuit containing 2
     * Bricks. Positive and negative test.
     */
    @Test
    public void testMakeLoopwith2Bricks() {

        /* Make loop without delay. the check must be negative */
        circuit.connect(notBrick.getOutput("o"), andBrick.getInput("a"));
        circuit.connect(andBrick.getOutput("o"), notBrick.getInput("a"));

        ZeroDelayLoopCheck zdlCheck = new ZeroDelayLoopCheck();
        Result result = zdlCheck.perform(circuit);
        boolean value = result.isPassed();
        assertFalse(value);

        /* set delay and check again. reset delay */
        andBrick.setDelay(1);
        result = zdlCheck.perform(circuit);
        value = result.isPassed();
        assertTrue(value);
        andBrick.setDelay(0);
        assertTrue(true);
    }

}
