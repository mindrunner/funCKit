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
import java.util.Map;
import java.util.Set;

/**
 * Represents a logical gate. It has {@link Set}s of associated {@link Input}s
 * and {@link Output}s. Each {@link Output} of a Gate represents the result of a
 * boolean function from the values on the {@link Input}s
 */
public interface Gate extends Brick {

    /**
     * Calculate the values of the {@link Output}s according to the values of
     * the {@link Input}s.
     * 
     * @param inputValues
     *            values of the {@link Input}s
     * @return values of the {@link Output}s
     */
    public Map<Output, Boolean> calculate(Map<Input, Boolean> inputValues);

    /**
     * Creates a new instance of this Gate.
     * 
     * @param position
     *            passed to the Constructor
     * @return the new Gate
     */
    @Override
    public Gate getNewInstance(Point position);

}