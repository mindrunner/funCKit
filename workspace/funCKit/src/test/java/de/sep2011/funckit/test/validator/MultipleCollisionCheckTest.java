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

import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.MultipleCollisionCheck;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for {@link MultipleCollisionCheck}.
 */
public class MultipleCollisionCheckTest {

    /**
     * Creates various Bricks (colliding and not colliding), add them to
     * circuits, do a {@link MultipleCollisionCheck} and check if it returns the
     * expected result.
     */
    @Test
    public void test() {
        And and = new And(new Rectangle(55, 66, 40, 50));
        Or or = new Or(new Rectangle(-66, -66, 40, 50));
        Not not = new Not(new Rectangle(-100, -100, 10, 10));
        Wire wire = new WireImpl(and.getOutputO(), or.getInputA());

        And and2 = new And(new Rectangle(44, 55, 40, 50));
        Or or2 = new Or(new Rectangle(-67, -77, 40, 50));
        Not not2 = new Not(new Rectangle(-50, -50, 10, 10));
        Wire wire2 = new WireImpl(and2.getOutputO(), not2.getInputA());

        And and3 = new And(new Rectangle(505, 606, 40, 50));
        Or or3 = new Or(new Rectangle(-660, -606, 40, 50));
        Not not3 = new Not(new Rectangle(-1000, -1000, 10, 10));
        Wire wire3 = new WireImpl(and3.getOutputO(), or3.getInputA());

        CircuitImpl cir = new CircuitImpl();
        cir.addBrick(and);
        cir.addBrick(or);
        cir.addBrick(not);
        cir.addWire(wire);

        Set<Element> elems2 = new LinkedHashSet<Element>();
        elems2.add(and2);
        elems2.add(or2);
        elems2.add(not2);
        elems2.add(wire2);
        Set<Element> elems3 = new LinkedHashSet<Element>();
        elems3.add(and3);
        elems3.add(or3);
        elems3.add(not3);
        elems2.add(wire3);

        Check failCheck = new MultipleCollisionCheck(elems2);
        Check passedCheck = new MultipleCollisionCheck(elems3);

        assertFalse(failCheck.perform(cir).isPassed());
        assertTrue(passedCheck.perform(cir).isPassed());
    }

}
