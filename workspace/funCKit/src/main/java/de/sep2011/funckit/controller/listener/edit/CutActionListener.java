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

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.implementations.commands.RemoveBunchOfElementsCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Listener object for actions that indicate a cut operation.
 */
public class CutActionListener extends AbstractAction {

    private static final long serialVersionUID = -2266594882044726219L;

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
     *            Associated View object, should not be null
     */
    public CutActionListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    /**
     * Action for cutting out selected elements. Removes a bunch of elements
     * from current circuit and fills copy buffer in session model with them.
     * 
     * @param event
     *            ActionEvent information when cut is initiated.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        SessionModel sessionModel = controller.getSessionModel();
        CopyActionListener.fillCopyBuffer(sessionModel);
        EditPanelModel editPanelModel = sessionModel
                .getSelectedEditPanelModel();
        if (editPanelModel != null) {
            LinkedHashSet<Element> elementsToRemove = new LinkedHashSet<Element>(
                    editPanelModel.getSelectedElements());
            
            // remove fixed elements from selection  
            for (Iterator<Element> it = elementsToRemove.iterator(); it.hasNext();) {
                Element element = it.next();
                if (element instanceof Brick
                        && ((Brick) element).isFixedHint()) {
                    it.remove();
                }
            }
            
            if(elementsToRemove.isEmpty()) {
                return;
            }

            sessionModel.getCurrentGraphCommandDispatcher().dispatch(
                    new RemoveBunchOfElementsCommand(editPanelModel
                            .getCircuit(), elementsToRemove));

            /*
             * Remove selected elements from selected models from current
             * project panels.
             */
            for (EditPanelModel model : view.getSessionModel()
                    .getCurrentProject().getOpenedEditPanelModels()) {
                if (model != null && model != editPanelModel) {
                    model.getSelectedElements().removeAll(elementsToRemove);
                }
            }
        }
    }
}