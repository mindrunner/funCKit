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

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.ComplexCommand;

/**
 * Command that moves a Set of {@link Element}s. Coordinates are
 * relative to the old position.
 * 
 */
public class MoveBunchOfElementsCommand extends ComplexCommand {

    private final Set<Element> elements;
    private final Circuit circuit;
    private final int dy;
    private final int dx;

    /**
     * Creates a new {@link MoveBunchOfElementsCommand}.
     * 
     * @param c
     *            circuit to operate on
     * @param elements
     *            elements to move
     * @param dx
     *            move by dx
     * @param dy
     *            move by dy
     */
    public MoveBunchOfElementsCommand(Circuit c, Set<Element> elements, int dx,
            int dy) {
        this.circuit = c;
        this.elements = new LinkedHashSet<Element>(elements);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        for (Element e : elements) {
            Point oldPoint = e.getBoundingRect().getLocation();
            Point newPos = new Point(oldPoint.x + dx, oldPoint.y + dy);

            Command cmd = new MoveElementCommand(e, newPos, circuit);
            cmd.setNotifyObserversHint(false);
            getDispatcher().dispatch(cmd);

        }

        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        getDispatcher().rewind();
        notifyObserversOn(circuit, isNotifyObserversHint());
    }
}
