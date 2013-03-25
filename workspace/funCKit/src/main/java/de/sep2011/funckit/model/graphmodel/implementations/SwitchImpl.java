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
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.util.Pair;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of a {@link Switch}.
 */
public class SwitchImpl extends BrickImpl implements Switch {

    /**
     * The value this switch has.
     */
    private boolean value;

    /**
     * The {@link Output} of this switch.
     */
    private Output outputO;

    /**
     * Create a new {@link Switch} @see GateImpl#GateImpl(Rectangle).
     * 
     * @param boundingRect
     */
    public SwitchImpl(Rectangle boundingRect) {
        super(boundingRect);
        init();
    }

    /**
     * Create a new {@link Switch} @see GateImpl#GateImpl(Point).
     * 
     * @param point
     */
    public SwitchImpl(Point point) {
        super(point);
        init();
    }

    /**
     * Create a new {@link Switch} @see GateImpl#GateImpl(Rectangle,String).
     * 
     * @param boundingRect the bounding rectangle of the new Switch
     * @param name the name for the new Switch
     */
    public SwitchImpl(Rectangle boundingRect, String name) {
        super(boundingRect, name);
        init();
    }

    /**
     * Helper method for constructors to initialize.
     */
    private void init() {
        outputO =
                new OutputImpl(this, new Point(DEFAULT_WIDTH,
                        (int) (DEFAULT_HEIGHT * 0.5)), DEFAULT_OUTPUT_NAME);
        outputs.add(outputO);
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void toggle() {
        setValue(!getValue());
    }

    /**
     * Get the output `o` of the IdPoint Gate. This is for convenience when
     * building {@link Circuit}s.
     * 
     * @return {@link Output} o
     */
    public Output getOutputO() {
        return outputO;
    }

    @Override
    public void dispatch(ElementDispatcher dispatcher) {
        dispatcher.visit(this);
    }

    @Override
    public boolean attributesEqual(Brick other) {
        return super.attributesEqual(other)
                && this.value == ((SwitchImpl) other).value;
    }

    @Override
    public Switch getNewInstance(Point position) {
        return new SwitchImpl(position);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Element: Switch, ");
        stringBuilder.append(super.toString());
        stringBuilder.append(", value: ");
        stringBuilder.append(value);
        return stringBuilder.toString();
    }

    @Override
    public Pair<Brick, Map<AccessPoint, AccessPoint>> getUnconnectedCopy() {
        SwitchImpl copy =
                new SwitchImpl((Rectangle) boundingRect.clone(), name);
        Map<AccessPoint, AccessPoint> oldNewMap =
                new LinkedHashMap<AccessPoint, AccessPoint>();

        Output outputOCopy =
                new OutputImpl(copy, new Point(outputO.getPosition()),
                        outputO.getName());

        copy.outputO = outputOCopy;
        copy.orientation = orientation;
        copy.delay = delay;

        copy.inputs.clear();
        copy.outputs.clear();
        copy.outputs.add(copy.outputO);
        oldNewMap.put(outputO, outputOCopy);
        copy.value = value;

        return new Pair<Brick, Map<AccessPoint, AccessPoint>>(copy, oldNewMap);
    }
}