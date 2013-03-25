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

import java.awt.Point;
import java.util.Set;

/**
 * An AccessPoint represents a connection point for {@link Wire}s and have
 * clearly specified positions on their associated brick. Each AccessPoint can
 * be connected with several wires, but exist only on one brick.
 */
public interface AccessPoint {

    /**
     * Returns the {@link Brick} this {@link AccessPoint} is set on (or
     * connected to).
     *
     * @return Brick object, that must not be null.
     */
    public Brick getBrick();

    /**
     * Returns set of {@link Wire}s connected to this {@link AccessPoint}. If no
     * AccessPoints are connected, it returns an empty set (not null!).
     *
     * @return {@link Set} of {@link Wire}s
     */
    public Set<Wire> getWires();

    /**
     * Adds given {@link Wire} to this {@link AccessPoint}. Given wire must not
     * be null. If wire is already connected (contained in this AccessPoint), it
     * takes no effect.
     *
     * @param w {@link Wire} to add.
     */
    public void addWire(Wire w);

    /**
     * Removes {@link Wire} from {@link AccessPoint}. Wire may not be null. If
     * given wire does not exist in this AccessPoint, it takes no effect.
     *
     * @param w {@link Wire} to remove
     */
    public void removeWire(Wire w);

    /**
     * Returns a descriptive name of this {@link AccessPoint}, which can be
     * freely specified with {@code setName()}.
     *
     * @return Current specified name. It can be null!
     */
    public String getName();

    /**
     * Specifies the descriptive name of {@link AccessPoint}. There is no logic
     * limit in length and also can be null.
     *
     * @param name New descriptive name.
     */
    public void setName(String name);

    /**
     * Returns position of the {@link AccessPoint} relative to its associated
     * {@link Brick}.
     *
     * @return Point specifying coordinates relative to upper left Brick
     *         corner.
     */
    public Point getPosition();

    /**
     * Specifies position of the {@link AccessPoint} relative to its {@link
     * Brick}.
     *
     * @param p New position relative to upper left Brick corner.
     */
    public void setPosition(Point p);

}
