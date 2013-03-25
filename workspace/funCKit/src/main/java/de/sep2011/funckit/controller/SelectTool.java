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

import com.google.common.collect.Sets;
import de.sep2011.funckit.model.graphmodel.*;
import de.sep2011.funckit.model.graphmodel.implementations.BrickWireDistinguishDispatcher;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.MoveBunchOfElementsCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel.ToolMode;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Pair;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.MultipleCollisionCheck;
import de.sep2011.funckit.validator.Result;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.swing.SwingUtilities.isLeftMouseButton;

/**
 * Tool to be used in Select mode (select and move bricks, ...).
 */
public class SelectTool extends AbstractTool {

    private Point moveStartPointInModel = null;
    private int currentMoveDifferenceX = 0;
    private int currentMoveDifferenceY = 0;

    private MouseEvent pressedMouseEvent; // stores event when mouse was pressed

    /**
     * Create a new SelectTool.
     * 
     * @param c
     *            the associated {@link Controller}, should not be null
     */
    public SelectTool(Controller c) {
        this.controller = c;
    }

    @Override
    public SelectTool getNewInstance(Controller c) {
        return new SelectTool(c);
    }

    @Override
    public void mouseReleased(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseReleased(e, editPanelModel);

        switch (editPanelModel.getToolMode()) {
        case SELECT_RECT_MODE:
            Point start = editPanelModel.getSelectionStart();
            Point end = editPanelModel.getSelectionEnd();

            // calculate selection rectangle
            int x = start.x < end.x ? start.x : end.x;
            int y = start.y < end.y ? start.y : end.y;
            int width = Math.abs(start.x - end.x);
            int height = Math.abs(start.y - end.y);
            Rectangle selectionArea = new Rectangle(x, y, width, height);

            Set<Element> selectedElements = editPanelModel.getCircuit().getIntersectingElements(
                    selectionArea);

            // add new selected elements to the previous selected ones if
            // control was pressed
            if (e.isControlDown()) {
                selectedElements.addAll(editPanelModel.getSelectedElements());
            }

            editPanelModel.setSelectedElements(selectedElements);

            // reset selection rectangle & tool mode
            editPanelModel.setSelectionStart(null);
            editPanelModel.setSelectionEnd(null);
            editPanelModel.setToolMode(ToolMode.DEFAULT_MODE);
            
            editPanelModel.setCursor(getToolDefaultCursor());
            
            break;

        case MOVE_ELEMENT_MODE:
            if (canDropElementsHere(editPanelModel)) {
                Command command = new MoveBunchOfElementsCommand(editPanelModel.getCircuit(),
                        editPanelModel.getSelectedElements(), currentMoveDifferenceX,
                        currentMoveDifferenceY);
                controller.getSessionModel().getCurrentGraphCommandDispatcher().dispatch(command);
            }
            // reset everything
            moveStartPointInModel = null;
            clearGhosts(editPanelModel);
            editPanelModel.setToolMode(ToolMode.DEFAULT_MODE);
            editPanelModel.setCursor(getToolDefaultCursor());
            break;

        default:
            break;
        }
    }

    private static boolean canDropElementsHere(EditPanelModel epm) {
        Set<Element> ghosts = epm.getGhosts();
        Set<Element> circuitElements = epm.getCircuit().getElements();
        Set<Element> selectedElements = epm.getSelectedElements();
        Set<Element> unselectedElements = Sets.difference(circuitElements, selectedElements);
        Check collisionCheck = new MultipleCollisionCheck(ghosts);
        Circuit tmpCircuit = new CircuitImpl();
        tmpCircuit.getElements().addAll(unselectedElements);

        Result result = collisionCheck.perform(tmpCircuit);

        return result.isPassed();
    }

    @Override
    public void mousePressed(MouseEvent e, EditPanelModel editPanelModel) {
        super.mousePressed(e, editPanelModel);

        /* click detection */
        pressedMouseEvent = e;
    }

