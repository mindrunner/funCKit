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
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.util.Pair;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link Gate} which represents a logical identity.
 */
public class IdPoint extends GateImpl implements Gate {

    /**
     * The {@link Input} of this point.
     */
    private Input inputA;

    /**
     * The {@link Output} of this point.
     */
    private Output outputO;

    /**
     * Create a new Id @see GateImpl#GateImpl(Rectangle).
     * 
     * @param boundingRect see {@link GateImpl#GateImpl(Rectangle)}
     */
    public IdPoint(Rectangle boundingRect) {
        super(boundingRect);
        init();
    }

    /**
     * Create a new {@link IdPoint} @see GateImpl#GateImpl(Rectangle).
     * 
     * @param point
     *            see {@link GateImpl#GateImpl(Rectangle)}
     */
    public IdPoint(Point point) {
        super(new Rectangle(point, new Dimension(4, 4)));
        // super(new Rectangle(point, new Dimension(40, 40))); //Wire Tool Debug
        init();
    }

    /**
     * Create a new Id @see GateImpl#GateImpl(Rectangle, String).
     * 
     * @param boundingRect see {@link GateImpl#GateImpl(Rectangle, String)}
     * @param name see {@link GateImpl#GateImpl(Rectangle, String)}
     */
    public IdPoint(Rectangle boundingRect, String name) {
        super(boundingRect, name);
        init();
    }

    /**
     * Helper for constructor.
     */
    private void init() {

        inputA = new InputImpl(this, new Point(
                (int) (getDimension().width * 0.5),
                (int) (getDimension().height * 0.5)), DEFAULT_INPUT_1_NAME);
        outputO = new OutputImpl(this, new Point(
                (int) (getDimension().width * 0.5),
                (int) (getDimension().height * 0.5)), DEFAULT_OUTPUT_NAME);

        // Wire Tool Debug

        /*
         * inputA = new InputImpl(this, new Point(0,0), DEFAULT_INPUT_1_NAME);
         * outputO = new OutputImpl(this, new Point(40, 40),
         * DEFAULT_OUTPUT_NAME);
         */
        inputs.add(inputA);
        outputs.add(outputO);
    }

    @Override
    public void dispatch(ElementDispatcher dispatcher) {
        dispatcher.visit(this);
    }

    @Override
    public Map<Output, Boolean> calculate(Map<Input, Boolean> inputValues) {
        boolean result = true;
        Map<Output, Boolean> results = new LinkedHashMap<Output, Boolean>();
        for (Input input : inputs) {
        	assert inputValues.get(input) != null;
            result = result && inputValues.get(input);
        }

        for (Output output : outputs) {
            results.put(output, result);
        }
        return results;
    }

    /**
     * Get the input `a` of the IdPoint Gate. This is for convenience when
     * building {@link Circuit}s.
     * 
     * @return {@link Input} a
     */
    public Input getInputA() {
        return inputA;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public IdPoint getNewInstance(Point position) {
        return new IdPoint(position);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Element: IdPoint, ");
        stringBuilder.append(super.toString());
        return stringBuilder.toString();
    }

    @Override
    public Pair<Brick, Map<AccessPoint, AccessPoint>> getUnconnectedCopy() {
        IdPoint copy = new IdPoint((Rectangle) boundingRect.clone(), name);
        Map<AccessPoint, AccessPoint> oldNewMap = new LinkedHashMap<AccessPoint, AccessPoint>();

        Input inputACopy = new InputImpl(copy, new Point(inputA.getPosition()),
                inputA.getName());
        Output outputOCopy = new OutputImpl(copy, new Point(
                outputO.getPosition()), outputO.getName());

        copy.inputA = inputACopy;
        copy.outputO = outputOCopy;
        copy.orientation = orientation;
        copy.delay = delay;

        copy.inputs.clear();
        copy.outputs.clear();
        copy.inputs.add(copy.inputA);
        oldNewMap.put(inputA, inputACopy);
        copy.outputs.add(copy.outputO);
        oldNewMap.put(outputO, outputOCopy);

        return new Pair<Brick, Map<AccessPoint, AccessPoint>>(copy, oldNewMap);
    }
}