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
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.Command;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that connects two {@link AccessPoint}s with a Wire (see
 * {@link Circuit#connect(AccessPoint, AccessPoint)}) and undoes this.
 */
public class BareConnectCommand extends Command {

    private final Circuit circuit;
    private final Wire wire;

    /**
     * Create a new ConnectCommand.
     * 
     * @param c
     *            the Circuit to operate on
     * @param accessPoint1
     *            the first {@link AccessPoint}
     * @param accessPoint2
     *            the second {@link AccessPoint}
     */
    public BareConnectCommand(Circuit c, AccessPoint accessPoint1,
            AccessPoint accessPoint2) {
        this.circuit = c;
        wire = new WireImpl(accessPoint1, accessPoint2);
    }

    private boolean isNotConnected(AccessPoint ap1, AccessPoint ap2) {

        for (Element e : circuit.getElements()) {
            if (e instanceof Brick) {
                Brick b = (Brick) e;
                if (ap1.getBrick() == b) {
                    for (Wire w : ap1.getWires()) {
                        if (ap2.equals(w.getOther(ap1))) {
                            return false;
                        }
                    }
                } else if (ap2.getBrick() == b) {
                    for (Wire w : ap2.getWires()) {
                        if (ap1.equals(w.getOther(ap2))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void execute() {
        AccessPoint ap1 = wire.getFirstAccessPoint();
        AccessPoint ap2 = wire.getSecondAccessPoint();
        if (wire.getFirstAccessPoint() != wire.getSecondAccessPoint()) {
            if (isNotConnected(ap1, ap2)) {
                checkAndUpdateExecutedOnExecute();
                Log.gl().debug("Execute Connect Operation");
                ap1.addWire(wire);
                ap2.addWire(wire);
                circuit.addWire(wire);
                notifyObserversOn(circuit, isNotifyObserversHint());
            } else {
                Log.gl().debug("Cannot connect already Connected Accespoints");
            }
        } else {
            Log.gl().debug("Cannot connect to same AP");
        }
    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        Log.gl().debug("Undoing Connect Operation");
        wire.getFirstAccessPoint().removeWire(wire);
        wire.getSecondAccessPoint().removeWire(wire);
        circuit.removeWire(wire);
        notifyObserversOn(circuit, isNotifyObserversHint());
    }
    
    /**
     * Returns the Wire this command created.
     * 
     * @return the Wire this command created
     */
    public Wire getWire() {
    	return wire;
    }

}
