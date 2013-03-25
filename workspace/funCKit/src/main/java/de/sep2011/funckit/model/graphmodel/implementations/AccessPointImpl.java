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
import de.sep2011.funckit.model.graphmodel.Wire;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An Implementation of {@link AccessPoint}.
 */
public class AccessPointImpl implements AccessPoint {

    /**
     * The {@link Wire}s this point is connected with.
     */
    private Set<Wire> wires;

    /**
     * The {@link Brick} this point is on.
     */
    private Brick brick;

    /**
     * The relative position of this point on the {@link Brick} it is on.
     */
    private Point position;

    /**
     * The name of this point.
     */
    private String name;

    /**
     * Create a new {@link AccessPointImpl}.
     * 
     * @param brick
     *            see {@link AccessPoint#getBrick()}
     * @param position
     *            {@link AccessPoint#getPosition()}
     * @param name
     *            see {@link AccessPoint#getName()}
     */
    public AccessPointImpl(Brick brick, Point position, String name) {
        init(brick, position, name);
    }

    /**
     * Helper for constructor.
     * 
     */
    private void init(Brick brick, Point position, String name) {
        wires = new LinkedHashSet<Wire>();
        assert brick != null;
        assert position != null;
        assert name != null;
        this.brick = brick;
        this.position = position;
        this.name = name;
    }

    @Override
    public Brick getBrick() {
        return this.brick;
    }

    /**
     * {@inheritDoc} Warning! Don't manipulate the set because its the reference
     * to the internal set.
     */
    @Override
    public Set<Wire> getWires() {
        return this.wires;
    }

    @Override
    public void addWire(Wire w) {
        assert w != null;
        this.wires.add(w);
    }

    @Override
    public void removeWire(Wire w) {
        this.wires.remove(w);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        assert name != null;
        this.name = name;

    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point p) {
        assert p != null;
        this.position = p;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName());
        stringBuilder.append("[");
        stringBuilder.append(getPosition().getX());
        stringBuilder.append(",");
        stringBuilder.append(getPosition().getY());
        stringBuilder.append("] on Brick ");
        stringBuilder.append(getBrick());
        return stringBuilder.toString();
    }
}
