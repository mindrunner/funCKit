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

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.common.collect.HashBiMap;

import de.sep2011.funckit.converter.StreamExporter;
import de.sep2011.funckit.converter.StreamImporter;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Brick.Orientation;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.util.Log;

/**
 * This class can import from the funCKit and SEP format and can export to the
 * funCKit format.
 */
public class SEPFormatConverter implements StreamExporter, StreamImporter,
        ErrorHandler {

    /**
     * Name of the attribute to set the schema language for in the document
     * builder factory.
     */
    private static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /**
     * Value to set the schema language attribute to in the document builder
     * factory.
     */
    private static final String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";

    /**
     * Path to the resource where the XML schema of the funCKit format is
     * stored.
     */
    private static final String FUNCKIT_SCHEMA_PATH = "/funckit.xsd";

    /**
     * Path to the resource where the SEP schema of the funCKit format is
     * stored.
     */
    private static final String SEP_SCHEMA_PATH = "/circuits-1.0.xsd";

    /**
     * Name of the attribute to which the schemas have to be set to use in the
     * document builder factory.
     */
    private static final String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * This factory is used to create a document builder to create a document.
     */
    private final DocumentBuilderFactory docBuilderFactory;

    /**
     * This document is used to build/read the xml file.
     */
    private Document doc;

    /**
     * The current name of the project.
     */
    private String projectName;

    /**
     * Circuit names visited on import. Used for recursion check.
     */
    private final Set<String> visitedCircuits;

    /**
     * Map of {@link Element} to the corresponding exporter for it.
     */
    private Map<Class<? extends de.sep2011.funckit.model.graphmodel.Element>, ElementExporter> exporterMap;

    /**
     * Used to assign {@link UUID}s to Objects for referencing in the file.
     */
    private final HashBiMap<Object, UUID> uuidMap;

    /**
     * Translate name string to {@link UUID}. Used when importing plain sep
     * format.
     */
    private final Map<String, UUID> nameToUUIDMap;

    /**
     * Set of already exported {@link ComponentType}s.
     */
    private final Set<ComponentType> exportedComponentTypes;

    /**
     * Queue of {@link ComponentType}s which have to get exported.
     */
    private final Queue<ComponentType> componentTypesToExport;

    /**
     * The last registered maximum position (maximum using x+y).
     */
    private Point maxPoint = new Point();

    /**
     * Translation of the whole circuit before exporting.
     */
    private Point translation = new Point();

    /**
     * Enum of the various converter modes for this converter.
     */
    public enum Mode {
    	
    	/**
    	 * Normal mode.
    	 */
        FUNCKITFORMAT,
        
        /**
         * Plain deprecated SEP-Format.
         */
        SEPFORMAT
    }

    /**
     * The converter mode of this converter.
     */
    private final Mode mode;

    /**
     * Handler that gets notified on some events (warnings) while importing.
     */
    private SEPFormatConverterProgressHandler progressHandler;

    /**
     * The default width used when importing SEP-Format files.
     */
    private int defaultWidth = Brick.DEFAULT_WIDTH;
    
    /**
     * The default height used when importing SEP-Format files.
     */
    private int defaultHeight = Brick.DEFAULT_HEIGHT;
    
    /**
     * The default orienation used when importing SEP-Format files.
     */
    private Orientation defaultOrientation = Brick.DEFAULT_ORIENTATION;

    /**
     * This constructor creates a new SEPFormatConverter with the specified
     * projectName set for exporting.
     * 
     * @param projectName
     *            the project name to use when exporting.
     * @param mode the mode
     */
    public SEPFormatConverter(String projectName, Mode mode) {
        this.projectName = projectName;
        this.mode = mode;
       
        // setup the document builder factory
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        Log.gl().debug("Using XML DocumentBuilderFactory: " + docBuilderFactory);
        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setValidating(true);
        docBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        // use build-in schemas
        InputStream schemas[] =
                { getClass().getResourceAsStream(SEP_SCHEMA_PATH),
                        getClass().getResourceAsStream(FUNCKIT_SCHEMA_PATH) };
        docBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemas);

        // initialize attributes
        visitedCircuits = new LinkedHashSet<String>();
        exportedComponentTypes = new LinkedHashSet<ComponentType>();
        componentTypesToExport = new LinkedList<ComponentType>();
        uuidMap = HashBiMap.create();
        nameToUUIDMap = new HashMap<String, UUID>();

        // set up map of elements to their exporters
        buildExporterMap();
    }

    /**
     * Set the settings that will be used for importing from SEP-Format.
     * 
     * @param defaultHeight the default height to use for importing.
     * @param defaultWidth the default width to use for importing.
     * @param defaultOrientation the default {@link Orientation} to use for importing.
     */
    public void setSettings(int defaultHeight, int defaultWidth, Orientation defaultOrientation) {
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        this.defaultOrientation = defaultOrientation;
    }

    /**
     * Returns the default height used when importing SEP-Format files.
     * 
     * @return default height used when importing SEP-Format files.
     */
    public int getDefaultHeight() {
        return defaultHeight;
    }
    
    /**
     * Returns the default width used when importing SEP-Format files.
     * 
     * @return default width used when importing SEP-Format files.
     */
    public int getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * Returns the default orientation used when importing SEP-Format files.
     * 
     * @return default orientation used when importing SEP-Format files.
     */
    public Orientation getDefaultOrientation() {
        return defaultOrientation;
    }
    
    /**
     * Sets up the map of elements to their exporters.
     */
    private void buildExporterMap() {
        exporterMap =
                new HashMap<Class<? extends de.sep2011.funckit.model.graphmodel.Element>, ElementExporter>();
        exporterMap.put(WireImpl.class, new WireExporter());
        exporterMap.put(And.class, new GateExporter());
        exporterMap.put(Or.class, new GateExporter());
        exporterMap.put(Not.class, new GateExporter());
        exporterMap.put(IdPoint.class, new GateExporter());
        exporterMap.put(Light.class, new LightExporter());
        exporterMap.put(SwitchImpl.class, new SwitchExporter());
        exporterMap.put(ComponentImpl.class, new ComponentExporter());
    }

    /**
     * Sets the {@link SEPFormatConverterProgressHandler} that gets notified on
     * events (warnings) while importing.
     * 
     * @param progressHandler
     *            the handler to set.
     */
    public void setProgressHandler(
            SEPFormatConverterProgressHandler progressHandler) {
        this.progressHandler = progressHandler;
    }

    /**
     * Gets the currently used project name. This has either been set or
     * imported.
     * 
     * @return the currently used project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the currently used project name. This one will be used for the next
     * export.
     * 
     * @param name
     *            the new project name.
     */
    public void setProjectName(String name) {
        projectName = name;
    }

    /**
     * Gets the {@link UUID} for the given {@link Object}. If not existent a new
     * random one is generated and associated.
     * 
     * @param o
     *            {@link Object} to get the {@link UUID} for.
     * @return {@link UUID} for the given {@link Object}.
     */
    UUID getUUID(Object o) {
        UUID id = uuidMap.get(o);
        if (id == null) {
            id = UUID.randomUUID();
            assert !uuidMap.containsValue(id);
            uuidMap.put(o, id);
        }
        return id;
    }

    /**
     * Translate the given name to an {@link UUID}.
     * 
     * @param name
     *            the name to translate.
     * @return the corresponding {@link UUID} for the given name.
     */
    UUID nameToUUID(String name) {
        UUID id = nameToUUIDMap.get(name);
        if (id == null) {
            id = UUID.randomUUID();
            assert !nameToUUIDMap.containsValue(id);
            assert !uuidMap.containsValue(id);
            nameToUUIDMap.put(name, id);
        }
        return id;
    }

    /**
     * Get the corresponding {@link Object} to the given {@link UUID}. If there
     * is no such {@link Object} null is returned.
     * 
     * @param id
     *            {@link UUID} to get the {@link Object} for.
     * @return the corresponding {@link Object} to the given {@link UUID} or
     *         null.
     */
    Object getObject(UUID id) {
        return uuidMap.inverse().get(id);
    }

    /**
     * Assign the given {@link Object} with the given {@link UUID}. Neither the
     * {@link Object} nor the {@link UUID} should be assigned before.
     * 
     * @param o
     *            {@link Object} to assign.
     * @param id
     *            {@link UUID} to assign.
     */
    void setUUID(Object o, UUID id) {
        assert !uuidMap.containsKey(o);
        assert !uuidMap.containsValue(id);
        uuidMap.put(o, id);
    }

    /**
     * Register the given point in the converter. After that the
     * {@link #getUniquePosition()} method will not return this point.
     * 
     * @param position
     *            the point to register.
     */
    void registerPosition(Point position) {
        if (position.x + position.y > (maxPoint.x + maxPoint.y)) {
            maxPoint = position;
        }
    }

    /**
     * Create a new unique point and return it. The point will not be equal to
     * one registered with the {@link #registerPosition(Point)} method or a
     * point generated by this method.
     * 
     * @return new unique point
     */
    Point getUniquePosition() {
        maxPoint = new Point(maxPoint.x + 1, maxPoint.y);
        return maxPoint;
    }

    /**
     * Translates the give position by the current translation value of the
     * whole circuit.
     * 
     * @param position
     *            the position to translate.
     * @return the translated position.
     */
    Point translatePosition(Point position) {
        return new Point(position.x + translation.x, position.y + translation.y);
    }

    /**
     * Mark the {@link Circuit} with the given name as visited.
     * 
     * @param name
     *            the name of the circuit to mark.
     */
    void setCircuitVisited(String name) {
        visitedCircuits.add(name);
    }

    /**
     * Check if the {@link Circuit} with the given name was already visited.
     * 
     * @param name
     *            the name of the {@link Circuit} to check.
     * @return true if the {@link Circuit} was visited, otherwise false.
     */
    boolean isCircuitVisited(String name) {
        return visitedCircuits.contains(name);
    }

    /**
     * Initialize all attributes for import.
     */
    private void initImport() {
        visitedCircuits.clear();
        uuidMap.clear();
        nameToUUIDMap.clear();
    }

    /**
     * Import a {@link ComponentType} from the given {@link InputStream}.
     * 
     * @param inputStream
     *            the {@link InputStream} to import.
     * @return the imported {@link ComponentType}.
     * @throws SEPFormatImportException
     */
    public ComponentType importComponentType(InputStream inputStream)
            throws SEPFormatImportException {
        initImport();
        loadXml(inputStream);
        Element root = doc.getDocumentElement();
        if (!root.getTagName().equals("circuits")) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.tagHasToHaveName", "root",
                    "circuits", root.getTagName());
        }
        if (!root.getAttribute("xmlns:fck").equals(
                "http://git.sep2011.de/funckit")) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.attributeHasToBe", "circuits",
                    "xmlns:fck", "http://git.sep2011.de/funckit",
                    root.getAttribute("xmlns:fck"));
        }
        if (!root.getAttribute("fck:componentType").equals("true")) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.attributeHasToBe", "circuits",
                    "fck:componentType", "true",
                    root.getAttribute("fck:componentType"));
        }
        String mainCircuit = ensureAttribute(root, "main");

        if (progressHandler != null) {
            progressHandler.handleCircuitsToImport(root.getElementsByTagName(
                    "circuit").getLength());
            progressHandler.handleComponentsToImport(root.getElementsByTagName(
                    "component").getLength());
            progressHandler.handleConnectionsToImport(root
                    .getElementsByTagName("connection").getLength());
        }
        return ComponentTypeImporter.importComponentType(doc, mainCircuit,
                this, true, false);
    }

    /**
     * Import a circuit from the given input stream. The stream has to hold a
     * file in valid funCKit or SEP format. Otherwise a
     * {@link SEPFormatImportException} is thrown.
     */
    @Override
    public Circuit doImport(InputStream inputStream)
            throws SEPFormatImportException {
        initImport();
        loadXml(inputStream);

        Element root = doc.getDocumentElement();
        if (!root.getTagName().equals("circuits")) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.tagHasToHaveName", "root",
                    "circuits", root.getTagName());
        }

        boolean funckitFormat =
                root.getAttribute("xmlns:fck").equals(
                        "http://git.sep2011.de/funckit");

        String mainCircuit = ensureAttribute(root, "main");
        if (funckitFormat) {
            projectName = ensureAttribute(root, "fck:projectname");
        } else {
            projectName = mainCircuit;
        }

        if (progressHandler != null) {
            progressHandler.handleCircuitsToImport(root.getElementsByTagName(
                    "circuit").getLength());
            progressHandler.handleComponentsToImport(root.getElementsByTagName(
                    "component").getLength());
            progressHandler.handleConnectionsToImport(root
                    .getElementsByTagName("connection").getLength());
        }

        return ComponentTypeImporter.importComponentType(doc, mainCircuit,
                this, funckitFormat, true).getCircuit();
    }

    /**
     * Ensures that the given tag has the given attribute. If not an
     * {@link SEPFormatImportException} is thrown.
     * 
     * @param tag
     *            the tag to ensure the attribute for.
     * @param attribute
     *            the attribute to ensure.
     * @return the value of the given attribute.
     * @throws SEPFormatImportException
     */
    String ensureAttribute(Element tag, String attribute)
            throws SEPFormatImportException {
        if (!tag.hasAttribute(attribute)) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.missingtag", tag.getNodeName(),
                    attribute);
        }
        return tag.getAttribute(attribute);
    }

    /**
     * Ensures that the given tag has the given attribute and that it is an
     * UUID. If not an {@link SEPFormatImportException} is thrown.
     * 
     * @param tag
     *            the tag to ensure the attribute for.
     * @param attribute
     *            the attribute to ensure.
     * @return the UUID value of the given attribute.
     * @throws SEPFormatImportException
     */
    UUID ensureUUID(Element tag, String attribute)
            throws SEPFormatImportException {
        String value = ensureAttribute(tag, attribute);
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.attributeHasToBe",
                    tag.getNodeName(), attribute, "UUID", value);
        }
    }

    /**
     * Ensures that the given tag has the given attribute and that it is an
     * integer. If not an {@link SEPFormatImportException} is thrown.
     * 
     * @param tag
     *            the tag to ensure the attribute for.
     * @param attribute
     *            the attribute to ensure.
     * @return the Integer value of the given attribute.
     * @throws SEPFormatImportException
     */
    int ensureInteger(Element tag, String attribute)
            throws SEPFormatImportException {
        String value = ensureAttribute(tag, attribute);
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.attributeHasToBe",
                    tag.getNodeName(), attribute, "integer", value);
        }
    }

    /**
     * Ensures that the given tag has the given attribute and that it is an
     * boolean. If not an {@link SEPFormatImportException} is thrown.
     * 
     * @param tag
     *            the tag to ensure the attribute for.
     * @param attribute
     *            the attribute to ensure.
     * @return the boolean value of the given attribute.
     * @throws SEPFormatImportException
     */
    boolean ensureBoolean(Element tag, String attribute)
            throws SEPFormatImportException {
        String value = ensureAttribute(tag, attribute);
        return Boolean.valueOf(value);
    }

    /**
     * Ensures that the given tag has the given attribute and that it is an
     * orientation. If not an {@link SEPFormatImportException} is thrown.
     * 
     * @param tag
     *            the tag to ensure the attribute for.
     * @param attribute
     *            the attribute to ensure.
     * @return the Orientation value of the given attribute.
     * @throws SEPFormatImportException
     */
    Orientation ensureOrientation(Element tag, String attribute)
            throws SEPFormatImportException {
        String value = ensureAttribute(tag, attribute);
        try {
            return Orientation.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.attributeHasToBe",
                    tag.getNodeName(), attribute, "orientation", value);
        }
    }

    /**
     * Ensures that if the given tag has the given attribute with the given
     * expected value it also has the given second attribute with the given
     * value. If not an {@link SEPFormatImportException} is thrown.
     * 
     * @param tag
     *            the tag to ensure the attribute for.
     * @param attribute1
     *            the attribute with the expected value.
     * @param expectedValue
     *            the expected value for attribute1.
     * @param attribute2
     *            the attribute to ensure the value for.
     * @param attributeValue
     *            the value to ensure for attribute2.
     * @throws SEPFormatImportException
     */
    void ensureTagWithValueHasAttribute(Element tag, String attribute1,
            String expectedValue, String attribute2, String attributeValue)
            throws SEPFormatImportException {
        if (tag.getAttribute(attribute1).equals(expectedValue)
                && !tag.getAttribute(attribute2).equals(attributeValue)) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.tagWithAttributeToValueHasToHaveAttributeWithValue",
                    tag, attribute1, expectedValue, attribute2, attributeValue);
        }
    }

    /**
     * Loads the xml file via DocumentBuilder from the inputStream in the
     * {@link SEPFormatConverter#doc}.
     * 
     * @param inputStream
     *            the stream to load the xml file from.
     * @throws SEPFormatImportException
     */
    private void loadXml(InputStream inputStream)
            throws SEPFormatImportException {
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.ParserConfigurationException", e);
        }
        try {
            docBuilder.setErrorHandler(this);
            doc =
                    docBuilder.parse(new InputStreamProgress(inputStream,
                            progressHandler));
        } catch (SAXException e) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.SAXException", e);
        } catch (IOException e) {
            throw new SEPFormatImportException(
                    "SEPFormatImportException.IOException", e);
        }
    }

    /**
     * Initialize all attributes for export.
     */
    private void initExport() {
        exportedComponentTypes.clear();
        componentTypesToExport.clear();
    }

    /**
     * Export the given {@link ComponentType} to the given {@link OutputStream}.
     * 
     * @param componentType
     *            {@link ComponentType} to export.
     * @param outputStream
     *            {@link OutputStream} to export to.
     * @throws SEPFormatExportException
     */
    public void exportComponentType(ComponentType componentType,
            OutputStream outputStream) throws SEPFormatExportException {
        initExport();
        componentTypesToExport.add(componentType);

        Element root =
                generateNewDocument(getUUID(componentType).toString(), true);

        // export component types as circuit
        while (!componentTypesToExport.isEmpty()) {
            ComponentType type = componentTypesToExport.poll();
            exportedComponentTypes.add(type);
            ComponentTypeExporter.exportComponentType(root, type, doc, this);
        }

        saveXml(outputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doExport(Circuit circuit, OutputStream outputStream)
            throws SEPFormatExportException {
        initExport();

        // translate to positive coordinates for sep format
        if (mode == Mode.SEPFORMAT) {
            Rectangle rect = circuit.getBoundingRectangle();
            int dx = Math.max(0, -rect.x);
            int dy = Math.max(0, -rect.y);
            translation = new Point(dx, dy);
        }
        Element mainCircuit = generateNewDocument("funCKitCircuit", false);

        exportCircuit(mainCircuit, circuit);

        // export component types as circuit
        while (!componentTypesToExport.isEmpty()) {
            ComponentType type = componentTypesToExport.poll();
            exportedComponentTypes.add(type);
            ComponentTypeExporter.exportComponentType(doc.getDocumentElement(),
                    type, doc, this);
        }

        saveXml(outputStream);
    }

    /**
     * Exports the given {@link Circuit} by exporting all {@link Element}s. Here
     * the {@link Brick}s are exported first, then the {@link Wire}s.
     * 
     * @param circuitElement
     *            the circuit tag to export to
     * @param circuit
     *            the {@link Circuit} to export.
     */
    void exportCircuit(Element circuitElement, Circuit circuit) {
        // export elements of the circuit. At first just export non wires
        // because of the xml sequence
        for (de.sep2011.funckit.model.graphmodel.Element element : circuit
                .getElements()) {
            // gl().debug("Current Element to export: " +
            // element.getClass().getSimpleName());
            if (!(element instanceof Wire)) {
                exportElement(circuitElement, element);
            }
        }

        // export the wires at last
        for (de.sep2011.funckit.model.graphmodel.Element element : circuit
                .getElements()) {
            if (element instanceof Wire) {
                exportElement(circuitElement, element);
            }
        }
    }

    /**
     * Exports the given {@link de.sep2011.funckit.model.graphmodel.Element} by
     * using the right {@link ElementExporter}. If a {@link Component} is
     * exported its type added to export too, if not already there.
     * 
     * @param c
     *            the circuit tag to export to.
     * @param element
     *            the {@link de.sep2011.funckit.model.graphmodel.Element} to
     *            export.
     */
    private void exportElement(Element c,
            de.sep2011.funckit.model.graphmodel.Element element) {
        ElementExporter exporter = exporterMap.get(element.getClass());
        if (exporter == null) {
            throw new UnsupportedOperationException("No exporter for '"
                    + element.getClass() + "' found!");
        }
        exporter.doExport(c, element, doc, this);

        // exported element was component of new type? => add to export
        if (element instanceof Component) {
            ComponentType type = ((Component) element).getType();
            if (!exportedComponentTypes.contains(type)
                    && !componentTypesToExport.contains(type)) {
                componentTypesToExport.offer(type);
            }
        }
    }

    /**
     * Creates a new {@link Document} with the right base layout for the funCKit
     * format and stores it in {@link SEPFormatConverter#doc}.
     * 
     * @param mainCircuitName
     *            the name to use for the main attribute of the circuits tag.
     * @param componentType
     *            generate document for a {@link ComponentType} or a
     *            {@link Circuit}.
     * @return the circuit tag of the main circuit if componentType is false,
     *         otherwise the root tag.
     * @throws SEPFormatExportException
     */
    private Element generateNewDocument(String mainCircuitName,
            boolean componentType) throws SEPFormatExportException {
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new SEPFormatExportException(
                    "SEPFormatExportException.ParserConfigurationException", e);
        }
        
        DOMImplementation impl = docBuilder.getDOMImplementation();
        doc = impl
                .createDocument("http://www.sosy-lab.org/Teaching/2011-WS-SEP/xmlns/circuits-1.0",
                        "circuits", null);
        Element root = doc.getDocumentElement();
        
        root.setAttribute("xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xmlns:fck", "http://git.sep2011.de/funckit");
        root.setAttribute("xsi:schemaLocation",
                "     http://git.sep2011.de/funckit    http://git.sep2011.de/funckit.xsd");
        root.setAttribute("main", mainCircuitName);
        if (componentType) {
            root.setAttribute("fck:componentType", "true");
            return root;
        }

        root.setAttribute("fck:projectname", projectName);
        Element circuit = doc.createElement("circuit");
        circuit.setAttribute("name", mainCircuitName);
        root.appendChild(circuit);
        return circuit;
    }

    /**
     * Saves the generated {@link SEPFormatConverter#doc} document in the given
     * outputStream.
     * 
     * @param outputStream
     *            {@link OutputStream} to save in.
     * @throws SEPFormatExportException
     */
    private void saveXml(OutputStream outputStream)
            throws SEPFormatExportException {
        doc.normalize();
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(outputStream);
        TransformerFactory tf;
        try {
            tf = TransformerFactory.newInstance(
                    "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
           
        } catch (TransformerFactoryConfigurationError e) {
            Log.gl().debug(e.getMessage());
            Log.gl().debug("java internal TransformerFactroy " +
            		"could not be loaded trying default TransformerFactory");
            tf = TransformerFactory.newInstance();
        }
        
        Log.gl().debug("Using XML TransformerFactory: " + tf);
        Transformer serializer;
        try {
            serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes"); // newlines
            serializer.setOutputProperty( // indentation
                    "{http://xml.apache.org/xslt}indent-amount", "2");

        } catch (TransformerConfigurationException e) {
            throw new SEPFormatExportException(
                    "SEPFormatExportException.TransformerConfigurationException",
                    e);
        }
        try {
            serializer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            throw new SEPFormatExportException(
                    "SEPFormatExportException.TransformerException", e);
        }
    }

    /**
     * Calls the currently set {@link SEPFormatConverterProgressHandler} and
     * notifies him about the given warning.
     * 
     * @param message
     *            the warning message.
     * @param args
     *            additional parameters.
     * @throws SEPFormatImportException
     *             if the handler decides this warning is fatal he throws an
     *             exception.
     */
    void importWarn(String message, Object... args)
            throws SEPFormatImportException {
        if (progressHandler != null) {
            progressHandler.handleImporterWarning(message, args);
        }
    }

    /**
     * Calls the currently set {@link SEPFormatConverterProgressHandler} and
     * notifies him about the given warning.
     * 
     * @param message
     *            the warning message.
     * @param args
     *            additional parameters.
     * @throws SEPFormatExportException
     *             if the handler decides this warning is fatal he throws an
     *             exception.
     */
    void exportWarn(String message, Object... args)
            throws SEPFormatExportException {
        if (progressHandler != null) {
            progressHandler.handleExporterWarning(message, args);
        }
    }

    /**
     * Calls the currently set {@link SEPFormatConverterProgressHandler} and
     * notifies him that a circuit has been imported.
     * 
     * @throws SEPFormatImportException
     */
    void circuitImported() throws SEPFormatImportException {
        if (progressHandler != null) {
            progressHandler.handleCircuitImported();
        }
    }

    /**
     * Calls the currently set {@link SEPFormatConverterProgressHandler} and
     * notifies him that a component has been imported.
     * 
     * @throws SEPFormatImportException
     */
    void componentImported() throws SEPFormatImportException {
        if (progressHandler != null) {
            progressHandler.handleComponentImported();
        }
    }

    /**
     * Calls the currently set {@link SEPFormatConverterProgressHandler} and
     * notifies him that a connection has been imported.
     * 
     * @throws SEPFormatImportException
     */
    void connectionImported() throws SEPFormatImportException {
        if (progressHandler != null) {
            progressHandler.handleConnectionImported();
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

}
