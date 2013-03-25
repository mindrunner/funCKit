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
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.Validator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link Validator}.
 */
public class ValidatorTest {

    /**
     * Adds Various dummy checks to the {@link Validator} and checks if it
     * performs all and gives the results in correct order.
     */
    @Test
    public void test() {
        Circuit cir = new CircuitImpl();
        Validator validator = new Validator();

        validator.addCheck(new Check() {

            @Override
            public Result perform(Circuit c) {
                return new Result(true, "foo", new ArrayList<Element>(), this);
            }

            @Override
            public String getName() {
                return "fluffy";
            }
        });

        validator.addCheck(new Check() {

            @Override
            public Result perform(Circuit c) {
                return new Result(false, "foo", new ArrayList<Element>(), this);
            }

            @Override
            public String getName() {
                return "fluffy";
            }
        });

        List<Result> results = validator.validate(cir);
        assertFalse(validator.allPassed());

        assertTrue(results.get(0).isPassed());
        assertFalse(results.get(1).isPassed());

        List<Check> checks = new ArrayList<Check>(validator.getChecks());

        for (Check check : checks) {
            validator.removeCheck(check);
        }

        assertEquals(0, validator.getChecks().size());

        for (Check check : checks) {
            validator.addCheck(check);
        }

        assertEquals(2, validator.getChecks().size());

        validator.removeAllChecks();
        assertEquals(0, validator.getChecks().size());
    }

}
