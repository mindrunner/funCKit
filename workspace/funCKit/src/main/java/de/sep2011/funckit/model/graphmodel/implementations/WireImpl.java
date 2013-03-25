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

package de.sep2011.funckit.model.graphmodel.implementations;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Wire;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Default Implementation of the {@link Wire} Interface.
 */
public class WireImpl extends ElementImpl implements Wire {

    /**
     * The first {@link AccessPoint} of this wire.
     */
    private AccessPoint firstAccessPoint;

    /**
     * The second {@link AccessPoint} of this wire.
     */
    private AccessPoint secondAccessPoint;

    /**
     * Create a new {@link WireImpl}.
     * 
     * @param fst
     *            the first {@link AccessPoint} the Wire is connected to
     * @param snd
     *            the second {@link AccessPoint} the Wire is connected to
     */
    public WireImpl(AccessPoint fst, AccessPoint snd) {
        super();
        init(fst, snd);
    }

    /**
     * Create a new {@link WireImpl}. Arguments may be null.
     * 
     * @param fst
     *            the first {@link AccessPoint} the Wire is connected to
     * @param snd
     *            the second {@link AccessPoint} the Wire is connected to
     * @param name
     *            The Descriptive Name see {@link Element#getName()}
     * @see Brick#getName()
     */
    public WireImpl(AccessPoint fst, AccessPoint snd, String name) {
        super(name);

        init(fst, snd);
    }

    /**
     * Helper for Constructor.
     */
    private void init(AccessPoint fst, AccessPoint snd) {
        firstAccessPoint = fst;
        secondAccessPoint = snd;
    }

    /**
     * Returns bounding rectangle of a wire. This rectangle does not get cached!
     * Caching is not possible as accesspoints could be moved by moving bricks.
     * This effects in a new bounding rectangle!
     * 
     * @return the bounding rect
     */
    @Override
    public Rectangle getBoundingRect() {
        int x;
        int y;
        int width;
        int height;

        int firstAccessPointX = firstAccessPoint.getBrick().getBoundingRect().x
                + firstAccessPoint.getPosition().x;
        int firstAccessPointY = firstAccessPoint.getBrick().getBoundingRect().y
                + firstAccessPoint.getPosition().y;
        int secondAccessPointX = secondAccessPoint.getBrick().getBoundingRect().x
                + secondAccessPoint.getPosition().x;
        int secondAccessPointY = secondAccessPoint.getBrick().getBoundingRect().y
                + secondAccessPoint.getPosition().y;

        if (firstAccessPointX > secondAccessPointX) {
            x = secondAccessPointX;
            width = firstAccessPointX - secondAccessPointX;
        } else {
            x = firstAccessPointX;
            width = secondAccessPointX - firstAccessPointX;
        }

        if (firstAccessPointY > secondAccessPointY) {
            y = secondAccessPointY;
            height = firstAccessPointY - secondAccessPointY;
        } else {
            y = firstAccessPointY;
            height = secondAccessPointY - firstAccessPointY;
        }

        Rectangle boundingRect = new Rectangle(x, y, width, height);
        return boundingRect;
    }

    @Override
    public AccessPoint getFirstAccessPoint() {
        return firstAccessPoint;
    }

    @Override
    public void setFirstAccessPoint(AccessPoint accessPoint1) {
        this.firstAccessPoint = accessPoint1;
    }

    @Override
    public AccessPoint getSecondAccessPoint() {
        return secondAccessPoint;
    }

    @Override
    public void setSecondAccessPoint(AccessPoint accessPoint2) {
        this.secondAccessPoint = accessPoint2;
    }

    @Override
    public AccessPoint getOther(AccessPoint ap) {
        if (ap == firstAccessPoint) {
            return secondAccessPoint;
        } else if (ap == secondAccessPoint) {
            return firstAccessPoint;
        } else {
            return null;
        }
    }

    @Override
    public void dispatch(ElementDispatcher dispatcher) {
        dispatcher.visit(this);
    }

    @Override
    public Wire getNewInstance(Point position) {
        return new WireImpl(null, null);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Element: Wire, ");
        stringBuilder.append(super.toString());
        stringBuilder.append(" with access points (");
        stringBuilder.append(firstAccessPoint);
        stringBuilder.append(", ");
        stringBuilder.append(secondAccessPoint);
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc} Returns always null.
     */
    @Override
    public AccessPoint getAccessPointAtPosition(Point position, int tolerance) {
        return null;
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        if (firstAccessPoint == null || secondAccessPoint == null
                || firstAccessPoint.getBrick() == null
                || secondAccessPoint.getBrick() == null) {
            return false;
        }

        int fstApX = firstAccessPoint.getBrick().getBoundingRect().x
                + firstAccessPoint.getPosition().x;
        int fstApY = firstAccessPoint.getBrick().getBoundingRect().y
                + firstAccessPoint.getPosition().y;
        int sndApX = secondAccessPoint.getBrick().getBoundingRect().x
                + secondAccessPoint.getPosition().x;
        int sndApY = secondAccessPoint.getBrick().getBoundingRect().y
                + secondAccessPoint.getPosition().y;

        Line2D line = new Line2D.Float(fstApX, fstApY, sndApX, sndApY);

        return line.intersects(rectangle);
    }

    /**
     * {@inheritDoc} This Method does nothing as the bounding Rectangle of a
     * Wire depends solely on the context.
     */
    @Override
    public void setBoundingRect(Rectangle rectangle) {
    }

    /**
     * {@inheritDoc} This Method does nothing.
     */
    @Override
    public void setPosition(Point position) {
    }

    /**
     * {@inheritDoc} This Method does nothing.
     */
    @Override
    public void setDimension(Dimension dimension) {
    }

}