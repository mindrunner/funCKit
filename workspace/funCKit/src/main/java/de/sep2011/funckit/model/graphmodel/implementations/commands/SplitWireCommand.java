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

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.ComplexCommand;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that splits a Wire by replacing it with 2 Wires connected by a
 * {@link IdPoint}.
 */
public class SplitWireCommand extends ComplexCommand {

    private final Circuit circuit;
    private boolean executed = false;
    private final Wire wire;
    private final IdPoint splitIdPoint;

    /**
     * Create a new ConnectCommand.
     * 
     * @param c
     *            the Circuit to operate on
     * @param wire
     *            Wire to split
     * @param splitPoint
     *            IdPoint used to connect the new Wires
     */
    public SplitWireCommand(Circuit c, Wire wire, IdPoint splitPoint) {
        this.circuit = c;
        this.wire = wire;
        this.splitIdPoint = splitPoint;
    }

    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException(
                    "Command has been executed multiple times");
        }

        executed = true;

        Log.gl().debug("Execute split wire Operation");

        /*
         * simply replay the commands inside the dispatcher to avoid problems
         */
        if (getDispatcher().canStepForward()) {
            getDispatcher().replay();
            return;
        }

        /* Remove old wire */
        getDispatcher().dispatch(
                new RemoveWireCommand(circuit, wire)
                        .setNotifyObserversHint(false));

        /* Add new IdPoint */
        getDispatcher().dispatch(
                new AddBrickCommand(circuit, splitIdPoint)
                        .setNotifyObserversHint(false));

        AccessPoint fstAp = wire.getFirstAccessPoint();
        AccessPoint sndAp = wire.getSecondAccessPoint();

        /* Connect first 2 APs */
        if (fstAp instanceof Input) {
            getDispatcher().dispatch(
                    new BareConnectCommand(circuit, fstAp, splitIdPoint
                            .getOutputO()).setNotifyObserversHint(false));
        } else if (fstAp instanceof Output) {
            getDispatcher().dispatch(
                    new BareConnectCommand(circuit, fstAp, splitIdPoint
                            .getInputA()).setNotifyObserversHint(false));
        }

        /* Connect second 2 APs */
        if (sndAp instanceof Input) {
            getDispatcher().dispatch(
                    new BareConnectCommand(circuit, sndAp, splitIdPoint
                            .getOutputO()).setNotifyObserversHint(false));
        } else if (sndAp instanceof Output) {
            getDispatcher().dispatch(
                    new BareConnectCommand(circuit, sndAp, splitIdPoint
                            .getInputA()).setNotifyObserversHint(false));
        }

        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("Cannot undo a Unexecuted Command");
        }

        executed = false;

        Log.gl().debug("Execute split wire Operation");
        getDispatcher().rewind();
        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

}
