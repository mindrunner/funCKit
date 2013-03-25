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

import com.google.common.collect.Lists;
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.ComplexCommand;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static de.sep2011.funckit.model.graphmodel.implementations.commands.CircuitCommandUtilities.notifyObserversOn;

/**
 * Command that connects 2 {@link AccessPoint} with a path of {@link Wire}s. To
 * do so it creates {@link AccessPoint}s for the vertices alongside the Graph.
 * 
 * @since implementation
 */
public class CreateWirePathCommand extends ComplexCommand {

    private final Circuit circuit;
    private final AccessPoint source;
    private final AccessPoint target;
    private final List<IdPoint> idPointPath;

    private final IdPoint wireSplitIdPoint;
    private final Wire wire;

    /**
     * Creates a new {@link CreateWirePathCommand}.
     * 
     * @param c
     *            circuit to operate on, not null
     * @param sourceAp
     *            the first {@link AccessPoint}, not null
     * @param targetAp
     *            the second {@link AccessPoint}, not null
     * @param path
     *            vertices of a path which will be converted to {@link IdPoint}
     *            s, not null
     * @param centerCreation
     *            if false Points are understood as leftTop Position of the
     *            Brick, if true they are understand as center of the Brick
     */
    public CreateWirePathCommand(Circuit c, AccessPoint sourceAp,
            List<Point> path, AccessPoint targetAp, boolean centerCreation) {
        this.circuit = c;
        this.source = sourceAp;
        this.target = targetAp;
        this.idPointPath = new ArrayList<IdPoint>(path.size());
        for (Point p : path) {

            IdPoint idp = new IdPoint((Point) p.clone());
            if (centerCreation) {
                Point pos = idp.getPosition();
                pos.x -= idp.getDimension().width / 2;
                pos.y -= idp.getDimension().height / 2;
                idp.setPosition(pos);
            }
            this.idPointPath.add(idp);
        }

        wire = null;
        wireSplitIdPoint = null;

    }

    /**
     * Creates a new {@link CreateWirePathCommand}. Splits the given Wire with
     * an {@link IdPoint} on path.get(0) and creates a path to targetAp.
     * 
     * @param c
     *            circuit to operate on, not null
     * @param wire
     *            The Wire to start from that will be splitted, not null
     * @param path
     *            list with one element minimum
     * @param targetAp
     *            the target {@link AccessPoint} of the path, not null
     * @param centerCreation
     *            if false Points are understood as leftTop Position of the
     *            Brick, if true they are understand as center of the Brick
     */
    public CreateWirePathCommand(Circuit c, Wire wire, List<Point> path,
            AccessPoint targetAp, boolean centerCreation) {
        assert !path.isEmpty();

        this.circuit = c;

        this.idPointPath = new ArrayList<IdPoint>(path.size());
        for (Point p : path) {
            IdPoint idp = new IdPoint((Point) p.clone());
            if (centerCreation) {
                Point pos = idp.getPosition();
                pos.x -= idp.getDimension().width / 2;
                pos.y -= idp.getDimension().height / 2;
                idp.setPosition(pos);
            }

            this.idPointPath.add(idp);
        }

        this.wireSplitIdPoint = this.idPointPath.get(0);
        idPointPath.remove(0); // remove first element as it is wireSplitIdPoint

        this.source = this.wireSplitIdPoint.getOutputO();
        this.target = targetAp;
        this.wire = wire;

    }

    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        /*
         * simply replay the commands inside the dispatcher as it easily solves
         * problems like that there are not the same Wires created on redo.
         */
        if (getDispatcher().canStepForward()) {
            getDispatcher().replay();
            notifyObserversOn(circuit, isNotifyObserversHint());
            return;
        }

        if (wireSplitIdPoint == null || wire == null) {
            if (source.getBrick() instanceof IdPoint
                    && target.getBrick() instanceof IdPoint) {
                // connectTwoIdPoints((IdPoint)source.getBrick(),
                // (IdPoint)target.getBrick());
            }
        } else {
            if (target.getBrick() instanceof IdPoint) {
                // TODO Maybe we can in some situations but must search for a
                // `normal` Brick connected
                // connectTwoIdPoints((IdPoint)source.getBrick(),
                // (IdPoint)target.getBrick());
            }

            getDispatcher().dispatch(
                    new SplitWireCommand(circuit, wire, this.wireSplitIdPoint)
                            .setNotifyObserversHint(false));

        }

