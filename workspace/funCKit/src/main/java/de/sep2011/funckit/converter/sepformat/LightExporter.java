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

import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides a method for exporting {@link Light}s.
 */
class LightExporter extends ElementExporter {

    /**
     * {@inheritDoc} In this case export the given {@link Light}.
     */
    @Override
    void doExport(Element xmlnode,
            de.sep2011.funckit.model.graphmodel.Element element, Document doc,
            SEPFormatConverter converter) {

        if (!(element instanceof Light)) {
            throw new UnsupportedOperationException(
                    "This Element is not a Light.");
        }
        Light li = (Light) element;

        Element component = doc.createElement("component");

        component.setAttribute("type", "out");
        component.setAttribute("name", converter.getUUID(li).toString());
        ElementExporter.exportPosition(component, li.getPosition(), converter);
        component.setAttribute("fck:type", "light"); // A real light, not a
                                                     // output of a component!
        component.setAttribute("fck:width",
                String.valueOf(li.getDimension().width));
        component.setAttribute("fck:height",
                String.valueOf(li.getDimension().height));
        component.setAttribute("fck:orientation", li.getOrientation()
                .toString());
        component.setAttribute("fck:name", li.getName());

        for (Input input : li.getInputs()) {
            AccessPointExporter.exportAccessPoint(component, input, doc,
                    converter);
        }
        for (Output output : li.getOutputs()) {
            AccessPointExporter.exportAccessPoint(component, output, doc,
                    converter);
        }

        xmlnode.appendChild(component);
    }

}
