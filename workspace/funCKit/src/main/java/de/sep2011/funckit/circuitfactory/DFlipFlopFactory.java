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

import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.util.GraphmodelUtil;

import java.awt.Point;

/**
 * Factory for a D-FlipFlop.
 */
public class DFlipFlopFactory extends AbstractCircuitFactory {

    /**
     * Creates a new {@link DFlipFlopFactory}.
     */
    public DFlipFlopFactory() {
        ComponentType dlatchType = new DLatchFactory().getComponentTypeForCircuit();

        circuit = new CircuitImpl();
        Component dlatch1 = new ComponentImpl(dlatchType, new Point(80, 20), "D-Latch1");
        Component dlatch2 = new ComponentImpl(dlatchType, new Point(160, 40), "D-Latch2");
        Not not = new Not(new Point(80, 100));
        not.setName("NOT");
        SwitchImpl pointD = new SwitchImpl(new Point(20, 20));
        pointD.setName("d");
        SwitchImpl pointClk = new SwitchImpl(new Point(20, 100));
        pointClk.setName("clk");
        Light pointQ = new Light(new Point(240, 40));
        pointQ.setName("q");
        Light pointNQ = new Light(new Point(240, 80));
        pointNQ.setName("nq");

        circuit.addBrick(dlatch1);
        circuit.addBrick(dlatch2);
        circuit.addBrick(not);
        circuit.addBrick(not);
        circuit.addBrick(pointD);
        circuit.addBrick(pointClk);
        circuit.addBrick(pointQ);
        circuit.addBrick(pointNQ);

        circuit.connect(dlatch2.getOutput("q"), pointQ.getInputA());
        circuit.connect(dlatch2.getOutput("nq"), pointNQ.getInputA());
        circuit.connect(dlatch1.getOutput("q"), dlatch2.getInput("d"));
        circuit.connect(not.getOutputO(), dlatch2.getInput("e"));
        circuit.connect(pointD.getOutputO(), dlatch1.getInput("d"));
        circuit.connect(pointClk.getOutputO(), dlatch1.getInput("e"));
        circuit.connect(pointClk.getOutputO(), not.getInputA());
    }

    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = GraphmodelUtil.convertToComponentType(circuit, "D-FlipFlop", true);
        return type;
    }


}
