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
 * A {@link Component} is a Brick which consists of another {@link Circuit}. The
 * behavior, the {@link AccessPoint}s and the initial size of a Component are
 * given by its associated {@link ComponentType}.
 */
public interface Component extends Brick {

    /**
     * Returns the {@link ComponentType} this Component is built of.
     * 
     * @return the associated {@link ComponentType}
     */
    public ComponentType getType();

    /**
     * If the given output (which has to be in the circuit of the component type
     * of this component) is actually an output for the component itself then
     * return the corresponding output of the component. Otherwise null.
     * 
     * @param innerOutput
     *            output of the circuit of the component type of this component
     * @return corresponding output of the component to the given output
     */
    public Output getOuterOutput(Output innerOutput);

    /**
     * If the given output is an output of the component the corresponding
     * output of the circuit of the component type is returned. Otherwise null
     * is returned.
     * 
     * @param outerOutput
     *            output of this component for which you want to get the inner
     *            one
     * @return corresponding output of the circuit of the {@link ComponentType}
     *         of this component
     */
    public Output getInnerOutput(Output outerOutput);

    /**
     * If the given Input (which has to be in the circuit of the
     * {@link ComponentType} of this component) is actually an Input for the
     * component itself then return the corresponding Input of the component.
     * Otherwise null.
     * 
     * @param innerInput
     *            output of the circuit of the component type of this component
     * @return corresponding Input of the component to the given output
     */
    public Input getOuterInput(Input innerInput);

    /**
     * If the given Input is an Input of the component the corresponding output
     * of the circuit of the component type is returned. Otherwise null is
     * returned.
     * 
     * @param outerInput
     *            output of this component for which you want to get the inner
     *            one
     * @return corresponding Input of the circuit of the {@link ComponentType}
     *         of this component
     */
    public Input getInnerInput(Input outerInput);

    /**
     * If the given AccessPoint is an AccessPoint of the component the
     * corresponding {@link AccessPoint} of the circuit of the
     * {@link ComponentType} is returned. Otherwise null is returned.
     * 
     * @param outerPoint
     *            AccessPoint of this component for which you want to get the
     *            inner one
     * @return corresponding AccessPoint of the circuit of the
     *         {@link ComponentType} of this component
     */
    public AccessPoint getInner(AccessPoint outerPoint);

    /**
     * If the given AccessPoint (which has to be in the circuit of the
     * {@link ComponentType} of this component) is actually an
     * {@link AccessPoint} for the Component itself then return the
     * corresponding {@link AccessPoint} of the component. Otherwise null.
     * 
     * @param innerPoint
     *            AccessPoint of the Circuit of the component type of this
     *            Component
     * @return corresponding {@link AccessPoint} of the component to the given
     *         output
     */
    public AccessPoint getOuter(AccessPoint innerPoint);

    /**
     * Creates a new instance of this Component.
     * 
     * @param position
     *            passed to the Constructor
     * @return the new Component
     */
    @Override
    public Component getNewInstance(Point position);
}