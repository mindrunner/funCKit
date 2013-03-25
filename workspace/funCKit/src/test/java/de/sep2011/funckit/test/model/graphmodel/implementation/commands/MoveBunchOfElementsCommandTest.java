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
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.commands.MoveBunchOfElementsCommand;
import org.junit.Test;

import java.awt.Point;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * This class contains Tests for the {@link MoveBunchOfElementsCommand}.
 */
public class MoveBunchOfElementsCommandTest {

    /**
     * This test moves all {@link Element}s from the {@link FullAdderFactory}'s
     * Circuit to another position and checks if they are there (and if they are
     * back on undo).
     */
    @Test
    public void testMoveBunchOfElementsCommand() {
        Circuit circuit = (new FullAdderFactory()).getCircuit();
        Set<Element> toMove = (new HalfAdderFactory()).getCircuit()
                .getElements();
        circuit.getElements().addAll(toMove);
        MoveBunchOfElementsCommand command = new MoveBunchOfElementsCommand(
                circuit, toMove, 20, 20);
        command.execute();

        Circuit expected = (new FullAdderFactory()).getCircuit();
        Set<Element> moved = (new HalfAdderFactory()).getCircuit()
                .getElements();
        for (Element e : moved) {
            Point p = e.getPosition();
            p.translate(20, 20);
            e.setPosition(p);
        }
        expected.getElements().addAll(moved);
        assertTrue(expected.equalGraph(circuit));

        command.undo();
        expected.getElements().removeAll(moved);
        expected.getElements().addAll(
                (new HalfAdderFactory()).getCircuit().getElements());
        assertTrue(expected.equalGraph(circuit));
    }

}
