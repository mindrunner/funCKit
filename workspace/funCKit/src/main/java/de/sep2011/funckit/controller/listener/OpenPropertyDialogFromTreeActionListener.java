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

package de.sep2011.funckit.controller.listener;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.view.ElementPropertyDialog;
import de.sep2011.funckit.view.View;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeBrickNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeProjectNode;
import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Listener object for actions that indicate a open property dialog event.
 */
public class OpenPropertyDialogFromTreeActionListener extends AbstractAction {

    private static final long serialVersionUID = 4505926980569401265L;

    /**
     * View object.
     */
    private final View view;

    private final TreePath path;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     * @param path the associated {@link TreePath} 
     */
    public OpenPropertyDialogFromTreeActionListener(View view,
            Controller controller, TreePath path) {
        this.view = view;
        this.path = path;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (path == null
                || !(path.getLastPathComponent() instanceof ProjectTreeBrickNode)) {
            return;
        }

        Project project = null;
        for (Object obj : path.getPath()) {
            if (obj instanceof ProjectTreeProjectNode) {
                project = ((ProjectTreeProjectNode) obj).getContent();
            }
        }

        if (project != null) {
            ElementPropertyDialog epd =
                    new ElementPropertyDialog(view.getMainFrame(),
                            ModalityType.DOCUMENT_MODAL);
            epd.setView(view);
            epd.setElement(((ProjectTreeBrickNode) path.getLastPathComponent())
                    .getContent(), project.getCircuit());

            epd.setLocationRelativeTo(view.getMainRootPane());

            epd.setVisible(true);
        }

    }

}
