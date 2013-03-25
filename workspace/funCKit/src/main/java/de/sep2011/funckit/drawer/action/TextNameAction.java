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
import de.sep2011.funckit.drawer.LayoutText;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;
import de.sep2011.funckit.util.Margin;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Action to display name (and currently type of element, too) and adds it int
 * layout object.
 */
public class TextNameAction extends DefaultDrawAction implements DrawAction {
    private static final Margin MARGIN_TEXT_NAME = new Margin(0.2, 0.2, 0.2, 0.2);
    private static final Font DEFAULT_ELEMENT_FONT = new Font("Serif", Font.PLAIN, 6);
    private static final Color DEFAULT_ELEMENT_NAME_COLOR = Color.black;

    /**
     * Suffix for texts that overflow a specific length.
     */
    private static final String TEXT_OVERFLOW_SUFFIX = "..";

    private static final double MAGIC_BASELINE_DRAFT_FACTOR = 0.2;

    private static final float MAXIMUM_FONT_SIZE = 30f;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Component component, Layout layout) {
        Rectangle r = component.getBoundingRect();

        /* Add text, that gets automatically adapted to size of component. */
        layout.addText(getAdaptedLayoutTextForName(
                component.getName(),
                r
        ));

        layout.addText(getLayoutTextForType("(" + component.getType().getName()
                + ")", r.width, r.height));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {
        Rectangle r = s.getBoundingRect();

        /* Add text, that gets automatically adapted to size of switch. */
        layout.addText(getAdaptedLayoutTextForName(
                s.getName(),
                r
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {
        Rectangle r = and.getBoundingRect();

        /* Add text, that gets automatically adapted to size of and-gate. */
        layout.addText(getAdaptedLayoutTextForName(
                and.getName(),
                r
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        Rectangle r = or.getBoundingRect();

        /* Add text, that gets automatically adapted to size of or-gate. */
        layout.addText(getAdaptedLayoutTextForName(
                or.getName(),
                r
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        Rectangle r = not.getBoundingRect();

        /* Add text, that gets automatically adapted to size of not-gate. */
        layout.addText(getAdaptedLayoutTextForName(
                not.getName(),
                r
        ));
    }

    /**
     * Calculates a {@link LayoutText} object for type of a brick.
     *
     * @param originalText Descriptive text for element type for display on UI.
     * @param width        Width of element.
     * @param height       Height of element.
     * @return Calculated text-layout descriptive object to add in a {@link
     *         Layout}.
     */
    private LayoutText getLayoutTextForType(String originalText, int width,
                                            int height) {
        Font font = new Font("Serif", Font.PLAIN, 5);
        String actualText = DrawUtil.cutTextToWidth(originalText, width, font, graphics,
                TEXT_OVERFLOW_SUFFIX);
        FontMetrics metrics = graphics.getFontMetrics(font);
        int actualTextWidth = metrics.stringWidth(actualText);
        int textHeight = metrics.getHeight();

        Point position = DrawUtil.getCenterPosition(width, height, actualTextWidth,
                textHeight);
        position.y += metrics.getHeight();

        return new LayoutText(actualText, position, font, settings.get(
                Settings.ELEMENT_TYPE_COLOR, Color.class));
    }

    /**
     * Returns a {@link LayoutText}, that is adapted to the given bounding
     * rectangle, considering margin values (currently taken from constants).
     *
     * @param actualText        Text to draw as name.
     * @param boundingRectangle Bounding rectangle ob element.
     * @return Calculated text-layout descriptive object to add in a {@link
     *         Layout}.
     */
    private LayoutText getAdaptedLayoutTextForName(String actualText, Rectangle boundingRectangle) {
        double marginLeft = boundingRectangle.width * MARGIN_TEXT_NAME.getLeft();
        double marginRight = boundingRectangle.width * MARGIN_TEXT_NAME.getRight();
        int textWidth = (int) Math.round(boundingRectangle.width - marginLeft - marginRight);

        double marginTop = boundingRectangle.height * MARGIN_TEXT_NAME.getTop();
        double marginBottom = boundingRectangle.height * MARGIN_TEXT_NAME.getBottom();
        int textHeight = (int) Math.round(boundingRectangle.height - marginTop - marginBottom);

        /* Receive font via settings. */
        Font font = settings.get(Settings.ELEMENT_NAME_FONT, Font.class);
        if (font == null) {
            font = DEFAULT_ELEMENT_FONT;
        }

        /* Try to fit text by increasing or decreasing font. */
        font = DrawUtil.resizeFontToFit(textWidth, textHeight, actualText, font, graphics);

        /* If font size is at minimum, cut text to fit width. */
        int fontSize = font.getSize();
        if (fontSize == 1) {
            actualText = DrawUtil.cutTextToWidth(actualText, textWidth, font, graphics,
                    TEXT_OVERFLOW_SUFFIX);
        }

        /* Limit font size to a maximum. */
        if (fontSize > MAXIMUM_FONT_SIZE) {
            font = font.deriveFont(MAXIMUM_FONT_SIZE);
        }

        /* Calculate text position. */
        FontMetrics metrics = graphics.getFontMetrics(font);
        int actualTextWidth = metrics.stringWidth(actualText);
        int fontHeight = metrics.getHeight();

        int startX = DrawUtil.calculateCenteredStart(
                boundingRectangle,
                actualTextWidth
        );
        int startY = (int) Math.round(boundingRectangle.height * 0.5 + fontHeight * MAGIC_BASELINE_DRAFT_FACTOR);
        Point position = new Point(startX, startY);

        /* Receive text color via settings. */
        Color nameColor = settings.get(Settings.ELEMENT_NAME_COLOR, Color.class);
        if (nameColor == null) {
            nameColor = DEFAULT_ELEMENT_NAME_COLOR;
        }

        return new LayoutText(actualText, position, font, nameColor);
    }

}
