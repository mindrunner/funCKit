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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.commands.PasteCommand;
import de.sep2011.funckit.test.factory.circuit.ComplexComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.ComponentCircuit1Factory;
import de.sep2011.funckit.util.command.Command;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link PasteCommand}.
 */
public class PasteCommandTest {

    /**
     * This test pastes the contents of a {@link ComponentCircuit1Factory}'s
     * Circuit to a Circuit from {@link ComponentCircuit1Factory} by checking if
     * they are contained then and removed after undo.
     */
    @Test
    public void test() {
        Circuit toPasteToCircuit = new ComplexComponentCircuit1Factory()
                .getCircuit();
        Circuit circuitToPaste = new ComponentCircuit1Factory(true)
                .getCircuit();

        Set<Element> circuitToPasteOrigElems = new LinkedHashSet<Element>(
                circuitToPaste.getElements());
        Set<Element> toPasteToCircuitOrigElems = new LinkedHashSet<Element>(
                toPasteToCircuit.getElements());

        Command pasteCommand = new PasteCommand(toPasteToCircuit,
                circuitToPaste);
        pasteCommand.execute();

        assertEquals(
                circuitToPasteOrigElems.size()
                        + toPasteToCircuitOrigElems.size(), toPasteToCircuit
                        .getElements().size());

        assertTrue(toPasteToCircuit.getElements().containsAll(
                circuitToPasteOrigElems));

        pasteCommand.undo();
        assertEquals(toPasteToCircuitOrigElems.size(), toPasteToCircuit
                .getElements().size());

        for (Element elem : circuitToPasteOrigElems) {
            assertFalse(toPasteToCircuit.getElements().contains(elem));
        }

        pasteCommand.execute();

        assertEquals(
                circuitToPasteOrigElems.size()
                        + toPasteToCircuitOrigElems.size(), toPasteToCircuit
                        .getElements().size());

        assertTrue(toPasteToCircuit.getElements().containsAll(
                circuitToPasteOrigElems));

    }

}
