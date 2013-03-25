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

package de.sep2011.funckit.drawer.action;

import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import static de.sep2011.funckit.util.DrawUtil.injectShape;

/**
 * Action that initializes {@link Layout.BASE_SHAPE} with simple shapes (simpler
 * than {@link FancyShapeAction}.
 */
public class SimpleShapeAction extends DefaultDrawAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Wire wire, Layout layout) {
        injectShape(layout, DrawUtil.calculateWireShape(wire));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Component component, Layout layout) {
        Rectangle r = component.getBoundingRect();
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        //layout.setShapePath(getRectangle(r.x, r.y, r.width, r.height));
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {
        Rectangle r = s.getBoundingRect();
        // layout.setShapePath(getRectangle(r.x, r.y, r.width, r.height));
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Light light, Layout layout) {
        injectShape(layout, DrawUtil.calculateLightShape(light));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {
        Rectangle r = and.getBoundingRect();
        //layout.setShapePath(getRectangle(r.x, r.y, r.width, r.height));
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        Rectangle r = or.getBoundingRect();
        //layout.setShapePath(getRectangle(r.x, r.y, r.width, r.height));
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        Rectangle r = not.getBoundingRect();
        //layout.setShapePath(getRectangle(r.x, r.y, r.width, r.height));
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    private Path2D getRectangle(int x, int y, int width, int height) {
        return new Path2D.Double(new Rectangle2D.Double(x, y, width, height));
    }
}
