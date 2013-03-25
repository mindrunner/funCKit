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

package de.sep2011.funckit.model.graphmodel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * {@link Element} is the Parent Interface for all elements of a {@link Circuit}
 * .
 */
public interface Element {

    /**
     * Every Element has a (non-unique) name this Method returns it.
     * 
     * @return the name of the component, never null
     */
    public String getName();

    /**
     * Every Element has a (non-unique) name this sets it.
     * 
     * @param n
     *            name to set, not null
     */
    public void setName(String n);

    /**
     * Used for double dispatch, accepts an {@link ElementDispatcher} as in the
     * visitor pattern.
     * 
     * @param dispatcher
     *            dispatcher to accept.
     */
    public void dispatch(ElementDispatcher dispatcher);

    /**
     * Every {@link Element} has a size and a position, this method returns it.
     * 
     * @return the Bounding {@link java.awt.Rectangle} of element
     */
    public Rectangle getBoundingRect();

    /**
     * Creates a new instance of this Element.
     * 
     * @param position
     *            passed to the Constructor
     * @return the new Element
     */
    public Element getNewInstance(Point position);

    /**
     * Checks if there is an {@link AccessPoint} on given position.
     * 
     * @param position
     *            Position to look for an {@link AccessPoint}. Position is a
     *            relative coordinate to Brick's rectangle and must not excess
     *            that border.
     * @param tolerance
     *            Tolerance value to receive nearest AccessPoint to position
     *            with this radius.
     * @return {@link AccessPoint} that was found or null if there is none.
     */
    public AccessPoint getAccessPointAtPosition(Point position, int tolerance);

    /**
     * Specifies bounding {@link Rectangle} of a Element. Bounding rectangle of
     * {@link Element}s define size and relative position inside a
     * {@link Circuit}. Implementors may ignore the passed information partially
     * or fully as appropriate.
     * 
     * @param rectangle
     *            may not be null
     */
    public void setBoundingRect(Rectangle rectangle);

    /**
     * Returns the position of the {@link Brick}.
     * 
     * @return the position of the {@link Brick}.
     */
    public Point getPosition();

    /**
     * Sets the position of the {@link Brick}.
     * 
     * @param position
     *            the position to set.
     */
    public void setPosition(Point position);

    /**
     * Returns the dimension of the {@link Brick}.
     * 
     * @return the dimension of the {@link Brick}.
     */
    public Dimension getDimension();

    /**
     * Sets the dimension of the {@link Brick}.
     * 
     * @param dimension
     *            the dimension to set.
     */
    public void setDimension(Dimension dimension);

    /**
     * Test for intersection.
     * 
     * @param rectangle
     *            a Rectangle
     * @return true if rectangle intersects this
     * @since implementation
     */
    public boolean intersects(Rectangle2D rectangle);

}