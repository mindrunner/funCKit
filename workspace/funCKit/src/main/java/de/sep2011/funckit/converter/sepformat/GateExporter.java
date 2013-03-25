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

import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides a method for exporting {@link Gate}s.
 */
class GateExporter extends ElementExporter {

    /**
     * {@inheritDoc} In this case export the given {@link Gate}.
     */
    @Override
    void doExport(Element xmlnode,
            de.sep2011.funckit.model.graphmodel.Element element, Document doc,
            SEPFormatConverter converter) {
        if (!(element instanceof Gate)) {
            throw new UnsupportedOperationException(
                    "This Element is not a Gate.");
        }
        Gate gate = (Gate) element;
        Element component = doc.createElement("component");
        String type;
        if (gate instanceof And) {
            type = "and";
        } else if (gate instanceof Or) {
            type = "or";
        } else if (gate instanceof Not) {
            type = "not";
        } else if (gate instanceof IdPoint) {
            type = "id";
        } else {
            throw new UnsupportedOperationException("Gate of type '"
                    + gate.getClass() + "' not supported.");
        }
        component.setAttribute("type", type);
        component.setAttribute("name", converter.getUUID(gate).toString());
        ElementExporter
                .exportPosition(component, gate.getPosition(), converter);
        if (gate instanceof IdPoint) {
            component.setAttribute("fck:type", "idpoint"); // special idpoint
                                                           // for branching
                                                           // wires
        }
        component.setAttribute("fck:width",
                String.valueOf(gate.getDimension().width));
        component.setAttribute("fck:height",
                String.valueOf(gate.getDimension().height));
        component.setAttribute("fck:orientation", gate.getOrientation()
                .toString());
        component.setAttribute("fck:delay", String.valueOf(gate.getDelay()));
        component.setAttribute("fck:name", gate.getName());

        for (Input input : gate.getInputs()) {
            AccessPointExporter.exportAccessPoint(component, input, doc,
                    converter);
        }
        for (Output output : gate.getOutputs()) {
            AccessPointExporter.exportAccessPoint(component, output, doc,
                    converter);
        }

        xmlnode.appendChild(component);
    }
}
