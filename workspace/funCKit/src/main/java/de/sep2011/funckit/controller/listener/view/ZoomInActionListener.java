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

package de.sep2011.funckit.controller.listener.view;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

/**
 * Listener that reacts on zoom in requests from the view.
 * 
 * @since implementation
 */
public class ZoomInActionListener extends AbstractAction {

    private static final long serialVersionUID = 6659982453215062535L;

    private static final double ZOOM_FACTOR = 1.1;

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
    public ZoomInActionListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        EditPanelModel panelModel = controller.getSessionModel()
                .getSelectedEditPanelModel();

        if (panelModel != null && view.getCurrentActiveEditPanel() != null) {
            double centerX = view.getCurrentActiveEditPanel().getBounds()
                    .getCenterX();
            double centerY = view.getCurrentActiveEditPanel().getBounds()
                    .getCenterY();
            panelModel.setAutoNotify(false);
            panelModel.setCenter(centerX, centerY);
            panelModel.setAutoNotify(true);
            panelModel.zoom(ZOOM_FACTOR);
        }

    }

}
