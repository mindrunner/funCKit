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
 * A Switch is a {@link Brick} with no {@link Input}s and one {@link Output}. On
 * the {@link Output} there is always one boolean value until it is changed to
 * the other.
 */
public interface Switch extends Brick {

    /**
     * Same as setValue(!getValue()).
     */
    public void toggle();

    /**
     * Set the Value this Switch should have on his output (turn switch on or
     * off).
     * 
     * @param value
     *            the new value
     */
    public void setValue(boolean value);

    /**
     * Get the Value this Switch has on its output (switch on or off).
     * 
     * @return the value
     */
    public boolean getValue();

    /**
     * Creates a new instance of this Switch.
     * 
     * @param position
     *            passed to the Constructor
     * @return the new Switch
     */
    @Override
    public Switch getNewInstance(Point position);
}
