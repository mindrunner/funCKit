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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

import java.awt.Rectangle;

public class MultiplexerFactory extends AbstractCircuitFactory {
    private static final String MUX_E0 = "e0";
    private static final String MUX_E1 = "e1";
    private static final String MUX_S0 = "s0";
    private static final String MUX_A = "a";
    private final And and1;
    private final And and2;
    private final Or or;
    private final IdPoint idPoint;

    public MultiplexerFactory() {
        circuit = new CircuitImpl();

        and1 = new And(new Rectangle(10, 10, Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT), "MUX-AND1");
        and2 = new And(new Rectangle(50, 50, Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT), "MUX-AND2");
        or = new Or(new Rectangle(100, 100, Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT), "MUX-OR");
        Not not = new Not(new Rectangle(150, 150, Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT), "MUX-NOT");
        idPoint = new IdPoint(new Rectangle(150, 150, Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT),
                "MUX-IDP");

        circuit.addBrick(and1);
        circuit.addBrick(and2);
        circuit.addBrick(or);
        circuit.addBrick(not);
        circuit.addBrick(idPoint);

        circuit.connect(not.getOutputO(), and1.getInputB());
        circuit.connect(and1.getOutputO(), or.getInputA());
        circuit.connect(and2.getOutputO(), or.getInputB());
        circuit.connect(idPoint.getOutputO(), not.getInputA());
        circuit.connect(idPoint.getOutputO(), and2.getInputB());
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = new ComponentTypeImpl(circuit, "Mux");
        type.setWidth(50);
        type.setHeight(90);

        /* Define inputs. */
        type.getInputs().add(and1.getInputA());
        type.getInputs().add(and2.getInputA());
        type.getInputs().add(idPoint.getInputA());
        type.setName(and1.getInputA(), MUX_E0);
        type.setName(and2.getInputA(), MUX_E1);
        type.setName(idPoint.getInputA(), MUX_S0);

        /* Define outputs */
        type.getOutputs().add(or.getOutputO());
        type.setName(or.getOutputO(), MUX_A);

        type.normalizePositions();
        return type;
    }
}
