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
import de.sep2011.funckit.view.EditPanel;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * Listener that reacts on fit in view action event.
 */
public class ModelFitsIntoCircuitListener extends AbstractAction {

    private static final long serialVersionUID = -6472890598461073621L;

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
    public ModelFitsIntoCircuitListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        fitModelIntoViewport();
    }

    private void fitModelIntoViewport() {
        EditPanel editPanel = view.getCurrentActiveEditPanel();
        EditPanelModel epm = controller.getSessionModel()
                .getSelectedEditPanelModel();

        if (editPanel != null && epm != null
                && !epm.getCircuit().getElements().isEmpty()) {
            editPanel.getPanelModel().setCenter(0, 0);
            epm.setAutoNotify(false);
            double widthWindow = editPanel.getBounds().width;
            double heightWindow = editPanel.getBounds().height;
            double relationWindow = widthWindow / heightWindow;
            Rectangle circuitRect = editPanel.getPanelModel().getCircuit()
                    .getBoundingRectangle();
            double widthCircuit = circuitRect.width;
            double heightCircuit = circuitRect.height;
            double relationCircuit = widthCircuit / heightCircuit;

            if (relationCircuit < relationWindow) {
                epm.setZoom(heightWindow / heightCircuit);
            } else {
                epm.setZoom(widthWindow / widthCircuit);
            }
            AffineTransform transformation = editPanel.getPanelModel()
                    .getTransformation();
            Point outerEdge = new Point((int) widthWindow, (int) heightWindow);
            Point windowEndEdgeInModel = new Point();
            Point windowStartEdgeInModel = new Point();
            try {
                transformation
                        .inverseTransform(outerEdge, windowEndEdgeInModel);
                transformation.inverseTransform(new Point(),
                        windowStartEdgeInModel);
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }
            int windowWidthInModel = windowEndEdgeInModel.x
                    - windowStartEdgeInModel.x;
            int windowHeightInModel = windowEndEdgeInModel.y
                    - windowStartEdgeInModel.y;
            epm.setTranslationX(circuitRect.getX()
                    - (windowWidthInModel - widthCircuit) / 2);
            epm.setAutoNotify(true);
            epm.setTranslationY(circuitRect.getY()
                    - (windowHeightInModel - heightCircuit) / 2);
        }
    }
}
