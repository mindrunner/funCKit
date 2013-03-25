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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.util.Pair;

/**
 * A Light is a Brick with one Input. In a (graphical) representation of a
 * circuit it is used show the value it has on its {@link Input}.
 */
public class Light extends BrickImpl implements Brick {

    private Input inputA;

    /**
     * Create a new Id @see GateImpl#GateImpl(Rectangle).
     * @param boundingRect see {@link GateImpl#GateImpl(Rectangle)}
     */
    public Light(Rectangle boundingRect) {
        super(boundingRect);
        init();
    }

    /**
     * Create a new Id @see GateImpl#GateImpl(Point).
     * @param point  see {@link GateImpl#GateImpl(Point)}
     */
    public Light(Point point) {
        super(point);
        init();
    }

    /**
     * Create a new Id @see {@link GateImpl#GateImpl(Rectangle, String)}.
     * @param boundingRect see {@link GateImpl#GateImpl(Rectangle, String)}
     * @param name see {@link GateImpl#GateImpl(Rectangle, String)}
     */
    public Light(Rectangle boundingRect, String name) {
        super(boundingRect, name);
        init();
    }

    /**
     * Helper for constructor.
     */
    private void init() {
        inputA = new InputImpl(this, new Point((int) (DEFAULT_HEIGHT * 0.5),
                (int) (DEFAULT_WIDTH * 0.5)), DEFAULT_INPUT_1_NAME);
        inputs.add(inputA);
    }

    /**
     * Get the input `a` of the And Gate. This is for convenience when building
     * {@link Circuit}s.
     * 
     * @return {@link Input} input a
     */
    public Input getInputA() {
        return inputA;
    }

    @Override
    public void dispatch(ElementDispatcher dispatcher) {
        dispatcher.visit(this);
    }

    @Override
    public Light getNewInstance(Point position) {
        return new Light(position);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Element: Light, ");
        stringBuilder.append(super.toString());
        return stringBuilder.toString();
    }

    @Override
    public Pair<Brick, Map<AccessPoint, AccessPoint>> getUnconnectedCopy() {
        Light copy = new Light((Rectangle) boundingRect.clone(), name);
        Map<AccessPoint, AccessPoint> oldNewMap = new LinkedHashMap<AccessPoint, AccessPoint>();

        Input inputACopy = new InputImpl(copy, new Point(inputA.getPosition()),
                inputA.getName());

        copy.inputA = inputACopy;
        copy.orientation = orientation;
        copy.delay = delay;

        copy.inputs.clear();
        copy.outputs.clear();
        copy.inputs.add(copy.inputA);
        oldNewMap.put(inputA, inputACopy);

        return new Pair<Brick, Map<AccessPoint, AccessPoint>>(copy, oldNewMap);
    }
}