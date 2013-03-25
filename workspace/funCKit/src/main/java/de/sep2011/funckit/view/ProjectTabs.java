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

package de.sep2011.funckit.view;

import com.jidesoft.swing.JideTabbedPane;
import de.sep2011.funckit.controller.listener.editpanel.EditPanelKeyListener;
import de.sep2011.funckit.controller.listener.editpanel.EditPanelMouseListener;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import javax.swing.Icon;

import java.util.Deque;
import java.util.Set;

import static de.sep2011.funckit.util.FunckitGuiUtil.iconFromResource;
import static de.sep2011.funckit.util.Log.gl;

/**
 * A TabbedPane which contains the {@link EditPanel}s of a {@link Project}.
 */
public class ProjectTabs extends JideTabbedPane implements ProjectObserver,
        GraphModelObserver {

    private static final long serialVersionUID = 6189405088304024465L;
    private static final Icon lockIcon = iconFromResource("/icons/misc/lock_14_16.png");
    private Project project;
    private View view;

    /**
     * Create a new instance of {@link ProjectTabs}.
     * 
     * @param project The {@link Project} this pane manages the tabs for, not null
     * @param view the associated view instance, not null
     */
    public ProjectTabs(Project project, View view) {
        super();
        assert project != null;
        assert view != null;
        this.project = project;
        this.view = view;
        project.addObserver(this);
        for (EditPanelModel panelModel : project.getOpenedEditPanelModels()) {
            addEditPanel(panelModel);
        }
        if (project.getCircuit() != null) {
            project.getCircuit().addObserver(this);
        }

    }

    /**
     * Returns the {@link Project} this pane manages the tabs for.
     * 
     * @return the {@link Project} this pane manages the tabs for
     */
    public Project getProject() {
        return project;
    }

    public void cleanObserved() {
        project.deleteObserver(this);
        if (project.getCircuit() != null) {
            project.getCircuit().deleteObserver(this);
        }

        for (int i = 0; i < getTabCount(); i++) {
            java.awt.Component comp = getComponentAt(i);
            if (comp instanceof EditPanelScrollPane) {
                EditPanel panel = ((EditPanelScrollPane) comp).getEditPanel();
                project.deleteObserver(panel);

                EditPanelModel panelModel = panel.getPanelModel();
                // remove as registered observer
                panelModel.deleteObserver(panel);
                panelModel.getCircuit().deleteObserver(panel);
                project.deleteObserver(panel);
                if (project.hasSimulation()) {
                    project.getSimulation().deleteObserver(panel);
                }
                view.getSessionModel().getSettings().deleteObserver(panel);
                view.getSessionModel().deleteObserver(panel);
            }
        }
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (i.hasEditPanelModelAdded()) {
            addEditPanel(i.getChangedModel());
        }
        if (i.hasEditPanelModelRemoved()) {
            removeEditPanel(i.getChangedModel());
        }
        if (i.isActiveEditPanelModelChanged()
                && source.getSelectedEditPanelModel() != null) {
            switchToTab(source.getSelectedEditPanelModel());
        }
        if (i.isNameChanged()) {
            updateNames();
        }
        if (i.isCircuitChanged() && source.getCircuit() != null) {
            source.getCircuit().addObserver(this);
        }
        if (i.isSimulationChanged()) {
            updateIcons(source);
        }
    }

    /**
     * Update the Titles with the name from displayed contents.
     */
    private void updateNames() {
        // look for matching tab
        for (int i = 0; i < getTabCount(); i++) {
            java.awt.Component comp = getComponentAt(i);
            if (comp instanceof EditPanelScrollPane) {

                // update name
                EditPanel panel = ((EditPanelScrollPane) comp).getEditPanel();
                EditPanelModel foundPanelModel = panel.getPanelModel();
                Component cmp = foundPanelModel.getComponentStack().peek();
                if (cmp == null) {
                    setTitleAt(i, project.getName());
                } else {
                    setTitleAt(i, cmp.getName() + " ("
                            + cmp.getType().getName() + ")");
                }

            }
        }

    }

    private void switchToTab(EditPanelModel panelModel) {

        // look for matching tab
        for (int i = 0; i < getTabCount(); i++) {
            java.awt.Component comp = getComponentAt(i);
            if (comp instanceof EditPanelScrollPane) {

                // switch to tab if matching model
                EditPanel panel = ((EditPanelScrollPane) comp).getEditPanel();
                EditPanelModel foundPanelModel = panel.getPanelModel();
                if (panelModel == foundPanelModel) {
                    setSelectedIndex(i);
                    gl().debug(
                            "switching tab. tabs size:" + getWidth() + ","
                                    + getHeight());
                    this.revalidate();
                    ((EditPanelScrollPane) comp).revalidate();
                    this.revalidate();
                    ((EditPanelScrollPane) comp).revalidate();
                    this.revalidate();
                    ((EditPanelScrollPane) comp).revalidate();
                    this.revalidate();
                    ((EditPanelScrollPane) comp).revalidate();
                    gl().debug(comp.getSize());
                    gl().debug(panel.getSize());
                    panel.setVisible(true);
                }
            }
        }
    }

    private void addEditPanel(EditPanelModel panelModel) {
        gl().debug("Adding EditPanel to tabs...");

        // setup of panel
        EditPanel panel = new EditPanel(view, panelModel);
        EditPanelScrollPane sp = new EditPanelScrollPane(view, panel);
        EditPanelMouseListener mouseListener = new EditPanelMouseListener(view,
                panelModel, view.getController());
        panel.addMouseListener(mouseListener);
        panel.addMouseMotionListener(mouseListener);
        panel.addMouseWheelListener(mouseListener);
        EditPanelKeyListener keyListener = new EditPanelKeyListener(view,
                panelModel, view.getController());
        panel.addKeyListener(keyListener);
        if (project.hasSimulation()) {
            project.getSimulation().addObserver(panel);
        }
        project.addObserver(panel);

        Component cmp = panelModel.getComponentStack().peek();
        String name = cmp == null ? project.getName() : cmp.getName() + " ("
                + cmp.getType().getName() + ")";

        addTab(name, sp);
        updateIcons(project);
    }

    private void updateIcons(Project project) {
        for (int i = 0; i < getTabCount(); i++) {
            if (getComponentAt(i) instanceof EditPanelScrollPane) {
                EditPanel ep = ((EditPanelScrollPane) getComponentAt(i))
                        .getEditPanel();
                if (ep != null) {
                    setIconAt(i, null); // remove icon first
                }
                /*
                 * Add lock icons to tabs with non empty stack or all when
                 * simulation is on
                 */
                if (ep != null
                        && (!ep.getPanelModel().getComponentStack().isEmpty() || project
                                .hasSimulation())) {
                    setIconAt(i, lockIcon);
                }
            }
        }
    }

    private void removeEditPanel(EditPanelModel panelModel) {

        // look for matching tab
        for (int i = 0; i < getTabCount(); i++) {
            java.awt.Component comp = getComponentAt(i);
            if (comp instanceof EditPanelScrollPane) {

                // close tab if matching model
                EditPanel panel = ((EditPanelScrollPane) comp).getEditPanel();
                EditPanelModel foundPanelModel = panel.getPanelModel();
                if (panelModel == foundPanelModel) {
                    remove(i);

                    // remove as registered observer
                    panelModel.deleteObserver(panel);
                    panelModel.getCircuit().deleteObserver(panel);
                    project.deleteObserver(panel);
                    if (project.hasSimulation()) {
                        project.getSimulation().deleteObserver(panel);
                    }
                    view.getSessionModel().getSettings().deleteObserver(panel);
                    view.getSessionModel().deleteObserver(panel);
                    i--;
                }
            }
        }
    }

    private void closeTabsWithComponent(Component component) {

        // look for matching tab
        for (int i = 0; i < getTabCount(); i++) {
            java.awt.Component comp = getComponentAt(i);
            if (comp instanceof EditPanelScrollPane) {

                // close tab if model has the component as root in its path
                EditPanel panel = ((EditPanelScrollPane) comp).getEditPanel();
                EditPanelModel panelModel = panel.getPanelModel();
                Deque<Component> stack = panelModel.getComponentStack();
                if (!stack.isEmpty() && stack.peekLast() == component) {
                    remove(i);

                    // remove as registered observer
                    panelModel.deleteObserver(panel);
                    panelModel.getCircuit().deleteObserver(panel);
                    project.deleteObserver(panel);
                    if (project.hasSimulation()) {
                        project.getSimulation().deleteObserver(panel);
                    }
                    view.getSessionModel().getSettings().deleteObserver(panel);
                    view.getSessionModel().deleteObserver(panel);
                    i--;
                }
            }
        }
    }

    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {

        if (i.isElementNameChanged()) {
            updateNames();
        }

        // close all tabs belonging to the removed components
        Set<Brick> removedBricks = i.getRemovedBricks();
        for (Brick b : removedBricks) {
            if (b instanceof Component) {
                closeTabsWithComponent((Component) b);
            }
        }
    }
}
