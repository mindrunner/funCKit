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

import java.io.IOException;

/**
 * An event handler for the {@link SEPFormatConverter}. This handler will get
 * notified of the progress of the {@link SEPFormatConverter}.
 */
public interface SEPFormatConverterProgressHandler {
	
	/**
	 * Gets called when a warning during the import occured. If the handler
	 * decides the warning was fatal it shall throw a {@link SEPFormatImportException}.
	 * @param message the message of the warning.
	 * @param args additional args for the message.
	 * @throws SEPFormatImportException shall be thrown if the warning was fatal.
	 */
    public void handleImporterWarning(String message, Object... args)
            throws SEPFormatImportException;

    /**
     * Gets called when a warning during the export occured. If the handler
	 * decides the warning was fatal it shall throw a {@link SEPFormatExportException}.
     * @param message the message of the warning.
     * @param args additional args for the message.
     * @throws SEPFormatExportException shall be thrown if the warning was fatal.
     */
    public void handleExporterWarning(String message, Object... args)
            throws SEPFormatExportException;

    /**
     * Gets called when a circuit was imported. The import can be canceled by
     * throwing an {@link SEPFormatImportException}
     * @throws SEPFormatImportException shall be thrown to cancel the import.
     */
    public void handleCircuitImported() throws SEPFormatImportException;

    /**
     * Gets called when the importer knows how many circuits it will need to import.
     * @param circuitsCount the amount of circuits needed to be imported.
     */
    public void handleCircuitsToImport(int circuitsCount);

    /**
     * Gets called when a component was imported. The import can be canceled by
     * throwing an {@link SEPFormatImportException}
     * @throws SEPFormatImportException shall be thrown to cancel the import.
     */
    public void handleComponentImported() throws SEPFormatImportException;

    /**
     * Gets called when the importer knows how many components it will need to import.
     * @param componentsCount the amount of components needed to be imported.
     */
    public void handleComponentsToImport(int componentsCount);

    /**
     * Gets called when a connections was imported. The import can be canceled by
     * throwing an {@link SEPFormatImportException}
     * @throws SEPFormatImportException shall be thrown to cancel the import.
     */
    public void handleConnectionImported() throws SEPFormatImportException;

    /**
     * Gets called when the importer knows how many connections it will need to import.
     * @param connectionsCount the amount of connections needed to be imported.
     */
    public void handleConnectionsToImport(int connectionsCount);

    /**
     * Gets called when some data was read from the stream. The import can be canceled by
     * throwing an {@link IOException}.
     * @param amount the amount of that in bytes that was read from the stream.
     * @throws IOException shall be thrown to cancel the import.
     */
    public void handleDataProgress(int amount) throws IOException;

    /**
     * Gets called when the importer finished reading data from the stream and starts the actual import.
     */
    public void handleDataClose();

    /**
     * Gets called when the importer knows how many bytes it will load from the stream.
     * @param size the bytes that will be loaded from the stream.
     */
    public void handleDataSize(int size);
}
