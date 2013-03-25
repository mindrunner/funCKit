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
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.util.command.ComplexCommand;

/**
 * Command that removes a {@link Wire} from a {@link Circuit} (see
 * {@link Circuit#removeWire(Wire)} and undoes this.
 */
public class RemoveWireCommand extends ComplexCommand {
    private final Circuit circuit;
    private final Wire wire;

    /**
     * Creates a new {@link RemoveWireCommand}.
     * 
     * @param circuit
     *            {@link Circuit} to operate on
     * @param wire
     *            {@link Wire} to remove
     */
    public RemoveWireCommand(Circuit circuit, Wire wire) {
        assert wire.getFirstAccessPoint() != null;
        assert wire.getSecondAccessPoint() != null;
        this.circuit = circuit;
        this.wire = wire;
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        AccessPoint ap1 = wire.getFirstAccessPoint();
        AccessPoint ap2 = wire.getSecondAccessPoint();

        ap1.removeWire(wire);
        ap2.removeWire(wire);

        circuit.removeWire(wire);

        CircuitReorganizeUtility.reorganizeCircuit(circuit, getDispatcher(),
                isNotifyObserversHint());

        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        wire.getFirstAccessPoint().addWire(wire);
        wire.getSecondAccessPoint().addWire(wire);
        circuit.addWire(wire);
        notifyObserversOn(circuit, isNotifyObserversHint());
    }
}
