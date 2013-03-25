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
import de.sep2011.funckit.model.graphmodel.implementations.BrickWireDistinguishDispatcher;
import de.sep2011.funckit.util.command.Command;

/**
 * Command that changes the name of an {@link Element} and undoes this.
 */
public class ElementSetNameCommand extends Command {

    private final Element element;
    private final String newName;
    private final String oldName;
    private final Circuit circuit;

    /**
     * Creates a new {@link ElementSetNameCommand}.
     * 
     * @param e
     *            the {@link Element} to set the Name on
     * @param name
     *            new Name of the {@link Element}
     * @param c
     *            the Circuit containing this Element, null if it is in no
     *            Circuit
     */
    public ElementSetNameCommand(Element e, String name, Circuit c) {
        this.element = e;
        this.newName = name;
        this.circuit = c;
        this.oldName = element.getName();
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        element.setName(this.newName);
        notifyAboutChange();

    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        element.setName(oldName);
        notifyAboutChange();
    }

    private void notifyAboutChange() {
        if (circuit == null) {
            return;
        }

        circuit.setChanged();
        circuit.getInfo().setElementNameChanged(true);

        new BrickWireDistinguishDispatcher() {

            {
                element.dispatch(this);
            }

            @Override
            protected void visitWire(Wire w) {
                circuit.getInfo().getChangedWires().add(w);

            }

            @Override
            protected void visitBrick(Brick b) {
                circuit.getInfo().getChangedBricks().add(b);

            }
        };

        if (isNotifyObserversHint()) {
            circuit.notifyObservers();
        }
    }

}
