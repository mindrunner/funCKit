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
import java.awt.geom.Path2D;

/**
 * Informational object for shapes, that are injected in a certain {@link
 * Layout} object. It specifies the shape itself, a border color and a fill
 * color.
 */
public class LayoutShape {
    /**
     * Shape path definition of this object.
     */
    private Path2D shapePath;

    /**
     * Border color {@link LayoutShape#shapePath} is drawn with.
     */
    private Color borderColor = Color.PINK;

    /**
     * Fill color for {@see LayoutShape#shapePath}. May not be null.
     * Transparency should work if graphics object in drawer supports
     * transparent colors.
     */
    private Color fillColor = Color.CYAN;

    /**
     * Constructor, that makes consistency of object sure.
     *
     * @param shape       Shape to add, that may not be null.
     * @param borderColor Color for shape border, that may not be null.
     * @param fillColor   Color for filling shape. May not be null.
     */
    public LayoutShape(Path2D shape, Color borderColor, Color fillColor) {
        assert shape != null;
        assert borderColor != null;
        assert fillColor != null;

        shapePath = shape;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    /**
     * Getter method for {@link LayoutShape#shapePath}.
     *
     * @return Shape path.
     */
    public Path2D getShapePath() {
        return shapePath;
    }

    /**
     * Getter method for border color.
     *
     * @return Border color.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Getter method for fill color.
     *
     * @return Fill color for shape.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Specifies new border color for this shape.
     *
     * @param borderColor New border color, that may not be null. But you can
     *                    use transparency. See {@link de.sep2011.funckit.util.DrawUtil#TRANSPARENT}.
     */
    public void setBorderColor(Color borderColor) {
        assert borderColor != null;
        this.borderColor = borderColor;
    }

    /**
     * Specifies new fill color for this shape.
     *
     * @param fillColor New fill color, that may not be null. But you can use
     *                  transparency. See {@link de.sep2011.funckit.util.DrawUtil#TRANSPARENT}.
     */
    public void setFillColor(Color fillColor) {
        assert fillColor != null;
        this.fillColor = fillColor;
    }
}
