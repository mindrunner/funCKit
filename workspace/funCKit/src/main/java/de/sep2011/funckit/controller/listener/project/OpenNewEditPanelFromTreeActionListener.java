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
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.view.ProjectTree;
import de.sep2011.funckit.view.View;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeBrickNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeComponentTypeNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeNodeVisitor;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeProjectNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeRootNode;
import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;

import java.awt.event.ActionEvent;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Listener that opens a new {@link EditPanelModel} Tab from a node in the {@link ProjectTree}.
 */
public class OpenNewEditPanelFromTreeActionListener extends AbstractAction {

    private static final long serialVersionUID = -6286819378658843919L;
    private final TreePath path;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     *
     * @param controller Application controller object, should not be null
     * @param view       associated View object, should not be null
     * @param path       The TreePath we react to
     */
    public OpenNewEditPanelFromTreeActionListener(View view, Controller controller,
                                                  TreePath path) {
        this.path = path;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (path == null) {
            return;
        }

        /* build stack */
        final Deque<Component> stack = new LinkedList<Component>();
        Project project = new ProjectTreeNodeVisitor() {

            Project pro = null;

            {
                for (Object object : path.getPath()) {
                    ((ProjectTreeNode<?>) object).accept(this);
                }
            }

            @Override
            public void visit(ProjectTreeComponentTypeNode node) {

            }

            @Override
            public void visit(ProjectTreeBrickNode node) {
                if (node.getContent() instanceof Component) {
                    stack.push((Component) node.getContent());
                }

            }

            @Override
            public void visit(ProjectTreeProjectNode node) {
                pro = node.getContent();
            }

            @Override
            public void visit(ProjectTreeRootNode node) {

            }

            Project getProject() {
                return pro;
            }
        }.getProject();

        if (project != null) {
            EditPanelModel panelModel = new EditPanelModel(stack.isEmpty() ? project.getCircuit()
                    : stack.peek().getType().getCircuit(), stack);
            project.addEditPanelModel(panelModel);
        }

    }

}
