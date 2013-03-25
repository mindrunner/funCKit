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
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class provides a static method for exporting a {@link ComponentType}.
 */
class ComponentTypeExporter {

    /**
     * Exports the given {@link ComponentType} to the given circuits tag.
     * 
     * @param xmlnode
     *            circuits tag to export to
     * @param type
     *            {@link ComponentType} to export.
     * @param doc
     *            {@link Document} to export in.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     */
    static void exportComponentType(Element xmlnode, ComponentType type,
            Document doc, SEPFormatConverter converter) {
        Element element = doc.createElement("circuit");
        element.setAttribute("name", converter.getUUID(type).toString());
        element.setAttribute("fck:name", type.getName());
        element.setAttribute("fck:width", String.valueOf(type.getWidth()));
        element.setAttribute("fck:height", String.valueOf(type.getHeight()));
        element.setAttribute("fck:orientation", type.getOrientation().name());
        element.setAttribute("fck:type", "component");

        // begin with exporting dummy in/outs
        Set<Element> connections = new LinkedHashSet<Element>();
        for (Input input : type.getInputs()) {
            exportTypeAccessPoint(element, type, input, doc, converter,
                    connections);
        }

        for (Output output : type.getOutputs()) {
            exportTypeAccessPoint(element, type, output, doc, converter,
                    connections);
        }

        // export the circuit of this type (exports bricks, then wires)
        converter.exportCircuit(element, type.getCircuit());

        // export the remaining wires of the dummy in/outs at last
        for (Element connection : connections) {
            element.appendChild(connection);
        }

        xmlnode.appendChild(element);
    }

    /**
     * Exports the given {@link AccessPoint} of the given {@link ComponentType}
     * in the given circuit tag as dummy in/outs, creates dummy wires for those
     * and adds them to the connections set.
     * 
     * @param xmlnode
     *            circuit tag to export the {@link AccessPoint} to.
     * @param type
     *            {@link ComponentType} the {@link AccessPoint} belongs to.
     * @param point
     *            {@link AccessPoint} to export.
     * @param doc
     *            {@link Document} to export in.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     * @param connections
     *            {@link Set} to add the dummy connection tags to.
     */
    private static void exportTypeAccessPoint(Element xmlnode,
            ComponentType type, AccessPoint point, Document doc,
            SEPFormatConverter converter, Set<Element> connections) {
        Element element = doc.createElement("component");

        // mark as component type accesspoint, because this holds the
        // information
        element.setAttribute("fck:componentTypePoint", "true");
        element.setAttribute("fck:name", type.getOuterName(point));
        element.setAttribute("fck:posx",
                String.valueOf(type.getOuterPosition(point).x));
        element.setAttribute("fck:posy",
                String.valueOf(type.getOuterPosition(point).y));

        String pointType = (point instanceof Input) ? "in" : "out";
        element.setAttribute("type", pointType);
        element.setAttribute("name", converter.getUUID(point).toString());
        Point position = converter.getUniquePosition();
        ElementExporter.exportPosition(element, position, converter);

        xmlnode.appendChild(element);

        // connect fake in / out
        element = doc.createElement("connection");
        element.setAttribute("fck:ignore", "true");
        if (point instanceof Input) {
            element.setAttribute("source", converter.getUUID(point).toString());
            element.setAttribute("sourcePort", "o");
            WireExporter.setBrickAndPort(element, "target", point, converter,
                    true);
        } else {
            WireExporter.setBrickAndPort(element, "source", point, converter,
                    true);
            element.setAttribute("target", converter.getUUID(point).toString());
            element.setAttribute("targetPort", "a");
        }

        connections.add(element);
    }
}