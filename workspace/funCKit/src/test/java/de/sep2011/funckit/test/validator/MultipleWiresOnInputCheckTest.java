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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.test.factory.circuit.MultipleWiresOnInputCircuitFactory;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.MultipleWiresOnInputCheck;
import de.sep2011.funckit.validator.Result;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for {@link MultipleWiresOnInputCheck}.
 */
public class MultipleWiresOnInputCheckTest {

    /**
     * Creates a {@link Circuit} with Bricks where multiple wires are connected
     * to one {@link Input}. Checks if the {@link Check} detects this returns
     * the correct affected Elements.
     */
    @Test
    public void testMultipleWiresOnInputCheck() {

        MultipleWiresOnInputCircuitFactory cf1 = new MultipleWiresOnInputCircuitFactory();

        Check ch1 = new MultipleWiresOnInputCheck();
        Result r1 = ch1.perform(cf1.getCircuit());

        // should fail
        assertFalse(r1.isPassed());

        // check results
        for (Brick b : cf1.getAffectedBricks()) {
            assertTrue(r1.getFlawElements().contains(b));
        }

        Check ch2 = new MultipleWiresOnInputCheck();
        Result r2 = ch2.perform(new SimpleCircuit1Factory(true).getCircuit());

        // should pass
        assertTrue(r2.isPassed());
    }
}
