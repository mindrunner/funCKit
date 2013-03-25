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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.sep2011.funckit.util.internationalization.Language.*;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeBrickNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeComponentTypeNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeModel;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeNodeVisitor;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeProjectNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeRootNode;

/**
 * The ProjectTree displays an overview of the {@link Circuit}s structure.
 */
public class ProjectTree extends JTree implements SessionModelObserver {

    private static final long serialVersionUID = 2823018766767811937L;

    private ProjectTreeModel model;

    /**
     * Create a new {@link ProjectTree}.
     * 
     * @param view
     */
    public ProjectTree(View view) {
        super();
        checkNotNull(view);
        initialize(view);
    }

    private void initialize(View view) {
        model = new ProjectTreeModel(view.getSessionModel());
        view.getSessionModel().addObserver(this);
        setModel(model);
        setRootVisible(false);
        setShowsRootHandles(true);
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        addTreeWillExpandListener(new TreeWillExpandListener() {

            @Override
            public void treeWillExpand(TreeExpansionEvent event)
                    throws ExpandVetoException {
                if (!ProjectTreeModel.expandNode(event.getPath().getLastPathComponent())) {
                    throw new ExpandVetoException(event);
                }

            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event)
                    throws ExpandVetoException {
                ProjectTreeModel.collapseNode(event.getPath().getLastPathComponent());
            }
        });

        ProjectTreeContextMenu menu = new ProjectTreeContextMenu(view);
        menu.setInvoker(this);
        setComponentPopupMenu(menu);
        ToolTipManager.sharedInstance().registerComponent(this);

    }

    @Override
    public boolean hasBeenExpanded(TreePath path) {
        /*
         * by always returning false we always get a root handle for every
         * non-leaf. grml...
         */
        return false;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        final TreePath path = getPathForLocation(event.getX(), event.getY());

        if (path == null) {
            return null;
        }

        return new ProjectTreeNodeVisitor() {

            String tooltip = null;

            {
                ((ProjectTreeNode<?>) path.getLastPathComponent()).accept(this);
            }

            @Override
            public void visit(ProjectTreeComponentTypeNode node) {

            }

            public String getText() {
                return tooltip;
            }

            @Override
            public void visit(ProjectTreeBrickNode node) {

            }

            @Override
            public void visit(ProjectTreeProjectNode node) {
                tooltip = node.getContent().getAbsolutePath();
                if (tooltip == null || tooltip.isEmpty()) {
                    tooltip = tr("ProjectTree.tooltip.unsavedProject");
                }
            }

            @Override
            public void visit(ProjectTreeRootNode node) {

            }
        }.getText();

    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.hasCurrentProjectChanged()) {
            TreePath path =
                    new TreePath(model.getRoot()
                            .getChildNodeByContent(source.getCurrentProject())
                            .getAbsolutePath());
            TreePath selectedPath = getSelectionPath();

            // select project if current selection does not belong to the
            // project
            if (!path.isDescendant(selectedPath)) {
                this.setSelectionPath(path);
            }
        }
    }
}