        if (idPointPath.isEmpty()) {
            getDispatcher().dispatch(
                    new ConnectCommand(circuit, source, target)
                            .setNotifyObserversHint(false));

        } else {
            doPathConnect();
        }

        CircuitReorganizeUtility.reorganizeCircuit(circuit, getDispatcher(),
                isNotifyObserversHint());
        notifyObserversOn(circuit, isNotifyObserversHint());
    }

    private void doPathConnect() {
        AccessPoint srcAp = this.source;
        AccessPoint tarAp = this.target;
        List<IdPoint> locIdPointPath = this.idPointPath;

        /* Make sure that we start on non IDPoint */
        if (srcAp.getBrick() instanceof IdPoint) {
            AccessPoint tmpFst = srcAp;
            srcAp = tarAp;
            tarAp = tmpFst;
            locIdPointPath = Lists.reverse(locIdPointPath);
        }

        /* Add path IdPoints */
        for (IdPoint idp : locIdPointPath) {
            Command addBrickCmd = new AddBrickCommand(circuit, idp);
            addBrickCmd.setNotifyObserversHint(false);
            getDispatcher().dispatch(addBrickCmd);
        }

        /* connect first AccessPoint to first IdPoint */

        AccessPoint connectAP1 = null;
        if (srcAp instanceof Output) {
            connectAP1 = locIdPointPath.get(0).getInputA();
        } else if (srcAp instanceof Input) {
            connectAP1 = locIdPointPath.get(0).getOutputO();
        }
        assert connectAP1 != null;
        Command connectCmd = new BareConnectCommand(circuit, srcAp, connectAP1);
        connectCmd.setNotifyObserversHint(false);
        getDispatcher().dispatch(connectCmd);

        /* connect the IdPoints */
        for (int i = 0; i < locIdPointPath.size() - 1; i++) {
            AccessPoint fstAp = null;
            AccessPoint sndAp = null;

            if (srcAp instanceof Output) {
                fstAp = locIdPointPath.get(i).getOutputO();
                sndAp = locIdPointPath.get(i + 1).getInputA();
            } else if (srcAp instanceof Input) {
                fstAp = locIdPointPath.get(i).getInputA();
                sndAp = locIdPointPath.get(i + 1).getOutputO();
            }

            assert fstAp != null && sndAp != null;

            Command idConnectCmd = new BareConnectCommand(circuit, fstAp, sndAp);
            idConnectCmd.setNotifyObserversHint(false);
            getDispatcher().dispatch(idConnectCmd);
        }

        /* connect second AccessPoint to last IdPoint */
        AccessPoint connectAP2 = null;
        if (tarAp instanceof Output && !(tarAp.getBrick() instanceof IdPoint)) {
            connectAP2 = locIdPointPath.get(locIdPointPath.size() - 1)
                    .getInputA();
        } else if (tarAp instanceof Input
                && !(tarAp.getBrick() instanceof IdPoint)) {
            connectAP2 = locIdPointPath.get(locIdPointPath.size() - 1)
                    .getOutputO();
        } else if (tarAp.getBrick() instanceof IdPoint
                && srcAp instanceof Input) {
            connectAP2 = locIdPointPath.get(locIdPointPath.size() - 1)
                    .getInputA();
            tarAp = ((IdPoint) tarAp.getBrick()).getOutputO();
        } else if (tarAp.getBrick() instanceof IdPoint
                && srcAp instanceof Output) {
            connectAP2 = locIdPointPath.get(locIdPointPath.size() - 1)
                    .getOutputO();
            tarAp = ((IdPoint) tarAp.getBrick()).getInputA();

        }
        assert connectAP2 != null;
        getDispatcher().dispatch(
                new BareConnectCommand(circuit, connectAP2, tarAp)
                        .setNotifyObserversHint(false));

    }

    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();
        getDispatcher().rewind();
        notifyObserversOn(circuit, isNotifyObserversHint());

    }

}
