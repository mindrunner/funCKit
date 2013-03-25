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

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.UUID;

/**
 * This class provides a static method to import connections as {@link Wire}s.
 */
class ConnectionImporter {

    /**
     * Imports the connection tags from a {@link Document}s circuit tag in a
     * {@link Circuit} as {@link Wire}s.
     * 
     * @param doc
     *            {@link Document} to import from.
     * @param element
     *            circuit tag to import the connections from.
     * @param circuit
     *            {@link Circuit} in which the imported {@link Wire}s will be
     *            stored.
     * @param circuitName
     *            name of the circuit we are currently importing. Used to make
     *            component names unique because they only are within one
     *            circuit.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @param funckitFormat
     *            import from clean funCKit format or just SEP-format?
     * @throws SEPFormatImportException
     */
    static void importConnection(Document doc, Element element,
            Circuit circuit, String circuitName, SEPFormatConverter converter,
            boolean funckitFormat) throws SEPFormatImportException {
        if (!element.getNodeName().equals("connection")) {
            throw new IllegalArgumentException();
        }

        if (funckitFormat && element.getAttribute("fck:ignore").equals("true")) {
            return; // nothing to import here
        }

        AccessPoint source = null;
        AccessPoint target = null;
        String name = "";
        source = importAccessPoint(element, "source", circuitName, converter,
                funckitFormat);
        target = importAccessPoint(element, "target", circuitName, converter,
                funckitFormat);
        if (source == null || target == null) {
            return; // ignore the connection (must be requested by user. no
                    // other cause possible)
        }
        if (!circuit.getElements().contains(source.getBrick())
                || !circuit.getElements().contains(target.getBrick())) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.connectionReferencesNoBrickInCircuit",
                    circuitName, element.getAttribute("source"), element
                            .getAttribute("target"));
        }

        if (funckitFormat) {
            name = converter.ensureAttribute(element, "fck:name");
        }

        Wire wire = new WireImpl(source, target, name);
        source.addWire(wire);
        target.addWire(wire);
        circuit.addWire(wire);

        converter.connectionImported();
    }

    /**
     * Imports the {@link AccessPoint} with the given attribute name
     * (source/target) from the given connection tag element.
     * 
     * @param element
     *            connection tag to import the {@link AccessPoint} from.
     * @param attribute
     *            Name of the {@link AccessPoint} to import. Must be "source" or
     *            "target".
     * @param circuitName
     *            name of the circuit we are currently importing. Used to make
     *            component names unique because they only are within one
     *            circuit.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @param funckitFormat
     *            import from clean funCKit format or just SEP-format?
     * @return the {@link AccessPoint} that was imported.
     * @throws SEPFormatImportException
     */
    static AccessPoint importAccessPoint(Element element, String attribute,
            String circuitName, SEPFormatConverter converter,
            boolean funckitFormat) throws SEPFormatImportException {
        AccessPoint point;
        if (funckitFormat) {
            UUID uuid = converter.ensureUUID(element, "fck:" + attribute);
            Object o = converter.getObject(uuid);
            if (o == null) {
                converter
                        .importWarn(
                                "SEPFormatImporter.Warning.wireReferencesNoBrickAccessPoint",
                                uuid);
                return null;
            }
            if (!(o instanceof AccessPoint)) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.attributeHasToReferenceClass",
                        "connection", "fck:" + attribute, "AccessPoint",
                        element.getAttribute("fck:" + attribute));
            }
            point = (AccessPoint) o;

        } else {
            String brickName = converter.ensureAttribute(element, attribute);
            UUID uuid = converter.nameToUUID(circuitName + ":" + brickName);
            Object o = converter.getObject(uuid);
            if (o == null) {
                converter
                        .importWarn(
                                "SEPFormatImporter.Warning.connectionReferencesNoBrick",
                                brickName);
                return null;
            }
            if (!(o instanceof Brick)) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.attributeHasToReferenceClass",
                        "connection", attribute, "Brick", element
                                .getAttribute(attribute));
            }
            Brick brick = (Brick) o;
            String portName = converter.ensureAttribute(element, attribute
                    + "Port");
            if (attribute.equals("source")) {
                point = brick.getOutput(portName);
                if (point == null) { // try input in case this is a invalid
                                     // circuit
                    point = brick.getInput(portName);
                }
            } else { // "target"
                point = brick.getInput(portName);
                if (point == null) { // try output in case this is a invalid
                                     // circuit
                    point = brick.getOutput(portName);
                }
            }
            if (point == null) {
                converter
                        .importWarn(
                                "SEPFormatImporter.Warning.connectionReferencesNoPortOnBrick",
                                brickName, portName);
                return null;
            }
        }

        return point;
    }
}