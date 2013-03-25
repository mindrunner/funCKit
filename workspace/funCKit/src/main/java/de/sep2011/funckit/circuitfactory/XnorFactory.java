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

public class XnorFactory extends AbstractCircuitFactory {

    private final int delay;
    private final IdPoint idp1;
    private final IdPoint idp2;
    private final Or or;

    public XnorFactory() {
        this(0);
    }

    private XnorFactory(int delay) {
        this.delay = delay;
        circuit = new CircuitImpl();

        idp1 = new IdPoint(new Point(100, 100));
        idp2 = new IdPoint(new Point(100, 300));
        Not not1 = new Not(new Rectangle(200, 100, 40, 40), "Not1-Xnor");
        Not not2 = new Not(new Rectangle(200, 150, 40, 40), "Not2-Xnor");
        And and1 = new And(new Rectangle(300, 100, 40, 40), "And1-Xnor");
        And and2 = new And(new Rectangle(300, 300, 40, 40), "And2-Xnor");
        or = new Or(new Rectangle(400, 150, 40, 40), "Or-Xnor");

        not1.setDelay(delay);
        not2.setDelay(delay);
        circuit.addBrick(idp1);
        circuit.addBrick(idp2);
        circuit.addBrick(not1);
        circuit.addBrick(not2);
        circuit.addBrick(and1);
        circuit.addBrick(and2);
        circuit.addBrick(or);

        Wire w1 = circuit.connect(idp1.getOutputO(), not1.getInputA());
        w1.setName("ID1-Not1-Wire");
        Wire w2 = circuit.connect(not1.getOutputO(), and1.getInputA());
        w2.setName("Not1-And1-Wire");
        Wire w3 = circuit.connect(idp2.getOutputO(), not2.getInputA());
        w3.setName("ID2-Not2-Wire");
        Wire w4 = circuit.connect(not2.getOutputO(), and1.getInputB());
        w4.setName("Not2-And1-Wire");
        Wire w5 = circuit.connect(idp1.getOutputO(), and2.getInputA());
        w5.setName("ID1-And2-Wire");
        Wire w6 = circuit.connect(idp2.getOutputO(), and2.getInputB());
        w6.setName("ID2-And-Wire");
        Wire w7 = circuit.connect(and1.getOutputO(), or.getInputA());
        w7.setName("And1-Or-Wire");
        Wire w8 = circuit.connect(and2.getOutputO(), or.getInputB());
        w8.setName("And2-Or-Wire");
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
    	String name = "Xnor";
    	if (delay != 0) {
    		name += "(" + delay + ")";
    	}
        ComponentType type = new ComponentTypeImpl(circuit, name);
        type.setHeight(40);
        type.setWidth(40);
        type.getInputs().add(idp1.getInputA());
        type.getInputs().add(idp2.getInputA());
        type.getOutputs().add(or.getOutputO());
        type.setName(idp1.getInputA(), "a");
        type.setName(idp2.getInputA(), "b");
        type.setName(or.getOutputO(), "o");
        type.normalizePositions();
        return type;

    }

}
