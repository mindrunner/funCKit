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
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides a method for exporting {@link Wire}s.
 */
class WireExporter extends ElementExporter {

    /**
     * {@inheritDoc} In this case export the given {@link Wire}.
     */
    @Override
    void doExport(Element xmlnode,
            de.sep2011.funckit.model.graphmodel.Element element, Document doc,
            SEPFormatConverter converter) {
        if (!(element instanceof Wire)) {
            throw new UnsupportedOperationException(
                    "This Element is not a Wire.");
        }
        Wire wire = (Wire) element;
        Element connection = doc.createElement("connection");
        AccessPoint source = wire.getFirstAccessPoint();
        AccessPoint target = wire.getSecondAccessPoint();

        // if wire is directed ensure right semantic of source and target
        if (source instanceof Input && target instanceof Output) {
            AccessPoint temp = source;
            source = target;
            target = temp;
        }

        setBrickAndPort(connection, "source", source, converter, false);
        setBrickAndPort(connection, "target", target, converter, false);
        connection.setAttribute("fck:name", wire.getName());

        xmlnode.appendChild(connection);
    }

    /**
     * Sets the source/target attribute and the -Port attribute.
     * 
     * @param connection
     *            the connection tag to export to.
     * @param name
     *            the name of the attribute to set. Has to be "source" or
     *            "target".
     * @param point
     *            the {@link AccessPoint} to export.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     * @param sepOnly
     */
    static void setBrickAndPort(Element connection, String name,
            AccessPoint point, SEPFormatConverter converter, boolean sepOnly) {

        // set referenced component + port for sep format
        connection.setAttribute(name, converter.getUUID(point.getBrick())
                .toString());
        connection.setAttribute(name + "Port", getPortName(point, converter));

        // store accesspoint reference for funCKit format
        if (!sepOnly) {
            connection.setAttribute("fck:" + name, converter.getUUID(point)
                    .toString());
        }
    }

    /**
     * Calculates the port name to use for this {@link AccessPoint}. This is
     * only needed for SEP format compatibility.
     * 
     * @param point
     *            {@link AccessPoint} to calculate the name for.
     * @param converter
     *            {@link SEPFormatConverter} to use for exporting.
     * @return the name of the {@link AccessPoint} needed by the SEP format.
     */
    private static String getPortName(AccessPoint point, SEPFormatConverter converter) {
        Brick b = point.getBrick();
        if (b instanceof SwitchImpl) {
            assert ((SwitchImpl) b).getOutputO() == point;
            return "o";
        } else if (b instanceof Light) {
            assert ((Light) b).getInputA() == point;
            return "a";
        } else if (b instanceof And) {
            And a = (And) b;
            if (a.getInputA() == point) {
                return "a";
            } else if (a.getInputB() == point) {
                return "b";
            } else if (a.getOutputO() == point) {
                return "o";
            }
            assert false;
        } else if (b instanceof Or) {
            Or o = (Or) b;
            if (o.getInputA() == point) {
                return "a";
            } else if (o.getInputB() == point) {
                return "b";
            } else if (o.getOutputO() == point) {
                return "o";
            }
            assert false;
        } else if (b instanceof Not) {
            Not n = (Not) b;
            if (n.getInputA() == point) {
                return "a";
            } else if (n.getOutputO() == point) {
                return "o";
            }
            assert false;
        } else if (b instanceof IdPoint) {
            IdPoint i = (IdPoint) b;
            if (i.getInputA() == point) {
                return "a";
            } else if (i.getOutputO() == point) {
                return "o";
            }
            assert false;
        } else if (b instanceof Component) {

            // set uuid of inner point as port because this will be the name of
            // the dummy in/out inserted
            AccessPoint innerPoint = ((Component) b).getInner(point);
            return converter.getUUID(innerPoint).toString();
        }
        assert false;
        throw new IllegalArgumentException("Cannot get name of AccessPoint.");
    }
}
