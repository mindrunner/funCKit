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

package de.sep2011.funckit.model.graphmodel.implementations.commands;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.ComplexCommand;

import java.util.LinkedHashSet;
import java.util.Set;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that removes a Set of {@link Element}s from a {@link Circuit}.
 * 
 */
public class RemoveBunchOfElementsCommand extends ComplexCommand {

    private final Set<Element> elements;
    private final Circuit circuit;

    /**
     * Creates a new {@link RemoveBunchOfElementsCommand}.
     * 
     * @param c
     *            circuit to operate on
     * @param elements
     *            elements to remove
     */
    public RemoveBunchOfElementsCommand(Circuit c, Set<Element> elements) {
        this.circuit = c;
        this.elements = new LinkedHashSet<Element>(elements);
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        for (Element e : elements) {
            if (e instanceof Wire) {
                getDispatcher().dispatch(
                        new RemoveWireCommand(circuit, (Wire) e)
                                .setNotifyObserversHint(false));
            } else if (e instanceof Brick) {
                getDispatcher().dispatch(
                        new RemoveBrickCommand(circuit, (Brick) e)
                                .setNotifyObserversHint(false));
            } else {
                Log.gl().warn(
                        "Try to remove an unknown Element subtype"
                                + e.getClass());
            }
        }

        CircuitReorganizeUtility.reorganizeCircuit(circuit, getDispatcher(),
                isNotifyObserversHint());

        notifyObserversOn(circuit, isNotifyObserversHint());
    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        getDispatcher().rewind();
        notifyObserversOn(circuit, isNotifyObserversHint());
    }

}
