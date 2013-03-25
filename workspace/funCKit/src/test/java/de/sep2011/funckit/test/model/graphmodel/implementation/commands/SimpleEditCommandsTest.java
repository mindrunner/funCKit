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

import de.sep2011.funckit.circuitfactory.DFlipFlopFactory;
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Brick.Orientation;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.BrickWireDistinguishDispatcher;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.AddBrickCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.BareConnectCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ChangeBrickOrientationCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ChangeElementSizeCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.DisconnectCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ElementSetNameCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.MoveElementCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.RemoveWireCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.SetBrickDelayCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.SetSwitchValueCommand;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.CommandDispatcher;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This Class contains tests for various (mostly simple) {@link Command}s
 * editing parts of the graph model.
 */
public class SimpleEditCommandsTest {

    private And and;
    private Or or;
    private IdPoint idp;
    private Not not;
    private WireImpl wire;
    private SwitchImpl sw;
    private Light light;
    private ComponentImpl component;

    @Before
    public void setUp() {
        and = new And(new Rectangle(1, 8, 13, 15));
        or = new Or(new Rectangle(5, 6, 20, 20));
        idp = new IdPoint(new Rectangle(7, 8, 5, 5));
        not = new Not(new Rectangle(4, 7, 90, 90));
        wire = new WireImpl(not.getOutputO(), or.getInputA());
        sw = new SwitchImpl(new Rectangle(7, 9, 50, 60));
        light = new Light(new Rectangle(13, 6, 22, 33));
        component = new ComponentImpl(
                new DFlipFlopFactory().getComponentTypeForCircuit(), new Point(
                99, 96));

    }

    /**
     * Tests {@link SetSwitchValueCommand} by setting the value of the switch
     * und undo it.
     */
    @Test
    public void testSetSwitchValueCommand() {
        Circuit cir = new CircuitImpl();
        cir.addBrick(sw);
        sw.setValue(false);

        Command setSwValueCmd = new SetSwitchValueCommand(cir, sw, true);
        setSwValueCmd.execute();
        assertTrue(sw.getValue());
        setSwValueCmd.undo();
        assertFalse(sw.getValue());
        setSwValueCmd.execute();
        assertTrue(sw.getValue());
    }

    /**
     * Tests {@link SetBrickDelayCommand} by setting some delays on {@link
     * Brick}s and undo it.
     */
    @Test
    public void testSetBrickDelayCommand() {
        Circuit cir = new CircuitImpl();

        Gate[] gates = {and, or, idp, not};
        for (Gate g : gates) {
            cir.addBrick(g);
        }
        int[] delays = {0, 99, 88, 66};

        for (Gate g : gates) {
            g.setDelay(delays[0]);
        }

        for (int i = 1; i < delays.length; i++) {
            for (Gate g : gates) {
                SetBrickDelayCommand cmd = new SetBrickDelayCommand(g,
                        delays[i], cir);
                assertEquals(delays[i - 1], g.getDelay());
                cmd.execute();
                assertEquals(delays[i], g.getDelay());
                assertTrue(cmd.isExecuted());
                cmd.undo();
                assertEquals(delays[i - 1], g.getDelay());
                cmd.execute();
                assertEquals(delays[i], g.getDelay());
            }
        }

    }

    /**
     * Tests {@link ElementSetNameCommand} by setting various names on various
     * subtypes of {@link Element} and undo it.
     */
    @Test
    public void testElementSetNameCommand() {
        final Circuit cir = new CircuitImpl();
        Element[] elements = {and, or, idp, not, wire, sw, light, component};

        for (final Element elem : elements) {
            new BrickWireDistinguishDispatcher() {
                {
                    elem.dispatch(this);
                }

                @Override
                protected void visitWire(Wire w) {
                    cir.addWire(w);
                }

                @Override
                protected void visitBrick(Brick b) {
                    cir.addBrick(b);

                }
            };

        }

        String[] names = {"", "fooo", "bar", "blahh"};

        for (Element e : elements) {
            e.setName(names[0]);
        }

        for (int i = 1; i < names.length; i++) {
            for (Element e : elements) {
                ElementSetNameCommand cmd = new ElementSetNameCommand(e,
                        names[i], cir);
                assertEquals(names[i - 1], e.getName());
                cmd.execute();
                assertEquals(names[i], e.getName());
                assertTrue(cmd.isExecuted());
                cmd.undo();
                assertEquals(names[i - 1], e.getName());
                cmd.execute();
                assertEquals(names[i], e.getName());
            }
        }
    }

