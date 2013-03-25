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
import de.sep2011.funckit.model.graphmodel.implementations.Not;

import java.awt.Rectangle;

/**
 * This factory creates a Nand Component consisting of a {@link And} and a
 * {@link Not}.
 */
public class NandFactory extends AbstractCircuitFactory {                                                                                        

	/**
	 * The {@link And} the Nand consists of.
	 */
    private final And and;
    
    /**
	 * The {@link Not} the Nand consists of.
	 */
    private final Not not;
    
    /**
	 * The delay the Nand has.
	 */
    private final int delay;

    /**
     * Create a normal Nand with no delay.
     */
    public NandFactory() {
        this(0);
    }

    /**
     * Create a Nand with the given delay.
     * @param delay the delay of the Nand.
     */
    private NandFactory(int delay) {
        this.delay = delay;
        circuit = new CircuitImpl();

        and = new And(new Rectangle(10, 10, 40, 40), "NAND-AND");
        not = new Not(new Rectangle(80, 10, 40, 40), "NAND-NOT");
        not.setDelay(delay);
        circuit.addBrick(and);
        circuit.addBrick(not);
        Wire w = circuit.connect(and.getOutputO(), not.getInputA());
        w.setName("NAND-WIRE");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentType getComponentTypeForCircuit() {
    	String name = "Nand";
    	if (delay != 0) {
    		name += "(" + delay + ")";
    	}
        ComponentType type = new ComponentTypeImpl(circuit, name);
        type.setHeight(40);
        type.setWidth(40);
        type.getInputs().add(and.getInputA());
        type.getInputs().add(and.getInputB());
        type.getOutputs().add(not.getOutputO());
        type.setName(and.getInputA(), "a");
        type.setName(and.getInputB(), "b");
        type.setName(not.getOutputO(), "o");
        type.normalizePositions();

        return type;
    }
}
