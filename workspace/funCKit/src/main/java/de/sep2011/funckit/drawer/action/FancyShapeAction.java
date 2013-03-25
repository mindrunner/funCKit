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
import de.sep2011.funckit.drawer.LayoutShape;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import static de.sep2011.funckit.util.DrawUtil.injectShape;

/**
 * Action, that prepares an element type specific shape.
 */
public class FancyShapeAction extends DefaultDrawAction {

    private static final double DEFAULT_ROUND_RECTANGLE_DIAMETER = 5;

    private int inputRadius;
    private int outputRadius;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);

        /* Receive input point radius from settings and cache it for this call. */
        inputRadius = settings.getInt(Settings.INPUT_RADIUS);
        outputRadius = settings.getInt(Settings.OUTPUT_RADIUS);

        // must be defined in default settings!
        assert inputRadius != 0;
        assert outputRadius != 0;
    }

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
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {
        Rectangle r = s.getBoundingRect();
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
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        Rectangle r = or.getBoundingRect();
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        Rectangle r = not.getBoundingRect();
        Path2D shape = getRectangle(r.x, r.y, r.width, r.height);
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(IdPoint idPoint, Layout layout) {
        Rectangle r = idPoint.getBoundingRect();
        Path2D shape = new Path2D.Double(new Ellipse2D.Double(r.x, r.y, r.width,
                r.height));
        injectShape(layout, shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input input, Layout layout) {
        Point p = input.getPosition();
        Rectangle r = input.getBrick().getBoundingRect();
        Path2D shape = getRectangle(r.x + p.x
                - inputRadius, r.y + p.y - inputRadius,
                2 * inputRadius, 2 * inputRadius);
        // injectShape(layout, shape);
        layout.putShape(
                Layout.INPUTS_SHAPE,
                new LayoutShape(
                        shape,
                        Layout.DEBUG_BORDER_COLOR,
                        Layout.DEBUG_FILL_COLOR
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        Point p = output.getPosition();
        Rectangle r = output.getBrick().getBoundingRect();
        Path2D shape = getRectangle(r.x + p.x
                - outputRadius, r.y + p.y - outputRadius,
                2 * outputRadius, 2 * outputRadius);
        // injectShape(layout, shape);
        layout.putShape(
                Layout.OUTPUTS_SHAPE,
                new LayoutShape(
                        shape,
                        Layout.DEBUG_BORDER_COLOR,
                        Layout.DEBUG_FILL_COLOR
                )
        );
    }

    /**
     * Returns a rectangle shape with given properties.
     *
     * @param x      Coordinate for x direction.
     * @param y      Coordinate for y direction.
     * @param width  Width of rectangle.
     * @param height Height of rectangle.
     * @return Shape of rectangle.
     */
    private Path2D getRectangle(int x, int y, int width, int height) {
        return new Path2D.Double(new RoundRectangle2D.Double(x, y,
                width, height, DEFAULT_ROUND_RECTANGLE_DIAMETER,
                DEFAULT_ROUND_RECTANGLE_DIAMETER));
    }

}