    /**
     * Tests {@link ChangeBrickOrientationCommand} by setting the {@link
     * Orientation} of various Brick and undo it.
     */
    @Test
    public void testChangeBrickOrientationCommand() {
        Brick[] bricks = {and, or, idp, not, sw, light, component};

        for (Brick b : bricks) {

            Orientation last = Orientation.WEST;
            b.setOrientation(last);

            for (Orientation o : Orientation.values()) {
                ChangeBrickOrientationCommand cmd = new ChangeBrickOrientationCommand(
                        b, o);
                assertEquals(last, b.getOrientation());
                cmd.execute();
                assertEquals(o, b.getOrientation());
                cmd.undo();
                assertEquals(last, b.getOrientation());
                cmd.execute();
                assertEquals(o, b.getOrientation());
                last = o;
            }

        }

    }

    /**
     * Tests {@link AddBrickCommand} by adding various subtypes of {@link Brick}
     * to the circuit. Checks if they are correctly added and removed on undo.
     */
    @Test
    public void testAddBrickCommand() {
        Brick[] bricks = {and, or, idp, not, sw, light, component};

        Circuit c = new SimpleCircuit1Factory(true).getCircuit();

        CommandDispatcher cd = new CommandDispatcher();

        for (Brick b : bricks) {
            assertFalse(c.getElements().contains(b));
            cd.dispatch(new AddBrickCommand(c, b));
            assertTrue(c.getElements().contains(b));
        }

        for (int i = 0; i < bricks.length; i++) {
            assertTrue(c.getElements().contains(bricks[bricks.length - i - 1]));
            cd.stepBack();
            assertFalse(c.getElements().contains(bricks[bricks.length - i - 1]));
        }

    }

    /**
     * Tests {@link MoveElementCommand} moving various subtypes of {@link
     * Element}. Checks if they are correctly moved and moved back on undo.
     */
    public void testMoveElement() {
        Brick[] bricks = {and, or, idp, not, sw, light, component};
        Point[] positions = {new Point(55, 88), new Point(0, 0),
                new Point(-99, -21312), new Point(-4, 5)};

        Circuit c = new SimpleCircuit1Factory(true).getCircuit();

        for (Brick b : bricks) {

            Point last = b.getBoundingRect().getLocation();

            for (Point p : positions) {
                MoveElementCommand cmd = new MoveElementCommand(b, p, c);
                assertEquals(last, b.getBoundingRect().getLocation());
                cmd.execute();
                assertEquals(p, b.getBoundingRect().getLocation());
                cmd.undo();
                assertEquals(last, b.getBoundingRect().getLocation());
                cmd.execute();
                assertEquals(p, b.getBoundingRect().getLocation());
                last = p;
            }
        }
    }

    /**
     * Tests {@link ChangeElementSizeCommand} changing the size of various
     * subtypes of {@link Element}. Checks if they are correctly sized and
     * resized to their normal positions undo.
     */
    @Test
    public void testResizeElement() {
        Brick[] bricks = {and, or, idp, not, sw, light, component};
        Dimension[] dimensions = {new Dimension(0, 0), new Dimension(333, 77),
                new Dimension(999883, 432423324), new Dimension()};

        Circuit c = new SimpleCircuit1Factory(true).getCircuit();

        for (Brick b : bricks) {

            Dimension last = b.getBoundingRect().getSize();

            for (Dimension d : dimensions) {
                ChangeElementSizeCommand cmd = new ChangeElementSizeCommand(b,
                        d, c);
                assertEquals(last, b.getBoundingRect().getSize());
                cmd.execute();
                assertEquals(d, b.getBoundingRect().getSize());
                cmd.undo();
                assertEquals(last, b.getBoundingRect().getSize());
                cmd.execute();
                assertEquals(d, b.getBoundingRect().getSize());
                last = d;
            }

        }
    }

