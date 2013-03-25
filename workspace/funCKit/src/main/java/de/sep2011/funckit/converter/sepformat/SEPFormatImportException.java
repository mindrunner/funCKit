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

package de.sep2011.funckit.converter.sepformat;

import de.sep2011.funckit.converter.ImportException;
import de.sep2011.funckit.util.internationalization.Language;

/**
 * Exception thrown when something goes wrong while importing from the sep
 * format.
 */
public class SEPFormatImportException extends ImportException {

    /**
     * 
     */
    private static final long serialVersionUID = 716271850734452788L;

    public static final SEPFormatImportException CANCELED = new SEPFormatImportException(
            "SEPFormatImportException.canceledByUser");

    private Object[] args;

    /**
     * see {@link ImportException#ImportException()}.
     */
    public SEPFormatImportException() {
        super();
    }

    /**
     * see {@link ImportException#ImportException(String, Throwable)}.
     * @param message
     * @param cause
     */
    public SEPFormatImportException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * see {@link ImportException#ImportException(String)}.
     * @param message
     * @param args
     */
    public SEPFormatImportException(String message, Object... args) {
        super(message);
        this.args = args;
    }

    /**
     * see {@link ImportException#ImportException(Throwable)}.
     * @param cause
     */
    public SEPFormatImportException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        Throwable cause = getCause();
        if (cause != null) {
            message += "The cause of this was: " + cause.getMessage();
        }
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        String message = super.getMessage();
        Throwable cause = getCause();
        if (cause != null) {
            message = Language.tr(message, cause.getLocalizedMessage());
        } else {
            message = Language.tr(message, args);
        }
        return message;
    }

}
