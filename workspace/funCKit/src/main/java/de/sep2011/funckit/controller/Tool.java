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

import de.sep2011.funckit.model.sessionmodel.EditPanelModel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static de.sep2011.funckit.util.FunckitGuiUtil.getCursorFromResource;

/**
 * A tool is a special listener object for actions, that are performed on the
 * view. For example graphical user interface, Concrete tool objects can react
 * in different way on same events. Thus different modes for same actions are
 * possible (see strategy pattern). Practically a tool is a combination of
 * several listener interfaces, accepting awt action events.
 */
public interface Tool {

    /**
     * Cursor with zero fingers.
     */
    public static final Cursor ZERO_FINGER_HAND_CURSOR = getCursorFromResource(
            "/icons/cursor/0fingerhand.png", new Point(8, 7), "0fingerhand");

    /**
     * Cursor with five fingers.
     */
    public static final Cursor FIVE_FINGER_HAND_CURSOR = getCursorFromResource(
            "/icons/cursor/5fingerhand.png", new Point(9, 9), "5fingerhand");

    /**
     * Mickey mouse cursor.
     */
    public static final Cursor MM_CURSOR = getCursorFromResource("/mickey.png", new Point(5, 5),
            "Mickey");

    /**
     * Event when key on edit panel is typed.
     * 
     * @param keyEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void keyTyped(KeyEvent keyEvent, EditPanelModel editPanelModel);

    /**
     * Event when a key is pressed down (edit panel focus).
     * 
     * @param keyEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void keyPressed(KeyEvent keyEvent, EditPanelModel editPanelModel);

    /**
     * Event when key on edit panel is released.
     * 
     * @param keyEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void keyReleased(KeyEvent keyEvent, EditPanelModel editPanelModel);

    /**
     * Event when click on edit panel is performed.
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseClicked(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when mouse is pressed on edit panel.
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mousePressed(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when mouse is pressed on edit panel.
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseReleased(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when pressed mouse enters edit panel area..
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseEntered(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when mouse exits area of edit panel.
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseExited(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when mouse is dragged on edit panel.
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseDragged(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when mouse is moved on edit panel.
     * 
     * @param mouseEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseMoved(MouseEvent mouseEvent, EditPanelModel editPanelModel);

    /**
     * Event when mouse wheel is moved.
     * 
     * @param mouseWheelEvent
     *            Awt event object
     * @param editPanelModel
     *            Additional edit panel model object to apply changes
     */
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent, EditPanelModel editPanelModel);

    /**
     * Returns a new instance of the {@link Tool}.
     * 
     * @param c
     *            is passed to the constructor
     * @return a new {@link Tool}
     */
    public Tool getNewInstance(Controller c);

    /**
     * Returns the default Cursor of this tool, not null.
     * 
     * @return the default Cursor of this tool, not null
     */
    public Cursor getToolDefaultCursor();

}