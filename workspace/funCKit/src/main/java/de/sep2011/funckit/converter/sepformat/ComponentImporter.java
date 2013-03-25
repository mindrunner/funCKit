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

import de.sep2011.funckit.circuitfactory.ClockFactory;
import de.sep2011.funckit.circuitfactory.IdentityFactory;
import de.sep2011.funckit.circuitfactory.RSFlipFlopFactory;
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Helper for the {@link SEPFormatConverter} for importing {@link Component}s.
 */
class ComponentImporter {
    private static final int SEP_FORMAT_CLOCK_DEFAULT_IMPORT_DELAY = 1;

    private static final Map<String, Brick> brickMap;

    static {
        brickMap = new HashMap<String, Brick>();
        brickMap.put("and", new And(new Point()));
        brickMap.put("or", new Or(new Point()));
        brickMap.put("not", new Not(new Point()));
        brickMap.put("id", new IdPoint(new Point()));
        brickMap.put("id:idpoint", new IdPoint(new Point()));
        brickMap.put("in", new SwitchImpl(new Point()));
        brickMap.put("out", new Light(new Point()));
        brickMap.put(
                "flipflop",
                new ComponentImpl(new RSFlipFlopFactory()
                        .getComponentTypeForCircuit()));
        brickMap.put(
                "clock",
                new ComponentImpl(new ClockFactory(
                        SEP_FORMAT_CLOCK_DEFAULT_IMPORT_DELAY)
                        .getComponentTypeForCircuit()));
    }

    /**
     * Imports a component from the given {@link Document} and the given circuit
     * tag and adds it to the given {@link Circuit}.
     * 
     * @param doc
     *            {@link Document} to import from.
     * @param element
     *            circuit tag to import the component from.
     * @param circuit
     *            {@link Circuit} to store the imported {@link Brick} in.
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
     */
    static void doImport(Document doc, Element element, Circuit circuit,
            String circuitName, SEPFormatConverter converter,
            boolean funckitFormat, boolean mainCircuit)
            throws SEPFormatImportException {
        if (!element.getNodeName().equals("component")) {
            throw new IllegalArgumentException();
        }

        // ignore dummy components and such flagged with ignore in funckitFormat
        if (funckitFormat
                && (element.getAttribute("fck:ignore").equals("true") || element
                        .getAttribute("fck:componentTypePoint").equals("true"))) {
            converter.componentImported();
            return; // nothing to import here
        }

        String type = converter.ensureAttribute(element, "type");
        if (type.equals("circuit")) { // import Component
            String type2 = converter.ensureAttribute(element, "type2");
            ComponentType ctype =
                    ComponentTypeImporter.importComponentType(doc, type2,
                            converter, funckitFormat, false);
            Component comp =
                    new ComponentImpl(ctype, getPosition(element, converter));
            setName(comp, element, circuitName, converter, funckitFormat);
            if (funckitFormat) {
                comp.setDimension(getDimension(element, converter));
                comp.setOrientation(converter.ensureOrientation(element,
                        "fck:orientation"));
                if (element.hasAttribute("fck:delay")) {
                    comp.setDelay(converter.ensureInteger(element, "fck:delay"));
                }
                setBrickAccessPoints(element, comp, converter);
            } else {
            	comp.setOrientation(converter.getDefaultOrientation());
            }
            circuit.addBrick(comp);
        } else if (type.equals("other")) {
            String type2 = converter.ensureAttribute(element, "type2");
            if (type2.equals("delay")) {
                Brick delay =
                        new ComponentImpl(
                                new IdentityFactory()
                                        .getComponentTypeForCircuit(),
                                getPosition(element, converter));
                setName(delay, element, circuitName, converter, funckitFormat);
                delay.setDelay(1);
                delay.setOrientation(converter.getDefaultOrientation());
                int width = converter.getDefaultWidth();
                int height = converter.getDefaultHeight();
                delay.setDimension(new Dimension(width, height));
                circuit.addBrick(delay);
            } else {
                converter.importWarn(
                        "SEPFormatImporter.Warning.unknownOtherType", type2);
            }
        } else if (type.equals("missing-circuit")) {
            converter.importWarn(
                    "SEPFormatImporter.Warning.unknownMissingCircuitType",
                    converter.ensureAttribute(element, "type2"));
        } else { // some brick
            if (funckitFormat) {
                converter.ensureTagWithValueHasAttribute(element, "type", "id",
                        "fck:type", "idpoint");
                converter.ensureTagWithValueHasAttribute(element, "type", "in",
                        "fck:type", "switch");
                converter.ensureTagWithValueHasAttribute(element, "type",
                        "out", "fck:type", "light");
                if (type.equals("id")) {
                    type = "id:idpoint";
                }
            }
            Brick brick = brickMap.get(type);
            if (brick == null) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.unknownComponentType", type);
            }
            brick = brick.getNewInstance(getPosition(element, converter));
            setName(brick, element, circuitName, converter, funckitFormat);
            if (funckitFormat) {
                brick.setDimension(getDimension(element, converter));
                brick.setOrientation(converter.ensureOrientation(element,
                        "fck:orientation"));
                if (brick instanceof Gate && element.hasAttribute("fck:delay")) {
                    brick.setDelay(converter
                            .ensureInteger(element, "fck:delay"));
                }
                if (brick instanceof Component) {
                    throw new SEPFormatImportException(
                            "SEPFormatImportException.componentTypeNotAllowedInFunckitFormat",
                            type);
                }
                setBrickAccessPoints(element, brick, converter);
                if (brick instanceof Switch) {
                    ((Switch) brick).setValue(converter.ensureBoolean(element,
                            "fck:value"));
                }

            } else if (!(brick instanceof IdPoint)){
                brick.setOrientation(converter.getDefaultOrientation());
                int width = converter.getDefaultWidth();
                int height = converter.getDefaultHeight();
                brick.setDimension(new Dimension(width, height));
            }
            circuit.addBrick(brick);
        }

