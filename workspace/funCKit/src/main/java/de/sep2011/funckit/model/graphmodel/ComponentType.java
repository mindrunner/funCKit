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

import de.sep2011.funckit.model.graphmodel.Brick.Orientation;

import java.awt.Point;
import java.util.Set;

/**
 * A ComponentType represents a description of user-defined bricks. While
 * Component is referencing its description and represents a concrete brick in
 * the {@link Circuit}, ComponentType encapsulates its actual circuit and
 * information about input and output {@link AccessPoint}s or default size. This
 * connection is comparable to a class- and object-relationship or a model and
 * its concrete instance.
 */
public interface ComponentType {
    /**
     * Returns descriptive circuit of component.
     * 
     * @return Circuit object, never null.
     */
    public Circuit getCircuit();

    /**
     * Returns name of this ComponentType.
     * 
     * @return name, never null.
     */
    public String getName();

    /**
     * Returns the {@link Input}s of this ComponentType. these are that
     * {@link Input}s inside the {@link Circuit} the ComponentType contains
     * which should be mapped to a {@link Component}s {@link Input}s.
     * 
     * @return the {@link Input}s of this ComponentType
     */
    public Set<Input> getInputs();

    /**
     * Returns the {@link Output}s of this ComponentType. these are that
     * {@link Output}s inside the {@link Circuit} the ComponentType contains
     * which should be mapped to a {@link Component}s {@link Output}s.
     * 
     * @return the {@link Output}s of this ComponentType
     */
    public Set<Output> getOutputs();

    /**
     * Can be used by a {@link Component} type to set its initial width.
     * 
     * @return the width of the {@link ComponentType}
     */
    public int getWidth();

    /**
     * Can be used by a {@link Component} type to set its initial height.
     * 
     * @return the height of the {@link ComponentType}
     */
    public int getHeight();

    /**
     * Specifies the width of the ComponentType.
     * 
     * @param width
     *            see {@link #getWidth()}
     */
    public void setWidth(int width);

    /**
     * Specifies the height of the ComponentType.
     * 
     * @param height
     *            set {@link #getHeight()}
     */
    public void setHeight(int height);

    /**
     * For an {@link AccessPoint} inside the {@link Circuit} return its outer
     * position. An outer position is the position the corresponding
     * {@link AccessPoint} of a {@link Component} will have.
     * 
     * @param accessPoint
     *            the {@link AccessPoint} to do the lookup for
     * @return the position or null if this is not an appropriate
     *         {@link AccessPoint}
     */
    public Point getOuterPosition(AccessPoint accessPoint);

    /**
     * Sets the outer position of an {@link AccessPoint} inside the
     * {@link Circuit}. An outer position is the position the corresponding
     * {@link AccessPoint} of a {@link Component} will have.
     * 
     * @param accessPoint
     *            should be one of {@link #getOutputs()} or {@link #getInputs()}
     *            and not null
     * @param position
     *            the position the corresponding {@link AccessPoint} inside the
     *            {@link Component} will have
     */
    public void setOuterPosition(AccessPoint accessPoint, Point position);

    /**
     * Calculates the outer positions of the {@link AccessPoint}s to place them
     * in a sane way.
     */
    public void normalizePositions();

    /**
     * Returns the name the outer {@link AccessPoint} of the {@link Component}
     * will have.
     * 
     * @param accessPoint
     *            should be one of {@link #getOutputs()} or {@link #getInputs()}
     * @return the name, null if not found
     */
    public String getOuterName(AccessPoint accessPoint);

    /**
     * Sets the name the outer {@link AccessPoint} of the {@link Component} will
     * have.
     * 
     * @param accessPoint
     *            should be one of {@link #getOutputs()} or {@link #getInputs()}
     * @param name
     *            the name, null if not found
     */
    public void setName(AccessPoint accessPoint, String name);

    /**
     * Returns the {@link Brick.Orientation} a {@link Component} using this type
     * should have.
     * 
     * @return the Orientation
     */
    public Brick.Orientation getOrientation();

    /**
     * Set the Orientation a {@link Component} using this Type should have.
     * 
     * @param orientation
     *            the {@link Orientation}
     */
    public void setOrientation(Brick.Orientation orientation);

    /**
     * Check if this type and the given type are equal by comparing the
     * attributes (e.g. size, name, ...) and ignoring the circuits. Inputs and
     * outputs are themself compared by their attributes
     * 
     * @param other
     *            type to compare with
     * @return true if both types are equal by the definition above, otherwise
     *         false
     */
    public boolean attributesEqual(ComponentType other);
    
}