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
import static com.google.common.base.Preconditions.*;

import java.awt.Point;
import java.util.ArrayList;

import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * This node type represents {@link Brick}s. It has
 * {@link ProjectTreeComponentTypeNode}s as children if the content is a
 * {@link Component} else it has no children.
 */
public class ProjectTreeBrickNode extends ProjectTreeNode<Brick> {

    /**
     * Creates a new {@link ProjectTreeBrickNode}.
     * 
     * @param tm
     *            the corresponding {@link ProjectTreeModel}, not null.
     * @param br
     *            the brick which is the content, not null.
     * @param parent
     *            the parent of this node, not null.
     */
    public ProjectTreeBrickNode(ProjectTreeModel tm, Brick br,
            ProjectTreeNode<?> parent) {
        checkNotNull(tm);
        checkNotNull(br);
        checkNotNull(parent);

        this.nodeContent = br;
        this.parent = parent;
        this.treeModel = tm;
    }

    @Override
    public boolean isLeaf() {
        return !(nodeContent instanceof Component);
    }

    @Override
    public boolean generateChildren() {
        if (nodeContent instanceof Component) {
            Component comp = (Component) nodeContent;

            if (comp.getType().getCircuit().getElements().isEmpty()) {
                return false;
            }

            children = new ArrayList<ProjectTreeNode<?>>();

            children.add(new ProjectTreeComponentTypeNode(treeModel, comp
                    .getType(), this));

            treeModel.fireTreeStructureChanged(getAbsolutePath());
        }
        return true;

    }

    @Override
    public void dispose() {
    }

    @Override
    public String toString() {

        String brickName = new ElementDispatcher() {
            String name = "unknown";

            {
                nodeContent.dispatch(this);
            }

            public String getName() {
                return name;
            }

            @Override
            public void visit(IdPoint idPoint) {
                name = "IdPoint";

            }

            @Override
            public void visit(Not not) {
                name = "Not";
            }

            @Override
            public void visit(Or or) {
                name = "Or";

            }

            @Override
            public void visit(And and) {
                name = "And";
            }

            @Override
            public void visit(Light light) {
                name = tr("ProjectTreeBrickNode.Light");

            }

            @Override
            public void visit(Switch s) {
                name = tr("ProjectTreeBrickNode.Switch");

            }

            @Override
            public void visit(Component component) {
                name = tr("ProjectTreeBrickNode.Component");

            }

            @Override
            public void visit(Wire wire) {
                name = "Wire";

            }

            @Override
            public void visit(Element element) {
                name = "Element";

            }
        }.getName();

        Point pos = nodeContent.getPosition();

        return new StringBuilder().append(brickName).append(": \"")
                .append(nodeContent.getName()).append("\" @ (").append(pos.x)
                .append(",").append(pos.y).append(")").toString();

    }

    @Override
    public void accept(ProjectTreeNodeVisitor visitor) {
        visitor.visit(this);
    }
}
