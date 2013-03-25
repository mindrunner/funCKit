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

package de.sep2011.funckit.test.factory.circuit;

import de.sep2011.funckit.circuitfactory.AbstractCircuitFactory;
import de.sep2011.funckit.circuitfactory.FullAdderFactory;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Creates a {@link Circuit} where some inputs and outputs are connected to each
 * other from the same type.
 */
public class InputAndOutputConnectedCircuitFactory extends
        AbstractCircuitFactory {

    private final SwitchImpl s1;
    private final SwitchImpl s2;
    private final And a1;
    private final And a2;
    private final Or o1;
    private final IdPoint id1;

    /**
     * Create a new {@link InputAndOutputConnectedCircuitFactory}
     */
    public InputAndOutputConnectedCircuitFactory() {
        circuit = new CircuitImpl();
        Circuit c = circuit;

        a1 = new And(new Rectangle(6, 7, 40, 40), "AND1");
        s1 = new SwitchImpl(new Rectangle(66, 77, 40, 40), "S1");
        s2 = new SwitchImpl(new Rectangle(-99, -77, 40, 40), "S2");
        SwitchImpl s3 = new SwitchImpl(new Rectangle(-999, -797, 40, 40), "S3");
        a2 = new And(new Rectangle(120, 120, 40, 40), "AND2");
        o1 = new Or(new Point(33, 44));
        id1 = new IdPoint(new Point(33, 77));
        Component comp1 = new ComponentImpl(
                new FullAdderFactory().getComponentTypeForCircuit());
        a1.setDelay(1);

        s1.toggle();
        s2.toggle();

        c.addBrick(a1);
        c.addBrick(a2);

        c.addBrick(s1);
        c.addBrick(s2);
        c.addBrick(s3);
        c.addBrick(comp1);
        c.connect(s1.getOutput("o"), a1.getInput("a"));
        c.connect(s2.getOutput("o"), a1.getInput("b"));
        c.connect(s3.getOutput("o"), a2.getInput("a"));

        c.connect(a1.getOutput("o"), a2.getInput("b"));

        // connect some inputs
        c.connect(a1.getInputA(), a1.getInputB());
        c.connect(o1.getInputB(), id1.getInputA());
        c.connect(o1.getInputA(), a1.getInputB());

        // connect some outputs
        c.connect(s1.getOutputO(), a2.getOutputO());
        c.connect(s2.getOutputO(), o1.getOutputO());
        c.connect(a1.getOutputO(), a1.getOutputO());
    }

    public Brick[] getConnectedOutputBricks() {
        Brick[] bs = { s1, a2, s2, o1, a1 };
        return bs;
    }

    public Brick[] getConnectedInputBricks() {
        Brick[] bs = { a1, o1, id1, a1 };
        return bs;
    }

}
