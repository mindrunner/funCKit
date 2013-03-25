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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.util.GraphmodelUtil;

import java.awt.Point;

/**
 * factory for a D-Latch.
 */
public class DLatchFactory extends AbstractCircuitFactory {

    /**
     * Generates a new {@link DLatchFactory}.
     */
    public DLatchFactory() {
        circuit = new CircuitImpl();
        Circuit c = circuit;

        Component rsFlipFlop = new ComponentImpl(
                new RSFlipFlopFactory().getComponentTypeForCircuit(), new Point(240, 60),
                "RS-flipflop");
        And and1 = new And(new Point(160, 20));
        and1.setName("AND1");
        And and2 = new And(new Point(160, 100));
        and2.setName("AND2");
        Not not = new Not(new Point(80, 20));
        not.setName("NOT");
        SwitchImpl pointD = new SwitchImpl(new Point(20, 40));
        pointD.setName("d");
        SwitchImpl pointE = new SwitchImpl(new Point(20, 120));
        pointE.setName("e");
        Light pointQ = new Light(new Point(300, 60));
        pointQ.setName("q");
        Light pointNQ = new Light(new Point(300, 100));
        pointNQ.setName("nq");

        c.addBrick(rsFlipFlop);
        c.addBrick(and1);
        c.addBrick(and2);
        c.addBrick(not);
        c.addBrick(pointD);
        c.addBrick(pointE);
        c.addBrick(pointQ);
        c.addBrick(pointNQ);

        c.connect(and1.getOutputO(), rsFlipFlop.getInput("r"));
        c.connect(and2.getOutputO(), rsFlipFlop.getInput("s"));
        c.connect(not.getOutputO(), and1.getInputA());
        c.connect(pointD.getOutputO(), not.getInputA());
        c.connect(pointD.getOutputO(), and2.getInputB());
        c.connect(pointE.getOutputO(), and1.getInputB());
        c.connect(pointE.getOutputO(), and2.getInputA());
        c.connect(rsFlipFlop.getOutput("q"), pointQ.getInputA());
        c.connect(rsFlipFlop.getOutput("nq"), pointNQ.getInputA());

    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        return GraphmodelUtil.convertToComponentType(circuit, "D-Latch", true);
    }

}
