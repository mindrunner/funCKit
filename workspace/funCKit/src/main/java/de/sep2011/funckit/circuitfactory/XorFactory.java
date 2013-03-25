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


public class XorFactory extends AbstractCircuitFactory {


    private final Or or;
    private final int delay;
    private final IdPoint idp1;
    private final IdPoint idp2;

    public XorFactory() {
        this(0);
    }

    private XorFactory(int delay) {
        this.delay = delay;
        circuit = new CircuitImpl();

        And and1 = new And(new Rectangle(300, 100, 40, 40), "XOR-AND1");
        And and2 = new And(new Rectangle(300, 300, 40, 40), "XOR-AND2");
        Not not1 = new Not(new Rectangle(200, 100, 40, 40), "XOR-NOT1");
        Not not2 = new Not(new Rectangle(200, 300, 40, 40), "XOR-NOT2");
        or = new Or(new Rectangle(400, 180, 40, 40), "XOR-OR");
        idp1 = new IdPoint(new Point(100, 100));
        idp2 = new IdPoint(new Point(100, 300));
        not1.setDelay(delay);
        not2.setDelay(delay);
        circuit.addBrick(and1);
        circuit.addBrick(and2);
        circuit.addBrick(not1);
        circuit.addBrick(not2);
        circuit.addBrick(or);
        circuit.addBrick(idp1);
        circuit.addBrick(idp2);
        Wire w1 = circuit.connect(idp1.getOutputO(), not1.getInputA());
        w1.setName("IdPoint1-Not1-Wire");
        Wire w2 = circuit.connect(not1.getOutputO(), and1.getInputA());
        w2.setName("NOT1-AND1-WIRE");
        Wire w3 = circuit.connect(idp1.getOutputO(), and2.getInputA());
        w3.setName("IdPoint1-And2-Wire");
        Wire w4 = circuit.connect(idp2.getOutputO(), and1.getInputB());
        w4.setName("IdPoint2-And1-Wire");
        Wire w5 = circuit.connect(idp2.getOutputO(), not2.getInputA());
        w5.setName("IdPoint2-Not2-Wire");
        Wire w6 = circuit.connect(not2.getOutputO(), and2.getInputB());
        w6.setName("NOT2-AND2-WIRE");
        Wire w7 = circuit.connect(and1.getOutputO(), or.getInputA());
        w7.setName("AND1-OR-WIRE");
        Wire w8 = circuit.connect(and2.getOutputO(), or.getInputB());
        w8.setName("AND2-OR-WIRE");


    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
    	String name = "Xor";
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
