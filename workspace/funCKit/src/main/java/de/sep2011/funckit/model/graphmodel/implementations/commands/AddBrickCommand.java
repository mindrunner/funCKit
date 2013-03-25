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
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.Command;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that adds a Brick to a {@link Circuit} (see
 * {@link Circuit#addBrick(Brick)}) and undoes this.
 */
public class AddBrickCommand extends Command {

    private final Circuit circuit;
    private final Brick brick;

    /**
     * Crate a new {@link AddBrickCommand}.
     * 
     * @param circuit
     *            {@link Circuit} to operate on
     * @param brick
     *            {@link Brick} to add
     */
    public AddBrickCommand(Circuit circuit, Brick brick) {
        this.circuit = circuit;
        this.brick = brick;
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        circuit.addBrick(brick);
        notifyObserversOn(circuit, isNotifyObserversHint());
        Log.gl().debug("Added " + brick);
    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        circuit.removeBrick(brick);
        notifyObserversOn(circuit, isNotifyObserversHint());
        Log.gl().debug("Removed " + brick);
    }
}