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

package de.sep2011.funckit.controller;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.AccessPointImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.AddBrickCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.CreateWirePathCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.SplitWireCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel.ToolMode;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.CommandDispatcher;
import de.sep2011.funckit.util.command.SimpleCommandCombiner;
import javax.swing.Timer;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static de.sep2011.funckit.util.Log.gl;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isMiddleMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * A Tool for drawing Complex Wires.
 */
public class WireTool extends AbstractTool implements Tool {
    private List<Point> wirePath = null;
    private AccessPoint firstClickedAccessPoint = null;
    private Wire startWire = null;
    private boolean wasDoubleClick;
    private CommandDispatcher commandDispatcher;
    private final List<Command> commandList = new LinkedList<Command>();
    private int clickCount;

    /**
     * Create a new WireTool.
     * 
     * @param c
     *            the associated {@link Controller}, should not be null
     */
    public WireTool(Controller c) {
        assert c != null;
        this.controller = c;
        commandDispatcher = controller.getSessionModel().getCurrentGraphCommandDispatcher();
    }

    @Override
    public WireTool getNewInstance(Controller c) {
        return new WireTool(c);
    }

    private void doubleClick(MouseEvent e, EditPanelModel editPanelModel) {
        endWirePathCreationFromNull(calculateInversePoint(e.getPoint(), editPanelModel.getTransformation()),editPanelModel);
    }

