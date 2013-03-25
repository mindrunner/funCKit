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
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides a method for exporting {@link Component}s.
 */
class ComponentExporter extends ElementExporter {

    /**
     * {@inheritDoc} In this case export the given {@link Component}.
     */
    @Override
    void doExport(Element xmlnode,
            de.sep2011.funckit.model.graphmodel.Element element, Document doc,
            SEPFormatConverter converter) {
        if (!(element instanceof Component)) {
            throw new UnsupportedOperationException(
                    "This Element is not a Component");
        }
        Component comp = (Component) element;
        Element sepcomp = doc.createElement("component");
        sepcomp.setAttribute("type", "circuit");
        sepcomp.setAttribute("type2", converter.getUUID(comp.getType())
                .toString());
        sepcomp.setAttribute("name", converter.getUUID(comp).toString());
        ElementExporter.exportPosition(sepcomp, comp.getPosition(), converter);
        sepcomp.setAttribute("fck:width",
                String.valueOf(comp.getDimension().width));
        sepcomp.setAttribute("fck:height",
                String.valueOf(comp.getDimension().height));
        sepcomp.setAttribute("fck:orientation", comp.getOrientation()
                .toString());
        sepcomp.setAttribute("fck:delay", String.valueOf(comp.getDelay()));
        sepcomp.setAttribute("fck:name", comp.getName());

        for (Input input : comp.getInputs()) {
            AccessPointExporter.exportAccessPoint(sepcomp, input, doc,
                    converter);
        }
        for (Output output : comp.getOutputs()) {
            AccessPointExporter.exportAccessPoint(sepcomp, output, doc,
                    converter);
        }

        for (Input input : comp.getInputs()) {
            exportAccessPointMapping(sepcomp, comp, input, doc, converter);
        }
        for (Output output : comp.getOutputs()) {
            exportAccessPointMapping(sepcomp, comp, output, doc, converter);
        }

        xmlnode.appendChild(sepcomp);
    }

    /**
     * Exports the mapping of the given {@link AccessPoint} from the given
     * {@link Component}.
     * 
     * @param xmlnode
     *            component tag to export to.
     * @param comp
     *            {@link Component} to export the mapping for.
     * @param point
     *            {@link AccessPoint} to export the mapping for.
     * @param doc
     *            {@link Document} to export in.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     */
    private void exportAccessPointMapping(Element xmlnode, Component comp,
            AccessPoint point, Document doc, SEPFormatConverter converter) {
        Element mapping = doc.createElement("fck:mapping");

        mapping.setAttribute("fck:outerPoint", converter.getUUID(point)
                .toString());
        mapping.setAttribute("fck:innerPoint",
                converter.getUUID(comp.getInner(point)).toString());

        xmlnode.appendChild(mapping);
    }
}
