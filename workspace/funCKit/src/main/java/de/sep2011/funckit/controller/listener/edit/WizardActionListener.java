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
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.view.GenerateDialog;
import de.sep2011.funckit.view.View;
import de.sep2011.funckit.view.WizardDialog;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

public class WizardActionListener extends AbstractAction {

    private static final long serialVersionUID = -5848044190946900330L;
    private final Controller controller;
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
    public WizardActionListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Log.gl().info("WizardActionListener.actionPerformed()");

        /*
         * Determine what wizarding should be performed (generating,
         * refactoring, ..).
         */
        EditPanelModel editPanelModel = controller.getSessionModel()
                .getSelectedEditPanelModel();
        if (editPanelModel == null) {
            return;
        }

        if (editPanelModel.getSelectedElements() != null
                && !editPanelModel.getSelectedElements().isEmpty()) {
            /*
             * Some elements are selected, so display refactoring or
             * circuit-generating dialog.
             */
            WizardDialog wizardDialog = new WizardDialog(view, controller);
            wizardDialog.setLocationRelativeTo(view.getMainRootPane());
            wizardDialog.setVisible(true);


            // currently we have only a generating dialog ..
            /*generateDialog = new GenerateDialog(view, controller, false);
            generateDialog.setLocationRelativeTo(view.getMainWindow());
            generateDialog.setVisible(true);*/
        } else {
            /* Nothing is selected, so display generate dialog. */
            GenerateDialog generateDialog = new GenerateDialog(view, controller);
            generateDialog.setLocationRelativeTo(view.getMainRootPane());
            generateDialog.setVisible(true);
        }
    }
}
