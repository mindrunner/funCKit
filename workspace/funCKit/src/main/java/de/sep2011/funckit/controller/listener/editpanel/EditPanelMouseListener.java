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

package de.sep2011.funckit.controller.listener.editpanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.Tool;
import de.sep2011.funckit.controller.listener.AbstractMouseListener;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.view.EditPanel;
import de.sep2011.funckit.view.View;

/**
 * Mouse listener object for mouse actions performed on edit panel or a similar
 * object that raises mouse events with same information as awt events.
 */
public class EditPanelMouseListener extends AbstractMouseListener {

    /**
     * Current mediating controller object.
     */
    private final Controller controller;
    private final EditPanelModel editPanelModel;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param editPanelModel
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public EditPanelMouseListener(View view, EditPanelModel editPanelModel,
            Controller controller) {
        this.editPanelModel = editPanelModel;
        this.controller = controller;
    }

    /**
     * Delegates click on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        ((EditPanel) event.getSource()).requestFocusInWindow();
        controller.getSessionModel().getTool().mouseClicked(event, editPanelModel);
    }

    /**
     * Delegates drag event on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseDragged(MouseEvent event) {
        controller.getSessionModel().getTool()
                .mouseDragged(event, editPanelModel);
    }

    /**
     * Delegates move event on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseMoved(MouseEvent event) {
        ((EditPanel) event.getSource()).requestFocusInWindow();
        controller.getSessionModel().getTool()
                .mouseMoved(event, editPanelModel);
    }

    /**
     * Delegates press on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mousePressed(MouseEvent event) {
        controller.getSessionModel().getTool()
                .mousePressed(event, editPanelModel);
    }

    /**
     * Delegates event on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseEntered(MouseEvent event) {
        controller.getSessionModel().getTool()
                .mouseEntered(event, editPanelModel);
    }

    /**
     * Delegates event on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseExited(MouseEvent event) {
        controller.getSessionModel().getTool()
                .mouseExited(event, editPanelModel);
    }

    /**
     * Delegates release event on {@link EditPanel} to current {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        controller.getSessionModel().getTool()
                .mouseReleased(event, editPanelModel);
    }

    /**
     * Delegates mouse wheel movement event on {@link EditPanel} to current
     * {@link Tool}.
     * 
     * @param event
     *            Additional information when method is triggered.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        controller.getSessionModel().getTool()
                .mouseWheelMoved(event, editPanelModel);
    }
}