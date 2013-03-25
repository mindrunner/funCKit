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

import de.sep2011.funckit.circuitfactory.AbstractCircuitFactory;
import de.sep2011.funckit.circuitfactory.ClockFactory;
import de.sep2011.funckit.circuitfactory.DFlipFlopFactory;
import de.sep2011.funckit.circuitfactory.DLatchFactory;
import de.sep2011.funckit.circuitfactory.FullAdderFactory;
import de.sep2011.funckit.circuitfactory.NorFactory;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.CollisionCheck;
import junit.framework.Assert;
import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This Class contains tests for the {@link CollisionCheck}.
 */
public class CollisionCheckTest {

    /**
     * Tests for collision with a lot of Colliding and not colliding Bricks.
     * Either manually created Bricks and Bricks and deterministic "randomly"
     * created Bricks.
     */
    @Test
    public void testCollisionCheck() {
        SimpleCircuit1Factory cf = new SimpleCircuit1Factory(true);
        Circuit c = cf.getCircuit();

        Or o1 = new Or(new Rectangle(32532, 2432, 7, 8));

        And a1 = cf.getAndA1().getNewInstance(
                new Point(cf.getAndA1().getPosition()));

        Check cc1 = new CollisionCheck(o1);
        assertTrue(cc1.perform(c).isPassed());

        Check cc2 = new CollisionCheck(a1);
        assertFalse(cc2.perform(c).isPassed());

        /* Test a lot of collisions. */
        int numberOfChecks = 200;
        for (int i = 0; i < numberOfChecks; i++) {
            AbstractCircuitFactory typeAFactory = new DFlipFlopFactory();
            AbstractCircuitFactory typeBFactory = new NorFactory();

            if (i % 3 == 0) {
                typeAFactory = new ClockFactory(2);
                typeBFactory = new DLatchFactory();
            } else if (i % 3 == 1) {
                typeAFactory = new FullAdderFactory();
            }

            ComponentType typeA = typeAFactory.getComponentTypeForCircuit();

            int x = i * typeA.getWidth();
            int y = i * typeA.getHeight();
            Component componentA = new ComponentImpl(typeA, new Point(x, y),
                    typeA.getName() + i);
            Log.gl().info(
                    "Adding " + typeA.getName() + "-" + i + " on " + x + "."
                            + y + " [" + typeA.getWidth() + ", "
                            + typeA.getHeight() + "].");
            c.addBrick(componentA);

            ComponentType typeB = typeBFactory.getComponentTypeForCircuit();
            int cx = x;
            int cy = y;
            int minWidth = Math.min(typeA.getWidth(), typeB.getWidth());
            int minHeight = Math.min(typeA.getHeight(), typeB.getHeight());
            if (i % 2 == 0) {
                cx = x;
                cy = y + (minHeight - 1);
            } else {
                cx = x + (minWidth - 1);
                cy = y + (minHeight - 1);
            }
            Log.gl().info(
                    "Conflicting " + typeB.getName() + "-" + i + " on " + cx
                            + "." + cy + " [" + typeB.getWidth() + ", "
                            + typeB.getHeight() + "].");
            Component componentB = new ComponentImpl(typeB, new Point(cx, cy),
                    typeB.getName() + i);

            Check check = new CollisionCheck(componentB);
            assertFalse(check.perform(c).isPassed());
        }
    }

    /**
     * Tests if the {@link Check} returns its name right.
     */
    @Test
    public void testCheckName() {
        Assert.assertEquals("Check.CollisionCheck", (new CollisionCheck(
                new And(new Rectangle()))).getName());
    }
}
