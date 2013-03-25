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

import de.sep2011.funckit.util.Pair;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a tree node used by {@link ProjectTreeModel}. It
 * represents an object of type T.
 * 
 * @param <T>
 *            The type of the content this tree node represents.
 */
public abstract class ProjectTreeNode<T> {
    /** The list of children of this node. null if no children generated. */
    List<ProjectTreeNode<?>> children;

    /** The {@link ProjectTreeModel} this node corresponds to. */
    ProjectTreeModel treeModel;

    /** The parent of this node. */
    ProjectTreeNode<?> parent;

    /** Stores the content of this node. */
    T nodeContent;

    /**
     * Returns true if this Node is a leaf of the tree.
     * 
     * @return true if this Node is a leaf of the tree.
     */
    public abstract boolean isLeaf();

    /**
     * Returns the parent of this node, null if this node has no parent.
     * 
     * @return the parent of this node, null if this node has no parent.
     */
    public ProjectTreeNode<?> getParent() {
        return parent;
    }

    /**
     * Returns the object the node represents.
     * 
     * @return the object the node represents
     */
    public T getContent() {
        return nodeContent;
    }

    /**
     * Tests if one of the direct children of this node contains the given
     * object as content.
     * 
     * @param content
     *            object to test
     * @return true if found, else false
     */
    boolean childContainsContent(Object content) {
        for (ProjectTreeNode<?> node : children) {
            if (node.getContent().equals(content)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the node inside the list of children at the given index.
     * 
     * @param index
     *            the index of the node
     * @return the child node, null if the internal children list is null.
     */
    public ProjectTreeNode<?> getChild(int index) {
        return children == null ? null : children.get(index);
    }

    /**
     * Returns the number of children this node has.
     * 
     * @return the number of children this node has.
     */
    public int getChildCount() {
        return children == null ? 0 : children.size();
    }

    /**
     * Calling this method triggers the lazy creation of the nodes Children.
     * 
     * @return true if the creation of the children succeeded, false if the node
     *         could not generate its children.
     */
    public abstract boolean generateChildren();

    /**
     * Disposes all children.
     */
    public void disposeChildren() {
        disposeChildren(true);
    }

    /**
     * Calls {@link #dispose()} on all children recursively.
     * 
     * @param root
     *            set this to true if this is the topmost node to dispose.
     */
    void disposeChildren(boolean root) {
        if (children != null) {
            for (ProjectTreeNode<?> node : children) {
                node.disposeChildren(false);
                node.dispose();
            }

            children = null;

            if (root) {
                treeModel.fireTreeStructureChanged(getAbsolutePath());
            }
        }
    }

    /**
     * Returns the first child node inside the list of children which has the
     * given object as content.
     * 
     * @param content
     *            the content object of the node to find.
     * @return the found node, null is none is found.
     */
    public ProjectTreeNode<?> getChildNodeByContent(Object content) {
        for (ProjectTreeNode<?> child : children) {
            if (child.getContent().equals(content)) {
                return child;
            }
        }

        return null;
    }

    /**
     * Get all child node which have one of the given object as content.
     * 
     * @param contents
     *            the content objects to get the nodes for
     * @return left: an array of indices of the corresponding child nodes <br>
     *         right: an array of the corresponding child nodes as
     *         {@link Object}s
     */
    Pair<int[], Object[]> getChildNodesByContent(Collection<?> contents) {
        if (children == null) {
            return null;
        }

        LinkedList<Integer> idxes = new LinkedList<Integer>();
        int idxCnt = 0;

        List<Object> affectedChildren = new LinkedList<Object>();

        for (ProjectTreeNode<?> child : children) {
            for (Object cont : contents) {
                if (cont.equals(child.getContent())) {
                    idxes.add(idxCnt);
                    affectedChildren.add(child);
                    break;
                }
            }
            idxCnt++;
        }

        int[] retArr = new int[idxes.size()];
        int retCnt = 0;
        for (int idx : idxes) {
            retArr[retCnt] = idx;
            retCnt++;
        }

        return new Pair<int[], Object[]>(retArr, affectedChildren.toArray());
    }

    /**
     * Returns the path from the root node to this node.
     * 
     * @return the path from the root node to this node.
     */
    public ProjectTreeNode<?>[] getAbsolutePath() {
        LinkedList<ProjectTreeNode<?>> path = new LinkedList<ProjectTreeNode<?>>();
        for (ProjectTreeNode<?> parent = this; parent != null; parent = parent
                .getParent()) {
            path.addFirst(parent);
        }

        return path.toArray(new ProjectTreeNode<?>[0]);
    }

    /**
     * Simpler version for removeChildrenByContent with only one content object.
     * 
     * @param content
     *            the object to remove.
     * @return left: an array of indices of the removed child nodes <br>
     *         right: an array of the removed child nodes as {@link Object}s
     */
    Pair<int[], Object[]> removeChildrenByContent(Object content) {
        Object[] contents = { content };
        return removeChildrenByContent(contents);
    }

    /**
     * This method removes all children which have one of the given content
     * objects as content.
     * 
     * @param contents
     *            the content objects for the corresponding children to remove,
     * @return
     */
    Pair<int[], Object[]> removeChildrenByContent(Object[] contents) {
        if (children == null) {
            return null;
        }

        LinkedList<Integer> idxes = new LinkedList<Integer>();
        int idxCnt = 0;

        List<ProjectTreeNode<?>> removedChildren = new LinkedList<ProjectTreeNode<?>>();

        for (Iterator<ProjectTreeNode<?>> it = children.iterator(); it
                .hasNext();) {

            ProjectTreeNode<?> next = it.next();

            for (Object cnt : contents) {
                if (next.getContent().equals(cnt)) {
                    idxes.add(idxCnt);
                    next.disposeChildren();
                    next.dispose();
                    it.remove();
                    removedChildren.add(next);
                    break;
                }
            }

            idxCnt++;
        }

        int[] retArr = new int[idxes.size()];
        int retCnt = 0;
        for (int idx : idxes) {
            retArr[retCnt] = idx;
            retCnt++;
        }

        return new Pair<int[], Object[]>(retArr, removedChildren.toArray());

    }

    /**
     * Does everything to dispose this node (e.g. unregister as observer).
     */
    protected abstract void dispose();

    /**
     * Subclasses need to implement this and call visitor.visit(this).
     * 
     * @param visitor
     *            The visitor to accept.
     */
    public abstract void accept(ProjectTreeNodeVisitor visitor);

    /**
     * Returns the index of the given child node.
     * 
     * @param child
     *            the child to get the index for.
     * @return the index of the given child node, -1 if the given node is not a
     *         child of this node.
     */
    int getIndexOfChild(ProjectTreeNode<?> child) {
        return children == null ? -1 : children.indexOf(child);
    }
}
