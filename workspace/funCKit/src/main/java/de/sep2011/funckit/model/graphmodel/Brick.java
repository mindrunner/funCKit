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

import de.sep2011.funckit.util.Pair;

import java.awt.Point;
import java.util.Map;
import java.util.Set;

/**
 * A Brick is a {@link Element} in a logical circuit. It has a set of
 * {@link Input}s and a set of {@link Output}s, where one of them may be empty.
 */
public interface Brick extends Element {
    /** The default orientation of the Brick. */
    public static final Orientation DEFAULT_ORIENTATION = Orientation.WEST;
    /** The default width of the Brick. */
    public static final int DEFAULT_WIDTH = 40;
    /** The default height of the Brick. */
    public static final int DEFAULT_HEIGHT = 40;
    /** The Default name of the first input. */
    public static final String DEFAULT_INPUT_1_NAME = "a";
    /** The Default name of the second input. */
    public static final String DEFAULT_INPUT_2_NAME = "b";
    /** The Default name of the first output. */
    public static final String DEFAULT_OUTPUT_NAME = "o";

    /**
     * Orientation of elements represent the direction where the {@link Output}s
     * are pointing to. Orientations are kept as geographical direction.
     */
    public enum Orientation {
        /**
         * outputs to north.
         */
        NORTH,
        /**
         * outputs to south.
         */
        SOUTH,
        /**
         * outputs to east.
         */
        EAST,
        /**
         * outputs to west.
         */
        WEST
    }

    /**
     * Returns current {@link Orientation} of the {@link Brick}.
     * 
     * @return Current orientation of the brick.
     */
    public Orientation getOrientation();

    /**
     * Sets the {@link Brick}s {@link Orientation}.
     * 
     * @param o
     *            New orientation.
     */
    public void setOrientation(Orientation o);

    /**
     * Return all {@link Input}s of the brick with direct access (no set copy).
     * 
     * @return inputs WARNING: you should not manipulate this {@link Set}
     *         directly
     */
    public Set<Input> getInputs();

    /**
     * Return all {@link Output}s of the brick with direct access (no set copy).
     * 
     * @return outputs WARNING: you should not manipulate this {@link Set}
     *         directly
     */
    public Set<Output> getOutputs();

    /**
     * Checks if a brick has a delay greater than zero.
     * 
     * @return <code>true</code> if delay is greater than zero,
     *         <code>false</code> otherwise.
     */
    boolean hasDelay();

    /**
     * Returns currently set delay of this brick.
     * 
     * @return delay Discrete delay value.
     */
    public int getDelay();

    /**
     * Change delay value of this brick. Has to be a non-negative number.
     * 
     * @param delay
     *            New discrete delay value.
     */
    public void setDelay(int delay);

    /**
     * Returns an {@link Input} by name. If two or more inputs have same name,
     * this method returns the first it finds. If no {@link Input} with the name
     * exists null is returned.
     * 
     * @param name
     *            Name of the input to search.
     * @return Corresponding input or null if nothing was found.
     */
    public Input getInput(String name);

    /**
     * Returns an {@link Output} by name. If two or more have the same name,
     * this method returns the first it finds. If no {@link Output} with the
     * name exists null is returned.
     * 
     * @param name
     *            Name of the output to search.
     * @return Corresponding output or null if nothing was found.
     */
    public Output getOutput(String name);

    /**
     * Check if this brick and the given brick are equal by comparing the
     * attributes (e.g. Position, name, ...) and ignoring graph attributes
     * (wires). Inputs and outputs are themself compared by their attributes
     * 
     * @param other
     *            brick to compare with
     * @return true if both bricks are equal by the definition above, otherwise
     *         false
     */
    public boolean attributesEqual(Brick other);

    /**
     * Creates a new instance of this Brick.
     * 
     * @param position
     *            passed to the Constructor
     * @return the new Brick
     */
    @Override
    public Brick getNewInstance(Point position);

    /**
     * Returns a copy of the Brick but with nothing Connected to it.
     * 
     * @return a copy of the Brick but with nothing Connected to it.
     */
    public Pair<Brick, Map<AccessPoint, AccessPoint>> getUnconnectedCopy();
    
    /**
     * A hint for a view that the {@link Brick} should be fixed and not editable
     * 
     * @return true or false
     */
    public boolean isFixedHint();
    
    /**
     * see {@link #isFixedHint()}
     */
    public void setFixedHint(boolean hint);
}