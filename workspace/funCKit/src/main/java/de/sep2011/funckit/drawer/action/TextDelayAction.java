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
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Action to display delay (if it is not zero) on elements.
 */
public class TextDelayAction extends DefaultDrawAction implements DrawAction {
    private static final Font DEFAULT_ELEMENT_DELAY_FONT = new Font("Helvetica", Font.BOLD, 6);
    private static final Color DEFAULT_ELEMENT_DELAY_COLOR = Color.black;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Component component, Layout layout) {
        Rectangle r = component.getBoundingRect();

        if (component.getDelay() != 0) {
            layout.addText(getLayoutTextForDelay(
                    component.getDelay(),
                    r.width,
                    r.height
            ));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {
        Rectangle r = and.getBoundingRect();

        if (and.getDelay() != 0) {
            layout.addText(getLayoutTextForDelay(
                    and.getDelay(),
                    r.width,
                    r.height
            ));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        Rectangle r = or.getBoundingRect();

        if (or.getDelay() != 0) {
            layout.addText(getLayoutTextForDelay(
                    or.getDelay(),
                    r.width,
                    r.height
            ));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        Rectangle r = not.getBoundingRect();

        if (not.getDelay() != 0) {
            layout.addText(getLayoutTextForDelay(
                    not.getDelay(),
                    r.width,
                    r.height
            ));
        }
    }

    /**
     * Calculates a {@link de.sep2011.funckit.drawer.LayoutText}, containing the
     * given delay value and positions it in relative position of given width
     * and height. Uses objects from settings to specify font and color of
     * text.
     *
     * @param delay  Delay value of element.
     * @param width  Elements width to position the text.
     * @param height Elements height to position the text.
     * @return Calculated text-layout descriptive object to append in a {@link
     *         Layout} object.
     */
    private LayoutText getLayoutTextForDelay(int delay, int width, int height) {
        Font font;
        if (settings.getString(Settings.ELEMENT_DELAY_FONT) != null) {
            font = settings.get(Settings.ELEMENT_DELAY_FONT, Font.class);
        } else {
            font = DEFAULT_ELEMENT_DELAY_FONT;
        }
        Color color;
        if (settings.getString(Settings.ELEMENT_DELAY_COLOR) != null) {
            color = settings.get(Settings.ELEMENT_DELAY_COLOR, Color.class);
        } else {
            color = DEFAULT_ELEMENT_DELAY_COLOR;
        }

        String delayText = String.valueOf(delay);
        FontMetrics metrics = graphics.getFontMetrics(font);
        int textWidth = metrics.stringWidth(delayText);

        return new LayoutText(
                delayText,
                new Point(
                        width - textWidth - 1,
                        height - 1
                ),
                font,
                color
        );
    }
}
