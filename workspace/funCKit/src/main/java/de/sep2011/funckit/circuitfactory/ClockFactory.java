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

import java.awt.Point;

/**
 * Generates a Clock.
 */
public class ClockFactory extends AbstractCircuitFactory {
    private final Not not;
    private final int clk;

    /**
     * Generates a new clock.
     * 
     * @param clk
     *            the delay the clock should have.
     */
    public ClockFactory(int clk) {
        this.clk = clk;
        circuit = new CircuitImpl();
        not = new Not(new Point(10, 10));
        not.setName("CLOCK-NOT");
        not.setDelay(clk);
        circuit.addBrick(not);
        Wire w = circuit.connect(not.getOutputO(), not.getInputA());
        w.setName("CLOCK-WIRE");
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = new ComponentTypeImpl(circuit, "Clock(" + clk + ")");
        type.setHeight(30);
        type.setWidth(30);
        type.getOutputs().add(not.getOutputO());
        type.setName(not.getOutputO(), "clk");
        type.normalizePositions();
        return type;
    }

}
