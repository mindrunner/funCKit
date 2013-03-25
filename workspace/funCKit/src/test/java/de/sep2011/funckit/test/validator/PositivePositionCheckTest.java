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
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.PositivePositionCheck;
import de.sep2011.funckit.validator.Result;
import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link PositivePositionCheck}.
 */
public class PositivePositionCheckTest {

    private static Result check(Circuit c) {
        PositivePositionCheck check = new PositivePositionCheck();
        return check.perform(c);
    }

    /**
     * Test if the {@link Check} recognizes if all Elements of a {@link Circuit}
     * are on positive positions.
     */
    @Test
    public void testAllPositive() {
        Circuit c = new CircuitImpl();
        And a1 = new And(new Point(10, 1000));
        And a2 = new And(new Point(30, 80));
        And a3 = new And(new Point(50, 60));
        Or o1 = new Or(new Point(20, 90));
        Or o2 = new Or(new Point(40, 70));
        Component comp1 = new ComponentImpl(
                new ShiftRegisterFactory(8).getComponentTypeForCircuit());

        c.addBrick(a1);
        c.addBrick(o1);
        c.addBrick(a2);
        c.addBrick(o2);
        c.addBrick(a3);
        c.addBrick(comp1);


        Result r = check(c);

        assertTrue(r.isPassed());
        assertFalse(r.getFlawElements().contains(a1));
        assertFalse(r.getFlawElements().contains(a2));
        assertFalse(r.getFlawElements().contains(a3));
        assertFalse(r.getFlawElements().contains(o1));
        assertFalse(r.getFlawElements().contains(o2));
    }

    /**
     * Test if the {@link Check} recognizes if all Elements of a {@link Circuit}
     * are on negative positions.
     */
    @Test
    public void testAllNegative() {
        Circuit c = new CircuitImpl();
        And a1 = new And(new Point(-10, -1000));
        And a2 = new And(new Point(-30, -80));
        And a3 = new And(new Point(-50, -60));
        Or o1 = new Or(new Point(-20, -90));
        Or o2 = new Or(new Point(-40, -70));
        Component comp1 = new ComponentImpl(
                new ShiftRegisterFactory(8).getComponentTypeForCircuit());

        c.addBrick(a1);
        c.addBrick(o1);
        c.addBrick(a2);
        c.addBrick(o2);
        c.addBrick(a3);
        c.addBrick(comp1);


        Result r = check(c);

        assertFalse(r.isPassed());
        assertTrue(r.getFlawElements().contains(a1));
        assertTrue(r.getFlawElements().contains(a2));
        assertTrue(r.getFlawElements().contains(a3));
        assertTrue(r.getFlawElements().contains(o1));
        assertTrue(r.getFlawElements().contains(o2));
    }

    /**
     * Test if the {@link Check} recognizes if some {@link Element}s have a
     * positive and some a negative position.
     */
    @Test
    public void testMixed() {
        Circuit c = new CircuitImpl();
        And a1 = new And(new Point(-10, -1000));
        And a2 = new And(new Point(-30, -80));
        And a3 = new And(new Point(50, 60));
        Or o1 = new Or(new Point(20, 90));
        Or o2 = new Or(new Point(40, 70));

        c.addBrick(a1);
        c.addBrick(o1);
        c.addBrick(a2);
        c.addBrick(o2);
        c.addBrick(a3);


        Result r = check(c);

        assertFalse(r.isPassed());
        assertTrue(r.getFlawElements().contains(a1));
        assertTrue(r.getFlawElements().contains(a2));
        assertFalse(r.getFlawElements().contains(a3));
        assertFalse(r.getFlawElements().contains(o1));
        assertFalse(r.getFlawElements().contains(o2));
    }
}
