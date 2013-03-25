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

package de.sep2011.funckit.converter;

/**
 * Exception thrown when something goes wrong while exporting.
 */
public class ExportException extends Exception {

    private static final long serialVersionUID = 2603851087557319865L;

    /**
     * see {@link Exception#Exception()}.
     */
    protected ExportException() {
        super();
    }

    /**
     * see {@link Exception#Exception(String)}.
     * @param message the message
     */
    protected ExportException(String message) {
        super(message);
    }

    /**
     * see {@link Exception#Exception(Throwable)}.
     * @param cause the cause
     */
    protected ExportException(Throwable cause) {
        super(cause);
    }

    /**
     * see {@link Exception#Exception(String, Throwable)}.
     * @param message the message
     * @param cause the cause
     */
    protected ExportException(String message, Throwable cause) {
        super(message, cause);
    }

}
