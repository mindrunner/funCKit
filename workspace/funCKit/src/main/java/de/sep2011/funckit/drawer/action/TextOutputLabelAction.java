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
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class TextOutputLabelAction extends DefaultDrawAction implements DrawAction {
    /**
     * Pixel puffer between access point and its label.
     */
    private static final int LABEL_PUFFER_TO_ACCESSPOINT = 2;

    /**
     * Space on left or right side of a brick for output relative to whole brick
     * width. Value between zero and one.
     */
    private static final double RELATIVE_SPACE_FOR_OUTPUT = 0.2;
    private static final Font DEFAULT_OUTPUT_FONT = new Font("Serif", Font.PLAIN, 8);

    private int outputRadius;
    private Font outputFont;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);

        /* Receive input point radius from settings and cache it for this call. */
        outputRadius = settings.getInt(Settings.OUTPUT_RADIUS);
        // must be defined in default settings!
        assert outputRadius != 0;

        if (settings.getString(Settings.OUTPUT_FONT) != null) {
            outputFont = settings.get(Settings.OUTPUT_FONT, Font.class);
        } else {
            outputFont = DEFAULT_OUTPUT_FONT;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        Point p = output.getPosition();
        Rectangle r = output.getBrick().getBoundingRect();

        /* Add label text for output. */
        Font font = outputFont;
        String actualText = getTextForAccessPoint(output.getName(), r.width,
                font);
        FontMetrics metrics = graphics.getFontMetrics(font);
        int actualTextWidth = metrics.stringWidth(actualText);
        int posX = p.x - actualTextWidth - outputRadius
                - LABEL_PUFFER_TO_ACCESSPOINT;
        int posY = p.y + outputRadius;
        layout.addText(new LayoutText(actualText, new Point(posX, posY), font,
                settings.get(Settings.ELEMENT_OUTPUT_LABEL_COLOR, Color.class)));
    }

    private String getTextForAccessPoint(String text, int width, Font font) {
        int spaceForAccessPoint = (int) Math.round(width * RELATIVE_SPACE_FOR_OUTPUT);
        return DrawUtil.cutTextToWidth(text, spaceForAccessPoint, font, graphics, "");
    }
}
