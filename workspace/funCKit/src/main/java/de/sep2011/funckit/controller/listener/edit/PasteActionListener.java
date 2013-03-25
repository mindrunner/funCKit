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

package de.sep2011.funckit.controller.listener.edit;

import de.sep2011.funckit.controller.AbstractTool;
import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.PasteTool;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.view.EditPanel;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.Point;
import java.awt.event.ActionEvent;

/**
 * Listener object for actions that indicate a paste event.
 */
public class PasteActionListener extends AbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = 5101012154890442909L;

    /**
     * Current mediating controller object.
     */
    private final Controller controller;

    /**
     * View object.
     */
    private final View view;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public PasteActionListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    /**
     * Action triggered when copy buffer should be pasted on a certain position
     * in current circuit object.
     * 
     * @param event
     *            ActionEvent information when pasting is initiated.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        paste(controller, view.getCurrentActiveEditPanel().getMousePosition());
    }

    /**
     * Executes a paste operation at the given position in the current active
     * selected {@link EditPanel} of the given session in the give
     * {@link Controller}.
     * 
     * @param controller
     *            the {@link Controller} to paste in.
     * @param mousePosition
     *            the position to show the ghosts.
     */
    public static void paste(Controller controller, Point mousePosition) {
        SessionModel sessionModel = controller.getSessionModel();
        EditPanelModel editPanelModel = sessionModel
                .getSelectedEditPanelModel();

        if (editPanelModel == null) {
            Log.gl().debug("Can't paste, no EditPanel selected");
            return;
        }

        if (sessionModel.getCopyBuffer().getElements().isEmpty()) {
            Log.gl().debug("Do not paste: nothing to paste");
            return;
        }

        PasteTool pasteTool = new PasteTool(controller);
        sessionModel.saveTool();
        sessionModel.setTool(pasteTool);

        /* set the ghosts */
        if (mousePosition != null) {
            Point triggeredMousePosition = AbstractTool.calculateInversePoint(
                    mousePosition, editPanelModel.getTransformation());
            pasteTool.moveElementsInCopyBuffer(editPanelModel,
                    triggeredMousePosition);
            pasteTool.showGhosts(editPanelModel, sessionModel);
        }
    }
}
