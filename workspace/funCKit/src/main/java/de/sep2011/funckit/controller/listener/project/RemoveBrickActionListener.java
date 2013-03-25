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
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.implementations.commands.RemoveBrickCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
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

/**
 * Listener object for removing a Brick from a Project.
 */
public class RemoveBrickActionListener extends AbstractAction {

    private static final long serialVersionUID = 9218193416305207236L;

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
     * @param path
     * @param view
     *            associated View object, should not be null
     */
    public RemoveBrickActionListener(View view, Controller controller,
            TreePath path) {
        this.view = view;
        this.path = path;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (path == null) {
            return;
        }

        class NodeVisitor implements ProjectTreeNodeVisitor {

            public Project project = null;
            public Brick clickBrick = null;
            public Circuit circuit = null;

            {
                for (Object obj : path.getPath()) {
                    ((ProjectTreeNode<?>) obj).accept(this);
                }
            }

            @Override
            public void visit(ProjectTreeComponentTypeNode node) {

            }

            @Override
            public void visit(ProjectTreeBrickNode node) {
                if (node.getParent() instanceof ProjectTreeProjectNode) {
                    clickBrick = node.getContent();
                }
            }

            @Override
            public void visit(ProjectTreeProjectNode node) {
                project = node.getContent();
                circuit = node.getContent().getCircuit();
            }

            @Override
            public void visit(ProjectTreeRootNode node) {

            }
        }

        NodeVisitor visitor = new NodeVisitor();

        if (visitor.project != null && visitor.clickBrick != null
                && visitor.circuit != null
                && !visitor.clickBrick.isFixedHint()) {
            /* First remove brick from selected elements. */
            EditPanelModel currentEditPanelModel = view.getSessionModel()
                    .getSelectedEditPanelModel();
            if (currentEditPanelModel != null) {
                currentEditPanelModel.getSelectedElements().remove(
                        visitor.clickBrick);
            }

            /* Then remove it from our model with command (see specification). */
            visitor.project.getGraphCommandDispatcher()
                    .dispatch(
                            new RemoveBrickCommand(visitor.circuit,
                                    visitor.clickBrick));

        }
    }

}