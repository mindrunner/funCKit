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

package de.sep2011.funckit.controller.listener.project;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;

/**
 * Listener object for actions that indicate a open property dialog event.
 */
public class DeleteProjectActionListener extends AbstractAction {

    private static final long serialVersionUID = 2670979073899255329L;

    /**
     * Current mediating controller object.
     */
    private final Controller controller;

    /**
     * View object.
     */
    private final View view;

    private final Project project;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param project
     * @param view
     *            associated View object, should not be null
     */
    public DeleteProjectActionListener(View view, Controller controller,
            Project project) {
        this.controller = controller;
        this.view = view;
        this.project = project;
    }

    /**
     * Trigger method to delete the current project.
     * 
     * @param event
     *            Additional event information.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        deleteProject(project, view, controller);
    }

    /**
     * Method to delete a given project with help of view and controller.
     * 
     * @param selectedProject
     *            Project to delete.
     * @param view
     *            Current applications view master object.
     * @param controller
     *            Current applications controller delegator.
     */
    public static void deleteProject(Project selectedProject, View view,
            Controller controller) {

        if (selectedProject.isModified()) {
            int choice = view.askForSaveUnsavedProjects();
            if (choice == JOptionPane.YES_OPTION) {
                if (!SaveFileActionListener.saveProject(selectedProject, view)) {
                    return;
                }
            } else if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            }

        }

        controller.getSessionModel().removeProject(selectedProject);
    }

}