        converter.componentImported();
    }

    /**
     * Gets the position attributes of the given component tag.
     * 
     * @param element
     *            tag to get the position attributes for. Has to be component
     *            tag.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @return the imported position.
     * @throws SEPFormatImportException
     */
    private static Point getPosition(Element element,
            SEPFormatConverter converter) throws SEPFormatImportException {
        int x = converter.ensureInteger(element, "posx");
        int y = converter.ensureInteger(element, "posy");
        return new Point(x, y);
    }

    /**
     * Gets the dimension attributes of the given component tag. (funCKit format
     * only)
     * 
     * @param element
     *            tag to get the dimension attributes for. Has to be component
     *            tag.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @return the imported dimension.
     * @throws SEPFormatImportException
     */
    private static Dimension getDimension(Element element,
            SEPFormatConverter converter) throws SEPFormatImportException {
        int width = converter.ensureInteger(element, "fck:width");
        int height = converter.ensureInteger(element, "fck:height");
        return new Dimension(width, height);
    }

    /**
     * Imports the name of the component and sets it to the {@link Brick}.
     * 
     * @param brick
     *            {@link Brick} to set the name for.
     * @param element
     *            component tag to import the name from.
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
    private static void setName(Brick brick, Element element,
            String circuitName, SEPFormatConverter converter,
            boolean funckitFormat) throws SEPFormatImportException {
        String name;
        UUID uuid;
        if (funckitFormat) {
            uuid = converter.ensureUUID(element, "name");
            name = converter.ensureAttribute(element, "fck:name");
        } else { // plain sep format
            name = converter.ensureAttribute(element, "name");
            uuid = converter.nameToUUID(circuitName + ":" + name);
        }

        if (converter.getObject(uuid) != null) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.nonUniqueAttribute", "component",
                    "name", element.getAttribute("name"));
        }

        converter.setUUID(brick, uuid);
        brick.setName(name);
    }

    /**
     * Sets the {@link AccessPoint}s of the {@link Brick} to the ones imported
     * from the given component tag.
     * 
     * @param element
     *            component tag to import from.
     * @param brick
     *            {@link Brick} to set the {@link AccessPoint}s for.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @throws SEPFormatImportException
     */
    private static void setBrickAccessPoints(Element element, Brick brick,
            SEPFormatConverter converter) throws SEPFormatImportException {
        int inputs = 0;
        int outputs = 0;
        NodeList componentList =
                element.getElementsByTagName("fck:accesspoint");
        for (int i = 0; i < componentList.getLength(); i++) {
            Element pointElement = (Element) componentList.item(i);
            String type = converter.ensureAttribute(pointElement, "fck:type");
            String name = converter.ensureAttribute(pointElement, "fck:name");
            UUID uuid = converter.ensureUUID(pointElement, "fck:id");
            if (converter.getObject(uuid) != null) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.nonUniqueAttribute",
                        "fck:accesspoint", "fck:id", uuid);
            }
            if (!(type.equals("input") || type.equals("output"))) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.attributeHasToBe",
                        "fck:accesspoint", "fck:type", "input/output", type);
            }

            AccessPoint point =
                    getBrickAccessPoint(element, type.equals("input"), uuid,
                            brick, inputs, outputs, converter);
            if (type.equals("input") && !(point instanceof Input)) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.attributeHasToReferenceClass",
                        "mapping", "fck:inner", "Input", point.getClass());
            } else if (type.equals("output") && !(point instanceof Output)) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.attributeHasToReferenceClass",
                        "mapping", "fck:inner", "Output", point.getClass());
            }
            point.setPosition(getPointPosition(pointElement, converter));
            point.setName(name);
            converter.setUUID(point, uuid);

            if (type.equals("input")) {
                inputs++;
            } else {
                outputs++;
            }
        }

        Pair<Integer, Integer> expectedAccessPoints =
                getAmountAccessPoints(brick);
        int expectedInputs = expectedAccessPoints.getLeft();
        if (inputs != expectedInputs) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.wrongAmountAccessPoints",
                    converter.getUUID(brick), expectedInputs, "Inputs", inputs);
        }
        int expectedOutputs = expectedAccessPoints.getRight();
        if (outputs != expectedOutputs) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.wrongAmountAccessPoints",
                    converter.getUUID(brick), expectedOutputs, "Outputs",
                    outputs);
        }
    }

    /**
     * Gets the {@link AccessPoint} for the given uuid on the given
     * {@link Brick}. On {@link Component}s this is done via the mapping,
     * otherwise this is based on the amount of already imported accesspoints.
     * 
     * @param element
     *            the component tag the the accesspoint is in.
     * @param input
     *            accesspoint is of type input?
     * @param pointUUID
     *            the uuid of the accesspoint.
     * @param brick
     *            the {@link Brick} the {@link AccessPoint} is on.
     * @param inputsImported
     *            the amount of already imported {@link Input}s.
     * @param outputsImported
     *            the amount of already imported {@link Output}s
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @return the {@link AccessPoint} of the accesspoint tag.
     * @throws SEPFormatImportException
     */
    private static AccessPoint getBrickAccessPoint(Element element,
            boolean input, UUID pointUUID, Brick brick, int inputsImported,
            int outputsImported, SEPFormatConverter converter)
            throws SEPFormatImportException {
        if (brick instanceof Component) {
            UUID innerUUID = getInnerMapping(element, pointUUID, converter);
            Object o = converter.getObject(innerUUID);
            if (o == null || !(o instanceof AccessPoint)) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.attributeHasToReferenceClass",
                        "mapping", "fck:inner", "AccessPoint",
                        o == null ? "null" : o.getClass());
            }
            AccessPoint outer = ((Component) brick).getOuter((AccessPoint) o);
            if (outer == null) {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.wrongMappedAccessPoint",
                        pointUUID, innerUUID);
            }
            return outer;
        }

        return getAccessPoint(brick, input, inputsImported, outputsImported);
    }

    /**
     * Gets the uuid of the mapped inner accesspoint to the given uuid of the
     * outer accesspoint for the given component tag by searching trough the
     * mapping tags.
     * 
     * @param element
     *            the component tag with an accesspoint with the given uuid.
     * @param outerUUID
     *            the uuid of the outer accesspoint
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @return the uuid of the mapped inner accesspoint.
     * @throws SEPFormatImportException
     */
    private static UUID getInnerMapping(Element element, UUID outerUUID,
            SEPFormatConverter converter) throws SEPFormatImportException {
        NodeList componentList = element.getElementsByTagName("fck:mapping");
        for (int i = 0; i < componentList.getLength(); i++) {
            Element mappingElement = (Element) componentList.item(i);
            UUID foundOuterUUID =
                    converter.ensureUUID(mappingElement, "fck:outerPoint");
            if (outerUUID.equals(foundOuterUUID)) {
                return converter.ensureUUID(mappingElement, "fck:innerPoint");
            }
        }

        throw new SEPFormatImportException(
                "SEPFormatImportException.unmappedAccessPoint", outerUUID);
    }

    /**
     * Returns the expected amount of {@link Input}s and {@link Output}s for the
     * given {@link Brick}.
     * 
     * @param brick
     *            {@link Brick} to get the expected amount of {@link Input}s and
     *            {@link Output}s for.
     * @return the expected amount of {@link Input}s and {@link Output}s for the
     *         given {@link Brick}.
     */
    private static Pair<Integer, Integer> getAmountAccessPoints(Brick brick) {
        if (brick instanceof And || brick instanceof Or) {
            return new Pair<Integer, Integer>(2, 1);
        } else if (brick instanceof Not || brick instanceof IdPoint) {
            return new Pair<Integer, Integer>(1, 1);
        } else if (brick instanceof Switch) {
            return new Pair<Integer, Integer>(0, 1);
        } else if (brick instanceof Light) {
            return new Pair<Integer, Integer>(1, 0);
        } else if (brick instanceof Component) {
            Component comp = (Component) brick;
            return new Pair<Integer, Integer>(
                    comp.getType().getInputs().size(), comp.getType()
                            .getOutputs().size());
        } else {
            throw new IllegalArgumentException("Brick type " + brick.getClass()
                    + " not supported.");
        }
    }

    /**
     * Gets the right {@link AccessPoint} of the given non {@link Component}
     * {@link Brick} based on what is wanted ({@link Input} or {@link Output})
     * and what was already imported.
     * 
     * @param brick
     *            {@link Brick} to get the {@link AccessPoint} for. Has to be a
     *            non {@link Component}.
     * @param input
     *            Get {@link Input}? (otherwise {@link Output}).
     * @param inputsImported
     *            Amount of already imported {@link Input}s.
     * @param outputsImported
     *            Amount of already imported {@link Output}s.
     * @return the next {@link AccessPoint} to import.
     * @throws SEPFormatImportException
     */
    private static AccessPoint getAccessPoint(Brick brick, boolean input,
            int inputsImported, int outputsImported)
            throws SEPFormatImportException {
        if (brick instanceof Switch) {
            if (!input && inputsImported == 0) {
                return ((SwitchImpl) brick).getOutputO();
            }
            throw new SEPFormatImportException(
                    "SEPFormatImportException.componentOfTypeMustAccessPointsAmount",
                    "in (Switch)", 0, "input accesspoints", 1,
                    "output accesspoint");
        } else if (brick instanceof Light) {
            if (input && outputsImported == 0) {
                return ((Light) brick).getInputA();
            }
            throw new SEPFormatImportException(
                    "SEPFormatImportException.componentOfTypeMustAccessPointsAmount",
                    "out (Light)", 1, "input accesspoint", 0,
                    "output accesspoints");
        } else if (brick instanceof And) {
            if (input && inputsImported == 0) {
                return ((And) brick).getInputA();
            } else if (input && inputsImported == 1) {
                return ((And) brick).getInputB();
            } else if (!input && outputsImported == 0) {
                return ((And) brick).getOutputO();
            } else {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.componentOfTypeMustAccessPointsAmount",
                        "and (And)", 2, "input accesspoints", 1,
                        "output accesspoint");
            }
        } else if (brick instanceof Or) {
            if (input && inputsImported == 0) {
                return ((Or) brick).getInputA();
            } else if (input && inputsImported == 1) {
                return ((Or) brick).getInputB();
            } else if (!input && outputsImported == 0) {
                return ((Or) brick).getOutputO();
            } else {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.componentOfTypeMustAccessPointsAmount",
                        "or (Or)", 2, "input accesspoints", 1,
                        "output accesspoint");
            }
        } else if (brick instanceof Not) {
            if (input && inputsImported == 0) {
                return ((Not) brick).getInputA();
            } else if (!input && outputsImported == 0) {
                return ((Not) brick).getOutputO();
            } else {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.componentOfTypeMustAccessPointsAmount",
                        "not (Not)", 1, "input accesspoint", 1,
                        "output accesspoint");
            }
        } else if (brick instanceof IdPoint) {
            if (input && inputsImported == 0) {
                return ((IdPoint) brick).getInputA();
            } else if (!input && outputsImported == 0) {
                return ((IdPoint) brick).getOutputO();
            } else {
                throw new SEPFormatImportException(
                        "SEPFormatImportException.componentOfTypeMustAccessPointsAmount",
                        "IdPoint", 1, "input accesspoint", 1,
                        "output accesspoint");
            }
        } else {
            throw new IllegalArgumentException("Brick type " + brick.getClass()
                    + " not supported.");
        }
    }

    /**
     * Gets the position attributes of the given fck:accesspoint tag.
     * 
     * @param element
     *            tag to get the position attributes for. Has to be
     *            fck:accesspoint tag.
     * @param converter
     *            {@link SEPFormatConverter} to use for importing.
     * @return the imported position.
     * @throws SEPFormatImportException
     */
    private static Point getPointPosition(Element element,
            SEPFormatConverter converter) throws SEPFormatImportException {
        int x = converter.ensureInteger(element, "fck:posx");
        int y = converter.ensureInteger(element, "fck:posy");
        return new Point(x, y);
    }
}
