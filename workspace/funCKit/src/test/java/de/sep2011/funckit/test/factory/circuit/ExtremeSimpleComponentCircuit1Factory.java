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
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashSet;
import java.util.Set;

//TODO: Julian Comment
public class ExtremeSimpleComponentCircuit1Factory extends
        AbstractCircuitFactory {

    public ExtremeSimpleComponentCircuit1Factory() {
        circuit = new CircuitImpl();
        Circuit compCircuit = circuit;

        ComponentType ct1 = (new InnerCircuitFactory())
                .getComponentTypeForCircuit();

        Component comp1 = new ComponentImpl(ct1, new Point(60, 10), "Comp1");
        compCircuit.addBrick(comp1);
        comp1.getInput("CompIn").setName("Comp1In");
        comp1.getOutput("CompOut").setName("Comp1Out");
        Switch sw = new SwitchImpl(new Rectangle(10, 10, 40, 40), "switch");
        sw.setValue(true);
        compCircuit.addBrick(sw);
        compCircuit.connect(sw.getOutput("o"), comp1.getInput("Comp1In"))
                .setName("Wire1");
        Light light = new Light(new Rectangle(120, 10, 40, 40), "light");
        compCircuit.addBrick(light);
        compCircuit.connect(light.getInputA(), comp1.getOutput("Comp1Out"))
                .setName("Wire2");
    }

    private static class InnerCircuitFactory extends AbstractCircuitFactory {
        private final Not not;

        public InnerCircuitFactory() {
            not = new Not(new Rectangle(10, 10, 40, 40), "innerNot");
            circuit = new CircuitImpl();
            circuit.addBrick(not);
        }

        @Override
        public ComponentType getComponentTypeForCircuit() {
            Set<Input> componentInputs = new LinkedHashSet<Input>();
            Set<Output> componentOutputs = new LinkedHashSet<Output>();
            componentInputs.add(not.getInputA());
            componentOutputs.add(not.getOutputO());

            ComponentType ct1 = new ComponentTypeImpl(circuit, "CompType1",
                    componentInputs, componentOutputs);
            ct1.normalizePositions();
            ct1.setName(not.getInputA(), "CompIn");
            ct1.setName(not.getOutputO(), "CompOut");

            return ct1;
        }
    }

}
