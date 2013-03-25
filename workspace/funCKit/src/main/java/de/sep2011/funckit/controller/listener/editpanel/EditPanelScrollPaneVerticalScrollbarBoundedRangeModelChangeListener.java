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
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.view.EditPanel;
import de.sep2011.funckit.view.EditPanelScrollPane;
import de.sep2011.funckit.view.View;
import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * Listener for changes on a {@link EditPanelScrollPane}s vertical scrollbars
 * {@link BoundedRangeModel}.
 */
public class EditPanelScrollPaneVerticalScrollbarBoundedRangeModelChangeListener
        implements ChangeListener {

    private final EditPanelScrollPane scrollPane;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     * @param scrollPane
     *            Scrollpane this listener is associated to
     */
    public EditPanelScrollPaneVerticalScrollbarBoundedRangeModelChangeListener(
            View view, Controller controller, EditPanelScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        EditPanel editPanel = scrollPane.getEditPanel();
        if (editPanel == null || scrollPane.isScrollbarsAdjustingNotFromUser()) {
            return;
        }

        BoundedRangeModel sbModel = (BoundedRangeModel) event.getSource();
        AffineTransform transformation = editPanel.getPanelModel()
                .getTransformation();
        Point transformedPos = new Point();
        try {
            transformation.inverseTransform(new Point(), transformedPos);
        } catch (NoninvertibleTransformException e) {
            Log.gl().error(e);
        }
        int newY = sbModel.getValue();
        editPanel.getPanelModel().translate(0, newY - transformedPos.y);
    }

}