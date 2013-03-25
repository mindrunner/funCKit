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
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.AccessPointImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.AddBrickCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ConnectCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel.ToolMode;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.CollisionCheck;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * CreateTool is the strategy object for creating new elements by clicking on
 * view. As a concrete implementation of tool interface it can be used as
 * injected strategy object in controller and therefore react on actions
 * performed on editing on view.
 */
public class CreateTool extends AbstractTool {
    private AccessPoint firstAccessPoint;

    private ToolMode clickDetectionToolModeCache = null; // click detection
    private MouseEvent pressedMouseEvent; // stores event when mouse was pressed
    private int movecount = 0;

    /**
     * Called when a new CreateTool object is created and only needs a
     * {@link Controller} object to get access to graph- and probably session
     * model.
     * 
     * @param controller
     *            Mediating controller object of application.
     */
    public CreateTool(Controller controller) {
        this.controller = controller;
    }

    @Override
    public CreateTool getNewInstance(Controller c) {
        return new CreateTool(c);
    }

    @Override
    public void mouseClicked(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseClicked(e, editPanelModel);
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:

            if (isLeftMouseButton(e)) {
                /*
                 * Remove selection, if there was one as this is a single click
                 * that might create or select only a single brick.
                 */
                editPanelModel.getSelectedElements().clear();
                editPanelModel.setSelectedElements(editPanelModel.getSelectedElements());
                clearGhosts(editPanelModel);

                /*
                 * Calculate possible new brick and if it would collide with
                 * others.
                 */
                Brick template = controller.getSessionModel().getCurrentBrick();
                assert template != null;
                Brick brickToAdd = template.getNewInstance(click);
                brickToAdd.setName(template.getName());
                brickToAdd.setDimension(template.getDimension());
                moveElementForPlacement(brickToAdd, controller.getSessionModel().getSettings()
                        .getBoolean(Settings.GRID_LOCK));

                Check collisionCheck = new CollisionCheck(brickToAdd);
                boolean collisionCheckPassed = collisionCheck.perform(editPanelModel.getCircuit())
                        .isPassed();

                if (collisionCheckPassed) {
                    /* Immediately create new brick as it was a single click. */
                    Command command = new AddBrickCommand(editPanelModel.getCircuit(), brickToAdd);
                    controller.getSessionModel().getCurrentGraphCommandDispatcher()
                            .dispatch(command);
                    controller.getSessionModel().restoreTool();

                } else {
                    showGhost(editPanelModel, click.x, click.y);
                }
            }

            break;

        default:
            break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e, EditPanelModel editPanelModel) {
        super.mousePressed(e, editPanelModel);

        /* click detection */
        pressedMouseEvent = e;
        clickDetectionToolModeCache = editPanelModel.getToolMode();

    }

