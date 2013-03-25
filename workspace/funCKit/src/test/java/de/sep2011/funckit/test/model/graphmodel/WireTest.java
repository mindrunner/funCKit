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
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import junit.framework.Assert;
import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Checks atomic operations on graphmodel in connection with wires. Therefore it
 * has to check for several {@link AccessPoint} or other graphmodel class
 * properties, too.
 */
public class WireTest {
    /**
     * Tests {@link AccessPoint}.getOther() for bijectivity.
     */
    @Test
    public void testReceivingOtherAccessPoint() {
        AccessPoint accessPoint1 = new AccessPointImpl(
                new And(new Rectangle()), new Point(1, 1), "a1");
        AccessPoint accessPoint2 = new AccessPointImpl(new Or(new Rectangle()),
                new Point(10, 10), "a2");
        Wire wire = new WireImpl(accessPoint1, accessPoint2);

        Assert.assertEquals(accessPoint1, wire.getFirstAccessPoint());
        Assert.assertEquals(accessPoint2, wire.getSecondAccessPoint());
        Assert.assertEquals(accessPoint2, wire.getOther(accessPoint1));
        Assert.assertEquals(accessPoint1, wire.getOther(accessPoint2));

        Assert.assertTrue(wire.getDimension().getWidth() > 0);
        Assert.assertTrue(wire.getDimension().getHeight() > 0);
    }

    /**
     * Performs positive and negative brick existence tests for both access
     * points of wire.
     */
    @Test
    public void testBrickExistence() {
        Brick brick1 = new And(new Rectangle());
        Brick brick2 = new IdPoint(new Rectangle());
        AccessPoint accessPoint1 = new AccessPointImpl(brick1, new Point(1, 1),
                "a1");
        AccessPoint accessPoint2 = new AccessPointImpl(brick2,
                new Point(10, 10), "a2");
        Wire wire = new WireImpl(accessPoint1, accessPoint2);

        Assert.assertEquals(brick1, wire.getFirstAccessPoint().getBrick());
        Assert.assertEquals(brick2, wire.getSecondAccessPoint().getBrick());
    }
}
