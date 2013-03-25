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
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.util.GraphmodelUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This class provides a static method for importing a {@link ComponentType}.
 */
class ComponentTypeImporter {

    /**
     * Imports the circuit tag with the given name from the given document and
     * creates a {@link ComponentType} from it which is returned.
     * 
     * @param doc
     *            {@link Document} where the circuit tag is in.
     * @param circuitName
     *            Name of the circuit tag to import.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @param funckitFormat
     *            import from clean funCKit format or jus SEP-format?
     * @param mainCircuit
     *            import the mainCircuit? (needs special treatment)
     * @return {@link ComponentType} with the imported circuit tag
     * @throws SEPFormatImportException
     *             see {@link SEPFormatImportException}
     */
    public static ComponentType importComponentType(Document doc,
            String circuitName, SEPFormatConverter converter,
            boolean funckitFormat, boolean mainCircuit)
            throws SEPFormatImportException {

        // In the FunCKit-Format the circuit name has to be an UUID.
        UUID id;
        if (funckitFormat && !mainCircuit) {
            try {
                id = UUID.fromString(circuitName);
            } catch (IllegalArgumentException e) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.nameHasToBeUUID", "Circuit",
                        circuitName);
            }
        } else {
            id = converter.nameToUUID(circuitName);
        }

        // Look if the circuit has already been imported.
        Object o = converter.getObject(id);
        if (o != null) {
            if (o instanceof ComponentType) {
                return (ComponentType) o; // circuit was imported => return it
            }
            throw new SEPFormatImportException(
                    "SEPFormatImportException.nameHasToRefernceClass",
                    "Circuit", circuitName, "ComponentType", o.getClass());
        }

        if (converter.isCircuitVisited(circuitName)) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.componentTypeRecursion",
                    circuitName);
        }
        converter.setCircuitVisited(circuitName);

        // start importing the circuit
        Circuit circuit = null;
        Element root = doc.getDocumentElement();
        Element circuitElement = null;

        // look for the circuit with the matching name
        NodeList circuitList = root.getElementsByTagName("circuit");
        for (int i = 0; i < circuitList.getLength(); i++) {
            circuitElement = (Element) circuitList.item(i);
            String name = converter.ensureAttribute(circuitElement, "name");
            if (name.equals(circuitName)) {

                // circuit found => import it
                circuit = new CircuitImpl();
                CircuitImporter.importCircuit(doc, circuitElement, circuit,
                        circuitName, converter, funckitFormat, mainCircuit);
                break;
            }
        }

        // circuit was not found? => error
        if (circuit == null) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.missingCircuit", circuitName);
        }

        // create the component type
        String name;
        if (funckitFormat && !mainCircuit) {
            name = converter.ensureAttribute(circuitElement, "fck:name");
        } else {
            name = circuitName;
        }

        ComponentType type;
        if (!mainCircuit) {

            if (funckitFormat) {
                // import the inputs/outputs of the component type at first
                Set<Input> inputs = new LinkedHashSet<Input>();
                Set<Output> outputs = new LinkedHashSet<Output>();
                Map<AccessPoint, Point> positions = new LinkedHashMap<AccessPoint, Point>();
                Map<AccessPoint, String> names = new LinkedHashMap<AccessPoint, String>();
                getAccessPoints(circuitElement, circuit, circuitName,
                        converter, inputs, outputs, positions, names);

                // create the type
                type = new ComponentTypeImpl(circuit, name, inputs, outputs,
                        positions);
                for (Map.Entry<AccessPoint, String> entry : names.entrySet()) {
                    type.setName(entry.getKey(), entry.getValue());
                }
                type.setWidth(converter.ensureInteger(circuitElement,
                        "fck:width"));
                type.setHeight(converter.ensureInteger(circuitElement,
                        "fck:height"));
                type.setOrientation(converter.ensureOrientation(circuitElement,
                        "fck:orientation"));
            } else {
                type = GraphmodelUtil.convertToComponentType(circuit, name,
                        false);
            }
        } else {

            // no inputs/outputs for the main circuit
            type = new ComponentTypeImpl(circuit, name);
        }

        converter.setUUID(type, id);
        return type;
    }

    /**
     * Get the {@link AccessPoint}s of the {@link ComponentType} by looking for
     * component tags in the circuit tag which have the fck:componentTypePoint
     * attribute and importing their information for funCKit-Format files.
     * 
     * @param circuitElement
     *            the tag holding the circuit which defines the
     *            {@link ComponentType}.
     * @param circuit
     *            The {@link Circuit} of the {@link ComponentType} of which the
     *            {@link AccessPoint}s are imported. Used to add dummy
     *            {@link Brick}s needed for some special cases with the plain
     *            sep format.
     * @param circuitName
     *            name of the circuit we are currently importing. Used to make
     *            component names unique because they only are within one
     *            circuit.
     * @param converter
     *            the {@link SEPFormatConverter} to use for importing.
     * @param inputs
     *            The imported {@link Input}s
     * @param outputs
     *            The imported {@link Output}s
     * @param positions
     *            The positions of the imported {@link AccessPoint}s. on plain
     *            sep format this will be empty.
     * @param names
     *            The names of the imported {@link AccessPoint}s
     * @throws SEPFormatImportException
     */
    private static void getAccessPoints(Element circuitElement,
            Circuit circuit, String circuitName, SEPFormatConverter converter,
            Set<Input> inputs, Set<Output> outputs,
            Map<AccessPoint, Point> positions, Map<AccessPoint, String> names)
            throws SEPFormatImportException {

        // look for the dummy in/outs which store the accesspoints referenced by
        // the component
        NodeList componentList = circuitElement
                .getElementsByTagName("component");
        for (int i = 0; i < componentList.getLength(); i++) {
            Element pointElement = (Element) componentList.item(i);
            if (pointElement.hasAttribute("fck:componentTypePoint")) {

                UUID uuid = converter.ensureUUID(pointElement, "name");
                Object o = converter.getObject(uuid);
                if (o == null || !(o instanceof AccessPoint)) {
                    throw new SEPFormatImportException(
                            "SEPFormatImportException.nameHasToReferenceClass",
                            "componentTypePoint",
                            pointElement.getAttribute("name"), "AccessPoint",
                            o == null ? "null" : o.getClass());
                }
                AccessPoint point = (AccessPoint) o;

                String pointName = converter.ensureAttribute(pointElement,
                        "fck:name");
                int posx = converter.ensureInteger(pointElement, "fck:posx");
                int posy = converter.ensureInteger(pointElement, "fck:posy");
                Point pointPosition = new Point(posx, posy);

                if (point instanceof Input) {
                    inputs.add((Input) point);
                } else {
                    outputs.add((Output) point);
                }
                names.put(point, pointName);
                positions.put(point, pointPosition);
            }
        }
    }
}
