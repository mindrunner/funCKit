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

/**
 * A Wire is a {@link Element} which connects two {@link AccessPoint}s.
 */
public interface Wire extends Element {

    /**
     * Get the first {@link AccessPoint} which is usually connected to a
     * {@link Brick}.
     * 
     * @return the first {@link AccessPoint}, can be null if this is an
     *         unconnected Wire
     */
    AccessPoint getFirstAccessPoint();

    /**
     * Set the first {@link AccessPoint} of this Wire which is usually connected
     * to a {@link Brick}.
     * 
     * @param fst
     *            the first second {@link AccessPoint}
     */
    void setFirstAccessPoint(AccessPoint fst);

    /**
     * Get the second {@link AccessPoint} of this Wire which is usually
     * connected to a {@link Brick}.
     * 
     * @return the second {@link AccessPoint}, can be null if this is an
     *         unconnected Wire
     */
    AccessPoint getSecondAccessPoint();

    /**
     * Set the second {@link AccessPoint} of this Wire which is usually
     * connected to a {@link Brick}.
     * 
     * @param snd
     *            the second {@link AccessPoint}
     */
    void setSecondAccessPoint(AccessPoint snd);

    /**
     * For a given {@link AccessPoint} get the {@link AccessPoint} it is
     * connected to by this Wire.
     * 
     * @param ap
     *            the given {@link AccessPoint}, should be one of the
     *            {@link Wire}s {@link AccessPoint}s
     * @return the other {@link AccessPoint}, can be null if other is null
     */
    AccessPoint getOther(AccessPoint ap);

    /**
     * Creates a new instance of this Wire.
     * 
     * @param position
     *            passed to the Constructor
     * @return the new Wire
     */
    @Override
    public Wire getNewInstance(Point position);
}