    @Override
    public void mouseDragged(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseDragged(e, editPanelModel);
        if (pressedMouseEvent == null) {
            pressedMouseEvent = e;
        }
        Point pressedPointOnPanel = pressedMouseEvent.getPoint(); // point when
        // mouse was pressed
        Point movedToPointInModel = calculateInversePoint(e.getPoint(),
                editPanelModel.getTransformation());

        // point we clicked on in the model when mouse was pressed
        Point pressedPointInModel = calculateInversePoint(pressedPointOnPanel,
                editPanelModel.getTransformation());
        Circuit c = editPanelModel.getCircuit();
        Brick brick = c.getBrickAtPosition(pressedPointInModel); // brick we
        // clicked on
        Set<Element> selected = editPanelModel.getSelectedElements();

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:
            if (brick == null) { // no brick when mouse pressed, accept
                // selection
                editPanelModel.setSelectionStart(pressedPointInModel);
                editPanelModel.setSelectionEnd(pressedPointInModel);
                editPanelModel.setToolMode(ToolMode.SELECT_RECT_MODE);
                editPanelModel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            } else if (selected.contains(brick)) {
                /*
                 * brick is already selected, start dragging selected Elements
                 */
                if (!brick.isFixedHint()) { // only drag non fixed
                    
                    for (Iterator<Element> it = selected.iterator(); it.hasNext();) {
                        Element elem = it.next();
                        if(elem instanceof Brick && ((Brick) elem).isFixedHint()) {
                            it.remove();
                        }
                    }
                    
                    moveStartPointInModel = pressedPointInModel;
                    editPanelModel.setToolMode(ToolMode.MOVE_ELEMENT_MODE);
                    editPanelModel.setSelectedBrick(brick);
                    editPanelModel.setSelectedElements(selected);
                    editPanelModel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
            } else {
                /*
                 * brick is not selected, select only that one and start
                 * dragging it
                 */
                selected.clear();
                selected.add(brick);
                editPanelModel.setSelectedElements(selected);
                if (!brick.isFixedHint()) { // only drag non fixed
                    moveStartPointInModel = pressedPointInModel;
                    editPanelModel.setToolMode(ToolMode.MOVE_ELEMENT_MODE);
                    editPanelModel.setSelectedBrick(brick);
                    editPanelModel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
            }
            break;
        case SELECT_RECT_MODE:
            editPanelModel.setSelectionEnd(movedToPointInModel);
            break;
        case MOVE_ELEMENT_MODE:
            createMoveGhosts(editPanelModel, movedToPointInModel);
            break;
        default:
            break;
        }
    }
    
    /**
     * Create ghosts at the given position in the model on the given
     * {@link EditPanelModel}. This is using the moveStartPositionInModel and
     * does rastering if enabled. It also updates the current move difference.
     */
    private void createMoveGhosts(final EditPanelModel editPanelModel, Point positionInModel) {
        currentMoveDifferenceX = positionInModel.x - moveStartPointInModel.x;
        currentMoveDifferenceY = positionInModel.y - moveStartPointInModel.y;

        if (gridlock()) {
            Brick brick = editPanelModel.getSelectedBrick();
            int ghostPositionX = brick.getPosition().x + currentMoveDifferenceX;
            int ghostPositionY = brick.getPosition().y + currentMoveDifferenceY;
            int centeredX = positionInModel.x - brick.getDimension().width / 2;
            int centeredY = positionInModel.y - brick.getDimension().height / 2;
            Point lockedCenter = lockPointOnGrid(new Point(centeredX, centeredY));
            currentMoveDifferenceX += lockedCenter.x - ghostPositionX;
            currentMoveDifferenceY += lockedCenter.y - ghostPositionY;
        }

        final Set<Element> selectionCopy = new LinkedHashSet<Element>();

        new BrickWireDistinguishDispatcher() {

            {
                for (Element elem : editPanelModel.getSelectedElements()) {
                    elem.dispatch(this);
                }
            }

            @Override
            public void visit(Element element) {
                selectionCopy.add(element);
            }

            @Override
            protected void visitWire(Wire w) {
            }

            @Override
            protected void visitBrick(Brick b) {

                for (Input i : b.getInputs()) {
                    selectionCopy.addAll(i.getWires());
                }

                for (Output o : b.getOutputs()) {
                    selectionCopy.addAll(o.getWires());
                }

                selectionCopy.add(b);
            }
        };

        Pair<Set<Wire>, Circuit> copy = editPanelModel.getCircuit().getPartCopyAndDiscardedWires(
                selectionCopy);

        Set<Element> ghosts = copy.getRight().getElements();
        ghosts.addAll(copy.getLeft());

        for (Element element : ghosts) {
            element.setPosition(new Point(element.getBoundingRect().x + currentMoveDifferenceX,
                    element.getBoundingRect().y + currentMoveDifferenceY));
        }

        /* draw error */
        if (canDropElementsHere(editPanelModel)) {
            controller.getSessionModel().getCurrentProject().setErrorGhosts(null);
        } else {
            controller.getSessionModel().getCurrentProject().setErrorGhosts(ghosts);
        }

        editPanelModel.setGhosts(ghosts);
    }

    @Override
    public void mouseClicked(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseClicked(e, editPanelModel);
        Settings settings = controller.getSessionModel().getSettings();

        // position clicked on in the model
        Point clickPointInModel = calculateInversePoint(e.getPoint(),
                editPanelModel.getTransformation());
        Circuit c = editPanelModel.getCircuit();
        Set<Element> selected = editPanelModel.getSelectedElements();

        // brick we clicked on
        Brick brick = c.getBrickAtPosition(clickPointInModel);

        // if we did not click on a brick look for a near wire, else take the
        // brick
        Element clickedElement = (brick == null ? c.getWireAtPosition(clickPointInModel,
                settings.getInt(Settings.WIRE_SCATTER_FACTOR)) : brick);

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:
            // clear selection if clicked on empty space
            if (clickedElement == null) {
                selected.clear();
                editPanelModel.setSelectedElements(selected);
            }
            // single click on an element with left mouse button?
            else if (e.getClickCount() == 1 && isLeftMouseButton(e)) {

                // not control button pressed? => only select the clicked brick
                if (!e.isControlDown()) {
                    selected.clear();
                    selected.add(clickedElement);
                } else { // control button pressed

                    // removed when was selected, add when was not
                    if (selected.contains(clickedElement)) {
                        selected.remove(clickedElement);
                    } else {
                        selected.add(clickedElement);
                    }
                }

                // update selected elements
                editPanelModel.setSelectedElements(selected);
            }
            break;

        default:
            break;
        }
    }

    private boolean gridlock() {
        return controller.getSessionModel().getSettings().getBoolean(Settings.GRID_LOCK);
    }

    @Override
    protected void cancelCurrentAction(EditPanelModel editPanelModel) {
        Set<Element> e = editPanelModel.getSelectedElements();
        e.clear();
        editPanelModel.setSelectedElements(e);
    }

}
