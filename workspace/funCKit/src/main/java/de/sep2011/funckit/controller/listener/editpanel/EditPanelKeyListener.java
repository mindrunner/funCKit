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

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.view.View;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Key listener object for listening on key events performed on edit panel.
 */
public class EditPanelKeyListener implements KeyListener {

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
    public EditPanelKeyListener(View view, EditPanelModel editPanelModel,
            Controller controller) {
        this.editPanelModel = editPanelModel;
        this.controller = controller;
    }

    /**
     * Delegation method for key-typed event to current tool.
     * 
     * @param event
     *            KeyEvent information when key is typed.
     */
    @Override
    public void keyTyped(KeyEvent event) {
        controller.getSessionModel().getTool().keyTyped(event, editPanelModel);
    }

    /**
     * Delegation method for key-pressed event to current tool.
     * 
     * @param event
     *            KeyEvent information when key is pressed.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        controller.getSessionModel().getTool()
                .keyPressed(event, editPanelModel);
    }

    /**
     * Delegation method for key-released event to current tool.
     * 
     * @param event
     *            KeyEvent information when key is released.
     */
    @Override
    public void keyReleased(KeyEvent event) {
        controller.getSessionModel().getTool()
                .keyReleased(event, editPanelModel);
    }

}