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
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Tool to be used when in Drag Viewport mode.
 */
public class DragViewportTool extends AbstractTool {

    /**
     * Create a new DragViewportTool.
     * 
     * @param c
     *            the associated {@link Controller}, should not be null
     */
    public DragViewportTool(Controller c) {
        this.controller = c;
    }

    @Override
    public Cursor getToolDefaultCursor() {
        return Tool.FIVE_FINGER_HAND_CURSOR;
    }

    @Override
    public DragViewportTool getNewInstance(Controller c) {
        return new DragViewportTool(c);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseMoved(mouseEvent, editPanelModel);

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseReleased(mouseEvent, editPanelModel);

        editPanelModel.setDragStartPoint(null);
        editPanelModel.setCursor(Tool.FIVE_FINGER_HAND_CURSOR);

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseDragged(mouseEvent, editPanelModel);
        dragViewport(mouseEvent, editPanelModel);

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mousePressed(mouseEvent, editPanelModel);

        editPanelModel.setDragStartPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));

        editPanelModel.setCursor(Tool.ZERO_FINGER_HAND_CURSOR);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseClicked(mouseEvent, editPanelModel);
        Point click = calculateInversePoint(mouseEvent.getPoint(),
                editPanelModel.getTransformation());

        Circuit c = editPanelModel.getCircuit();
        Brick brick = c.getBrickAtPosition(click);
        if (brick instanceof Switch) {
            Switch s = (Switch) brick;
            s.toggle();
        }
    }

}
