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

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.AccessPointImpl;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Checks constraints a circuit object must fulfill. E.g. atomic operations like
 * adding elements has to work properly. Receiving elements in ranges or certain
 * points has to work, too and getter methods should not return null values.
 */
public class CircuitTest {

    private Circuit testCircuit;
    private Point point1;
    private Point point2;
    private Brick testBrick;

    /**
     * Creates a test Circuit for each test to operate on.
     */
    @Before
    public void setUp() {

        testCircuit = new CircuitImpl();
        point1 = new Point();
        point2 = new Point();
        Dimension dimension = new Dimension();

        point1.x = 30;
        point1.y = 22;

        point2.x = 2 * point1.x;
        point2.y = 2 * point1.y;

        dimension.width = 12;
        dimension.height = 15;
        testBrick = new And(point1);
        testBrick.setDimension(dimension);

        Brick testBrick2 = new Or(point2);
        testBrick2.setDimension(dimension);
    }

    /**
     * tests the method {@link Circuit#addBrick(Brick)}.
     */
    @Test
    public void testAddBrick() {
        testCircuit.addBrick(testBrick);
        for (int i = point1.x; i < (point1.x + (testBrick.getDimension()).width); i++) {
            for (int j = point1.y; j < (point1.y + (testBrick.getDimension()).height); j++) {
                point2.x = i;
                point2.y = j;

                Assert.assertTrue(testCircuit.getBrickAtPosition(point2) == testBrick);

            }
        }
        point2.x = point1.x - 1;
        Assert.assertFalse(testCircuit.getBrickAtPosition(point2) == testBrick);

        point2.y = point1.y + (testBrick.getDimension()).height + 1;
        Assert.assertFalse(testCircuit.getBrickAtPosition(point2) == testBrick);

    }

    /**
     * tests the method {@link Circuit#addWire(Wire)}.
     */
    @Test
    public void testAddWire() {
    	Wire wire = new WireImpl(new AccessPointImpl(testBrick, new Point(), "a"),
    			new AccessPointImpl(testBrick, new Point(), "b"));
        testCircuit.addWire(wire);
        Assert.assertTrue(testCircuit.getElements().contains(wire));
        testCircuit.removeWire(wire);
        Assert.assertFalse(testCircuit.getElements().contains(wire));
    }

    /**
     * Tests the method {@link Circuit#connect(AccessPoint, AccessPoint)}.
     */
    @Test
    public void testConnect() {
        Brick brick1 = new And(new Rectangle());
        AccessPoint accessPoint1 = new AccessPointImpl(brick1, new Point(5, 5),
                "a1");
        Brick brick2 = new And(new Rectangle());
        AccessPoint accessPoint2 = new AccessPointImpl(brick2, new Point(3, 3),
                "a2");
        Circuit circuit = new CircuitImpl();
        circuit.connect(accessPoint1, accessPoint2);
        Assert.assertTrue(circuit.getElements().size() == 1);
        Element element = circuit.getElements().iterator().next();
        Assert.assertTrue(element instanceof Wire);
        Wire wire = (Wire) element;
        Assert.assertEquals(accessPoint1, wire.getFirstAccessPoint());
        Assert.assertEquals(accessPoint2, wire.getSecondAccessPoint());
        Assert.assertEquals(brick1, wire.getFirstAccessPoint().getBrick());
        Assert.assertEquals(brick2, wire.getSecondAccessPoint().getBrick());
    }

    /**
     * Tests the method {@link Circuit#removeBrick(Brick)}.
     */
    @Test
    public void testRemoveBrick() {
        Brick brick = new And(new Rectangle(5, 5));
        Circuit circuit = new CircuitImpl();
        circuit.addBrick(brick);
        Assert.assertTrue(circuit.getElements().size() == 1);
        Assert.assertTrue(circuit.getElements().contains(brick));
        circuit.removeBrick(brick);
        Assert.assertTrue(circuit.getElements().size() == 0);
        Assert.assertFalse(circuit.getElements().contains(brick));
    }

    /**
     * Tests the method {@link Circuit#disconnect(AccessPoint, AccessPoint)}..
     */
    @Test
    public void testDisconnect() {
        Brick brick1 = new And(new Rectangle());
        AccessPoint accessPoint1 = new AccessPointImpl(brick1, new Point(5, 5),
                "a1");
        Brick brick2 = new And(new Rectangle());
        AccessPoint accessPoint2 = new AccessPointImpl(brick2, new Point(3, 3),
                "a2");
        Circuit circuit = new CircuitImpl();
        circuit.connect(accessPoint1, accessPoint2);
        Assert.assertTrue(circuit.getElements().size() == 1);
        Assert.assertTrue(accessPoint1.getWires().size() == 1);
        Assert.assertTrue(accessPoint2.getWires().size() == 1);
        Element element = circuit.getElements().iterator().next();
        Assert.assertTrue(element instanceof Wire);
        Wire wire = (Wire) element;

        circuit.disconnect(accessPoint1, accessPoint2);
        Assert.assertTrue(circuit.getElements().size() == 0);
        Assert.assertFalse(accessPoint1.getWires().contains(wire));
        Assert.assertFalse(accessPoint2.getWires().contains(wire));
        Assert.assertTrue(accessPoint1.getWires().size() == 0);
        Assert.assertTrue(accessPoint2.getWires().size() == 0);
    }

    /**
     * tests the method {@link Circuit#removeWire(Wire)}.
     */
    @Test
    public void testRemoveWire() {
    	Wire wire = new WireImpl(new AccessPointImpl(testBrick, new Point(), "a"),
    			new AccessPointImpl(testBrick, new Point(), "b"));
        testCircuit.addWire(wire);
        Assert.assertTrue(testCircuit.getElements().contains(wire));
        testCircuit.removeWire(wire);
        Assert.assertFalse(testCircuit.getElements().contains(wire));
    }

    /**
     * tests the method {@link Circuit#getBrickAtPosition(Point)}.
     */
    @Test
    public void testGetBrickAtPosition() {
        testAddBrick();  // TESTED IN addBrick
    }

    /**
     * tests the method {@link Circuit#getIntersectingElements(Rectangle)}.
     */
    @Test
    public void testGetIntersectingElements() {
    	testCircuit.addBrick(testBrick);
		Assert.assertTrue(testCircuit.getIntersectingElements(
				new Rectangle(point1, new Dimension(10, 10))).contains(testBrick));
	}

    /**
     * tests the method {@link Circuit#getElements()}.
     */
    @Test
    public void testGetElements() {
        Assert.assertNotNull(testCircuit.getElements());
    }
}
