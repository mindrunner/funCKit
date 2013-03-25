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
import de.sep2011.funckit.model.graphmodel.Input;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides a static method for exporting {@link AccessPoint}s.
 */
class AccessPointExporter {

    /**
     * Exports the given {@link AccessPoint} to the given component tag in the
     * given {@link Document}.
     * 
     * @param xmlnode
     *            component tag to export to.
     * @param point
     *            {@link AccessPoint} to export.
     * @param doc
     *            {@link Document} to export in.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     */
    static void exportAccessPoint(Element xmlnode, AccessPoint point,
            Document doc, SEPFormatConverter converter) {
        Element element = doc.createElement("fck:accesspoint");
        String type = (point instanceof Input) ? "input" : "output";

        element.setAttribute("fck:type", type);
        element.setAttribute("fck:name", point.getName());
        element.setAttribute("fck:id", converter.getUUID(point).toString());
        element.setAttribute("fck:posx", String.valueOf(point.getPosition().x));
        element.setAttribute("fck:posy", String.valueOf(point.getPosition().y));

        xmlnode.appendChild(element);

    }
}