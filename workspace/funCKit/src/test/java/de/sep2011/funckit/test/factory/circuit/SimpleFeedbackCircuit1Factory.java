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
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;

import java.awt.Rectangle;

/**
 * Creates a simple nonsense {@link Circuit} with a Feedback loop.
 */
public class SimpleFeedbackCircuit1Factory extends AbstractCircuitFactory {

    /**
     * Create a new {@link SimpleFeedbackCircuit1Factory}
     * 
     * @param addSwitches
     *            should there be switches connected to the component?
     */
    public SimpleFeedbackCircuit1Factory(boolean addSwitches) {
        circuit = new CircuitImpl();
        Circuit c = circuit;

        And a1 = new And(new Rectangle(6, 7, 40, 40), "AND1");
        Switch s1 = new SwitchImpl(new Rectangle(66, 77, 40, 40), "S1");
        Switch s2 = new SwitchImpl(new Rectangle(-99, -77, 40, 40), "S2");
        And a2 = new And(new Rectangle(120, 120, 40, 40), "AND2");
        a1.setDelay(1);

        s1.toggle();
        s2.toggle();

        c.addBrick(a1);
        c.addBrick(a2);

        if (addSwitches) {
            c.addBrick(s1);
            c.addBrick(s2);
            c.connect(s1.getOutput("o"), a1.getInput("a"));
            c.connect(s2.getOutput("o"), a1.getInput("b"));
        }

        c.connect(a1.getOutput("o"), a1.getInput("b"));
        c.connect(a1.getOutput("o"), a2.getInput("b"));
    }

}
