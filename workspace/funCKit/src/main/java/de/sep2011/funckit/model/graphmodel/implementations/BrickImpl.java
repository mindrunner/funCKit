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
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of a {@link Brick}.
 */
public abstract class BrickImpl extends ElementImpl implements Brick {

    /**
     * The {@link Input}s this {@link Brick} has.
     */
    Set<Input> inputs;

    /**
     * The {@link Output}s this {@link Brick} has.
     */
    Set<Output> outputs;

    /**
     * The {@link Rectangle} that defines the position and size of this
     * {@link Brick}.
     */
    Rectangle boundingRect;

    /**
     * The {@link Orientation} this {@link Brick} has.
     */
    Orientation orientation;

    /**
     * The delay of this {@link Brick}.
     */
    int delay;
    
    private boolean fixedHint = false;

    /**
     * Create a new Brick.
     * 
     * @param position
     *            The Position of the Brick
     */
    BrickImpl(Point position) {
        super();
        init(0, DEFAULT_ORIENTATION, new Rectangle(position.x, position.y,
                DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /**
     * Create a new Brick.
     * 
     * @param boundingRect
     *            see {@link Element#getBoundingRect()}
     */
    BrickImpl(Rectangle boundingRect) {
        super();
        init(0, DEFAULT_ORIENTATION, boundingRect);
    }

    /**
     * Create a new Brick.
     * 
     * @param boundingRect
     *            see {@link Element#getBoundingRect()}
     * @param name
     *            see {@link Element#getName()}
     */
    BrickImpl(Rectangle boundingRect, String name) {
        super(name);
        init(0, DEFAULT_ORIENTATION, boundingRect);
    }

    /**
     * as there are several constructors, do the initialization here.
     */
    private void init(int delay, Orientation orientation, Rectangle boundingRect) {
        this.delay = delay;
        this.orientation = orientation;
        this.boundingRect = boundingRect;
        inputs = new LinkedHashSet<Input>();
        outputs = new LinkedHashSet<Output>();
    }

    @Override
    public AccessPoint getAccessPointAtPosition(Point position, int tolerance) {
        Set<AccessPoint> accessPoints = new LinkedHashSet<AccessPoint>(inputs);
        accessPoints.addAll(outputs);

        AccessPoint result = null;
        double smallestDistance = Double.MAX_VALUE;
        for (AccessPoint accessPoint : accessPoints) {
            double distance = accessPoint.getPosition().distance(position);

            if (distance < tolerance && distance < smallestDistance) {
                smallestDistance = distance;
                result = accessPoint;
            }
        }

        return result;
    }

    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void setPosition(Point position) {
        boundingRect.setLocation(position);
    }

    @Override
    public void setDimension(Dimension dimension) {
        int oldWidth = boundingRect.width;
        int oldHeight = boundingRect.height;
        calculateRelativeAccessPointPositions(oldWidth, oldHeight,
                dimension.width, dimension.height);
        boundingRect.setSize(dimension);
    }

    @Override
    public boolean hasDelay() {
        return delay > 0;
    }

    @Override
    public Set<Input> getInputs() {
        return inputs;
    }

    @Override
    public Set<Output> getOutputs() {
        return outputs;
    }

    @Override
    public void setOrientation(Orientation o) {
        orientation = o;
    }

    @Override
    public Rectangle getBoundingRect() {
        return boundingRect;
    }

    @Override
    public void setBoundingRect(Rectangle rectangle) {
        int oldWidth = boundingRect.width;
        int oldHeight = boundingRect.height;
        calculateRelativeAccessPointPositions(oldWidth, oldHeight,
                rectangle.width, rectangle.height);
        this.boundingRect = rectangle;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public Input getInput(String name) {
        for (Input i : inputs) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public Output getOutput(String name) {
        for (Output o : outputs) {
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public boolean attributesEqual(Brick other) {
        if (!(this.getClass().equals(other.getClass()))) {
            return false;
        }
        if (!(this.boundingRect.equals(other.getBoundingRect())
                && this.delay == other.getDelay()
                && this.orientation == other.getOrientation() && this.name
                    .equals(other.getName()))) {
            return false;
        }
        return this.getInputs().size() == other.getInputs().size() && this.getOutputs().size() == other.getOutputs().size() && inputsEqual(this.getInputs(), other.getInputs()) && outputsEqual(this.getOutputs(), other.getOutputs());
    }

    /**
     * Checks if the two given {@link Set}s of {@link Input}s are equal by
     * comparing the name and position of the {@link Input}s.
     * 
     * @param aps1
     *            first {@link Set} of {@link Input}s
     * @param aps2
     *            second {@link Set} of {@link Input}s
     * @return true if both {@link Set}s of {@link Input}s are equal by the
     *         definition above, otherwise false
     */
    private boolean inputsEqual(Set<Input> aps1, Set<Input> aps2) {
        for (AccessPoint point : aps1) {
            boolean found = false;
            for (AccessPoint otherPoint : aps2) {
                if (point.getName().equals(otherPoint.getName())
                        && point.getPosition().equals(otherPoint.getPosition())) {
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
     * Checks if the two given {@link Output}s of {@link Output}s are equal by
     * comparing the name and position of the {@link Output}s.
     * 
     * @param aps1
     *            first {@link Set} of {@link Output}s
     * @param aps2
     *            second {@link Set} of {@link Output}s
     * @return true if both {@link Set}s of {@link Output}s are equal by the
     *         definition above, otherwise false
     */
    private boolean outputsEqual(Set<Output> aps1, Set<Output> aps2) {
        for (AccessPoint point : aps1) {
            boolean found = false;
            for (AccessPoint otherPoint : aps2) {
                if (point.getName().equals(otherPoint.getName())
                        && point.getPosition().equals(otherPoint.getPosition())) {
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(super.toString());
        stringBuilder.append(" boundingRect: ");
        stringBuilder.append(boundingRect);
        stringBuilder.append(", delay: ");
        stringBuilder.append(delay);
        stringBuilder.append(", orientation: ");
        stringBuilder.append(orientation);
        stringBuilder.append(", inputs: (");
        for (Input input : inputs) {
            stringBuilder.append(input.getName());
            stringBuilder.append(":");
            stringBuilder.append(input.getPosition());
            stringBuilder.append(" ");
        }
        stringBuilder.append("), outputs: (");
        for (Output output : outputs) {
            stringBuilder.append(output.getName());
            stringBuilder.append(":");
            stringBuilder.append(output.getPosition());
            stringBuilder.append(" ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        return getBoundingRect().intersects(rectangle);
    }

    private void calculateRelativeAccessPointPositions(int oldWidth,
            int oldHeight, int newWidth, int newHeight) {
        double widthRelation = newWidth / (double) oldWidth;
        double heightRelation = newHeight / (double) oldHeight;
        for (Output output : outputs) {
            int x = (int) Math.round(output.getPosition().x * widthRelation);
            int y = (int) Math.round(output.getPosition().y * heightRelation);
            output.setPosition(new Point(x, y));
        }
        for (Input input : inputs) {
            int x = (int) Math.round(input.getPosition().x * widthRelation);
            int y = (int) Math.round(input.getPosition().y * heightRelation);
            input.setPosition(new Point(x, y));
        }
    }
    
    @Override
    public boolean isFixedHint() {
        return fixedHint;
    }

    @Override
    public void setFixedHint(boolean hint) {
        fixedHint = hint;        
    }
}
