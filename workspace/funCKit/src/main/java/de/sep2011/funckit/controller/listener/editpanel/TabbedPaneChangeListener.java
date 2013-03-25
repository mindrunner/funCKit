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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.listener.project.SaveFileActionListener;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel.ViewType;
import de.sep2011.funckit.view.EditPanel;
import de.sep2011.funckit.view.EditPanelScrollPane;
import de.sep2011.funckit.view.ProjectTabs;
import de.sep2011.funckit.view.View;

public class TabbedPaneChangeListener extends AbstractAction implements ChangeListener {
    /**
     * 
     */
    private static final long serialVersionUID = -6590677788430430183L;
    private final View view;
    private final Controller controller;
    private final ProjectTabs parent;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param parent
     *            the associated tabbed pane
     * @param view
     *            associated View object, should not be null
     */
    public TabbedPaneChangeListener(View view, Controller controller, ProjectTabs parent) {
        this.controller = controller;
        this.view = view;
        this.parent = parent;
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        if (event.getSource() instanceof ProjectTabs) {
            ProjectTabs tabs = (ProjectTabs) event.getSource();
            if (tabs.getSelectedComponent() instanceof EditPanelScrollPane) {
                EditPanel editPanel = ((EditPanelScrollPane) tabs.getSelectedComponent())
                        .getEditPanel();
                Project project = tabs.getProject();
                EditPanelModel panelModel = editPanel.getPanelModel();
                project.setSelectedEditPanelModel(panelModel);
                if (project.hasSimulation()) {
                    controller.enterSimulationMode();
                } else {
                    controller.enterEditMode(panelModel.hasMainCircuit());
                }
                return;
            }
            tabs.getProject().setSelectedEditPanelModel(null);
        } else {
            assert false;
        }
    }

    /**
     * called when a tab close button is clicked.
     * 
     * @param event
     *            the event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        EditPanelScrollPane scrollPaneToRemove = (EditPanelScrollPane) event.getSource();
        EditPanel panel = scrollPaneToRemove.getEditPanel();
        EditPanelModel panelModel = panel.getPanelModel();
        Project project = parent.getProject();

        // do not close the project inside applet
        if (project.getOpenedEditPanelModels().size() == 1
                && (view.getSessionModel().getViewType() == ViewType.VIEW_TYPE_ELEANING_SOLVE || view
                        .getSessionModel().getViewType() == ViewType.VIEW_TYPE_PRESENTER)) {
            return;
        }

        if (project.getOpenedEditPanelModels().size() == 1 && project.isModified()) {
            int choice = view.askForSaveUnsavedProjects();
            if (choice == JOptionPane.YES_OPTION) {
                if (!SaveFileActionListener.saveProject(project, view)) {
                    return;
                }
            } else if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        project.removeEditPanelModel(panelModel);

        // last tab closed? => close project
        if (project.getOpenedEditPanelModels().size() == 0) {
            project.setCircuit(null); // unload circuit
            project.setModified(false);

            // new unsaved project? => remove from tree
            if (project.getAbsolutePath() == null || project.getAbsolutePath().equals("")) {
                controller.getSessionModel().removeProject(project);
            }
        }
    }

}