    @Override
    public void mouseClicked(final MouseEvent e, final EditPanelModel editPanelModel) {
        clickCount++;
        final Settings settings = controller.getSessionModel().getSettings();

        // only do super double click in default mode
        if (editPanelModel.getToolMode() == ToolMode.DEFAULT_MODE) {
            super.mouseClicked(e, editPanelModel);
        }

        if (clickCount == 2) {
            if (editPanelModel.getToolMode() != ToolMode.DEFAULT_MODE) {
                this.doubleClick(e, editPanelModel);
            }
            wasDoubleClick = true;
            clickCount = 0;
        } else {
            Integer timerinterval = 150;
            Timer timer = new Timer(timerinterval, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    clickCount = 0;
                    if (wasDoubleClick) {
                        wasDoubleClick = false; // reset flag
                    } else {

                        Point click = calculateInversePoint(e.getPoint(),
                                editPanelModel.getTransformation());

                        AccessPoint apUnderMouse = editPanelModel.getCircuit()
                                .getAccessPointAtPositon(click,
                                        settings.getInt(Settings.ACCESS_POINT_SCATTER_FACTOR));

                        Wire wireUnderMouse = apUnderMouse == null ? editPanelModel.getCircuit()
                                .getWireAtPosition(click,
                                        settings.getInt(Settings.WIRE_SCATTER_FACTOR)) : null;

                        switch (editPanelModel.getToolMode()) {
                        case DEFAULT_MODE:
                            if (apUnderMouse != null && isLeftMouseButton(e)) {
                                startWirePathCreationFromAp(apUnderMouse, editPanelModel);
                            } else if (wireUnderMouse != null && isLeftMouseButton(e)) {
                                startWirePathCreationFromWire(wireUnderMouse, click, editPanelModel);
                            } else if (isLeftMouseButton(e)) {
                                startWirePathCreationFromNull(click, editPanelModel);
                            }
                            break;

                        case CREATE_WIRE_PATH_FROM_WIRE:
                            if (isRightMouseButton(e)) { // cancel on
                                                         // right click
                                finalizeWirePathCreation(editPanelModel);
                            } else if (isMiddleMouseButton(e)) {
                                if (wireUnderMouse != null) {
                                    IdPoint idp = new IdPoint(click);
                                    commandList.add(new SplitWireCommand(editPanelModel
                                            .getCircuit(), wireUnderMouse,
                                            (IdPoint) moveElementForPlacement(idp, false)));
                                    endWirePathCreationFromWire(editPanelModel, idp.getOutputO());
                                } else {
                                    endWirePathCreationFromWire(editPanelModel, click);
                                }
                            } else if (wirePath.size() == 1 && click.equals(wirePath.get(0))
                                    && e.getClickCount() == 2) {
                                /* Splits Wire on double click */

                                commandList.add(new SplitWireCommand(editPanelModel.getCircuit(),
                                        startWire, (IdPoint) moveElementForPlacement(new IdPoint(
                                                click), false)));
                                finalizeWirePathCreation(editPanelModel);
                            } else if (wireUnderMouse != null) {
                                IdPoint idp = new IdPoint(click);
                                commandList.add(new SplitWireCommand(editPanelModel.getCircuit(),
                                        wireUnderMouse, (IdPoint) moveElementForPlacement(idp,
                                                false)));
                                endWirePathCreationFromWire(editPanelModel, idp.getOutputO());
                            } else if (apUnderMouse == null) {
                                wirePath.add(click);
                                updateWirePathGhostsFromWire(editPanelModel, click);
                            } else {
                                endWirePathCreationFromWire(editPanelModel, apUnderMouse);
                            }
                            break;
                        case CREATE_WIRE_PATH_FROM_AP:
                            if (isRightMouseButton(e)) { // cancel on
                                                         // right click
                                finalizeWirePathCreation(editPanelModel);
                            } else if (isMiddleMouseButton(e)) {
                                if (wireUnderMouse != null) {
                                    IdPoint idp = new IdPoint(click);
                                    commandList.add(new SplitWireCommand(editPanelModel.getCircuit(), wireUnderMouse, (IdPoint) moveElementForPlacement(idp, false)));
                                    endWirePathCreationFromAp(editPanelModel, idp.getOutputO());
                                } else {
                                    endWirePathCreationFromNull(click, editPanelModel);
                                }
                            } else if (wireUnderMouse != null) {
                                IdPoint idp = new IdPoint(click);
                                commandList.add(new SplitWireCommand(editPanelModel.getCircuit(), wireUnderMouse, (IdPoint) moveElementForPlacement(idp, false)));
                                endWirePathCreationFromAp(editPanelModel, idp.getOutputO());
                            } else if (apUnderMouse == null) {
                                wirePath.add(click);
                                updateWirePathGhostsFromAp(editPanelModel, click);
                            } else {
                                endWirePathCreationFromAp(editPanelModel, apUnderMouse);
                            }
                            break;

                        default:
                            break;
                        }
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void endWirePathCreationFromNull(Point point, EditPanelModel editPanelModel) {
        IdPoint idp = new IdPoint(point);
        commandList.add(new AddBrickCommand(editPanelModel.getCircuit(), firstClickedAccessPoint.getBrick()));
        commandList.add(new AddBrickCommand(editPanelModel.getCircuit(), (IdPoint) moveElementForPlacement(idp, false)));
        commandList.add(new CreateWirePathCommand(editPanelModel.getCircuit(), firstClickedAccessPoint, wirePath, idp.getInputA(), true));
        finalizeWirePathCreation(editPanelModel);
    }

    private void startWirePathCreationFromNull(Point p, EditPanelModel editPanelModel) {
        wirePath = new LinkedList<Point>();
        IdPoint idp = new IdPoint(p);
        idp = (IdPoint) moveElementForPlacement(idp, false);
        firstClickedAccessPoint = idp.getOutputO();
        editPanelModel.setToolMode(ToolMode.CREATE_WIRE_PATH_FROM_AP);
    }

    @Override
    public void mouseMoved(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseMoved(e, editPanelModel);

        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:
            // Log.gl().info("def");
            break;

        case CREATE_WIRE_PATH_FROM_AP:
            // Log.gl().info("ap");
            updateWirePathGhostsFromAp(editPanelModel, click);
            break;

        case CREATE_WIRE_PATH_FROM_WIRE:
            // Log.gl().info("wire");
            updateWirePathGhostsFromWire(editPanelModel, click);
            break;

        default:
            break;
        }

    }

    public void startWirePathFromCreateTool(AccessPoint ap1, Point p, EditPanelModel editPanelModel) {
        startWirePathCreationFromAp(ap1, editPanelModel);
        wirePath.add(p);
        updateWirePathGhostsFromAp(editPanelModel, p);
    }

    private void startWirePathCreationFromAp(AccessPoint startAp, EditPanelModel editPanelModel) {
        Log.gl().debug("Start Wire Path Creation from AccessPoint");
        wirePath = new LinkedList<Point>();
        firstClickedAccessPoint = startAp;
        editPanelModel.setToolMode(ToolMode.CREATE_WIRE_PATH_FROM_AP);

    }

    private void startWirePathCreationFromWire(Wire startWire, Point startPoint,
            EditPanelModel editPanelModel) {
        Log.gl().debug("Start Wire Path Creation from Wire");
        this.startWire = startWire;
        wirePath = new LinkedList<Point>();
        wirePath.add(startPoint);
        editPanelModel.setToolMode(ToolMode.CREATE_WIRE_PATH_FROM_WIRE);
    }

    private void endWirePathCreationFromWire(EditPanelModel editPanelModel,
            AccessPoint endAccessPoint) {

        commandList.add(new CreateWirePathCommand(editPanelModel.getCircuit(), startWire, wirePath,
                endAccessPoint, true));

        finalizeWirePathCreation(editPanelModel);
    }

    private void endWirePathCreationFromWire(EditPanelModel editPanelModel, Point point) {

        IdPoint idp = new IdPoint(point);
        commandList.add(new AddBrickCommand(editPanelModel.getCircuit(), idp));

        commandList.add(new CreateWirePathCommand(editPanelModel.getCircuit(), startWire, wirePath,
                idp.getOutputO(), true));

        finalizeWirePathCreation(editPanelModel);
    }

    private void finalizeWirePathCreation(EditPanelModel editPanelModel) {
        commandDispatcher.dispatch(new SimpleCommandCombiner(commandList));
        commandList.clear();
        clearGhosts(editPanelModel);
        startWire = null;
        firstClickedAccessPoint = null;
        wirePath = null;
        editPanelModel.setToolMode(ToolMode.DEFAULT_MODE);
        controller.getSessionModel().restoreTool();

    }

    private void endWirePathCreationFromAp(EditPanelModel editPanelModel, AccessPoint endAccessPoint) {
        Log.gl().debug("End Wire Path Creation from AccessPoint");
        if (!editPanelModel.getCircuit().getElements().contains(firstClickedAccessPoint.getBrick())) {
            commandList.add(new AddBrickCommand(editPanelModel.getCircuit(),
                    firstClickedAccessPoint.getBrick()));
        }

        if (firstClickedAccessPoint instanceof Input) {
            gl().debug("first: input");
        }
        if (firstClickedAccessPoint instanceof Output) {
            gl().debug("first: output");
        }
        if (endAccessPoint instanceof Input) {
            gl().debug("last: input");
        }
        if (endAccessPoint instanceof Output) {
            gl().debug("last: output");
        }

        commandList.add(new CreateWirePathCommand(editPanelModel.getCircuit(),
                firstClickedAccessPoint, wirePath, endAccessPoint, true));

        finalizeWirePathCreation(editPanelModel);
    }

    private void updateWirePathGhostsFromAp(EditPanelModel editPanelModel, Point click) {

        Brick template = controller.getSessionModel().getCurrentBrick();
        /* create a Dummy AccessPoint where the mouse Pointer is */
        Brick clickDummyBrick = template.getNewInstance(click);
        AccessPoint clickDummyAccessPoint = new AccessPointImpl(clickDummyBrick, new Point(0, 0),
                "");

        /* create a Dummy AccessPoint where the start AccessPoint is */
        Brick startDummyBrick = template.getNewInstance(new Point(firstClickedAccessPoint
                .getBrick().getPosition().x + firstClickedAccessPoint.getPosition().x,
                firstClickedAccessPoint.getBrick().getPosition().y
                        + firstClickedAccessPoint.getPosition().y));

        AccessPoint startDummyAccessPoint = new AccessPointImpl(startDummyBrick, new Point(0, 0),
                "");

        Set<Element> ghosts = new LinkedHashSet<Element>();

        if (wirePath.isEmpty()) {
            Wire ghostWire = new WireImpl(startDummyAccessPoint, clickDummyAccessPoint);
            ghosts.add(ghostWire);
        } else {
            IdPoint idp = new IdPoint(wirePath.get(0));

            idp = (IdPoint) moveElementForPlacement(idp, false);

            AccessPoint ap = idp.getInputA();

            Wire ghostWire = new WireImpl(startDummyAccessPoint, ap);
            ghosts.add(ghostWire);

            List<IdPoint> idPointPath = new ArrayList<IdPoint>(wirePath.size());
            ghosts.addAll(getGhosts(wirePath, idPointPath));

            ghosts.add(new WireImpl(idPointPath.get(idPointPath.size() - 1).getOutputO(),
                    clickDummyAccessPoint));
        }

        editPanelModel.setGhosts(ghosts);

    }

    private void updateWirePathGhostsFromWire(EditPanelModel editPanelModel, Point click) {
        Brick template = controller.getSessionModel().getCurrentBrick();
        /* create a Dummy AccessPoint where the mouse Pointer is */
        Brick clickDummyBrick = template.getNewInstance(click);
        AccessPoint clickDummyAccessPoint = new AccessPointImpl(clickDummyBrick, new Point(0, 0),
                "");

        List<IdPoint> idPointPath = new ArrayList<IdPoint>(wirePath.size());
        Set<Element> ghosts = getGhosts(wirePath, idPointPath);

        ghosts.add(new WireImpl(idPointPath.get(idPointPath.size() - 1).getOutputO(),
                clickDummyAccessPoint));

        editPanelModel.setGhosts(ghosts);
    }

    private Set<Element> getGhosts(List<Point> wirePath, List<IdPoint> idPointPath) {
        Set<Element> ghosts = new LinkedHashSet<Element>();

        for (Point p : wirePath) {
            IdPoint idp = new IdPoint(p);
            idPointPath.add(idp);

            /* Move IdPoints to center */
            moveElementForPlacement(idp, false);
            ghosts.add(idp);
        }

        for (int i = 0; i < idPointPath.size() - 1; i++) {
            IdPoint idp1 = idPointPath.get(i);
            IdPoint idp2 = idPointPath.get(i + 1);
            Wire ghostWire2 = new WireImpl(idp1.getOutputO(), idp2.getInputA());
            ghosts.add(ghostWire2);
        }

        return ghosts;
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseExited(mouseEvent, editPanelModel);
    }

    @Override
    protected void cancelCurrentAction(EditPanelModel editPanelModel) {
        finalizeWirePathCreation(editPanelModel);
    }
}