    @Override
    public void mouseDragged(MouseEvent e, EditPanelModel editPanelModel) {
        cleanUp(editPanelModel);
        Settings settings = controller.getSessionModel().getSettings();

        if (pressedMouseEvent == null) {
            pressedMouseEvent = e;
        }
        Point pressedPoint = pressedMouseEvent.getPoint();

        /* if the mouse was pressed before this, initialize dragging */
        if (!e.getPoint().equals(pressedPoint) && pressedPoint != null) {
            Point click = calculateInversePoint(pressedPoint, editPanelModel.getTransformation());

            AccessPoint apUnderMouse = editPanelModel.getCircuit().getAccessPointAtPositon(click,
                    settings.getInt(Settings.ACCESS_POINT_SCATTER_FACTOR));

            switch (editPanelModel.getToolMode()) {
            case DEFAULT_MODE:
                if (isRightMouseButton(pressedMouseEvent)) {
                    // no brick and right click, accept selection
                    editPanelModel.setSelectionStart(click);
                    editPanelModel.setSelectionEnd(click);
                    editPanelModel.setToolMode(ToolMode.SELECT_RECT_MODE);
                    clearGhosts(editPanelModel);
                } else if (apUnderMouse != null) {
                    firstAccessPoint = apUnderMouse;
                    editPanelModel.setToolMode(ToolMode.CREATE_SINGLE_WIRE_MODE); // TODO:
                                                                                  // completly
                                                                                  // remove
                                                                                  // singlewiremode
                    clearGhosts(editPanelModel);
                }
                break;
            default:
                break;
            }

            pressedPoint = null;
        }

        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:
            showGhost(editPanelModel, click.x, click.y);
            break;
        case SELECT_RECT_MODE:
            editPanelModel.setSelectionEnd(click);
            break;
        case CREATE_SINGLE_WIRE_MODE:
            /* Draw a ghost wire until mouse is released. */
            Brick template = controller.getSessionModel().getCurrentBrick();
            Brick temporaryBrick = template.getNewInstance(click);
            AccessPoint temporaryAccessPoint = new AccessPointImpl(temporaryBrick, new Point(0, 0),
                    "");
            Wire ghostWire = new WireImpl(firstAccessPoint, temporaryAccessPoint);
            Set<Element> ghosts = new LinkedHashSet<Element>();
            ghosts.add(ghostWire);
            editPanelModel.setGhosts(ghosts);
            break;
        default:
            break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, EditPanelModel editPanelModel) {
        cleanUp(editPanelModel);
        Settings settings = controller.getSessionModel().getSettings();
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());

        // restore mode if clicked
        if (pressedMouseEvent != null && e.getPoint().equals(pressedMouseEvent.getPoint())) {
            editPanelModel.setToolMode(clickDetectionToolModeCache);
        }

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:

            break;
        case SELECT_RECT_MODE:
            Point start = editPanelModel.getSelectionStart();
            Point end = editPanelModel.getSelectionEnd();
            int x = start.x < end.x ? start.x : end.x;
            int y = start.y < end.y ? start.y : end.y;
            int width = Math.abs(start.x - end.x);
            int height = Math.abs(start.y - end.y);
            Rectangle selectionArea = new Rectangle(x, y, width, height);
            Set<Element> selectedElements = editPanelModel.getCircuit().getIntersectingElements(
                    selectionArea);

            if (e.isControlDown()) { // Add to prev selected elements
                selectedElements.addAll(editPanelModel.getSelectedElements());
            }

            editPanelModel.setSelectedElements(selectedElements);
            editPanelModel.setSelectionStart(null);
            editPanelModel.setSelectionEnd(null);
            editPanelModel.setToolMode(ToolMode.DEFAULT_MODE);

            /* Switch to selection tool if elements are selected. */
            if (!selectedElements.isEmpty()) {
                controller.getSessionModel().setTool(new SelectTool(controller));
            } else {
                /* We want to see our brick ghost again. */
                showGhost(editPanelModel, click.x, click.y);
            }

            break;

        case CREATE_SINGLE_WIRE_MODE:
            /* Draw a ghost wire until mouse is released. */

            AccessPoint secondAccessPoint = editPanelModel.getCircuit().getAccessPointAtPositon(
                    click, settings.getInt(Settings.ACCESS_POINT_SCATTER_FACTOR));

            if (secondAccessPoint != null
                    && !(firstAccessPoint.getBrick() instanceof IdPoint && secondAccessPoint
                            .getBrick() instanceof IdPoint)) {
                Command cmd = new ConnectCommand(editPanelModel.getCircuit(), firstAccessPoint,
                        secondAccessPoint);
                controller.getSessionModel().getCurrentGraphCommandDispatcher().dispatch(cmd);
            } else {
                IdPoint idp = new IdPoint(new Point(click));

                if (firstAccessPoint instanceof Input) {
                    secondAccessPoint = idp.getOutputO();
                } else {
                    secondAccessPoint = idp.getInputA();
                }

                WireTool wireTool = new WireTool(controller);

                controller.getSessionModel().saveTool();
                controller.getSessionModel().setTool(wireTool);

                wireTool.startWirePathFromCreateTool(firstAccessPoint, click, editPanelModel);
                // clearGhosts(editPanelModel);
                editPanelModel.setSelectionStart(null);
                editPanelModel.setSelectionEnd(null);

                break;
            }

