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
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

import java.awt.Point;
import java.awt.Rectangle;

import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * Factory for a full adder.
 */
public class FullAdderFactory extends AbstractCircuitFactory {

    private final ComponentImpl halfAdder1 = new ComponentImpl(
            new HalfAdderFactory().getComponentTypeForCircuit());
    private final ComponentImpl halfAdder2 = new ComponentImpl(
            new HalfAdderFactory().getComponentTypeForCircuit());
    private final Or or;

    /**
     * Generates a new {@link FullAdderFactory}.
     */
    public FullAdderFactory() {
        circuit = new CircuitImpl();

        halfAdder1.setBoundingRect(new Rectangle(100, 100, 40, 40));
        halfAdder1.setName("halfAdder1");
        halfAdder2.setBoundingRect(new Rectangle(200, 200, 40, 40));
        halfAdder2.setName("halfAdder2");

        or = new Or(new Point(300, 100));
        or.setName("OR");

        circuit.addBrick(halfAdder1);
        circuit.addBrick(halfAdder2);
        circuit.addBrick(or);

        circuit.connect(halfAdder1.getOutput("c"), or.getInputA()).setName("WIRE1");
        circuit.connect(halfAdder1.getOutput("s"), halfAdder2.getInput("a")).setName("WIRE2");
        circuit.connect(halfAdder2.getOutput("c"), or.getInputB()).setName("WIRE3");
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = new ComponentTypeImpl(circuit, tr("circuitFactory.fullAdder"));
        type.setHeight(40);
        type.setWidth(40);

        type.getInputs().add(halfAdder1.getInput("a"));
        type.getInputs().add(halfAdder1.getInput("b"));
        type.getInputs().add(halfAdder2.getInput("b"));

        type.getOutputs().add(or.getOutputO());
        type.getOutputs().add(halfAdder2.getOutput("s"));

        type.setName(halfAdder1.getInput("a"), "a");
        type.setName(halfAdder1.getInput("b"), "b");
        type.setName(halfAdder2.getInput("b"), "cin");

        type.setName(or.getOutputO(), "cout");
        type.setName(halfAdder2.getOutput("s"), "s");

        type.normalizePositions();

        return type;
    }

}
