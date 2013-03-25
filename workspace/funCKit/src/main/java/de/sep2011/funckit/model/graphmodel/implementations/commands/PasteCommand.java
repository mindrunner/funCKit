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
import de.sep2011.funckit.util.command.Command;

import java.util.LinkedList;
import java.util.List;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that that Pastes a set of Elements into the Circuit.
 */
public class PasteCommand extends Command {

    private final Circuit circuit;
    private final List<Wire> wires;
    private final List<Brick> bricks;

    /**
     * Crate a new {@link PasteCommand}.
     * 
     * @param circuit
     *            {@link Circuit} to operate on
     * @param circuitToPaste
     *            Circuit to paste into the circuit. Note that all elements will
     *            be removed from this {@link Circuit} as they cannot be in
     *            both.
     */
    public PasteCommand(Circuit circuit, Circuit circuitToPaste) {
        this.circuit = circuit;
        this.wires = new LinkedList<Wire>();
        this.bricks = new LinkedList<Brick>();

        for (Element e : circuitToPaste.getElements()) {
            if (e instanceof Brick) {
                this.bricks.add((Brick) e);
            } else if (e instanceof Wire) {
                this.wires.add((Wire) e);
            }
        }

        /* Simply clear it because of performance */
        circuitToPaste.getElements().clear();
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        for (Brick b : bricks) {
            circuit.addBrick(b);
        }

        /*
         * Note: We need not connect the Wires here as they are all already
         * connected
         */
        for (Wire w : wires) {
            circuit.addWire(w);
        }

        Log.gl().debug("executed: " + this);

        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        /*
         * Note: We need not disconnect the Wires as all connected Bricks are
         * removed too
         */
        for (Wire w : wires) {
            circuit.removeWire(w);
        }

        for (Brick b : bricks) {
            circuit.removeBrick(b);
        }

        Log.gl().debug("undone: " + this);

        notifyObserversOn(circuit, isNotifyObserversHint());

    }
}