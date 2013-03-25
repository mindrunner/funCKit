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

package de.sep2011.funckit.view.projecttreemodel;

import de.sep2011.funckit.model.sessionmodel.SessionModel;

import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * This class implements the {@link TreeModel} interface used by the the
 * {@link JTree} representing the Project tree.
 */
public class ProjectTreeModel implements TreeModel {

    private final ProjectTreeNode<?> root;
    private final EventListenerList treeModelListeners;

    /**
     * Creates a new {@link ProjectTreeModel}. It builds a tree with
     * {@link ProjectTreeRootNode} as its root node.
     * 
     * @param sm
     *            The {@link SessionModel} the root node will be initialized
     *            with.
     */
    public ProjectTreeModel(SessionModel sm) {
        this.root = new ProjectTreeRootNode(this, sm);
        this.treeModelListeners = new EventListenerList();

        this.root.generateChildren();
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the changed elements
     * @param children
     *            the changed elements
     * @see EventListenerList
     */
    void fireTreeNodesChanged(Object[] path, int[] childIndices,
            Object[] children) {

        // Guaranteed to return a non-null array
        Object[] listeners = treeModelListeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                TreeModelEvent ev = new TreeModelEvent(this, path,
                        childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(ev);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the new elements
     * @param children
     *            the new elements
     * @see EventListenerList
     */
    void fireTreeNodesInserted(Object[] path, int[] childIndices,
            Object[] children) {

        // Guaranteed to return a non-null array
        Object[] listeners = treeModelListeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                TreeModelEvent ev = new TreeModelEvent(this, path,
                        childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(ev);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the removed elements
     * @param children
     *            the removed elements
     * @see EventListenerList
     */
    void fireTreeNodesRemoved(Object[] path, int[] childIndices,
            Object[] children) {

        // Guaranteed to return a non-null array
        Object[] listeners = treeModelListeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                TreeModelEvent ev = new TreeModelEvent(this, path,
                        childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(ev);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param path
     *            the path to the root node
     * @see EventListenerList
     */
    void fireTreeStructureChanged(Object[] path) {

        // Guaranteed to return a non-null array
        Object[] listeners = treeModelListeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                TreeModelEvent ev = new TreeModelEvent(this, path);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(ev);
            }
        }
    }

    @Override
    public ProjectTreeNode<?> getRoot() {
        return root;
    }

    @Override
    public ProjectTreeNode<?> getChild(Object parent, int index) {
        return ((ProjectTreeNode<?>) parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((ProjectTreeNode<?>) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((ProjectTreeNode<?>) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Unimplemented!");

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (!(parent instanceof ProjectTreeNode<?>)
                || !(child instanceof ProjectTreeNode<?>)) {
            return -1;
        }

        return ((ProjectTreeNode<?>) parent)
                .getIndexOfChild((ProjectTreeNode<?>) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(TreeModelListener.class, l);
    }

    /**
     * Do everything necessary to expand the tree. Meant to be called from a
     * {@link TreeWillExpandListener}.
     * 
     * @param node
     *            The node to expand.
     * @return true if the expansion was successful, false otherwise
     */
    public static boolean expandNode(Object node) {
        return ((ProjectTreeNode<?>) node).generateChildren();
    }

    /**
     * Do everything necessary to collapse the tree. Meant to be called from a
     * {@link TreeWillExpandListener}.
     * 
     * @param node
     *            The node to collapse.
     * @return true if the collapse was successful, false otherwise
     */
    public static boolean collapseNode(Object node) {
        ((ProjectTreeNode<?>) node).disposeChildren();
        return true;
    }
}
