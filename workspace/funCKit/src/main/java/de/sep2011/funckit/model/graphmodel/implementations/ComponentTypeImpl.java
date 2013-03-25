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
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of a {@link ComponentType}.
 */
public class ComponentTypeImpl implements ComponentType {

    /**
     * Delimiter for the {@link AccessPoint}s used when calculating positions.
     */
    private final static int ACCESSPOINT_DELIMITER_RATIO = 5;

    /**
     * The {@link Circuit} this type has.
     */
    private Circuit circuit;

    /**
     * The name of this type.
     */
    private String name;

    /**
     * The {@link Input}s this type has. These are only references to
     * {@link Input}s within the {@link Circuit}.
     */
    private Set<de.sep2011.funckit.model.graphmodel.Input> inputs;

    /**
     * The {@link Output}s this type has. These are only references to
     * {@link Output}s within the {@link Circuit}.
     */
    private Set<de.sep2011.funckit.model.graphmodel.Output> outputs;

    /**
     * Assigns a position to an {@link AccessPoint}.
     */
    private Map<AccessPoint, Point> accessPointPositions;

    /**
     * Assigns a name to an {@link AccessPoint}.
     */
    private Map<AccessPoint, String> accessPointNames;

    /**
     * The default {@link Orientation} of {@link Component}s of this type.
     */
    private Brick.Orientation orientation;

    /**
     * The default width of {@link Component}s of this type.
     */
    private int width;

    /**
     * The default height of {@link Component}s of this type.
     */
    private int height;

    /**
     * Create a new {@link ComponentTypeImpl}.
     * 
     * @param circuit
     *            the {@link Circuit} it should contain. Has to be non null.
     * @param name
     *            name of this {@link ComponentType}
     * @param inputs
     *            inputs inside the circuit which should be inputs of this
     *            {@link ComponentType}
     * @param outputs
     *            outputs inside the circuit which should be inputs of this
     *            {@link ComponentType}
     * @param positions
     *            a map where the outer positions of the inputs and outputs see
     *            {@link ComponentType#getOuterPosition(AccessPoint)}
     */
    public ComponentTypeImpl(Circuit circuit, String name, Set<Input> inputs,
            Set<Output> outputs, Map<AccessPoint, Point> positions) {
        initialize(circuit, name, inputs, outputs, positions);
    }

    /**
     * Creates a new {@link ComponentTypeImpl}.
     * 
     * @param circuit
     *            the {@link Circuit} it should contain
     * @param name
     *            name of this {@link ComponentType}
     * @param inputs
     *            inputs inside the circuit which should be inputs of this
     *            {@link ComponentType}
     * @param outputs
     *            outputs inside the circuit which should be inputs of this
     *            {@link ComponentType}
     */
    public ComponentTypeImpl(Circuit circuit, String name, Set<Input> inputs,
            Set<Output> outputs) {
        initialize(circuit, name, inputs, outputs, null);
    }

    /**
     * Create a new {@link ComponentTypeImpl}.
     * 
     * @param circuit
     *            the {@link Circuit} it should contain, not null
     * @param name
     *            name of this {@link ComponentType}
     */
    public ComponentTypeImpl(Circuit circuit, String name) {
        initialize(circuit, name, null, null, null);
    }

    /**
     * Create a new {@link ComponentTypeImpl}.
     * 
     * @param name
     *            name of this {@link ComponentType}
     */
    public ComponentTypeImpl(String name) {
        initialize(new CircuitImpl(), name, null, null, null);
    }

