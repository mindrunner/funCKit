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
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.test.validator.ComponentTypeRecursionCheckTest;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Creates a Circuit with a circular {@link Component} recursion. Helper for
 * {@link ComponentTypeRecursionCheckTest}
 */
public class ComponentTypeRecursionFactory extends AbstractCircuitFactory {

    /**
     * Creates a new {@link ComponentTypeRecursionFactory}.
     */
    public ComponentTypeRecursionFactory() {
        circuit = new CircuitImpl();
        circuit.addBrick(new And(new Point(33, 44)));

        final AbstractCircuitFactory cf1 = new AbstractCircuitFactory() {
            {
                circuit = new CircuitImpl();
                Component comp = new ComponentImpl(
                        ComponentTypeRecursionFactory.this
                                .getComponentTypeForCircuit());
                circuit.addBrick(comp);
            }
        };
        final AbstractCircuitFactory cf2 = new AbstractCircuitFactory() {
            {
                circuit = new CircuitImpl();
                And a1 = new And(new Rectangle(22, 33, 44, 55));
                circuit.addBrick(a1);

                Component comp = new ComponentImpl(
                        cf1.getComponentTypeForCircuit());
                circuit.addBrick(comp);

                circuit.connect(a1.getOutputO(), comp.getInputs().iterator()
                        .next());
            }
        };

        final AbstractCircuitFactory cf3 = new AbstractCircuitFactory() {
            {
                circuit = new CircuitImpl();
                Component comp = new ComponentImpl(
                        cf2.getComponentTypeForCircuit());
                circuit.addBrick(comp);
            }
        };

        Component comp = new ComponentImpl(cf3.getComponentTypeForCircuit());
        circuit.addBrick(comp);

    }

}
