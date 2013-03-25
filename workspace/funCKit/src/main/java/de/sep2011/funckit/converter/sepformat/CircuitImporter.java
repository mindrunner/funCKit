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

import de.sep2011.funckit.model.graphmodel.Circuit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class provides a static method for importing a {@link Circuit}.
 */
class CircuitImporter {

    /**
     * Import a {@link Circuit} from the given {@link Document}.
     * 
     * @param doc
     *            {@link Document} to import the {@link Circuit} from.
     * @param element
     *            circuit tag to import the {@link Circuit} from.
     * @param circuit
     *            Empty {@link Circuit} which stores the result.
     * @param circuitName
     *            name of the circuit we are currently importing. Used to make
     *            component names unique because they only are within one
     *            circuit.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @param funckitFormat
     *            import from clean funCKit format or just SEP-format?
     * @param mainCircuit
     *            import the mainCircuit? (needs special treatment)
     * @throws SEPFormatImportException
     *
     */
    static void importCircuit(Document doc, Element element, Circuit circuit,
            String circuitName, SEPFormatConverter converter,
            boolean funckitFormat, boolean mainCircuit)
            throws SEPFormatImportException {
        if (!element.getNodeName().equals("circuit")) {
            throw new IllegalArgumentException();
        }

        // import bricks at first
        NodeList componentList = element.getElementsByTagName("component");
        for (int i = 0; i < componentList.getLength(); i++) {
            Element component = (Element) componentList.item(i);
            ComponentImporter.doImport(doc, component, circuit, circuitName,
                    converter, funckitFormat, mainCircuit);
        }

        // import wires at last
        NodeList connectionList = element.getElementsByTagName("connection");
        for (int i = 0; i < connectionList.getLength(); i++) {
            Element connection = (Element) connectionList.item(i);
            ConnectionImporter.importConnection(doc, connection, circuit,
                    circuitName, converter, funckitFormat);
        }

        converter.circuitImported();
    }

}
