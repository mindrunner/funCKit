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
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

import java.awt.Point;

import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * Factory for a half adder.
 */
public class HalfAdderFactory extends AbstractCircuitFactory {

    private final And and3;
    private final Or or;
    private final IdPoint idp1;
    private final IdPoint idp2;
    private final IdPoint idp3;
    private final IdPoint idp4;

    /**
     * Creates a new {@link HalfAdderFactory}.
     */
    public HalfAdderFactory() {
        circuit = new CircuitImpl();

        And and1 = new And(new Point(400, 100));
        and1.setName("AND1");

        And and2 = new And(new Point(400, 200));
        and2.setName("AND2");

        and3 = new And(new Point(400, 300));
        and3.setName("AND3");

        or = new Or(new Point(500, 100));
        or.setName("OR");

        Not not1 = new Not(new Point(300, 100));
        not1.setName("NOT1");
        Not not2 = new Not(new Point(300, 400));
        not2.setName("NOT2");

        idp1 = new IdPoint(new Point(200, 100));
        idp2 = new IdPoint(new Point(100, 200));
        idp3 = new IdPoint(new Point(200, 300));
        idp4 = new IdPoint(new Point(100, 400));

        circuit.addBrick(and1);
        circuit.addBrick(and2);
        circuit.addBrick(and3);
        circuit.addBrick(or);
        circuit.addBrick(not1);
        circuit.addBrick(not2);
        circuit.addBrick(idp1);
        circuit.addBrick(idp2);
        circuit.addBrick(idp3);
        circuit.addBrick(idp4);
        circuit.connect(idp1.getOutputO(), not1.getInputA()).setName("WIRE1");
        circuit.connect(not1.getOutputO(), and1.getInputA()).setName("WIRE2");
        circuit.connect(idp1.getOutputO(), idp3.getInputA()).setName("WIRE3");
        circuit.connect(idp2.getOutputO(), and1.getInputB()).setName("WIRE4");
        circuit.connect(idp3.getOutputO(), and2.getInputA()).setName("WIRE5");
        circuit.connect(idp3.getOutputO(), and3.getInputA()).setName("WIRE6");
        circuit.connect(idp2.getOutputO(), idp4.getInputA()).setName("WIRE7");
        circuit.connect(not2.getOutputO(), and2.getInputB()).setName("WIRE8");
        circuit.connect(idp4.getOutputO(), not2.getInputA()).setName("WIRE9");
        circuit.connect(idp4.getOutputO(), and3.getInputB()).setName("WIRE10");
        circuit.connect(and1.getOutputO(), or.getInputA()).setName("WIRE11");
        circuit.connect(and2.getOutputO(), or.getInputB()).setName("WIRE12");
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = new ComponentTypeImpl(circuit, tr("circuitFactory.halfAdder"));
        type.setHeight(40);
        type.setWidth(40);
        type.getInputs().add(idp1.getInputA());
        type.getInputs().add(idp2.getInputA());
        type.getOutputs().add(or.getOutputO());
        type.getOutputs().add(and3.getOutputO());
        type.setName(idp1.getInputA(), "a");
        type.setName(idp2.getInputA(), "b");
        type.setName(or.getOutputO(), "s");
        type.setName(and3.getOutputO(), "c");
        type.normalizePositions();
        return type;
    }

}
