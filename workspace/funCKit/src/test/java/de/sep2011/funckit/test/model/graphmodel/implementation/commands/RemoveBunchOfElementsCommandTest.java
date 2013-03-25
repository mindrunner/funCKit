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

package de.sep2011.funckit.test.model.graphmodel.implementation.commands;

import de.sep2011.funckit.circuitfactory.FullAdderFactory;
import de.sep2011.funckit.circuitfactory.HalfAdderFactory;
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.implementations.ElementImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.RemoveBunchOfElementsCommand;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * This class contains Tests for the {@link RemoveBunchOfElementsCommand}.
 */
public class RemoveBunchOfElementsCommandTest {

    /**
     * Tests the Command using a Circuit from {@link FullAdderFactory} and
     * remove all {@link Element}s from it (and checks if they are there again
     * after undo).
     */
    @Test
    public void testRemoveBunchOfElementsCommand() {
        Circuit circuit = (new FullAdderFactory()).getCircuit();
        Set<Element> toRemove = (new HalfAdderFactory()).getCircuit()
                .getElements();
        circuit.getElements().addAll(toRemove);
        RemoveBunchOfElementsCommand command = new RemoveBunchOfElementsCommand(
                circuit, toRemove);
        command.execute();

        Circuit expected = (new FullAdderFactory()).getCircuit();
        assertTrue(expected.equalGraph(circuit));

        command.undo();

        expected.getElements().addAll(
                (new HalfAdderFactory()).getCircuit().getElements());
        assertTrue(expected.equalGraph(circuit));
    }

    /**
     * Tests the command with an unknown (dummy) subtype of {@link Element}.
     */
    @Test
    public void testUnknownElement() {
        Circuit circuit = (new FullAdderFactory()).getCircuit();
        Set<Element> toRemove = new LinkedHashSet<Element>();

        /* Add a dummy Element of unknown type */
        toRemove.add(new ElementImpl() {

            @Override
            public void setPosition(Point position) {

            }

            @Override
            public void setDimension(Dimension dimension) {

            }

            @Override
            public void setBoundingRect(Rectangle rectangle) {

            }

            @Override
            public boolean intersects(Rectangle2D rectangle) {
                return false;
            }

            @Override
            public Element getNewInstance(Point position) {
                return null;
            }

            @Override
            public Rectangle getBoundingRect() {
                return null;
            }

            @Override
            public AccessPoint getAccessPointAtPosition(Point position,
                    int tolerance) {
                return null;
            }

            @Override
            public void dispatch(ElementDispatcher dispatcher) {

            }
        });

        RemoveBunchOfElementsCommand command = new RemoveBunchOfElementsCommand(
                circuit, toRemove);
        command.execute();
    }
}
