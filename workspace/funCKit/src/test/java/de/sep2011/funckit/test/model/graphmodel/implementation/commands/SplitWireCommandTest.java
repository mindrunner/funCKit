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
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.SplitWireCommand;
import de.sep2011.funckit.util.command.Command;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link SplitWireCommand}.
 */
public class SplitWireCommandTest {

    private Or or;
    private IdPoint idp;
    private Not not;
    private WireImpl wireIO;
    private WireImpl wireOO;
    private WireImpl wireII;

    @Before
    public void setUp() {
        or = new Or(new Rectangle(5, 6, 20, 20));
        idp = new IdPoint(new Rectangle(7, 8, 5, 5));
        not = new Not(new Rectangle(4, 7, 90, 90));
        wireIO = new WireImpl(not.getOutputO(), or.getInputA());
        wireOO = new WireImpl(not.getOutputO(), or.getOutputO());
        wireII = new WireImpl(not.getInputA(), or.getInputA());

    }

    /**
     * Tests splitting a wire which connects an {@link Input} with an
     * {@link Output}.
     */
    @Test
    public void testInputOutput() {
        Circuit cir = new CircuitImpl();
        cir.addBrick(not);
        cir.addBrick(or);
        cir.addWire(wireIO);

        Command splitCmd = new SplitWireCommand(cir, wireIO, idp);
        splitCmd.execute();
        assertEquals(not.getOutputO(), idp.getInputA().getWires().iterator()
                .next().getOther(idp.getInputA()));
        assertEquals(or.getInputA(), idp.getOutputO().getWires().iterator()
                .next().getOther(idp.getOutputO()));
        assertFalse(cir.getElements().contains(wireIO));

        splitCmd.undo();
        assertEquals(0, idp.getInputA().getWires().size());
        assertEquals(0, idp.getOutputO().getWires().size());
        assertEquals(or.getInputA(), not.getOutputO().getWires().iterator()
                .next().getOther(not.getOutputO()));
        assertTrue(cir.getElements().contains(wireIO));

        splitCmd.execute();
        assertEquals(not.getOutputO(), idp.getInputA().getWires().iterator()
                .next().getOther(idp.getInputA()));
        assertEquals(or.getInputA(), idp.getOutputO().getWires().iterator()
                .next().getOther(idp.getOutputO()));
        assertFalse(cir.getElements().contains(wireIO));

    }

    /**
     * Tests splitting a wire which connects two {@link Output}s.
     */
    @Test
    public void testOutputOutput() {
        Circuit cir = new CircuitImpl();
        cir.addBrick(not);
        cir.addBrick(or);
        cir.addWire(wireOO);

        Command splitCmd = new SplitWireCommand(cir, wireOO, idp);
        splitCmd.execute();

        AccessPoint notWireOther = not.getOutputO().getWires().iterator()
                .next().getOther(not.getOutputO());
        AccessPoint orWireOther = or.getOutputO().getWires().iterator().next()
                .getOther(or.getOutputO());

        assertTrue(notWireOther.equals(idp.getInputA())
                || notWireOther.equals(idp.getOutputO()));
        assertTrue(orWireOther.equals(idp.getInputA())
                || orWireOther.equals(idp.getOutputO()));
        assertFalse(cir.getElements().contains(wireOO));

        splitCmd.undo();
        assertEquals(0, idp.getInputA().getWires().size());
        assertEquals(0, idp.getOutputO().getWires().size());
        assertEquals(or.getOutputO(), not.getOutputO().getWires().iterator()
                .next().getOther(not.getOutputO()));
        assertTrue(cir.getElements().contains(wireOO));

        splitCmd.execute();

        AccessPoint notWireOther2 = not.getOutputO().getWires().iterator()
                .next().getOther(not.getOutputO());
        AccessPoint orWireOther2 = or.getOutputO().getWires().iterator().next()
                .getOther(or.getOutputO());

        assertTrue(notWireOther2.equals(idp.getInputA())
                || notWireOther2.equals(idp.getOutputO()));
        assertTrue(orWireOther2.equals(idp.getInputA())
                || orWireOther2.equals(idp.getOutputO()));
        assertFalse(cir.getElements().contains(wireOO));
    }

    /**
     * Tests splitting a wire which connects two {@link Input}s.
     */
    @Test
    public void testInputInput() {
        Circuit cir = new CircuitImpl();
        cir.addBrick(not);
        cir.addBrick(or);
        cir.addWire(wireII);

        Command splitCmd = new SplitWireCommand(cir, wireII, idp);
        splitCmd.execute();

        AccessPoint notWireOther = not.getInputA().getWires().iterator().next()
                .getOther(not.getInputA());
        AccessPoint orWireOther = or.getInputA().getWires().iterator().next()
                .getOther(or.getInputA());

        assertTrue(notWireOther.equals(idp.getInputA())
                || notWireOther.equals(idp.getOutputO()));
        assertTrue(orWireOther.equals(idp.getInputA())
                || orWireOther.equals(idp.getOutputO()));
        assertFalse(cir.getElements().contains(wireII));

        splitCmd.undo();
        assertEquals(0, idp.getInputA().getWires().size());
        assertEquals(0, idp.getOutputO().getWires().size());
        assertEquals(or.getInputA(), not.getInputA().getWires().iterator()
                .next().getOther(not.getInputA()));
        assertTrue(cir.getElements().contains(wireII));

        splitCmd.execute();

        AccessPoint notWireOther2 = not.getInputA().getWires().iterator()
                .next().getOther(not.getInputA());
        AccessPoint orWireOther2 = or.getInputA().getWires().iterator().next()
                .getOther(or.getInputA());

        assertTrue(notWireOther2.equals(idp.getInputA())
                || notWireOther2.equals(idp.getOutputO()));
        assertTrue(orWireOther2.equals(idp.getInputA())
                || orWireOther2.equals(idp.getOutputO()));
        assertFalse(cir.getElements().contains(wireII));
    }

    /**
     * Tests if double execute() throws {@link IllegalStateException}.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionExecute() {
        Circuit cir = new CircuitImpl();
        cir.addBrick(not);
        cir.addBrick(or);
        cir.addWire(wireII);

        Command splitCmd = new SplitWireCommand(cir, wireII, idp);
        splitCmd.execute();
        splitCmd.execute();
    }

    /**
     * Tests if undo() before execute() throws {@link IllegalStateException}.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionUndo() {
        Circuit cir = new CircuitImpl();
        cir.addBrick(not);
        cir.addBrick(or);
        cir.addWire(wireII);

        Command splitCmd = new SplitWireCommand(cir, wireII, idp);
        splitCmd.undo();
    }

}
