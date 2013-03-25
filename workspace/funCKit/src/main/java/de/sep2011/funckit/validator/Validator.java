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

package de.sep2011.funckit.validator;

import de.sep2011.funckit.model.graphmodel.Circuit;

import java.util.LinkedList;
import java.util.List;

/**
 * Performs a List of checks on a {@link Circuit}. Checks are Performed in the
 * order they were added.
 */
public class Validator {

    private final List<Check> checks = new LinkedList<Check>();
    private boolean allPassed = false;

    /**
     * Adds a {@link Check}.
     * 
     * @param c
     *            Check to add
     */
    public void addCheck(Check c) {
        assert c != null;
        checks.add(c);
        allPassed = false;
    }

    /**
     * Removes a {@link Check}.
     * 
     * @param c
     *            check to remove
     */
    public void removeCheck(Check c) {
        assert c != null;
        checks.remove(c);
        allPassed = false;
    }

    /**
     * Removes all checks inside this validator.
     */
    public void removeAllChecks() {
        checks.clear();
        allPassed = false;
    }

    /**
     * Returns the list of checks. This is the internal {@link List}, so do not
     * modify it directly
     * 
     * @return a list of Checks
     */
    public List<Check> getChecks() {
        return checks;
    }

    /**
     * Performs all checks in the list.
     * 
     * @param c
     *            the {@link Circuit} the test should be performed on
     * @return a {@link List} of {@link Result}s
     */
    public List<Result> validate(Circuit c) {
        assert c != null;

        List<Result> results = new LinkedList<Result>();
        this.allPassed = true;
        for (Check check : this.checks) {
            Result r = check.perform(c);
            results.add(r);
            if (!r.isPassed()) {
                this.allPassed = false;
            }
        }
        return results;
    }

    /**
     * Checks if all checks passed.
     * 
     * @return true if all checks passed, false otherwise.
     */
    public boolean allPassed() {
        return allPassed;
    }
}