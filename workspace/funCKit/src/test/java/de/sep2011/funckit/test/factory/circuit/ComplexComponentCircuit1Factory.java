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
import de.sep2011.funckit.circuitfactory.ClockFactory;
import de.sep2011.funckit.circuitfactory.ShiftRegisterFactory;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * This factory creates a circuit with switches, lights and a shift register.
 */
public class ComplexComponentCircuit1Factory extends AbstractCircuitFactory {

    private static final int LENGTH = 5;

    /**
     * creates a ComplexComponentCircuit1Factory with a default length
     * shiftregister and clocks for the inputs.
     */
    public ComplexComponentCircuit1Factory() {
        this(LENGTH, false);
    }

    /**
     * creates a ComplexComponentCircuit1Factory with a default length
     * shiftregister.
     * 
     * @param useSwitches
     *            use Switches or Clocks for the inputs?
     */
    public ComplexComponentCircuit1Factory(boolean useSwitches) {
        this(LENGTH, useSwitches);
    }

    /**
     * creates a ComplexComponentCircuit1Factory.
     * 
     * @param length
     *            the length of the shiftregister
     * @param useSwitches
     *            use Switches or Clocks for the inputs?
     */
    private ComplexComponentCircuit1Factory(int length, boolean useSwitches) {
        circuit = new CircuitImpl();
        ComponentType shiftRegisterType = new ShiftRegisterFactory(length)
                .getComponentTypeForCircuit();
        ComponentType clkType5 = new ClockFactory(5)
                .getComponentTypeForCircuit();
        ComponentType clkType20 = new ClockFactory(20)
                .getComponentTypeForCircuit();
        Component shiftRegister = new ComponentImpl(shiftRegisterType,
                new Point(80, 10), "SHIFTREGISTER");
        circuit.addBrick(shiftRegister);

        SwitchImpl switchD = new SwitchImpl(new Point(10, 10));
        switchD.setName("data");
        SwitchImpl switchClk = new SwitchImpl(new Point(10, 80));
        switchClk.setName("clk");
        Component clk1 = new ComponentImpl(clkType20, new Point(10, 10),
                "DATA-CLK");
        Component clk2 = new ComponentImpl(clkType5, new Point(10, 80), "CLK");
        if (useSwitches) {
            circuit.addBrick(switchD);
            circuit.addBrick(switchClk);
        } else {
            circuit.addBrick(clk1);
            circuit.addBrick(clk2);
        }
        if (useSwitches) {
            Wire w = circuit.connect(switchD.getOutputO(),
                    shiftRegister.getInput("d"));
            w.setName("DATA-WIRE");
            w = circuit.connect(switchClk.getOutputO(),
                    shiftRegister.getInput("clk"));
            w.setName("CLK-WIRE");
        } else {
            Wire w = circuit.connect(clk1.getOutput("clk"),
                    shiftRegister.getInput("d"));
            w.setName("DATA-WIRE");
            w = circuit.connect(clk2.getOutput("clk"),
                    shiftRegister.getInput("clk"));
            w.setName("CLK-WIRE");
        }

        for (int i = 1; i <= length; i++) {
            Light light = new Light(new Rectangle(150, 10 + 60 * (i - 1), 40,
                    40), "LIGHT" + i);
            circuit.addBrick(light);
            Wire w = circuit.connect(shiftRegister.getOutput("q" + i),
                    light.getInputA());
            w.setName("WIRE" + i);
        }

    }

}
