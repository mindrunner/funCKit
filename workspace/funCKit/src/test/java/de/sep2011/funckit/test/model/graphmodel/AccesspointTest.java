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
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.AccessPointImpl;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for {@link Wire}s and {@link AccessPoint}s.
 */
public class AccesspointTest {

    private final Brick andBrick = new And(new Point());
    private final AccessPoint accesspoint1 = new AccessPointImpl(andBrick,
            new Point(), "andbrick");

    private final Brick orBrick = new And(new Point());
    private final AccessPoint accesspoint2 = new AccessPointImpl(orBrick,
            new Point(), "orbrick");

    private final Point point1 = new Point();

    private Wire wire1;

    @Before
    public void setUp() {
        wire1 = new WireImpl(accesspoint1, accesspoint2, "bla");
        accesspoint1.addWire(wire1);
        accesspoint2.addWire(wire1);
        point1.x = 20;
        point1.y = 35;
    }

    /**
     * Tests if the Wires that are connected to the {@link AccessPoint} has are
     * all registered on the {@link AccessPoint}.
     */
    @Test
    public void testWire() { // BESSER AUSTESTEN

        Set<Wire> wireSet = accesspoint1.getWires();
        assertTrue(wireSet.contains(wire1));

        wireSet = accesspoint2.getWires();
        assertTrue(wireSet.contains(wire1));

        accesspoint1.removeWire(wire1);
        wireSet = accesspoint1.getWires();
        assertFalse(wireSet.contains(wire1));

        accesspoint2.removeWire(wire1);
        wireSet = accesspoint2.getWires();
        assertFalse(wireSet.contains(wire1));

        // System.out.println(accesspoint1.getWires());
    }

    /**
     * tests if the {@link AccessPoint} correctly returns its Brick.
     */
    @Test
    public void testBrick() { // BESSER!!
        assertTrue(accesspoint1.getBrick() == andBrick);

    }

    /**
     * tests if the {@link AccessPoint} correctly returns its Name.
     */
    @Test
    public void testName() {

        accesspoint1.setName("testName");
        assertTrue(accesspoint1.getName() == "testName");

        /**
         * tests the position of the {@link AccessPoint}.
         */
    }

    @Test
    public void testPosition() {
        accesspoint1.setPosition(point1);
        Point point2 = accesspoint1.getPosition();
        assertTrue(point1 == point2);
    }

}
