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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.test.factory.circuit.ComponentTypeRecursionFactory;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.ComponentTypeRecursionCheck;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains Tests for {@link ComponentTypeRecursionCheck}.
 */
public class ComponentTypeRecursionCheckTest {

    /**
     * This test creates a {@link Circuit} with a circular recursion and one
     * without and checks if the {@link Check} gives the appropriate result.
     */
    @Test
    public void testComponentTypeRecursionCheck() {

        Circuit cir1 = new ComponentTypeRecursionFactory().getCircuit();
        Circuit cir2 = new SimpleCircuit1Factory(true).getCircuit();

        /* Should fail */
        Check recursionCheck1 = new ComponentTypeRecursionCheck();
        assertFalse(recursionCheck1.perform(cir1).isPassed());

        /* Should pass */
        Check recursionCheck2 = new ComponentTypeRecursionCheck();
        assertTrue(recursionCheck2.perform(cir2).isPassed());

    }
}
