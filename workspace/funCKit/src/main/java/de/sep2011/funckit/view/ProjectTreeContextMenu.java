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

import static de.sep2011.funckit.util.internationalization.Language.tr;
import static de.sep2011.funckit.model.sessionmodel.SessionModel.ViewType.*;

import java.awt.event.ActionEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.TreePath;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.sep2011.funckit.controller.listener.OpenPropertyDialogFromTreeActionListener;
import de.sep2011.funckit.controller.listener.project.DeleteProjectActionListener;
import de.sep2011.funckit.controller.listener.project.OpenAsNewPojectActionListener;
import de.sep2011.funckit.controller.listener.project.OpenNewEditPanelFromTreeActionListener;
import de.sep2011.funckit.controller.listener.project.RemoveBrickActionListener;
import de.sep2011.funckit.controller.listener.project.TreeCloseProjectActionListener;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeBrickNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeComponentTypeNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeNodeVisitor;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeProjectNode;
import de.sep2011.funckit.view.projecttreemodel.ProjectTreeRootNode;

/**
 * A context menu, used as the context menu for the {@link ProjectTree}.
 */
public class ProjectTreeContextMenu extends JPopupMenu implements
        PopupMenuListener {

    private static final long serialVersionUID = -3400964856046068517L;
    private View view;
    private int x = 0;
    private int y = 0;
    private Multimap<Object, SessionModel.ViewType> viewTypeObjectMap;

    /**
     * Creates a new {@link ProjectTreeContextMenu}.
     * 
     * @param view the associated view.
     */
    public ProjectTreeContextMenu(View view) {
        super();
        init(view);
    }
    /**
     * Creates a new {@link ProjectTreeContextMenu}.
     * 
     * @param view the associated view.
     * @param label see {@link JPopupMenu#JPopupMenu(String)}
     */
    
    public ProjectTreeContextMenu(String label, View view) {
        super(label);
        init(view);
    }

    private void init(View view) {
        this.view = view;
        viewTypeObjectMap = HashMultimap.create();
        addPopupMenuListener(this);
    }

    @Override
    public void show(java.awt.Component invoker, int x, int y) {
        this.x = x;
        this.y = y;
        super.show(invoker, x, y);
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        final Set<JMenuItem> addItems = new LinkedHashSet<JMenuItem>();

        final ProjectTree tree = (ProjectTree) getInvoker();

        final TreePath path = tree.getPathForLocation(x, y);

        tree.setSelectionPath(path); // select item at menu position

        final JMenuItem openInTabItem =
                new JMenuItem(tr("ProjectTreeContextMenu.openInNewTab"));
        openInTabItem
                .addActionListener(new OpenNewEditPanelFromTreeActionListener(
                        view, view.getController(), path));
        viewTypeObjectMap.put(openInTabItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(openInTabItem, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(openInTabItem, VIEW_TYPE_PRESENTER);


        final JMenuItem editPropertyItem =
                new JMenuItem(tr("ProjectTreeContextMenu.editProperties"));
        editPropertyItem
                .addActionListener(new OpenPropertyDialogFromTreeActionListener(
                        view, view.getController(), path));
        viewTypeObjectMap.put(editPropertyItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(editPropertyItem, VIEW_TYPE_ELEANING_SOLVE);

        final JMenuItem deleteProjectItem =
                new JMenuItem(tr("ProjectTreeContextMenu.deleteProject"));
        viewTypeObjectMap.put(deleteProjectItem, VIEW_TYPE_STANDALONE);
        
        final JMenuItem closeProjectItem =
                new JMenuItem(tr("ProjectTreeContextMenu.closeProject"));
        viewTypeObjectMap.put(closeProjectItem, VIEW_TYPE_STANDALONE);

        final JMenuItem openAsNewProjectItem =
                new JMenuItem(tr("ProjectTreeContextMenu.openAsNewProject"));
        viewTypeObjectMap.put(openAsNewProjectItem, VIEW_TYPE_STANDALONE);

        final JMenuItem removeBrickFromProjectItem =
                new JMenuItem(
                        tr("ProjectTreeContextMenu.removeBrickFromProject"));
        removeBrickFromProjectItem
                .addActionListener(new RemoveBrickActionListener(view, view
                        .getController(), path));
        viewTypeObjectMap.put(removeBrickFromProjectItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(removeBrickFromProjectItem, VIEW_TYPE_ELEANING_SOLVE);

        final JMenuItem changeProjectNameItem =
                new JMenuItem(tr("ProjectTreeContextMenu.changeBrickNameItem"));
        viewTypeObjectMap.put(changeProjectNameItem, VIEW_TYPE_STANDALONE);

        new ProjectTreeNodeVisitor() {

            {
                if (path != null) {
                    ((ProjectTreeNode<?>) path.getLastPathComponent())
                            .accept(this);
                }

            }

            @Override
            public void visit(ProjectTreeComponentTypeNode node) {
            }

            @Override
            public void visit(ProjectTreeBrickNode node) {
                if (node.getContent() instanceof Component) {
                    addItems.add(openInTabItem);

                    openAsNewProjectItem
                            .addActionListener(new OpenAsNewPojectActionListener(
                                    view, view.getController(),
                                    ((Component) node.getContent()).getType()));
                    addItems.add(openAsNewProjectItem);
                }

                if (node.getParent() instanceof ProjectTreeProjectNode) {
                    Project pro = (Project) node.getParent().getContent();
                    addItems.add(editPropertyItem);
                    editPropertyItem.setEnabled(!pro.hasSimulation());

                    addItems.add(removeBrickFromProjectItem);
                    removeBrickFromProjectItem
                            .setEnabled(pro.getSimulation() == null);
                }

            }

            @Override
            public void visit(final ProjectTreeProjectNode node) {

                addItems.add(deleteProjectItem);
                deleteProjectItem
                        .addActionListener(new DeleteProjectActionListener(
                                view, view.getController(), node.getContent()));

                if (node.getContent().getCircuit() != null) {
                    addItems.add(changeProjectNameItem);
                    changeProjectNameItem
                            .addActionListener(new AbstractAction() {
                            	
								private static final long serialVersionUID = -4757644591573034715L;

								@Override
                                public void actionPerformed(ActionEvent e) {
                                    JDialog dialog =
                                            new ChangeProjectNameDialog(view,
                                                    node.getContent());
                                    dialog.setLocationRelativeTo(view
                                            .getMainRootPane());
                                    dialog.setVisible(true);

                                }
                            });

                    addItems.add(openInTabItem);
                    addItems.add(closeProjectItem);
                    closeProjectItem
                            .addActionListener(new TreeCloseProjectActionListener(
                                    view, view.getController(), node
                                            .getContent()));
                }
            }

            @Override
            public void visit(ProjectTreeRootNode node) {
            }

        };

        if (addItems.contains(openInTabItem)) {
            addViewTypeDependant(openInTabItem);
        }

        if (addItems.contains(openAsNewProjectItem)) {
            addViewTypeDependant(openAsNewProjectItem);
        }

        if (addItems.contains(closeProjectItem)) {
            addViewTypeDependant(closeProjectItem);
        }

        if (addItems.contains(deleteProjectItem)) {
            addViewTypeDependant(deleteProjectItem);
        }

        if (addItems.contains(removeBrickFromProjectItem)) {
            addViewTypeDependant(removeBrickFromProjectItem);
        }

        if (addItems.contains(changeProjectNameItem)) {
            addViewTypeDependant(changeProjectNameItem);
        }

        if (addItems.contains(editPropertyItem)) {
            addViewTypeDependant(editPropertyItem);
        }

    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        removeAll();

    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        removeAll();

    }
    
    public void addViewTypeDependant(JMenuItem item) {
        if(viewTypeObjectMap.containsEntry(item, view.getSessionModel().getViewType())) {
            add(item);
        }
    }

}
