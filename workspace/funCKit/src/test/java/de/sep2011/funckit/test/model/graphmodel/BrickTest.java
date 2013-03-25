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

import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;

import junit.framework.Assert;

import org.junit.Test;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Brick.Orientation;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.And;

/**
 * Checks certain operations all bricks have in common. E.g. defining bounding
 * rectangles, delays or receiving {@link Input}s and {@link Output}s.
 */
public class BrickTest {

    private final Brick brick = new And(new Point());

    /**
     * Sets the Orientation in different directions and checks if the
     * Orientation has changed.
     */
    @Test
    public void testOrientation() {
        Orientation orientation = Orientation.NORTH;
        for (int i = 0; i < 4; i++) {
            brick.setOrientation(orientation);
            Orientation compareOrientation = brick.getOrientation();
            // System.out.println(orientation == compareOrientation);
            boolean value = orientation == compareOrientation;
            assertTrue(value);
            orientation = changeOrientation(orientation);
        }
    }

    /**
     * Checks if some Bricks has delay, changes delays and tests if the
     * functionality worked.
     */
    @Test
    public void testDelay() {
        for (int i = 0; i < 10; i++) {
            brick.setDelay(i);
            assertTrue(brick.getDelay() == i);
            assertTrue(brick.hasDelay() == i > 0);
        }

    }

    private static Orientation changeOrientation(Orientation o) {
        if (o == Orientation.NORTH) {
            return Orientation.EAST;
        } else if (o == Orientation.EAST) {
            return Orientation.SOUTH;
        } else if (o == Orientation.SOUTH) {
            return Orientation.WEST;
        } else {
            return Orientation.NORTH;

        }
    }

    /**
     * Tests for null on {@link Brick#getInput(String)} and
     * {@link Brick#getInput(String)} for non-existing {@link AccessPoint}s.
     */
    @Test
    public void testGetInputOutput() {
        Assert.assertNull(brick.getInput("NOT THERE"));
        Assert.assertNull(brick.getOutput("NOT THERE"));
    }

    /**
     * Test if {@link Brick#intersects(java.awt.geom.Rectangle2D)} works
     * correctly.
     */
    @Test
    public void testIntersects() {
        brick.setBoundingRect(new Rectangle(50, 50));
        brick.setPosition(new Point(10, 10));
        Assert.assertTrue(brick.intersects(new Rectangle(0, 0, 20, 20)));
    }
}
