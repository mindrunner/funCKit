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
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.util.Log;

import java.util.ArrayList;

import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * This node type represents {@link ComponentType}s. It has
 * {@link ProjectTreeBrickNode}s coming from the {@link ComponentType}'s
 * {@link Circuit} as children. It observes the the {@link Circuit} after
 * expansion..
 * 
 */
public class ProjectTreeComponentTypeNode extends
        ProjectTreeNode<ComponentType> implements GraphModelObserver {

    /**
     * Creates a new {@link ProjectTreeComponentTypeNode}.
     * 
     * @param tm
     *            the corresponding {@link ProjectTreeModel}, not null.
     * @param ct
     *            the {@link ComponentType} that is the content of this node,
     *            not null.
     * @param parent
     *            the parent of this node, not null.
     */
    public ProjectTreeComponentTypeNode(ProjectTreeModel tm, ComponentType ct,
            ProjectTreeNode<?> parent) {
        checkNotNull(ct);
        checkNotNull(tm);
        checkNotNull(parent);

        this.parent = parent;
        this.nodeContent = ct;
        this.treeModel = tm;
    }

    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {
        // the circuit of a Component doesn't change => nothing to do here
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean generateChildren() {
        if (nodeContent.getCircuit().getElements().isEmpty()) {
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
                            ProjectTreeComponentTypeNode.this));
                }
            };

            nodeContent.getCircuit().addObserver(this);
            treeModel.fireTreeStructureChanged(getAbsolutePath());

        } else {
            Log.gl().debug("Children already generated!");
        }

        return true;

    }

    @Override
    public void dispose() {
        nodeContent.getCircuit().deleteObserver(this);
    }

    @Override
    public String toString() {
        return tr("ProjectTreeComponentTypeNode.ComponentType") + ": \""
                + nodeContent.getName() + "\"";
    }

    @Override
    public void accept(ProjectTreeNodeVisitor visitor) {
        visitor.visit(this);
    }

}
