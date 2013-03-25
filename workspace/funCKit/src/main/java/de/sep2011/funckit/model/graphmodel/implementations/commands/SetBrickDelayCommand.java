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
import de.sep2011.funckit.util.command.Command;

/**
 * Changes delay of a given brick and remembers the change.
 */
public class SetBrickDelayCommand extends Command {
    private final Brick brick;
    private final int oldDelay;
    private final int newDelay;
    private final Circuit circuit;

    /**
     * Constructs command with brick reference and new delay value.
     * 
     * @param brick
     *            Brick to change delay on (reference to brick in a circuit)
     * @param delay
     *            New delay value.
     * @param c
     *            the Circuit containing this Element, null if it is in no
     *            Circuit
     */
    public SetBrickDelayCommand(Brick brick, int delay, Circuit c) {
        this.brick = brick;
        this.newDelay = delay;
        this.circuit = c;
        this.oldDelay = brick.getDelay();
    }

    /**
     * {@inheritDoc} Performs delay change of brick.
     */
    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        brick.setDelay(newDelay);
        notifyAboutChange();

    }

    /**
     * {@inheritDoc} Undoes delay change of brick.
     */
    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        brick.setDelay(oldDelay);
        notifyAboutChange();
    }

    private void notifyAboutChange() {
        if (circuit == null) {
            return;
        }

        circuit.setChanged();
        circuit.getInfo().setBrickDelayChanged(true).getChangedBricks()
                .add(brick);
        if (isNotifyObserversHint()) {
            circuit.notifyObservers();
        }
    }
}
