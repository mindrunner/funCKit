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
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.util.GraphmodelUtil;

import java.awt.Point;

/**
 * This factory creates a Shiftregister with variable size.
 * The Shiftregister is made of D-FlipFlops produced by {@link DFlipFlopFactory}.
 */
public class ShiftRegisterFactory extends AbstractCircuitFactory {

	/**
	 * This constructor creates a Shiftregister with the given length.
	 * @param length the length of the created Shiftregister.
	 */
    public ShiftRegisterFactory(int length) {
        ComponentType dflipflopType = new DFlipFlopFactory()
                .getComponentTypeForCircuit();
        circuit = new CircuitImpl();

        SwitchImpl pointClk = new SwitchImpl(new Point(20, 160));
        pointClk.setName("clk");
        Component dflipflop = new ComponentImpl(dflipflopType,
                new Point(80, 80), "SHFITREGISTER-DFLIPFLOP1");
        SwitchImpl pointD = new SwitchImpl(new Point(20, 80));
        pointD.setName("d");
        Light pointQ1 = new Light(new Point(120, 20));
        pointQ1.setName("q1");

        circuit.addBrick(pointClk);
        circuit.addBrick(dflipflop);
        circuit.addBrick(pointD);
        circuit.addBrick(pointQ1);

        circuit.connect(pointClk.getOutputO(),
                dflipflop.getInput("clk"));
        circuit.connect(pointD.getOutputO(), dflipflop.getInput("d"));
        circuit.connect(pointQ1.getInputA(), dflipflop.getOutput("q"));


        for (int i = 1; i < length; i++) {
            Component dflipflop2 = new ComponentImpl(dflipflopType, new Point(
                    80 + 60 * i, 80), "SHFITREGISTER-DFLIPFLOP" + (i + 1));
            circuit.addBrick(dflipflop2);
            circuit.connect(dflipflop.getOutput("q"),
                    dflipflop2.getInput("d"));
            circuit.connect(pointClk.getOutputO(),
                    dflipflop2.getInput("clk"));
            Light pointQX = new Light(new Point(120 + 60 * i, 20));
            pointQX.setName("q" + (i + 1));
            circuit.addBrick(pointQX);
            circuit.connect(dflipflop2.getOutput("q"), pointQX.getInputA());
            dflipflop = dflipflop2;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = GraphmodelUtil.convertToComponentType(circuit, "Shiftregister", true);
        return type;
    }

}
