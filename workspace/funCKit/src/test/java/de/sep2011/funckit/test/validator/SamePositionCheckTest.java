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

import de.sep2011.funckit.circuitfactory.ShiftRegisterFactory;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.SamePositionCheck;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for {@link SamePositionCheck}.
 */
public class SamePositionCheckTest {

    /**
     * Creates a Circuit with Bricks on the same Position and Checks if the
     * Check detects it correctly and gives the correct flawElements.
     */
    @Test
    public void negativeTest() {
        Circuit circuit = new CircuitImpl();
        Brick failedBrick1 = new And(new Rectangle(0, 0, 40, 40));
        Brick failedBrick2 = new Or(new Rectangle(0, 0, 40, 40));
        Brick failedBrick3 = new And(new Rectangle(0, 0, 40, 40));
        Brick failedBrick4 = new ComponentImpl(
                new ShiftRegisterFactory(7).getComponentTypeForCircuit());
        failedBrick4.setPosition(new Point(0, 0));
        failedBrick4.setDimension(new Dimension(40, 40));

        Set<Element> flaws = new HashSet<Element>();
        flaws.add(failedBrick1);
        flaws.add(failedBrick2);
        flaws.add(failedBrick3);
        flaws.add(failedBrick4);
        circuit.addBrick(failedBrick1);
        circuit.addBrick(failedBrick2);
        circuit.addBrick(failedBrick3);
        circuit.addBrick(failedBrick4);

        Check positionCheck = new SamePositionCheck();
        Result result = positionCheck.perform(circuit);
        assertFalse(result.isPassed());
        assertEquals(flaws, result.getFlawElements());
    }

    /**
     * Creates a Circuit with Bricks not on the same Position and Checks if the
     * Check detects it correctly and that flawElements is empty then.
     */
    @Test
    public void positiveTest() {
        Circuit circuit = new CircuitImpl();
        circuit.addBrick(new And(new Rectangle(10, 10, 40, 40)));
        circuit.addBrick(new Or(new Rectangle(10, 20, 40, 40)));
        Brick failedBrick4 = new ComponentImpl(
                new ShiftRegisterFactory(7).getComponentTypeForCircuit());
        failedBrick4.setPosition(new Point(10, 30));
        failedBrick4.setDimension(new Dimension(40, 40));
        circuit.addBrick(failedBrick4);

        Check positionCheck = new SamePositionCheck();
        Result result = positionCheck.perform(circuit);
        assertTrue(result.isPassed());

        Set<Element> flaws = new HashSet<Element>();
        assertEquals(flaws, result.getFlawElements());
    }

}
