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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.util.command.Command;

import java.awt.Dimension;

/**
 * Command for individual brick size change with possibility to undo operation.
 */
public class ChangeElementSizeCommand extends Command {
    private final Element element;
    private final Dimension oldSize;
    private final Dimension newSize;
    private final Circuit circuit;

    /**
     * Constructs command with brick reference and new size values.
     * 
     * @param element
     * @param dimension
     *            new bounding rectangle
     * @param c
     *            the Circuit containing this Element, null if it is in no
     *            Circuit
     */
    public ChangeElementSizeCommand(Element element, Dimension dimension,
            Circuit c) {
        this.element = element;
        this.newSize = new Dimension(dimension);
        this.oldSize = new Dimension(element.getDimension());
        this.circuit = c;
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        element.setDimension(new Dimension(newSize));
        notifyAboutChange();
    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        element.setDimension(oldSize);

        notifyAboutChange();
    }

    private void notifyAboutChange() {
        if (circuit == null) {
            return;
        }

        circuit.setChanged();
        circuit.getInfo().setElementBoundsChanged(true);
        if (isNotifyObserversHint()) {
            circuit.notifyObservers();
        }
    }
}
