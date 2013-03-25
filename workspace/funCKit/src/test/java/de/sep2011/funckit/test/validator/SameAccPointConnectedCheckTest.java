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

package de.sep2011.funckit.test.validator;

import de.sep2011.funckit.circuitfactory.HalfAdderFactory;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.SameAccPointConnectedCheck;
import org.junit.Test;

import java.awt.Rectangle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for {@link SameAccPointConnectedCheck}.
 */
public class SameAccPointConnectedCheckTest {

    /**
     * This test creates 2 Bricks and connects one Input with itself and one
     * Output with itself and check if the check finds this.
     */
    @Test
    public void testSameAccPointConnectedCheck() {
        Circuit c = new CircuitImpl();

        Or b1 = new Or(new Rectangle());
        And b2 = new And(new Rectangle());
        Component comp1 = new ComponentImpl(
                new HalfAdderFactory().getComponentTypeForCircuit());

        c.addBrick(b1);
        c.addBrick(b2);
        c.addBrick(comp1);
        c.connect(b1.getOutputO(), b2.getInputA());
        c.connect(b1.getOutputO(), b2.getInputB());

        Result r1 = (new SameAccPointConnectedCheck()).perform(c);
        assertTrue(r1.isPassed());
        assertTrue(r1.getFlawElements().isEmpty());

        c.connect(b1.getInputA(), b1.getInputA());
        c.connect(b2.getOutputO(), b2.getOutputO());

        Result r2 = (new SameAccPointConnectedCheck()).perform(c);

        assertFalse(r2.isPassed());
        assertEquals(4, r2.getFlawElements().size());
    }
}
