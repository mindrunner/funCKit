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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.commands.PasteCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.validator.Check;
import de.sep2011.funckit.validator.MultipleCollisionCheck;
import de.sep2011.funckit.validator.Result;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Tool for pasting Elements.
 */
public class PasteTool extends AbstractTool {

    /**
     * Create a new Paste Tool.
     * 
     * @param c
     *            the associated {@link Controller}, should not be null
     */
    public PasteTool(Controller c) {
        this.controller = c;
    }

    @Override
    public void mouseMoved(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseMoved(e, editPanelModel);
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());
        moveElementsInCopyBuffer(editPanelModel, click);
        SessionModel sm = controller.getSessionModel();
        showGhosts(editPanelModel, sm);
    }

    @Override
    public void mouseDragged(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseDragged(e, editPanelModel);
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());
        moveElementsInCopyBuffer(editPanelModel, click);
        SessionModel sm = controller.getSessionModel();
        showGhosts(editPanelModel, sm);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e, EditPanelModel editPanelModel) {
        super.mouseWheelMoved(e, editPanelModel);
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());
        moveElementsInCopyBuffer(editPanelModel, click);
        SessionModel sm = controller.getSessionModel();
        showGhosts(editPanelModel, sm);
    }

    /**
     * Setsa the ghosts for the paste tool into the model.
     * 
     * @param editPanelModel the panel model to show the ghosts in
     * @param sessionModel the currently used session model
     */
    public void showGhosts(EditPanelModel editPanelModel, SessionModel sessionModel) {
        boolean wasAuto = editPanelModel.isAutoNotify();
        editPanelModel.setAutoNotify(false);

        if (editPanelModel.getGhosts() != null && editPanelModel.getGhosts().isEmpty()) {
            editPanelModel.setGhosts(new LinkedHashSet<Element>(sessionModel.getCopyBuffer()
                    .getElements()));
        } else {
            editPanelModel.setGhosts(editPanelModel.getGhosts());
        }

        Set<Element> ghosts = editPanelModel.getGhosts();
        Check collisionCheck = new MultipleCollisionCheck(ghosts);
        Result result = collisionCheck.perform(editPanelModel.getCircuit());
        if (result.isPassed()) {
            sessionModel.getCurrentProject().setErrorGhosts(null);
        } else {
            sessionModel.getCurrentProject().setErrorGhosts(ghosts);
        }

        editPanelModel.notifyObservers();
        editPanelModel.setAutoNotify(wasAuto);
    }

    /**
     * Moves the Elements inside the copy buffer to the given position (mouse in center).
     * 
     * @param editPanelModel the affected edit panel model
     * @param click the position to move to (center of the elements)
     */
    public void moveElementsInCopyBuffer(EditPanelModel editPanelModel, Point click) {
        boolean gridLock = controller.getSessionModel().getSettings()
                .getBoolean(Settings.GRID_LOCK);
        SessionModel sm = controller.getSessionModel();
        Circuit copyBuffer = sm.getCopyBuffer();
        Set<Element> cpBufferElems = copyBuffer.getElements();
        Rectangle copyCircuitRect = new Rectangle(copyBuffer.getBoundingRectangle());

        /* find some Brick to use for Grid Lock if enabled (uses first it finds) */
        Element gridLockElem = null;
        for (Iterator<Element> it = cpBufferElems.iterator(); it.hasNext() && gridLockElem == null
                && gridLock;) {
            Element elem = it.next();
            if (elem instanceof Brick) {
                gridLockElem = elem;
            }
        }

        int lockDx = 0;
        int lockDy = 0;
        if (gridLockElem != null) {
            Point p1 = new Point(gridLockElem.getPosition());

            elementToClickPos(p1, copyCircuitRect, click);
            posMouseOnCenter(p1, copyCircuitRect, click);
            Point p2 = lockPointOnGrid(p1);

            lockDx = p2.x - p1.x;
            lockDy = p2.y - p1.y;
        }

        /* Move Elements to click Position */
        for (Element elem : cpBufferElems) {
            Point pos = elem.getPosition();

            elementToClickPos(pos, copyCircuitRect, click);

            /* move to grid lock */
            pos.x += lockDx;
            pos.y += lockDy;

            posMouseOnCenter(pos, copyCircuitRect, click);

            elem.setPosition(pos);
        }

    }

    private static void elementToClickPos(Point pos, Rectangle copyCircuitRect, Point click) {

        /* Adjust positions so the bounding rect starts at (0,0) */
        pos.x -= copyCircuitRect.x;
        pos.y -= copyCircuitRect.y;

        /* move to mouse Position */
        pos.x += click.x;
        pos.y += click.y;

    }

    private static void posMouseOnCenter(Point pos, Rectangle copyCircuitRect, Point click) {

        /* Cursor to center */
        pos.x -= copyCircuitRect.width / 2;
        pos.y -= copyCircuitRect.height / 2;

    }

    @Override
    public void mouseClicked(MouseEvent e, EditPanelModel editPanelModel) {
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());
        SessionModel sessionModel = controller.getSessionModel();

        if (isLeftMouseButton(e)) {
            moveElementsInCopyBuffer(editPanelModel, click);

            List<Brick> bricks = new LinkedList<Brick>();
            for (Element element : sessionModel.getCopyBuffer().getElements()) {
                if (element instanceof Brick) {
                    bricks.add((Brick) element);
                }
            }

            Check collision = new MultipleCollisionCheck(bricks);

            if (collision.perform(editPanelModel.getCircuit()).isPassed()) {

                Circuit pasteCircuit = sessionModel.getCopyBuffer().getCopy();

                sessionModel.getCurrentGraphCommandDispatcher().dispatch(
                        new PasteCommand(editPanelModel.getCircuit(), pasteCircuit));

                editPanelModel.setGhosts(new LinkedHashSet<Element>());
            }

        } else if (isRightMouseButton(e)) {
            sessionModel.restoreTool(); // NOTE: tool has been saved before
                                        // paste tool gets set.
        }
    }

    @Override
    public PasteTool getNewInstance(Controller c) {
        return this;
    }

    @Override
    protected void cancelCurrentAction(EditPanelModel editPanelModel) {
        controller.getSessionModel().restoreTool();

    }

}