            clearGhosts(editPanelModel);
            editPanelModel.setSelectionStart(null);
            editPanelModel.setSelectionEnd(null);
            editPanelModel.setToolMode(ToolMode.DEFAULT_MODE);

            /* We want to see our brick ghost again. */
            showGhost(editPanelModel, click.x, click.y);

            break;
        default:
            break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseMoved(e, editPanelModel);
        cleanUp(editPanelModel);

        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:
            moveGhost(editPanelModel, click);

            break;

        default:
            break;
        }
    }

    private void moveGhost(EditPanelModel editPanelModel, Point click) {
        if (this.movecount++ >= 10) {
            /* Calculate possible new brick and if it would collide with others. */
            Brick template = controller.getSessionModel().getCurrentBrick();
            assert template != null;
            Brick brickToAdd = template.getNewInstance(click);
            brickToAdd.setName(template.getName());
            brickToAdd.setDimension(template.getDimension());
            moveElementForPlacement(brickToAdd, controller.getSessionModel().getSettings()
                    .getBoolean(Settings.GRID_LOCK));

            Check collisionCheck = new CollisionCheck(brickToAdd);
            boolean collisionCheckPassed = collisionCheck.perform(editPanelModel.getCircuit())
                    .isPassed();
            if (collisionCheckPassed) {
                showGhost(editPanelModel, click.x, click.y);
            } else {
                clearGhosts(editPanelModel);
            }

        } else
            showGhost(editPanelModel, click.x, click.y);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent, EditPanelModel editPanelModel) {
        super.mouseWheelMoved(mouseWheelEvent, editPanelModel);
        cleanUp(editPanelModel);
        Point click = calculateInversePoint(mouseWheelEvent.getPoint(),
                editPanelModel.getTransformation());
        moveGhost(editPanelModel, click);
    }

    @Override
    public void mouseExited(MouseEvent e, EditPanelModel editPanelModel) {
        clearGhosts(editPanelModel);
    }

    private void showGhost(EditPanelModel editPanelModel, int x, int y) {
        Brick template = controller.getSessionModel().getCurrentBrick();
        assert template != null;
        Brick ghostBrick = template.getNewInstance(new Point(x, y));
        ghostBrick.setName(template.getName());
        moveElementForPlacement(ghostBrick,
                controller.getSessionModel().getSettings().getBoolean(Settings.GRID_LOCK));

        ghostBrick.setDimension(template.getDimension());

        Set<Element> ghosts = editPanelModel.getGhosts();
        ghosts.clear();
        ghosts.add(ghostBrick);
        editPanelModel.setGhosts(ghosts);
    }
    
    
    
    @Override
	public void keyPressed(KeyEvent keyEvent, EditPanelModel editPanelModel) {
		super.keyPressed(keyEvent, editPanelModel);
        cleanUp(editPanelModel);
	}

	@Override
	public void keyReleased(KeyEvent keyEvent, EditPanelModel editPanelModel) {
		super.keyReleased(keyEvent, editPanelModel);
        cleanUp(editPanelModel);
	}

	private void cleanUp(EditPanelModel editPanelModel) {
    	if (firstAccessPoint != null && !editPanelModel.getCircuit().getElements().contains(firstAccessPoint.getBrick())) {
    		firstAccessPoint = null;
    		if (editPanelModel.getToolMode() == ToolMode.CREATE_SINGLE_WIRE_MODE) {
    			editPanelModel.setToolMode(ToolMode.DEFAULT_MODE);
    		}
    		clearGhosts(editPanelModel);
    	}
    }

}
