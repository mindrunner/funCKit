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

import de.sep2011.funckit.model.sessionmodel.Project;
import static com.google.common.base.Preconditions.*;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Pair;

import java.util.ArrayList;

/**
 * Represents the root of the project tree. It has {@link ProjectTreeRootNode}s
 * as children and observes the given {@link SessionModel} for changed projects
 * if expanded.
 */
public class ProjectTreeRootNode extends ProjectTreeNode<SessionModel>
        implements SessionModelObserver {

    /**
     * Creates a new {@link ProjectTreeRootNode}.
     * 
     * @param tm
     *            the {@link ProjectTreeModel} this node corresponds to, not
     *            null.
     * @param sm
     *            the {@link SessionModel} where the node gets its data from,
     *            not null.
     */
    public ProjectTreeRootNode(ProjectTreeModel tm, SessionModel sm) {
        checkNotNull(tm);
        checkNotNull(sm);

        this.treeModel = tm;
        this.nodeContent = sm;
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.hasProjectAdded()) {
            ProjectTreeNode<?> newNode = new ProjectTreeProjectNode(treeModel,
                    i.getChangedProject(), this);
            children.add(newNode);

            int[] chInd = { children.size() - 1 };
            Object[] chNode = { newNode };
            treeModel.fireTreeNodesInserted(getAbsolutePath(), chInd, chNode);
        }

        if (i.hasProjectRemoved()) {
            Pair<int[], Object[]> rmpair = removeChildrenByContent(i
                    .getChangedProject());

            treeModel.fireTreeNodesRemoved(getAbsolutePath(), rmpair.getLeft(),
                    rmpair.getRight());
        }

    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean generateChildren() {
        if (children == null) {
            children = new ArrayList<ProjectTreeNode<?>>();
            for (Project proj : nodeContent.getProjects()) {
                ProjectTreeProjectNode child = new ProjectTreeProjectNode(
                        treeModel, proj, this);
                children.add(child);
            }

            nodeContent.addObserver(this);
            treeModel.fireTreeStructureChanged(getAbsolutePath());
        } else {
            Log.gl().debug("Children already generated!");
        }

        return true;
    }

    @Override
    public void dispose() {
        nodeContent.deleteObserver(this);
    }

    @Override
    public void accept(ProjectTreeNodeVisitor visitor) {
        visitor.visit(this);
    }
}
