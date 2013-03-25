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
import de.sep2011.funckit.drawer.LayoutText;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Margin;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Action for layouts, that display a queue of output values (from simulation).
 * Only applied if simulation is running. Adds shape to {@link
 * Layout.QUEUE_SHAPE}.
 */
public class QueueAction extends DefaultDrawAction {
    private static final Color DEFAULT_BORDER_COLOR = Color.black;
    private static final Color DEFAULT_FILL_COLOR = Color.WHITE;
    private static final Font DEFAULT_TEXT_FONT = new Font("Helvetica", Font.PLAIN, 6);
    private static final Color DEFAULT_TEXT_COLOR = Color.black;
    private static final Margin DEFAULT_MARGIN = new Margin(2, 2, 2, 2);
    private static final int QUEUE_DISTANCE_TO_OUTPUT = 1;
    private static final String QUEUE_TEXT_LOGIC_1 = "1";
    private static final String QUEUE_TEXT_LOGIC_0 = "0";
    private Color queueBorderColor;
    private Color queueFillColor;
    private Font queueTextFont;
    private Color queueTextColor;

    /**
     * Margin with absolute pixel values.
     */
    private Margin queueMargin;
    private int outputRadius;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);

        /* Receive input point radius from settings and cache it for this call. */
        outputRadius = settings.getInt(Settings.OUTPUT_RADIUS);

        /* Take queue border color from settings. */
        if (settings.getString(Settings.QUEUE_BORDER_COLOR) != null) {
            queueBorderColor = settings.get(Settings.QUEUE_BORDER_COLOR, Color.class);
        } else {
            queueBorderColor = DEFAULT_BORDER_COLOR;
        }

        /* Take queue fill color from settings. */
        if (settings.getString(Settings.QUEUE_FILL_COLOR) != null) {
            queueFillColor = settings.get(Settings.QUEUE_FILL_COLOR, Color.class);
        } else {
            queueFillColor = DEFAULT_FILL_COLOR;
        }

        /* Take queue font from settings. */
        if (settings.getString(Settings.QUEUE_TEXT_FONT) != null) {
            queueTextFont = settings.get(Settings.QUEUE_TEXT_FONT, Font.class);
        } else {
            queueTextFont = DEFAULT_TEXT_FONT;
        }

        /* Take queue text color from settings. */
        if (settings.getString(Settings.QUEUE_TEXT_COLOR) != null) {
            queueTextColor = settings.get(Settings.QUEUE_TEXT_COLOR, Color.class);
        } else {
            queueTextColor = DEFAULT_TEXT_COLOR;
        }

        /* Take queue text color from settings. */
        if (settings.getString(Settings.QUEUE_MARGIN) != null) {
            queueMargin = settings.get(Settings.QUEUE_MARGIN, Margin.class);
        } else {
            queueMargin = DEFAULT_MARGIN;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Component component, Layout layout) {
        addQueueShapeAndText(
                layout,
                component.getBoundingRect(),
                component.getOutputs()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {
        addQueueShapeAndText(
                layout,
                and.getBoundingRect(),
                and.getOutputs()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        addQueueShapeAndText(
                layout,
                or.getBoundingRect(),
                or.getOutputs()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        addQueueShapeAndText(
                layout,
                not.getBoundingRect(),
                not.getOutputs()
        );
    }

    private void addQueueShapeAndText(
            Layout layout,
            Rectangle boundingRectangle,
            Set<Output> outputs) {
        Map<Output, Queue<Boolean>> outputQueueMap = layout.getOutputQueueMap();
        Path2D allQueueShapes = new Path2D.Double();
        int cx = boundingRectangle.x;
        int cy = boundingRectangle.y;

        for (Output output : outputs) {
            Queue<Boolean> queue = outputQueueMap.get(output);
            if (queue == null) {
                continue;
            }

            Point p = output.getPosition();

            StringBuilder queueText = new StringBuilder();
            int i = 0;
            for (boolean value : queue) {
                if (i != 0) {
                    queueText.append(' ');
                }
                i++;
                if (value) {
                    queueText.append(QUEUE_TEXT_LOGIC_1);
                } else {
                    queueText.append(QUEUE_TEXT_LOGIC_0);
                }
            }
            queueText.reverse();

            FontMetrics metrics = graphics.getFontMetrics(queueTextFont);
            int textWidth = metrics.stringWidth(queueText.toString());
            int textHeight = metrics.getHeight();

            int textX = (int) Math.round(p.x
                    - textWidth
                    - queueMargin.getRight()
                    - outputRadius
                    - QUEUE_DISTANCE_TO_OUTPUT);
            int textY = p.y + (int) Math.round(0.5 * outputRadius);

            /* Calculate queue width, height and position with margin. */
            int queueWidth = (int) Math.round(textWidth + queueMargin.getLeft() + queueMargin.getRight());
            int queueHeight = (int) Math.round(textHeight + queueMargin.getTop() + queueMargin.getBottom());
            int queueX = (int) Math.round(cx + textX - queueMargin.getLeft());
            int queueY = cy + textY - textHeight;

            /* As text starts at lower left corner (base line), draft Y by textHeight. */
            //textY += textHeight;

            Rectangle2D rectangle = new Rectangle2D.Double(queueX, queueY, queueWidth, queueHeight);
            allQueueShapes.append(rectangle, false);

            layout.addText(new LayoutText(
                    queueText.toString(),
                    new Point(textX, textY),
                    queueTextFont,
                    queueTextColor
            ));
        }

        layout.putShape(
                Layout.QUEUE_SHAPE,
                new LayoutShape(allQueueShapes, queueBorderColor, queueFillColor)
        );
    }

}
