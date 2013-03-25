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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.util.Pair;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of a {@link Component}.
 */
public class ComponentImpl extends BrickImpl implements Component {

    /**
     * The {@link ComponentType} this {@link Component} has.
     */
    private ComponentType type;

    /**
     * Maps from outer {@link AccessPoint} (of the {@link Component}) to inner
     * {@link AccessPoint (of the {@link ComponentType}).
     */
    private HashBiMap<AccessPoint, AccessPoint> accessPointMap;

    /**
     * Create a new ComponentImpl.
     * 
     * @param type
     *            see {@link Component#getType()}. Has to be non null.
     * @param position
     *            position of the new component
     */
    public ComponentImpl(ComponentType type, Point position) {
        super(new Rectangle(position.x, position.y, type.getWidth(),
                type.getHeight()));
        init(type);
    }

    /**
     * Create a new ComponentImpl.
     * 
     * @param type
     *            see {@link Component#getType()}. Has to be non null.
     */
    public ComponentImpl(ComponentType type) {
        super(new Rectangle(type.getWidth(), type.getHeight()));
        init(type);
    }

    /**
     * Create a new ComponentImpl.
     * 
     * @param type
     *            see {@link Component#getType()} Has to be non null.
     * @param position
     *            position of the new component
     * @param name
     *            see {@link Element#getName()}
     */
    public ComponentImpl(ComponentType type, Point position, String name) {
        super(new Rectangle(position.x, position.y, type.getWidth(),
                type.getHeight()), name);
        init(type);
    }

    /**
     * Do initialization here as there are many constructors.
     */
    private void init(ComponentType type) {
        this.type = type;
        this.orientation = type.getOrientation();
        accessPointMap = HashBiMap.create();
        for (Input inner : type.getInputs()) {
            assert type.getOuterPosition(inner) != null;
            assert type.getOuterName(inner) != null;
            Input outer = new InputImpl(this, type.getOuterPosition(inner),
                    type.getOuterName(inner));
            inputs.add(outer);
            accessPointMap.put(outer, inner);
        }
        for (Output inner : type.getOutputs()) {
            assert type.getOuterPosition(inner) != null;
            assert type.getOuterName(inner) != null;
            Output outer = new OutputImpl(this, type.getOuterPosition(inner),
                    type.getOuterName(inner));
            outputs.add(outer);
            accessPointMap.put(outer, inner);
        }
    }

    @Override
    public void dispatch(ElementDispatcher dispatcher) {
        dispatcher.visit(this);
    }

    @Override
    public ComponentType getType() {
        return type;
    }

    @Override
    public Output getInnerOutput(Output outerInput) {
        return (Output) accessPointMap.get(outerInput);
    }

    @Override
    public Output getOuterOutput(Output innerInput) {
        return (Output) accessPointMap.inverse().get(innerInput);
    }

    @Override
    public Input getInnerInput(Input outerInput) {
        return (Input) accessPointMap.get(outerInput);
    }

    @Override
    public Input getOuterInput(Input innerInput) {
        return (Input) accessPointMap.inverse().get(innerInput);
    }

    @Override
    public AccessPoint getInner(AccessPoint outerPoint) {
        return accessPointMap.get(outerPoint);
    }

    @Override
    public AccessPoint getOuter(AccessPoint innerPoint) {
        return accessPointMap.inverse().get(innerPoint);
    }

    @Override
    public ComponentImpl getNewInstance(Point position) {
        return new ComponentImpl(type, position);
    }

    @Override
    public boolean attributesEqual(Brick other) {
        if (!super.attributesEqual(other)) {
            return false;
        }
        Component comp = (Component) other;
        return this.getType().attributesEqual(comp.getType())
                && this.getType().getCircuit()
                        .equalGraph(comp.getType().getCircuit());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Element: Component, ");
        stringBuilder.append(super.toString());
        stringBuilder.append(", type: ");
        stringBuilder.append(type.getName());
        return stringBuilder.toString();
    }

    @Override
    public Pair<Brick, Map<AccessPoint, AccessPoint>> getUnconnectedCopy() {
        ComponentImpl copy = new ComponentImpl(getType());
        Map<AccessPoint, AccessPoint> oldNewMap = new LinkedHashMap<AccessPoint, AccessPoint>();

        // copy.accessPointMap = null; //
        copy.boundingRect = new Rectangle(boundingRect);
        copy.delay = delay;
        // copy.inputs = null; //
        copy.name = name;
        copy.orientation = orientation;
        // copy.outputs = null; //

        BiMap<AccessPoint, AccessPoint> oldApMapInv = accessPointMap.inverse();
        BiMap<AccessPoint, AccessPoint> newApMapInv = copy.accessPointMap
                .inverse();

        for (AccessPoint ap : oldApMapInv.keySet()) {
            oldNewMap.put(oldApMapInv.get(ap), newApMapInv.get(ap));
        }

        /* Adapt properties of copied inputs and outputs. */
        for (Input input : copy.getInputs()) {
            AccessPoint other = oldApMapInv.get(copy.accessPointMap.get(input));
            input.setName(other.getName());
            input.setPosition(other.getPosition());
        }
        for (Output output : copy.getOutputs()) {
            AccessPoint other = oldApMapInv
                    .get(copy.accessPointMap.get(output));
            output.setName(other.getName());
            output.setPosition(other.getPosition());
        }

        return new Pair<Brick, Map<AccessPoint, AccessPoint>>(copy, oldNewMap);
    }
}
