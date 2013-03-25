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

import java.awt.Point;
import java.awt.Rectangle;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.sep2011.funckit.circuitfactory.ClockFactory;
import de.sep2011.funckit.circuitfactory.DFlipFlopFactory;
import de.sep2011.funckit.circuitfactory.NandFactory;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.AddBrickCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.RemoveBrickCommand;
import de.sep2011.funckit.util.command.CommandDispatcher;

/**
 * Tests cases related to AddBrickCommand from graphmodel. As this is no atomic
 * command (or rather operation or method), it tests not only adding simple
 * bricks, but checks if the command behaves correctly (in its specified
 * approach). E.g. if references of components work.
 */
public class AddBrickCommandTest {
    private Circuit circuit;
    private CommandDispatcher dispatcher;
    private int elementCount;

    @Before
    public void setUp() {
        /* Create circuit and dispatcher all tests are working with. */
        circuit = new CircuitImpl();
        dispatcher = new CommandDispatcher();
        elementCount = circuit.getElements().size();
    }

    /**
     * Tests adding several simple bricks and checks if circuit object has
     * changed in the desired way. Depends on atomic tests for graph model and
     * only tests higher-level commands.
     */
    @Test
    public void testAddBrick() {
        Circuit expected = new CircuitImpl();

        /* Add simple and. */
        And and = new And(new Rectangle(10, 10, 50, 50));
        expected.addBrick(and);
        AddBrickCommand addCommand1 = new AddBrickCommand(circuit, and);
        dispatcher.dispatch(addCommand1);
        Assert.assertTrue(circuit.getElements().contains(and));
        Assert.assertTrue(circuit.getElements().size() == ++elementCount);
        Assert.assertEquals(expected.getElements(), circuit.getElements());

        /* Add light. */
        Light light = new Light(new Rectangle(10, 10, 50, 50));
        expected.addBrick(light);
        AddBrickCommand addLightCommand = new AddBrickCommand(circuit, light);
        dispatcher.dispatch(addLightCommand);
        Assert.assertTrue(circuit.getElements().contains(light));
        Assert.assertTrue(circuit.getElements().size() == ++elementCount);
        Assert.assertEquals(expected.getElements(), circuit.getElements());

        /* Add a huge amount of bricks. */
        for (int i = 0; i < 1000; i++) {
            /* Add or. */
            Or or = new Or(new Rectangle(10, 10, 50, 50));
            expected.addBrick(or);
            AddBrickCommand addOrCommand = new AddBrickCommand(circuit, or);
            dispatcher.dispatch(addOrCommand);
            Assert.assertTrue(circuit.getElements().contains(or));
            Assert.assertTrue(circuit.getElements().size() == ++elementCount);
            Assert.assertEquals(expected.getElements(), circuit.getElements());
        }

        /* Add clock component. */
        ClockFactory factory = new ClockFactory(2);
        Component clock = new ComponentImpl(
                factory.getComponentTypeForCircuit(), new Point(10, 100));
        expected.addBrick(clock);
        AddBrickCommand addCommand2 = new AddBrickCommand(circuit, clock);
        dispatcher.dispatch(addCommand2);
        Assert.assertTrue(circuit.getElements().contains(clock));
        Assert.assertTrue(circuit.getElements().size() == ++elementCount);
        Assert.assertEquals(expected.getElements(), circuit.getElements());
    }

    /**
     * Extends see {@link #testAddBrick()} by checking if an
     * {@link AddBrickCommand} can be undone properly. Undoing AddBrickCommands
     * should remove previously added brick. It also checks if wires were
     * connected to brick in meantime and should remove them, too! In this
     * connection a command dispatcher object is used.
     */
    @Test
    public void testAddAndUndoBrick() {
        Circuit expected = new CircuitImpl();

        /* Add nand-component. */
        NandFactory factory = new NandFactory();
        Component nand = new ComponentImpl(
                factory.getComponentTypeForCircuit(), new Point(10, 100));
        expected.addBrick(nand);
        AddBrickCommand addCommand1 = new AddBrickCommand(circuit, nand);
        dispatcher.dispatch(addCommand1);
        Assert.assertTrue(circuit.getElements().contains(nand));
        Assert.assertTrue(circuit.getElements().size() == ++elementCount);
        Assert.assertEquals(expected.getElements(), circuit.getElements());

        /* Remove nand-component. */
        expected.removeBrick(nand);
        RemoveBrickCommand removeCommand1 = new RemoveBrickCommand(circuit,
                nand);
        dispatcher.dispatch(removeCommand1);
        Assert.assertFalse(circuit.getElements().contains(nand));
        Assert.assertTrue(circuit.getElements().size() == --elementCount);
        Assert.assertEquals(expected.getElements(), circuit.getElements());

        /* Add a huge amount of bricks. */
        int elementsToAdd = 500;
        for (int i = 0; i < elementsToAdd; i++) {
            /* Add or. */
            Or or = new Or(new Rectangle(i * 10, 10, 50, 50));
            expected.addBrick(or);
            AddBrickCommand addOrCommand = new AddBrickCommand(circuit, or);
            dispatcher.dispatch(addOrCommand);
            Assert.assertTrue(circuit.getElements().contains(or));
            Assert.assertTrue(circuit.getElements().size() == ++elementCount);
            Assert.assertEquals(expected.getElements(), circuit.getElements());
        }

        int sizeBeforeAddingAndRemoving = circuit.getElements().size();

        /* Add a huge amount of bricks and immediately delete them. */
        int elementsToAddAndRemove = 1000;
        for (int i = 0; i < elementsToAddAndRemove; i++) {
            /* Add switch. */
            Switch s = new SwitchImpl(new Rectangle(10, i * 10, 50, 50));
            expected.addBrick(s);
            AddBrickCommand addSwitchCommand = new AddBrickCommand(circuit, s);
            dispatcher.dispatch(addSwitchCommand);
            Assert.assertTrue(circuit.getElements().contains(s));
            Assert.assertTrue(circuit.getElements().size() == ++elementCount);
            Assert.assertEquals(expected.getElements(), circuit.getElements());

            /* And remove it! */
            expected.removeBrick(s);
            dispatcher.stepBack();
            Assert.assertFalse(circuit.getElements().contains(s));
            Assert.assertTrue(circuit.getElements().size() == --elementCount);
            Assert.assertEquals(expected.getElements(), circuit.getElements());
        }

        Assert.assertEquals(sizeBeforeAddingAndRemoving, circuit.getElements()
                .size());
    }

    /**
     * Adds more complex components of same or different component types to
     * circuit and checks if all references of elements work properly after
     * adding.
     */
    @Test
    public void testAddComponents() {
        Circuit expected = new CircuitImpl();

        /* Add dFlipFlop component. */
        DFlipFlopFactory factory = new DFlipFlopFactory();
        Component dFlipFlop = new ComponentImpl(
                factory.getComponentTypeForCircuit(), new Point(-3245, 764));
        expected.addBrick(dFlipFlop);
        AddBrickCommand addCommand2 = new AddBrickCommand(circuit, dFlipFlop);
        dispatcher.dispatch(addCommand2);
        Assert.assertTrue(circuit.getElements().contains(dFlipFlop));
        Assert.assertTrue(circuit.getElements().size() == ++elementCount);
        Assert.assertEquals(expected.getElements(), circuit.getElements());
    }

    /**
     * Adds several complex components of same and / or different component
     * types to circuit object, checks references, connects different bricks and
     * then performs several undo-operations on previously used add-brick-
     * commands to finally check if all is set back properly and references
     * still work fine.
     */
    @Test
    public void testAddAndUndoComponents() {

    }
}
