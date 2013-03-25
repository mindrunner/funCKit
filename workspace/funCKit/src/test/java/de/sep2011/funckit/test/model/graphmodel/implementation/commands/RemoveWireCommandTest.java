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

package de.sep2011.funckit.test.model.graphmodel.implementation.commands;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.RemoveWireCommand;
import de.sep2011.funckit.util.command.Command;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.LinkedHashSet;
import java.util.Set;

import static de.sep2011.funckit.util.Log.gl;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Combines several tests concentrated on {@link RemoveWireCommand}, including
 * negative tests by simulating possible error cases.
 */
public class RemoveWireCommandTest {
    /**
     * Tests removing several wires on same or different {@link AccessPoint} of
     * a circuit with some bricks of different types.
     */
    @Test
    public void testRemoveWire() {

    }

    /**
     * Tests removing wire, performing other changes and undoing removing.
     */
    @Test
    public void testRemoveAndUndoWire() {

    }

    /**
     * Tests several negative tests, that should throw exceptions or do nothing
     * as between several commands some get undone without a master command
     * dispatcher object or other changes were directly applied on circuit, e.g.
     * bricks get removed.
     */
    @Test
    public void testRemoveAndUndoWireNegativeTests() {

    }

    @Deprecated
    @Test
    public void testRemoveWireCommand() {
        gl().info("testRemoveWireCommand()");

        /* Circuit to run the remove command on. */
        Circuit testCircuit = getCommonForRemoveWireCommandTest();

        /* Create expected object. */
        Circuit expected = getCommonForRemoveWireCommandTest();

        /*
         * Add new bricks to both circuits, as they must have same circuit
         * elements.
         */
        Brick brick1 = new And(new Rectangle(40, 40, 40, 40), "AND a");
        Brick brick2 = new And(new Rectangle(40, 40, 40, 40), "AND b");
        testCircuit.addBrick(brick1);
        testCircuit.addBrick(brick2);
        expected.addBrick(brick1);
        expected.addBrick(brick2);

        /* Only connect bricks on test circuit. */
        AccessPoint a1 = brick1.getOutput("o");
        AccessPoint a2 = brick1.getInput("b");
        testCircuit.connect(a1, a2);

        /* Execute concrete command on object to receive actual result object. */
        for (Wire w : a1.getWires()) {
            Command command = new RemoveWireCommand(testCircuit, w);
            command.execute();
        }

        assertEquals(testCircuit.getElements().size(), expected.getElements()
                .size());
        assertTrue(testCircuit.equalGraph(expected));
    }

    private static Circuit getCommonForRemoveWireCommandTest() {
        Circuit commonBaseCircuit = new CircuitImpl();
        Brick brick1 = new And(new Rectangle(0, 0, 40, 40), "AND1");
        Brick brick2 = new And(new Rectangle(10, 10, 40, 40), "AND2");
        Brick brick3 = new And(new Rectangle(20, 20, 40, 40), "AND3");
        Brick brick4 = new And(new Rectangle(30, 30, 40, 40), "AND4");

        commonBaseCircuit.addBrick(brick1);
        commonBaseCircuit.addBrick(brick2);
        commonBaseCircuit.addBrick(brick3);
        commonBaseCircuit.addBrick(brick4);
        commonBaseCircuit.connect(brick1.getOutput("o"), brick2.getInput("a"));
        commonBaseCircuit.connect(brick2.getOutput("o"), brick3.getInput("a"));
        commonBaseCircuit.connect(brick3.getOutput("o"), brick4.getInput("a"));

        return commonBaseCircuit;
    }

    @Deprecated
    @Test
    public void testRemoveWireAndUndoCommand() {
        gl().info("testRemoveWireAndUndoCommand()");

        /* Circuit to run the remove command on. */
        Circuit testCircuit = getCommonForRemoveWireCommandTest();

        /* Create expected object. */
        Circuit expected = getCommonForRemoveWireCommandTest();

        /*
         * Add new bricks to both circuits, as they must have same circuit
         * elements.
         */
        Brick brick1 = new And(new Rectangle(40, 40, 40, 40), "AND a");
        Brick expectedBrick1 = new And(new Rectangle(40, 40, 40, 40), "AND a");
        Brick brick2 = new And(new Rectangle(40, 40, 40, 40), "AND b");
        Brick expectedBrick2 = new And(new Rectangle(40, 40, 40, 40), "AND b");
        testCircuit.addBrick(brick1);
        testCircuit.addBrick(brick2);
        expected.addBrick(expectedBrick1);
        expected.addBrick(expectedBrick2);

        /* Connect bricks on both circuits. */
        AccessPoint a1 = brick1.getOutput("o");
        AccessPoint a2 = brick1.getInput("b");
        testCircuit.connect(a1, a2);
        AccessPoint expectedA1 = expectedBrick1.getOutput("o");
        AccessPoint expectedA2 = expectedBrick1.getInput("b");
        expected.connect(expectedA1, expectedA2);

        /* Execute concrete command on object to receive actual result object. */
        Set<Wire> wires = new LinkedHashSet<Wire>(a1.getWires());
        for (Wire w : wires) {
            int size = testCircuit.getElements().size();
            Command command = new RemoveWireCommand(testCircuit, w);
            command.execute();
            assertEquals(testCircuit.getElements().size(), size - 1);
            command.undo();
        }

        assertEquals(testCircuit.getElements().size(), expected.getElements()
                .size());
        assertTrue(testCircuit.equalGraph(expected));
    }
}