    /**
     * As there are several constructors, do the initialization here.
     * 
     * @param circuit
     * @param name
     * @param inputs
     * @param outputs
     * @param positions
     */
    private void initialize(Circuit circuit, String name, Set<Input> inputs,
            Set<Output> outputs, Map<AccessPoint, Point> positions) {
        /* Circuit is essential for constructing component type. */
        assert circuit != null;

        /* Initialize some simple properties. */
        this.circuit = circuit;
        this.name = name;
        accessPointNames = new LinkedHashMap<AccessPoint, String>();
        this.orientation = Brick.DEFAULT_ORIENTATION;
        this.width = Brick.DEFAULT_WIDTH;
        this.height = Brick.DEFAULT_HEIGHT;

        /* Take access points and make sure, data structures are not null. */
        if (inputs != null) {
            this.inputs = inputs;
        } else {
            this.inputs = new LinkedHashSet<Input>();
        }
        if (outputs != null) {
            this.outputs = outputs;
        } else {
            this.outputs = new LinkedHashSet<Output>();
        }

        /*
         * Specify access point positions. They may not be null etc. Assume that
         * given positions are ok as long as number of positions equal number of
         * inputs and outputs (every AccessPoint must have a position).
         */
        if (positions != null
                && positions.size() == this.inputs.size() + this.outputs.size()) {
            accessPointPositions = positions;
        } else {
            normalizeSize();
            normalizePositions();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void normalizePositions() {
        assert inputs != null;
        assert outputs != null;

        accessPointPositions = new LinkedHashMap<AccessPoint, Point>();
        calculatePositions(inputs, 0);
        calculatePositions(outputs, getWidth());
    }

    private void normalizeSize() {
        int points = Math.max(inputs.size(), outputs.size());
        height =
                Math.max(Brick.DEFAULT_HEIGHT, 2 * ACCESSPOINT_DELIMITER_RATIO
                        + 2 * (points * 6) - 6);
        width = Math.max(Brick.DEFAULT_WIDTH, height / 2);
    }

    /**
     * Calculates the outer positions of the {@link AccessPoint}s to place them
     * in a sane way.
     */
    private void calculatePositions(Set<? extends AccessPoint> accessPoints,
            int x) {
        int numberOfPoints = accessPoints.size();
        if (numberOfPoints > 0) {
            double spaceRatio = ACCESSPOINT_DELIMITER_RATIO / (double) 100;
            double space = Math.max(0, getHeight() * spaceRatio);
            double step = ((getHeight() - 2 * space) / (numberOfPoints + 1));
            int orderNumber = 1;
            for (AccessPoint accessPoint : accessPoints) {
                double yPosition = space + orderNumber * step;
                accessPointPositions.put(accessPoint,
                        new Point(x, (int) Math.round(yPosition)));
                orderNumber++;
            }
        }
    }

    @Override
    public Circuit getCircuit() {
        return circuit;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc} Warning! This allows direct access to the internal used
     * set.
     */
    @Override
    public Set<Input> getInputs() {
        return inputs;
    }

    /**
     * {@inheritDoc} Warning! This allows direct access to the internal used
     * set.
     */
    @Override
    public Set<Output> getOutputs() {
        return outputs;
    }

    @Override
    public Point getOuterPosition(AccessPoint accessPoint) {
        return accessPointPositions.get(accessPoint);
    }

    @Override
    public void setOuterPosition(AccessPoint accessPoint, Point point) {
        assert accessPoint != null;
        assert point != null;
        accessPointPositions.put(accessPoint, point);
    }

    @Override
    public String getOuterName(AccessPoint accessPoint) {
        return accessPointNames.get(accessPoint);
    }

    @Override
    public void setName(AccessPoint accessPoint, String name) {
        assert accessPoint != null;
        assert name != null;
        accessPointNames.put(accessPoint, name);
    }

    @Override
    public Brick.Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(Brick.Orientation orientation) {
        assert orientation != null;
        this.orientation = orientation;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        assert width > 0;
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        assert height > 0;
        this.height = height;
    }

    @Override
    public boolean attributesEqual(ComponentType other) {
        return this.name.equals(other.getName())
                && this.height == other.getHeight()
                && this.width == other.getWidth()
                && inputsEqual(this, this.getInputs(), other, other.getInputs())
                && outputsEqual(this, this.getOutputs(), other,
                        other.getOutputs());

    }

    /**
     * Checks if the two given {@link Set} of {@link Input}s with the associated
     * {@link ComponentType}s are equal by comparing the {@link Input}
     * attributes (name, position on the {@link ComponentType}.
     * 
     * @param type1
     *            {@link ComponentType} of the first {@link Set}.
     * @param aps1
     *            First {@link Set} of {@link Input}s.
     * @param type2
     *            {@link ComponentType} of the second {@link Set}.
     * @param aps2
     *            Second {@link Set} of {@link Input}s.
     * @return true if both {@link Set} of {@link Input}s are equal by the
     *         definition above, otherwise false
     */
    private boolean inputsEqual(ComponentType type1, Set<Input> aps1,
            ComponentType type2, Set<Input> aps2) {
        for (AccessPoint point : aps1) {
            boolean found = false;
            for (AccessPoint otherPoint : aps2) {
                if (type1.getOuterName(point).equals(
                        type2.getOuterName(otherPoint))
                        && type1.getOuterPosition(point).equals(
                                type2.getOuterPosition(otherPoint))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the two given {@link Set} of {@link Output}s with the
     * associated {@link ComponentType}s are equal by comparing the
     * {@link Output} attributes (name, position on the {@link ComponentType}.
     * 
     * @param type1
     *            {@link ComponentType} of the first {@link Set}.
     * @param aps1
     *            First {@link Set} of {@link Output}s.
     * @param type2
     *            {@link ComponentType} of the second {@link Set}.
     * @param aps2
     *            Second {@link Set} of {@link Output}s.
     * @return true if both {@link Set} of {@link Output}s are equal by the
     *         definition above, otherwise false
     */
    private boolean outputsEqual(ComponentType type1, Set<Output> aps1,
            ComponentType type2, Set<Output> aps2) {
        for (AccessPoint point : aps1) {
            boolean found = false;
            for (AccessPoint otherPoint : aps2) {
                if (type1.getOuterName(point).equals(
                        type2.getOuterName(otherPoint))
                        && type1.getOuterPosition(point).equals(
                                type2.getOuterPosition(otherPoint))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}
