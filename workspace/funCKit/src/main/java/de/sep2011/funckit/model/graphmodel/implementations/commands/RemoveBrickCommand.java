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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.ComplexCommand;

/**
 * Command that removes a {@link Brick} from a {@link Circuit} (see
 * {@link Circuit#removeBrick(Brick)} and undoes this. It is very important to
 * notice, that removed brick can still be contained in sets of some other
 * models and thus hold information about selection or erroneous bricks, so you
 * have to check those sets or lists after executing this command, too.
 */
public class RemoveBrickCommand extends ComplexCommand {
    private final Circuit circuit;
    private final Brick brick;

    /**
     * Create a new {@link RemoveBrickCommand}.
     * 
     * @param circuit
     *            {@link Circuit} to operate on
     * @param brick
     *            {@link Brick} to Remove
     */
    public RemoveBrickCommand(Circuit circuit, Brick brick) {
        this.circuit = circuit;
        this.brick = brick;
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        /**
         * First remove all connected wires.
         */
        Set<Input> inputs = brick.getInputs();
        Set<Output> outputs = brick.getOutputs();
        Set<Wire> removedWires = new LinkedHashSet<Wire>();

        /*
         * Save RemoveWireCommands in lists first and then dispatch them. This
         * should avoid the beloved ConcurrentModificationException.
         */
        List<Command> inputsToDispatch = new LinkedList<Command>();
        List<Command> outputsToDispatch = new LinkedList<Command>();

        for (Input i : inputs) {
            for (Wire w : i.getWires()) {
                Command remove = new RemoveWireCommand(circuit, w);
                remove.setNotifyObserversHint(false);
                inputsToDispatch.add(remove);
                removedWires.add(w);
            }
        }

        for (Output o : outputs) {
            for (Wire w : o.getWires()) {
                Command remove = new RemoveWireCommand(circuit, w);
                remove.setNotifyObserversHint(false);
                outputsToDispatch.add(remove);
                removedWires.add(w);
            }
        }

        for (Command cmd : inputsToDispatch) {
            getDispatcher().dispatch(cmd);
        }

        for (Command cmd : outputsToDispatch) {
            getDispatcher().dispatch(cmd);
        }

        /*
         * Then remove brick.
         */
        circuit.removeBrick(brick);

        notifyObserversOn(circuit, isNotifyObserversHint());
    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        circuit.addBrick(brick);
        getDispatcher().rewind();
        notifyObserversOn(circuit, isNotifyObserversHint());
    }
}