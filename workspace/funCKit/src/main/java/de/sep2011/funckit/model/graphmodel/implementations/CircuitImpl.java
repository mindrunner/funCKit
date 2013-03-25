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
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.observer.AbstractObservable;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.util.Pair;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link Circuit}.
 */
public class CircuitImpl extends
        AbstractObservable<GraphModelObserver, GraphModelInfo> implements
        Circuit {

    /**
     * Elements the {@link Circuit} is built of.
     */
    private Set<Element> elements;

    /**
     * Create a new {@link CircuitImpl}.
     */
    public CircuitImpl() {
        init();
    }

    /**
     * Helper for Constructor.
     */
    void init() {
        elements = new LinkedHashSet<Element>();
        initInfo(GraphModelInfo.getInfo());
        setAutoNotify(false);
    }

    @Override
    public void addBrick(Brick b) {
        assert b != null;

        elements.add(b);
        setChanged();
        getInfo().getAddedBricks().add(b);
        notifyObserversIfAuto();
    }

    @Override
    public void addWire(Wire w) {
        assert w != null;

        elements.add(w);
        setChanged();
        getInfo().getAddedWires().add(w);
        notifyObserversIfAuto();
    }

    @Override
    public void injectCircuit(Circuit copy) {
        elements.addAll(copy.getElements());
        copy.getElements().clear();
    }

    @Override
    public void addCircuit(Circuit other) {
        Circuit copy = other.getCopy();
        elements.addAll(copy.getElements());
    }

    @Override
    public void addAll(Collection<Element> elements) {
        this.elements.addAll(elements);
    }

    @Override
    public void removeWire(Wire w) {
        elements.remove(w);
        setChanged();
        getInfo().getRemovedWires().add(w);
        notifyObserversIfAuto();
    }

    @Override
    public Wire connect(AccessPoint p1, AccessPoint p2) {
        assert p1 != null;
        assert p2 != null;

        Wire w = new WireImpl(p1, p2);
        p1.addWire(w);
        p2.addWire(w);
        elements.add(w);

        /* Notify observers. */
        setChanged();
        getInfo().getAddedWires().add(w);
        notifyObserversIfAuto();
        return w;
    }

    @Override
    public void disconnect(AccessPoint accessPoint1, AccessPoint accessPoint2) {
        assert accessPoint1 != null;
        assert accessPoint2 != null;

        for (Iterator<Wire> i = accessPoint1.getWires().iterator(); i.hasNext();) {
            Wire w = i.next();
            if ((w.getFirstAccessPoint() == accessPoint1 && w
                    .getSecondAccessPoint() == accessPoint2)
                    || (w.getFirstAccessPoint() == accessPoint2 && w
                            .getSecondAccessPoint() == accessPoint1)) {
                i.remove();
                accessPoint2.removeWire(w);
                elements.remove(w);
                setChanged();
                getInfo().getRemovedWires().add(w);
            }
        }

        /* Notify observers. */
        notifyObserversIfAuto();
    }

    @Override
    public void removeBrick(Brick b) {
        elements.remove(b);

        /* Notify observers. */
        setChanged();
        getInfo().getRemovedBricks().add(b);
        notifyObserversIfAuto();
    }

    @Override
    public Set<Element> getIntersectingElements(Rectangle rectangle) {
        Set<Element> result = new LinkedHashSet<Element>();

        for (Element element : elements) {
            if (element.intersects(rectangle)) {
                result.add(element);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc} WARNING: this is the internal {@link Set}! Do not change it
     * directly.
     */
    @Override
    public Set<Element> getElements() {
        return this.elements;
    }

    @Override
    public Brick getBrickAtPosition(Point position) {
        for (Element elem : elements) {
            if (elem instanceof Brick) {
                Brick b = (Brick) elem;
                if (b.getBoundingRect().contains(position)) {
                    return b;
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc} Therefore every brick is associated with a corresponding
     * brick which has the same attributes (position, name, ...) and wires are
     * associated with corresponding wires with the same attributes (name) and
     * which connect bricks with equal attributes.
     */
    @Override
    public boolean equalGraph(Circuit c) {

        // first check bricks for equal attributes
        for (Element e : this.elements) {
            if (e instanceof Brick) {
                Brick b = (Brick) e;
                boolean found = false;
                for (Element otherE : c.getElements()) {
                    if (otherE instanceof Brick) {
                        Brick otherB = (Brick) otherE;
                        if (b.attributesEqual(otherB)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    return false;
                }
            } else { // Wire
                Wire w = (Wire) e;
                boolean found = false;
                for (Element otherE : c.getElements()) {
                    if (otherE instanceof Wire) {
                        Wire otherW = (Wire) otherE;
                        if ((w.getFirstAccessPoint()
                                .getBrick()
                                .attributesEqual(
                                        otherW.getFirstAccessPoint().getBrick()) && w
                                .getSecondAccessPoint()
                                .getBrick()
                                .attributesEqual(
                                        otherW.getSecondAccessPoint()
                                                .getBrick()))
                                || (w.getFirstAccessPoint()
                                        .getBrick()
                                        .attributesEqual(
                                                otherW.getSecondAccessPoint()
                                                        .getBrick()) && w
                                        .getSecondAccessPoint()
                                        .getBrick()
                                        .attributesEqual(
                                                otherW.getFirstAccessPoint()
                                                        .getBrick()))) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Elements of circuit:\n");
        builder.append("----------\n");
        for (Element e : getElements()) {
            builder.append("- ").append(e).append("\n");
        }
        builder.append("----------");
        return builder.toString();
    }

    @Override
    public void notifyObserver(GraphModelInfo i, GraphModelObserver obs) {
        obs.graphModelChanged(this, i);
    }

    @Override
    public Rectangle getBoundingRectangle() {
        Rectangle bounding = null;
        for (Element e : elements) {
            if (e instanceof Brick) {
                bounding =
                        bounding == null ? e.getBoundingRect() : bounding
                                .union(e.getBoundingRect());
            }
        }

        return bounding == null ? new Rectangle() : bounding;
    }

    @Override
    public AccessPoint getAccessPointAtPositon(Point pos, int tolerance) {
        double smallestDistance = Double.MAX_VALUE;
        AccessPoint nearestAp = null;

        for (Element e : elements) {
            Point localP =
                    new Point(pos.x - e.getBoundingRect().x, pos.y
                            - e.getBoundingRect().y);
            AccessPoint localAp = e.getAccessPointAtPosition(localP, tolerance);

            if (localAp != null
                    && localAp.getPosition().distance(localP) < smallestDistance) {
                smallestDistance = localAp.getPosition().distance(localP);
                nearestAp = localAp;

            }
        }

        return nearestAp;
    }

    @Override
    public Wire getWireAtPosition(Point pos, int scatterFactor) {
        double smallestDistance = Double.MAX_VALUE;
        Wire nearestWire = null;

        for (Element e : elements) {
            if (e instanceof Wire) {
                double distance = getWireDistance(pos, (Wire) e);

                if (distance < scatterFactor && distance < smallestDistance) {
                    smallestDistance = distance;
                    nearestWire = (Wire) e;
                }
            }
        }

        return nearestWire;
    }

    private static double getWireDistance(Point p, Wire w) {
        AccessPoint firstAccessPoint = w.getFirstAccessPoint();
        AccessPoint secondAccessPoint = w.getSecondAccessPoint();

        assert firstAccessPoint != null;
        assert secondAccessPoint != null;
        assert firstAccessPoint.getBrick() != null;
        assert secondAccessPoint.getBrick() != null;

        int fstApX =
                firstAccessPoint.getBrick().getBoundingRect().x
                        + firstAccessPoint.getPosition().x;
        int fstApY =
                firstAccessPoint.getBrick().getBoundingRect().y
                        + firstAccessPoint.getPosition().y;
        int sndApX =
                secondAccessPoint.getBrick().getBoundingRect().x
                        + secondAccessPoint.getPosition().x;
        int sndApY =
                secondAccessPoint.getBrick().getBoundingRect().y
                        + secondAccessPoint.getPosition().y;

        Line2D line = new Line2D.Float(fstApX, fstApY, sndApX, sndApY);

        return line.ptSegDist(p);
    }

    @Override
    public Circuit getCopy() {
        return getPartCopy(this.elements);
    }

    @Override
    public Circuit getPartCopy(Collection<Element> elems) {
        return getPartCopyAndDiscardedWires(elems).getRight();
    }
    
    /**
     * Like {@link #getCopy()} but clones only the specified Elements.
     * 
     * @param elems
     *            elements to put into the new Circuit. Wires connected to
     *            outside this set will be omitted.
     * @return first: a map from old Bricks from the original to new bricks from
     *         the copy second: Set of Wires which were discarded on copying
     *         because they point outside of the Circuit. Note: These Wires are
     *         also copies but have a Brick of this circuit set at one side and
     *         a Brick of the copy Circuit at the other side (but not
     *         connected), be careful! third: Circuit containing a copy of the
     *         given elements. <br>
     */
    static Pair<Map<Brick, Brick>, Pair<Set<Wire>, Circuit>> getBrickMapAndPartCopyAndDiscardedWires(
            Collection<Element> elems) {
        /* Old Brick to new Brick Map */
        Map<Brick, Brick> oldNewMap = new HashMap<Brick, Brick>();

        /* Map from new Bricks to old to new Ap Map */
        Map<Brick, Map<AccessPoint, AccessPoint>> newToApMap =
                new LinkedHashMap<Brick, Map<AccessPoint, AccessPoint>>();

        /* Set With discarded Wires */
        Set<Wire> discardedWires = new LinkedHashSet<Wire>();

        CircuitImpl copy = new CircuitImpl();

        /* first, fill newToApMap an copy.elements with Bricks */
        for (Element e : elems) {
            if (e instanceof Brick) {
                Brick eb = (Brick) e;
                Pair<Brick, Map<AccessPoint, AccessPoint>> copyPair =
                        eb.getUnconnectedCopy();
                copy.elements.add(copyPair.getLeft());
                oldNewMap.put(eb, copyPair.getLeft());
                newToApMap.put(copyPair.getLeft(), copyPair.getRight());
            }
        }

        /* Copy Wires */
        for (Element e : elems) {
            if (e instanceof Wire) {
                Wire ew = (Wire) e;

                AccessPoint fstOldAp = ew.getFirstAccessPoint();
                AccessPoint sndOldAp = ew.getSecondAccessPoint();
                Brick fstOldBrick = fstOldAp.getBrick();
                Brick sndOldBrick = sndOldAp.getBrick();

                Brick fstNewBrick = oldNewMap.get(fstOldBrick);
                Brick sndNewBrick = oldNewMap.get(sndOldBrick);

                // filter out unconnected Wires in copy
                if (fstNewBrick != null && sndNewBrick != null) {
                    AccessPoint newFstAp =
                            newToApMap.get(fstNewBrick).get(fstOldAp);
                    AccessPoint newSndAp =
                            newToApMap.get(sndNewBrick).get(sndOldAp);

                    WireImpl copyWire = new WireImpl(newFstAp, newSndAp);
                    newFstAp.addWire(copyWire);
                    newSndAp.addWire(copyWire);
                    copy.elements.add(copyWire);
                } else if (fstNewBrick == null && sndNewBrick != null) {
                    // Fill discarded Wire Set
                    AccessPoint newSndAp =
                            newToApMap.get(sndNewBrick).get(sndOldAp);
                    WireImpl copyWire = new WireImpl(fstOldAp, newSndAp);
                    discardedWires.add(copyWire);
                } else if (fstNewBrick != null && sndNewBrick == null) {
                    // Fill discarded Wire Set
                    AccessPoint newFstAp =
                            newToApMap.get(fstNewBrick).get(fstOldAp);

                    WireImpl copyWire = new WireImpl(newFstAp, sndOldAp);
                    discardedWires.add(copyWire);
                }
            }
        }

        return new Pair<Map<Brick, Brick>, Pair<Set<Wire>, Circuit>>(oldNewMap,
                new Pair<Set<Wire>, Circuit>(discardedWires, copy));
    }

    @Override
    public Pair<Set<Wire>, Circuit> getPartCopyAndDiscardedWires(
            Collection<Element> elems) {
        return getBrickMapAndPartCopyAndDiscardedWires(elems).getRight();
    }

    @Override
    public Pair<Map<Brick, Brick>, Circuit> getBrickMapAndCopy() {
        Pair<Map<Brick, Brick>, Pair<Set<Wire>, Circuit>> pair =
                getBrickMapAndPartCopyAndDiscardedWires(elements);
        return new Pair<Map<Brick, Brick>, Circuit>(pair.getLeft(), pair
                .getRight().getRight());
    }

}
