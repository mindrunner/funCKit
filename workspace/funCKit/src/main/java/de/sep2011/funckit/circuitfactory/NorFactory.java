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
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

import java.awt.Rectangle;

public class NorFactory extends AbstractCircuitFactory {

    private final Or or;
    private final Not not;
    private final int delay;

    public NorFactory() {
        this(0);
    }

    private NorFactory(int delay) {
        this.delay = delay;
        circuit = new CircuitImpl();

        or = new Or(new Rectangle(10, 10, 40, 40), "NOR-OR");
        not = new Not(new Rectangle(80, 10, 40, 40), "NOR-NOT");
        not.setDelay(delay);
        circuit.addBrick(or);
        circuit.addBrick(not);
        Wire w = circuit.connect(or.getOutputO(), not.getInputA());
        w.setName("NAND-WIRE");

    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
    	String name = "Nor";
    	if (delay != 0) {
    		name += "(" + delay + ")";
    	}
        ComponentType type = new ComponentTypeImpl(circuit, name);
        type.setHeight(40);
        type.setWidth(40);
        type.getInputs().add(or.getInputA());
        type.getInputs().add(or.getInputB());
        type.getOutputs().add(not.getOutputO());
        type.setName(or.getInputA(), "a");
        type.setName(or.getInputB(), "b");
        type.setName(not.getOutputO(), "o");
        type.normalizePositions();

        return type;
    }

}
