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

import de.sep2011.funckit.model.graphmodel.Element;

import java.util.Collection;

/**
 * Stores result information of a {@link Check}.
 */
public class Result {
    /**
     * Flag to determine if check has passed.
     */
    private final boolean passed;

    /**
     * Additional information message (can be used in combination of i18n).
     */
    private final String message;

    /**
     * With check associated elements (e.g. Elements that caused error).
     */
    private final Collection<Element> flawElements;

    /**
     * Reference to {@link Check} this result was generated from.
     */
    private Check cause;

    /**
     * Constructs new result object. FlawElements specify elements associated
     * with the performed {@link Check}. Check must not be null.
     * 
     * @param passed
     *            see {@link #isPassed()}
     * @param message
     *            see {@link #getMessage()}
     * @param flawElements
     *            see {@link #flawElements}
     * @param cause
     *            {@link #getClass()}
     */
    public Result(boolean passed, String message,
            Collection<Element> flawElements, Check cause) {
        this.passed = passed;
        this.message = message;
        this.flawElements = flawElements;

        assert cause != null;
        this.cause = cause;
    }

    /**
     * Specifies if {@link Check} passed (true) or not (false).
     * 
     * @return true iff {@link Check} passed.
     */
    public boolean isPassed() {
        return this.passed;
    }

    /**
     * returns Elements that are affected by this Test.
     * 
     * @return Elements that are affected by this Test
     * @since implementation
     */
    public Collection<Element> getFlawElements() {
        return flawElements;
    }

    /**
     * Returns message string, that can be null, empty or meaningful.
     * 
     * @return message string, that can be null, empty or meaningful.
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Returns the {@link Check} that caused this result.
     * 
     * @return the {@link Check} that caused this result.
     */
    public Check getCause() {
    	return cause;
    }
}