    /**
     * Tests {@link BareConnectCommand} by connecting various {@link
     * AccessPoint} and check if wires exist between them and if they are
     * removed on undo. Also checks if the command reacts correctly on
     * connecting the same {@link AccessPoint} or on connecting two already
     * connected {@link AccessPoint}s.
     */
    @Test
    public void testBareConnectCommand() {
        Circuit c = new SimpleCircuit1Factory(true).getCircuit();
        c.addBrick(and);
        c.addBrick(or);

        Command cmd = new BareConnectCommand(c, and.getOutputO(),
                or.getInputA());

        cmd.execute();

        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        /* Try to connect already connected aps */
        Command cmd2 = new BareConnectCommand(c, and.getOutputO(),
                or.getInputA());
        cmd2.execute();
        Command cmd3 = new BareConnectCommand(c, or.getInputA(),
                and.getOutputO());
        cmd3.execute();

        /* Connect to same AP */
        Command cmd4 = new BareConnectCommand(c, or.getInputA(), or.getInputA());
        cmd4.execute();

        cmd.undo();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

        cmd.execute();
        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        cmd.undo();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

    }

    /**
     * Tests {@link DisconnectCommand} by disconnecting some {@link AccessPoint}
     * s which are connected and check if this correctly done and undone.
     */
    @Test
    public void testDisconnectCommand() {
        Circuit c = new SimpleCircuit1Factory(true).getCircuit();
        c.addBrick(and);
        c.addBrick(or);

        c.connect(and.getOutputO(), or.getInputA());

        Command cmd = new DisconnectCommand(c, and.getOutputO(), or.getInputA());

        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        cmd.execute();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

        cmd.undo();
        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        cmd.execute();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

        cmd.undo();

        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }
    }

    /**
     * like {@link #testDisconnectCommand()} but with the reverse order of the
     * {@link AccessPoint}s
     */
    @Test
    public void DisconnectCommandReverseApOrder() {
        Circuit c = new SimpleCircuit1Factory(true).getCircuit();
        c.addBrick(and);
        c.addBrick(or);

        c.connect(and.getOutputO(), or.getInputA());

        Command cmd = new DisconnectCommand(c, or.getInputA(), and.getOutputO());

        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        cmd.execute();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

        cmd.undo();
        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }
    }

    /**
     * Tests the {@link RemoveWireCommand} by checking if the wire is correctly
     * removed and readded on undo.
     */
    @Test
    public void RemoveWireCommandTest() {
        Circuit c = new SimpleCircuit1Factory(true).getCircuit();
        c.addBrick(and);
        c.addBrick(or);

        c.connect(and.getOutputO(), or.getInputA());

        Wire wire = and.getOutputO().getWires().iterator().next();

        Command cmd = new RemoveWireCommand(c, wire);

        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        cmd.execute();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

        cmd.undo();
        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

        cmd.execute();
        assertTrue(and.getOutputO().getWires().isEmpty());
        assertTrue(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));

        cmd.undo();

        assertFalse(and.getOutputO().getWires().isEmpty());
        assertFalse(or.getInputA().getWires().isEmpty());
        assertTrue(and.getOutputO().getWires()
                .equals(or.getInputA().getWires()));
        for (Wire w : and.getOutputO().getWires()) {
            assertEquals(w.getFirstAccessPoint(), and.getOutputO());
            assertEquals(w.getSecondAccessPoint(), or.getInputA());
        }

    }
}
