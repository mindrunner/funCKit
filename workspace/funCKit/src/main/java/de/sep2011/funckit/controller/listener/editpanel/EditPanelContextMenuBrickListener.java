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
import de.sep2011.funckit.controller.CreateTool;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.sessionmodel.SessionModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Sets {@link SessionModel#setCurrentBrick(Brick)} from edit panel context
 * menu.
 */
public class EditPanelContextMenuBrickListener extends MouseAdapter {

    private final SessionModel sessionModel;
    private final Controller controller;
    private final Brick brick;

    /**
     * Creates a new EditPanelContextMenuBrickListener.
     * 
     * @param sessionModel
     *            the session model to use
     * @param c
     *            the associated controller
     * @param b
     *            the brick to set
     */
    public EditPanelContextMenuBrickListener(SessionModel sessionModel, Controller c, Brick b) {
        this.sessionModel = sessionModel;
        this.controller = c;
        this.brick = b;
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        this.sessionModel.saveTool();
        this.sessionModel.setCurrentBrick(this.brick);
        this.sessionModel.setTool(new CreateTool(this.controller));
    }
}
