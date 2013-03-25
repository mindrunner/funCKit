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
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.util.command.ComplexCommand;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that connects two {@link AccessPoint}s with a Wire (see
 * {@link Circuit#connect(AccessPoint, AccessPoint)}) and undoes this. Cares
 * about the right IdPoint connection of {@link IdPoint}s, regardless which of
 * its {@link AccessPoint}s are given.
 */
public class ConnectCommand extends ComplexCommand {

    private final BareConnectCommand connectCmd;
    private final Circuit circuit;

    private AccessPoint ap1;
    private AccessPoint ap2;

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
    public ConnectCommand(Circuit c, AccessPoint accessPoint1,
            AccessPoint accessPoint2) {
        circuit = c;
        this.ap1 = accessPoint1;
        this.ap2 = accessPoint2;

        // if (accessPoint1.getBrick() instanceof IdPoint
        // && accessPoint2.getBrick() instanceof IdPoint) {

        // }

        /* Handle IdPoints */
        if (accessPoint1.getBrick() instanceof IdPoint) {
            IdPoint idp = (IdPoint) accessPoint1.getBrick();

            if (accessPoint2 instanceof Input) {
                ap1 = idp.getOutputO();
            } else if (accessPoint2 instanceof Output) {
                ap1 = idp.getInputA();
            }

        }

        if (accessPoint2.getBrick() instanceof IdPoint) {
            IdPoint idp = (IdPoint) accessPoint2.getBrick();

            if (accessPoint1 instanceof Input) {
                ap2 = idp.getOutputO();
            } else if (accessPoint1 instanceof Output) {
                ap2 = idp.getInputA();
            }

        }

        connectCmd = new BareConnectCommand(c, ap1, ap2);

    }

    @Override
    public void execute() {
        getDispatcher().dispatch(connectCmd.setNotifyObserversHint(false));
        CircuitReorganizeUtility.reorganizeCircuit(circuit, getDispatcher(),
                isNotifyObserversHint());
        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public void undo() {
        getDispatcher().rewind();
        notifyObserversOn(circuit, isNotifyObserversHint());

    }

    @Override
    public boolean isExecuted() {
        return connectCmd.isExecuted();
    }

}
