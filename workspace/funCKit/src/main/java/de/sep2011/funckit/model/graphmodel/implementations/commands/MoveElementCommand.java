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
import javax.swing.text.Position;

import java.awt.Point;
import java.awt.Rectangle;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Changes position of brick contained in an circuit.
 */
public class MoveElementCommand extends Command {
    private final Element element;
    private final Point oldPosition;
    private final Point newPosition;
    private final Circuit circuit;

    /**
     * MoveBrickCommand needs only a Element reference contained in a circuit
     * and its new position.
     * 
     * @param element
     *            the element to move
     * @param newPosition
     *            new {@link Position} of the Element
     * @param c
     *            the Circuit containing this Element, null if it is in no
     *            Circuit
     */
    public MoveElementCommand(Element element, Point newPosition, Circuit c) {
        this.element = element;
        this.newPosition = newPosition;
        this.circuit = c;
        oldPosition = element.getBoundingRect().getLocation();
    }

    /**
     * {@inheritDoc} Performs the position change.
     */
    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        /* Change bounding rectangle of brick to new position. */
        element.setBoundingRect(new Rectangle(newPosition.x, newPosition.y,
                element.getBoundingRect().width,
                element.getBoundingRect().height));

        notifyAboutChange();
    }

    /**
     * {@inheritDoc} Undoes position change.
     */
    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        element.setBoundingRect(new Rectangle(oldPosition.x, oldPosition.y,
                element.getBoundingRect().width,
                element.getBoundingRect().height));

        notifyAboutChange();
    }

    private void notifyAboutChange() {
        if (circuit == null) {
            return;
        }

        circuit.setChanged();
        circuit.getInfo().setElementBoundsChanged(true);

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

        notifyObserversOn(circuit, isNotifyObserversHint());
    }
}
