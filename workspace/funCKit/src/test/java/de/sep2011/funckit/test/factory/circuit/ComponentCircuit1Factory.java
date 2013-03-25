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
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Creates a Circuit with a simple mostly useless component in it.
 */
public class ComponentCircuit1Factory extends AbstractCircuitFactory {

    /**
     * Creates a new {@link ComponentCircuit1Factory}.
     * 
     * @param addSwitches
     *            should there be switches connected to the component?
     */
    public ComponentCircuit1Factory(boolean addSwitches) {
        circuit = new CircuitImpl();

        Circuit compCircuit = circuit;

        Component comp1 = new ComponentImpl(
                (new SimpleCircuit1Factory(false)).getComponentTypeForCircuit(),
                new Point(80, 40), "Comp1");
        Switch sA = new SwitchImpl(new Rectangle(10, 10, 40, 40), "sA");
        Switch sB = new SwitchImpl(new Rectangle(10, 80, 40, 40), "sB");
        sA.setValue(true);
        sB.setValue(false);

        compCircuit.addBrick(comp1);
        if (addSwitches) {
            compCircuit.addBrick(sA);
            compCircuit.addBrick(sB);

            compCircuit.connect(sA.getOutput("o"), comp1.getInput("i0"));
            compCircuit.connect(sA.getOutput("o"), comp1.getInput("i1"));
            compCircuit.connect(sB.getOutput("o"), comp1.getInput("i2"));
        }

    }

}
