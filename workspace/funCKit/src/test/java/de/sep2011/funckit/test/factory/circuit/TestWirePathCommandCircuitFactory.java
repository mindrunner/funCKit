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
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.CreateWirePathCommand;

import java.awt.Point;

/**
 * Helper Factory to generate a Circuit for {@link CreateWirePathCommand}.
 */
public class TestWirePathCommandCircuitFactory extends AbstractCircuitFactory {

    private final And a1;
    private final Or o1;
    private final IdPoint lonelyIdPoint;
    private final IdPoint onlyToInputConnectedIdPoint;
    private final IdPoint onlyToOutputConnectedIdPoint;
    private final WireImpl w1;

    public TestWirePathCommandCircuitFactory() {
        circuit = new CircuitImpl();
        a1 = new And(new Point(22, 55));
        Not n1 = new Not(new Point(99, 88));
        Not n2 = new Not(new Point(99, 88));
        w1 = new WireImpl(n1.getOutputO(), n2.getInputA());
        circuit.addWire(w1);
        o1 = new Or(new Point(22, 55));
        lonelyIdPoint = new IdPoint(new Point(44, 676));
        onlyToInputConnectedIdPoint = new IdPoint(new Point(445, 676));
        onlyToOutputConnectedIdPoint = new IdPoint(new Point(4453, 656));

        circuit.connect(onlyToInputConnectedIdPoint.getOutputO(),
                a1.getInputA());
        circuit.connect(onlyToOutputConnectedIdPoint.getInputA(),
                o1.getOutputO());

        circuit.addBrick(a1);
        circuit.addBrick(o1);

    }

    public And getA1() {
        return a1;
    }

    public Or getO1() {
        return o1;
    }

    public IdPoint getLonelyIdPoint() {
        return lonelyIdPoint;
    }

    public IdPoint getOnlyToInputConnectedIdPoint() {
        return onlyToInputConnectedIdPoint;
    }

    public IdPoint getOnlyToOutputConnectedIdPoint() {
        return onlyToOutputConnectedIdPoint;
    }

    public Wire getWire() {
        return w1;
    }

}
