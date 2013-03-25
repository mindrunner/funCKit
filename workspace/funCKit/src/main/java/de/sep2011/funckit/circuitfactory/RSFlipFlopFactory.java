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
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.util.GraphmodelUtil;

import java.awt.Point;

/**
 * This factory creates a RS-FlipFlop.
 */
public class RSFlipFlopFactory extends AbstractCircuitFactory {

	/**
	 * Constructor that creates the RS-FlipFlop.
	 */
    public RSFlipFlopFactory() {
        circuit = new CircuitImpl();
        Circuit c = circuit;

        ComponentType delayType = new IdentityFactory().getComponentTypeForCircuit();

        SwitchImpl pointR = new SwitchImpl(new Point(20, 0));
        pointR.setName("r");
        Light pointQ = new Light(new Point(480, 20));
        pointQ.setName("q");
        Component delay1 = new ComponentImpl(delayType, new Point(20, 40), "Delay1");
        delay1.setDelay(1);
        Or or1 = new Or(new Point(80, 20));
        or1.setName("Or1");
        Not not1 = new Not(new Point(160, 20));
        not1.setName("Not1");

        SwitchImpl pointS = new SwitchImpl(new Point(20, 100));
        pointS.setName("s");
        Light pointNQ = new Light(new Point(480, 100));
        pointNQ.setName("nq");
        Component delay2 = new ComponentImpl(delayType, new Point(240, 80), "Delay2");
        delay2.setDelay(1);
        Or or2 = new Or(new Point(320, 100));
        or2.setName("Or2");
        Not not2 = new Not(new Point(400, 100));
        not2.setName("Not2");

        c.addBrick(delay1);
        c.addBrick(or1);
        c.addBrick(not1);
        c.addBrick(delay2);
        c.addBrick(or2);
        c.addBrick(not2);
        c.addBrick(pointR);
        c.addBrick(pointS);
        c.addBrick(pointQ);
        c.addBrick(pointNQ);

        c.connect(delay1.getOutput("o"), or1.getInputB());
        c.connect(or1.getOutputO(), not1.getInputA());
        c.connect(not1.getOutputO(), delay2.getInput("a"));
        c.connect(delay2.getOutput("o"), or2.getInputA());
        c.connect(or2.getOutputO(), not2.getInputA());
        c.connect(not2.getOutputO(), delay1.getInput("a"));
        c.connect(pointR.getOutputO(), or1.getInputA());
        c.connect(pointS.getOutputO(), or2.getInputB());
        c.connect(not1.getOutputO(), pointQ.getInputA());
        c.connect(not2.getOutputO(), pointNQ.getInputA());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentType getComponentTypeForCircuit() {
        ComponentType type = GraphmodelUtil.convertToComponentType(circuit, "RS-FlipFlop", true);
        return type;

    }

}
