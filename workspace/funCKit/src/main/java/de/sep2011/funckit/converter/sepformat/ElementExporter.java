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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.Point;

/**
 * This class is used as interface with package visibility for the various
 * exporters for the various {@link Element}s.
 */
abstract class ElementExporter {

    /**
     * Export the given {@link de.sep2011.funckit.model.graphmodel.Element} as
     * new tag in the given circuit tag from the given {@link Document}.
     * 
     * @param xmlnode
     *            circuit tag to export to.
     * @param element
     *            {@link de.sep2011.funckit.model.graphmodel.Element} to export.
     * @param doc
     *            {@link Document} to export in.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     */
    abstract void doExport(Element xmlnode,
            de.sep2011.funckit.model.graphmodel.Element element, Document doc,
            SEPFormatConverter converter);

    /**
     * Exports the given position to the given component tag, considering the
     * current translation of the circuit.
     * 
     * @param component
     *            the component tag to export the position to.
     * @param position
     *            the position to export.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     */
    static void exportPosition(Element component, Point position,
            SEPFormatConverter converter) {
        converter.registerPosition(position);
        position = converter.translatePosition(position);
        component.setAttribute("posx", String.valueOf(position.x));
        component.setAttribute("posy", String.valueOf(position.y));
    }
}
