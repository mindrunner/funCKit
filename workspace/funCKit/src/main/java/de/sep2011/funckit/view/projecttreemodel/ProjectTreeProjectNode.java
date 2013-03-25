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

import de.sep2011.funckit.model.graphmodel.Brick;
import static com.google.common.base.Preconditions.*;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This tree node has {@link Project}s as content. It has
 * {@link ProjectTreeBrickNode}s coming from the {@link Project}'s
 * {@link Circuit} as children. It observes the {@link Project} and the
 * corresponding {@link Circuit} after expansion..
 */
public class ProjectTreeProjectNode extends ProjectTreeNode<Project> implements
        ProjectObserver, GraphModelObserver {

    /* keep this reference so we can be sure that we can unregister on dispose() */
    private Circuit observedCircuit;

    /**
     * Create a new {@link ProjectTreeProjectNode}.
     * 
     * @param tm
     *            the corresponding {@link ProjectTreeModel}, not null.
     * @param pro
     *            the project which is the content of this node, not null.
     * @param parent
     *            the parent of this node, not null.
     */
    public ProjectTreeProjectNode(ProjectTreeModel tm, Project pro,
            ProjectTreeNode<?> parent) {
        checkNotNull(tm);
        checkNotNull(pro);
        checkNotNull(parent);

        this.treeModel = tm;
        this.nodeContent = pro;
        this.parent = parent;

        pro.addObserver(this);
    }

    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {
        if (children == null) {
            Log.gl().warn("children is null but should not be");
            return;
        }

        if (!i.getAddedBricks().isEmpty()) {
            int startIdx = children.size();
            for (Brick brick : i.getAddedBricks()) {
                if (!(brick instanceof IdPoint) && !childContainsContent(brick)) {
                    children.add(new ProjectTreeBrickNode(treeModel, brick,
                            this));
                }
            }

            int[] idxes = new int[children.size() - startIdx];
            ProjectTreeNode<?>[] added = new ProjectTreeNode<?>[children.size()
                    - startIdx];

            for (int j = 0; j < idxes.length; j++) {
                idxes[j] = startIdx + j;
                added[j] = children.get(startIdx + j);
            }

            treeModel.fireTreeNodesInserted(getAbsolutePath(), idxes, added);

        }

        if (!i.getRemovedBricks().isEmpty()) {
            Pair<int[], Object[]> rmpair = removeChildrenByContent(i
                    .getRemovedBricks().toArray());

            treeModel.fireTreeNodesRemoved(getAbsolutePath(), rmpair.getLeft(),
                    rmpair.getRight());
        }

        if (!i.getChangedBricks().isEmpty()) {
            Pair<int[], Object[]> chpair = getChildNodesByContent(i
                    .getChangedBricks());

            Log.gl().debug(
                    Arrays.toString(chpair.getLeft()) + "~~"
                            + Arrays.toString(chpair.getRight()));

            treeModel.fireTreeNodesChanged(getAbsolutePath(), chpair.getLeft(),
                    chpair.getRight());
        }
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (i.isCircuitChanged()) {
            treeModel.fireTreeStructureChanged(getAbsolutePath());
        }

        if (i.isNameChanged()) {
            int[] idxes = { parent.getIndexOfChild(this) };
            ProjectTreeNode<?>[] chch = { this };

            treeModel.fireTreeNodesChanged(parent.getAbsolutePath(), idxes, chch);
        }

    }

    @Override
    public boolean isLeaf() {
        return nodeContent.getCircuit() == null;
    }

    @Override
    public boolean generateChildren() {
        if (nodeContent.getCircuit() == null
                || nodeContent.getCircuit().getElements().isEmpty()) {
            return false;
        }

        if (children == null) {
            children = new ArrayList<ProjectTreeNode<?>>();

            new ElementDispatcher() {

                {
                    for (Element elem : nodeContent.getCircuit().getElements()) {
                        elem.dispatch(this);

                    }
                }

                @Override
                public void visit(IdPoint idPoint) {
                }

                @Override
                public void visit(Not not) {
                    addBrick(not);

                }

                @Override
                public void visit(Or or) {
                    addBrick(or);

                }

                @Override
                public void visit(And and) {
                    addBrick(and);
                }

                @Override
                public void visit(Light light) {
                    addBrick(light);

                }

                @Override
                public void visit(Switch s) {
                    addBrick(s);

                }

                @Override
                public void visit(Component component) {
                    addBrick(component);

                }

                @Override
                public void visit(Wire wire) {

                }

                @Override
                public void visit(Element element) {

                }

                private void addBrick(Brick b) {
                    children.add(new ProjectTreeBrickNode(treeModel, b,
                            ProjectTreeProjectNode.this));
                }
            };

            observedCircuit = nodeContent.getCircuit();
            observedCircuit.addObserver(this);
            treeModel.fireTreeStructureChanged(getAbsolutePath());

        } else {
            Log.gl().debug("Children already generated!");
        }

        return true;

    }

    @Override
    public void dispose() {
        nodeContent.deleteObserver(this);
        if (observedCircuit != null) {
            observedCircuit.deleteObserver(this);
            observedCircuit = null;
        }
    }

    @Override
    public String toString() {
        return nodeContent.getName();
    }

    @Override
    public void accept(ProjectTreeNodeVisitor visitor) {
        visitor.visit(this);
    }

}
