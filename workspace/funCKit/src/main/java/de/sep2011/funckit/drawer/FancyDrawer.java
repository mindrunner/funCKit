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

package de.sep2011.funckit.drawer;

import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/**
 * Full featured drawer, that draws elements with all its labels, access points,
 * surrounding wires and connection points ({@link IdPoint}s).
 */
public class FancyDrawer implements Drawer {
    /**
     * LayoutResolver object for access points to enable more flexible layouts
     * for them.
     */
    private LayoutResolver accessPointLayoutResolver = new LayoutResolver();

    /**
     * State resolver for using decision table in connection with access points
     */
    private ElementStateResolver accessPointStateResolver;

    /**
     * Injected graphics object on which this drawer paints all the stuff on.
     */
    private Graphics2D graphics;

    /**
     * Injected action, that is performed to apply all information to the
     * prepared layout object for drawing.
     */
    private DrawAction action;

    /**
     * Injected layout object with basic information; e.g. about simulation
     * queue or other information, that can not be resolved via {@link
     * DrawAction} (and thus {@link DecisionTable}).
     */
    private Layout layout;

    /**
     * Injected application settings object to apply latest configurations for
     * colors, fonts, sizes, margins etc. pp.
     */
    private Settings settings;

    /**
     * Constructor with settings injection.
     *
     * @param settings Application's settings object.
     */
    public FancyDrawer(Settings settings) {
        assert settings != null;
        this.layout = new Layout();
        this.settings = settings;
    }

    /**
     * Other constructor to inject a modified {@link LayoutResolver}, that is
     * used for resolving {@link Layout} objects for {@link AccessPoint}s in
     * further drawing phases. Thus it would be possible to access simulation
     * information for access points, so they could be drawn in a simulated
     * state for example.
     *
     * @param settings                  Regular applications settings object.
     * @param accessPointLayoutResolver Modified non-null layout resolver.
     */
    public FancyDrawer(
            Settings settings,
            LayoutResolver accessPointLayoutResolver,
            ElementStateResolver accessPointStateResolver) {
        assert settings != null;
        assert accessPointLayoutResolver != null;
        this.layout = new Layout();
        this.settings = settings;
        this.accessPointLayoutResolver = accessPointLayoutResolver;
        this.accessPointStateResolver = accessPointStateResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGraphics(Graphics graphics) {
        assert graphics != null;
        this.graphics = (Graphics2D) graphics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAction(DrawAction action) {
        assert action != null;
        this.action = action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLayout(Layout layout) {
        assert layout != null;
        this.layout = layout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Element element) {
        throw new UnsupportedOperationException(
                "Raw element object can not be drawn.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Wire wire) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(wire, layout);

        drawShapes(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Component component) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(component, layout);

        drawShapes(layout);
        drawAccessPoints(component);
        drawTexts(component, layout, graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Switch s) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(s, layout);

        drawShapes(layout);
        drawAccessPoints(s);
        drawTexts(s, layout, graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Light light) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(light, layout);

        drawShapes(layout);
        drawAccessPoints(light);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(And and) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(and, layout);

        drawShapes(layout);
        drawAccessPoints(and);
        drawTexts(and, layout, graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Or or) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(or, layout);

        drawShapes(layout);
        drawAccessPoints(or);
        drawTexts(or, layout, graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Not not) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(not, layout);

        drawShapes(layout);
        drawAccessPoints(not);
        drawTexts(not, layout, graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdPoint idPoint) {
        assert graphics != null;
        assert action != null;
        assert layout != null;

        action.setUp(layout, settings, graphics);
        action.prepare(layout);
        action.prepare(idPoint, layout);

        drawShapes(layout);
    }

    /**
     * {@inheritDoc}
     */
    private void drawShapes(Layout layout) {
        for (String shapeName : layout.getShapeMap().keySet()) {
            LayoutShape layoutShape = layout.getShape(shapeName);
            drawShape(
                    layoutShape.getShapePath(),
                    layoutShape.getBorderColor(),
                    layoutShape.getFillColor()
            );
        }
    }

    /**
     * Draws a given shape with a specific border and fill color on the current
     * graphics object.
     *
     * @param shape  Shape descriptive object, that should be drawn on current
     *               graphics object.
     * @param border Color to use for shapes border.
     * @param fill   Color used for filling shape.
     */
    private void drawShape(Path2D shape, Color border, Color fill) {
        graphics.setColor(fill);
        graphics.fill(shape);
        graphics.setColor(border);
        graphics.draw(shape);
    }

    /**
     * Draws all access points of the given brick.
     *
     * @param brick Brick which access points should be drawn.
     */
    private void drawAccessPoints(Brick brick) {
        for (Input input : brick.getInputs()) {
            /*
             * Resolve layout for input object. If layout resolver has already injected a
             * simulation object, resolver can set layout to simulated state.
             */
            Layout inputLayout = new Layout();
            accessPointLayoutResolver.setLayout(inputLayout);
            accessPointLayoutResolver.resolve(input);

            DrawAction inputAction = action;

            /*
             * If there is a own {@link ElementStateResolver} for access points
             * injected, then use that resolver to change the action for setting
             * up the layout of current input, so we can draw different selection
             * or active states for single access points.
             */
            if (accessPointStateResolver != null) {
                ElementState state = accessPointStateResolver.resolve(input);
                inputAction = DecisionTable.resolve(state);
            }

            inputAction.setUp(inputLayout, settings, graphics);
            inputAction.prepare(inputLayout);
            inputAction.prepare(input, inputLayout);
            drawAccessPoint(brick, inputLayout);
        }

        for (Output output : brick.getOutputs()) {
            /*
             * Resolve layout for output object. If layout resolver has already injected a
             * simulation object, resolver can set layout to simulated state.
             */
            Layout outputLayout = new Layout();
            accessPointLayoutResolver.setLayout(outputLayout);
            accessPointLayoutResolver.resolve(output);

            DrawAction outputAction = action;

            /*
             * If there is a own {@link ElementStateResolver} for access points
             * injected, then use that resolver to change the action for setting
             * up the layout of current input, so we can draw different selection
             * or active states for single access points.
             */
            if (accessPointStateResolver != null) {
                ElementState state = accessPointStateResolver.resolve(output);
                outputAction = DecisionTable.resolve(state);
            }

            outputAction.setUp(outputLayout, settings, graphics);
            outputAction.prepare(outputLayout);
            outputAction.prepare(output, outputLayout);
            drawAccessPoint(brick, outputLayout);
        }
    }

    /**
     * Draws a certain access point from a given layout with information from
     * its associated brick.
     *
     * @param brick  Brick of the current drawn access point.
     * @param layout Layout object with descriptive information about access
     *               point design.
     */
    private void drawAccessPoint(Brick brick, Layout layout) {
        /*drawShape(layout.getAccessPointShapePath(), layout.getBorderColor(),
                layout.getFillColor());*/
        drawShapes(layout);
        drawTexts(brick, layout, graphics);
    }

    private static void drawTexts(Brick s, Layout layout, Graphics2D graphics) {
        for (LayoutText text : layout.getTextList()) {
            graphics.setColor(text.getColor());
            graphics.setFont(text.getFont());
            graphics.drawString(text.getText(),
                    s.getBoundingRect().x + text.getRelativePosition().x,
                    s.getBoundingRect().y + text.getRelativePosition().y);
        }
    }
}
