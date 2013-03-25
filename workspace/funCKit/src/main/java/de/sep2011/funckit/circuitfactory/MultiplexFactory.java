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

package de.sep2011.funckit.circuitfactory;

import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

import java.awt.Point;
import java.awt.Rectangle;

public class MultiplexFactory extends AbstractCircuitFactory {

    private final And and1;
    private final And and2;
    private final Not not;
    private final Or or;
    private final IdPoint idp1;
    private final IdPoint idp2;
    private final IdPoint idp3;

    public MultiplexFactory(int delay, int defaultWidth, int defaultHeight) {
        circuit = new CircuitImpl();

        idp1 = new IdPoint(new Point(100, 100));
        idp2 = new IdPoint(new Point(100, 50));
        idp3 = new IdPoint(new Point(100, 400));
        and1 = new And(
                new Rectangle(200, 100, defaultWidth, defaultHeight),
                "AND1-Multi"
        );
        and2 = new And(
                new Rectangle(200, 200, defaultWidth, defaultHeight),
                "AND2-Multi"
        );
        not = new Not(
                new Rectangle(100, 300, defaultWidth, defaultHeight),
                "Not-Multi"
        );
        or = new Or(
                new Rectangle(300, 350, defaultWidth, defaultHeight),
                "Or-Multi"
        );
        not.setDelay(delay);


        circuit.addBrick(idp1);
        circuit.addBrick(idp2);
        circuit.addBrick(idp3);
        circuit.addBrick(and1);
        circuit.addBrick(and2);
        circuit.addBrick(not);
        circuit.addBrick(or);


        Wire w1 = circuit.connect(idp1.getOutputO(), and1.getInputA());
        w1.setName("ID1-And2-Wire");
        Wire w2 = circuit.connect(idp1.getOutputO(), not.getInputA());
        w2.setName("ID1-Not-Wire");
        Wire w3 = circuit.connect(not.getOutputO(), and2.getInputA());
        w3.setName("Not-And1-Wire");
        Wire w4 = circuit.connect(and1.getOutputO(), or.getInputA());
        w4.setName("And1-Or-Wire");
        Wire w5 = circuit.connect(and2.getOutputO(), or.getInputB());
        w5.setName("And2-Or-Wire");
        Wire w6 = circuit.connect(idp2.getOutputO(), and1.getInputB());
        w6.setName("ID2-And1-Wire");
        Wire w7 = circuit.connect(idp3.getOutputO(), and2.getInputB());
        w7.setName("ID3-And2-Wire");
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = new ComponentTypeImpl(circuit, "Multiplexer");
        type.setHeight(40);
        type.setWidth(40);
        type.getInputs().add(idp1.getInputA());
        type.getInputs().add(idp2.getInputA());
        type.getInputs().add(idp3.getInputA());
        type.getOutputs().add(or.getOutputO());
        type.setName(idp1.getInputA(), "s");
        type.setName(idp2.getInputA(), "a");
        type.setName(idp3.getInputA(), "b");
        type.setName(or.getOutputO(), "o");
        return type;

    }

}
