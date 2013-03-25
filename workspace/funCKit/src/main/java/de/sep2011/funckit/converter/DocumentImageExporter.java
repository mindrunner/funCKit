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

import de.sep2011.funckit.drawer.*;
import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.Settings;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.io.OutputStream;

/**
 * Exports a Circuit into a (XML) Document Image.
 */
public abstract class DocumentImageExporter extends ImageExporter {

    SVGGraphics2D graphicsContext;
    double width;
    double height;

    /**
     * Creates a new {@link DocumentImageExporter}.
     * 
     * @param settings
     *            settings to use
     */
    protected DocumentImageExporter(Settings settings) {
        super(settings);
    }

    private Document createDocument(Rectangle rectangle) {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        Document document = impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();

        Element root = document.getDocumentElement();
        root.setAttributeNS(null, "width", String.valueOf((int) rectangle.getWidth()));
        root.setAttributeNS(null, "height", String.valueOf((int) rectangle.getHeight()));

        return document;
    }

    @Override
    protected abstract void saveToStream(OutputStream outputStream);

    @Override
    public void doExport(Circuit circuit, OutputStream outputStream) {

        Rectangle rectangle = this.getRect(circuit);
        Document document = this.createDocument(rectangle);

        this.graphicsContext = new SVGGraphics2D(document);
        this.graphicsContext.setColor(Color.WHITE);
        this.graphicsContext.fillRect(0, 0, (int) rectangle.getWidth(), (int) rectangle.getHeight());
        Drawer drawer = new FancyDrawer(settings);

        repositionRectangle(rectangle, graphicsContext);

        drawer.setGraphics(this.graphicsContext);
        for (de.sep2011.funckit.model.graphmodel.Element element : circuit.getElements()) {
            ElementState state = new ElementState();
            Layout layout = new Layout();
            DrawAction action = DecisionTable.resolve(state);
            drawer.setLayout(layout);
            drawer.setAction(action);
            element.dispatch(drawer);
        }
        this.saveToStream(outputStream);
    }

}