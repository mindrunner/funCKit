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

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

/**
 * Descriptive object for texts in a layout object, containing information about
 * font, size, position and actual text.
 */
public class LayoutText {
    /**
     * Displayed text. May not be null (but empty).
     */
    private String text;

    private Color color;

    private Point relativePosition;

    /**
     * Font for displayed text, also containing its size information.
     */
    private Font font;

    /**
     * Constructor that expects all necessary information.
     *
     * @param text             Text to write on UI.
     * @param relativePosition Relative position of text (lower left corner of
     *                         font) in an element.
     * @param font             Font to draw text with.
     * @param color            Color to draw text with.
     */

    public LayoutText(
            String text,
            Point relativePosition,
            Font font, Color color) {
        assert text != null;
        assert font != null;
        assert relativePosition != null;
        assert color != null;

        this.text = text;
        this.font = font;
        this.relativePosition = relativePosition;
        this.color = color;
    }

    /**
     * Getter method for text.
     *
     * @return Text as string, that may not be null.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns relative position of this text according to associated {@link
     * Layout} and thus {@link Element}.
     *
     * @return Non-null relative position.
     */
    public Point getRelativePosition() {
        return relativePosition;
    }

    /**
     * Getter method for text font.
     *
     * @return Text font. May not be null.
     */
    public Font getFont() {
        return font;
    }

    /**
     * Getter method for text color.
     *
     * @return Text color. May not be null.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Specifies a new text color.
     *
     * @param color New color. May not be null.
     */
    public void setColor(Color color) {
        assert color != null;
        this.color = color;
    }
}
