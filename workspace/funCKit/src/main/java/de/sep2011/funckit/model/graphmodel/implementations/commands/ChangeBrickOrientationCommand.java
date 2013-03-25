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
import de.sep2011.funckit.util.command.Command;

/**
 * Changes brick to a new orientation and remembers change.
 */
public class ChangeBrickOrientationCommand extends Command {
    private final Brick brick;
    private final Brick.Orientation oldOrientation;
    private final Brick.Orientation newOrientation;

    /**
     * Constructs command with brick reference and new orientation value.
     * 
     * @param brick
     *            Brick to change orientation on (reference to brick in a
     *            circuit)
     * @param orientation
     *            new orientation
     */
    public ChangeBrickOrientationCommand(Brick brick,
            Brick.Orientation orientation) {
        this.brick = brick;
        this.newOrientation = orientation;
        this.oldOrientation = brick.getOrientation();
    }

    /**
     * Performs orientation change of brick.
     */
    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        brick.setOrientation(newOrientation);

    }

    /**
     * Undoes orientation change of brick.
     */
    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        brick.setOrientation(oldOrientation);
    }
}
