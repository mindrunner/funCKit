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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.util.command.Command;

/**
 * Command that disconnects two {@link AccessPoint}s (see
 * {@link Circuit#disconnect(AccessPoint, AccessPoint)} and undoes this.
 */
public class DisconnectCommand extends Command {

    private final Circuit circuit;
    private final AccessPoint accessPoint1;
    private final AccessPoint accessPoint2;
    private final Set<Wire> removedWires;

    /**
     * Create a new DisconnectCommand.
     * 
     * @param c
     *            the Circuit to operate on
     * @param accessPoint1
     *            the first {@link AccessPoint}
     * @param accessPoint2
     *            the second {@link AccessPoint}
     */
    public DisconnectCommand(Circuit c, AccessPoint accessPoint1,
            AccessPoint accessPoint2) {
        this.circuit = c;
        this.accessPoint1 = accessPoint1;
        this.accessPoint2 = accessPoint2;
        removedWires = new LinkedHashSet<Wire>();
    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        for (Iterator<Wire> i = accessPoint1.getWires().iterator(); i.hasNext();) {
            Wire w = i.next();
            if ((w.getFirstAccessPoint() == accessPoint1 && w
                    .getSecondAccessPoint() == accessPoint2)
                    || (w.getFirstAccessPoint() == accessPoint2 && w
                            .getSecondAccessPoint() == accessPoint1)) {
                i.remove();
                accessPoint2.removeWire(w);
                circuit.removeWire(w);
            }
            removedWires.add(w);
        }

        notifyObserversOn(circuit, isNotifyObserversHint());
    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        for (Wire w : removedWires) {
            w.getFirstAccessPoint().addWire(w);
            w.getSecondAccessPoint().addWire(w);
            circuit.addWire(w);
        }

        notifyObserversOn(circuit, isNotifyObserversHint());
    }

}
