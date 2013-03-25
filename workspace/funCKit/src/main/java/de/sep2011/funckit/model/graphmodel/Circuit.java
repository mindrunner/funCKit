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
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.sep2011.funckit.observer.FunckitObservable;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.util.Pair;

/**
 * The main interface of graph model. A {@link Circuit} consists of
 * {@link Brick}s with two (or rather one) sets of {@link AccessPoint}s namely
 * {@link Input}s and {@link Output}s; {@link AccessPoint}s can be connected
 * With {@link Wire}s to build a logical circuit.
 */
// make that sure in Application
public interface Circuit extends
        FunckitObservable<GraphModelObserver, GraphModelInfo> {

    /**
     * Adds a {@link Brick} to the {@link Circuit}. Brick must not be null. If
     * brick is already contained in circuit, this method does nothing. The
     * given {@link Brick} should not be part of another {@link Circuit} as this
     * may lead to weird behavior (E.g. when two {@link AccessPoint}s are
     * connected in one {@link Circuit} the {@link Wire} is missing in the
     * others).
     * 
     * @param b
     *            the Brick to add
     */
    public void addBrick(Brick b);

    /**
     * Adds a {@link Wire} to {@link Circuit}. Given wire must not be null. If
     * related {@link AccessPoint}s are not associated to {@link Brick}s, this
     * method throws an exception. If related {@link Brick}s are not contained
     * in circuit, it also throws an exception as this operation has to be as
     * atomic as possible (no conflict management), but may not lead to
     * inconsistent states.
     * 
     * @param w
     *            the Wire to add
     */
    public void addWire(Wire w);

    /**
     * Adds all elements of given {@link Circuit} to this circuit object without
     * copying them, but just adding their reference. This assumes in general,
     * that the given circuit is already a copy and thus <b>empties</b> the
     * given object to make sure, that there are no two circuits with same
     * elements. To add a circuit by copying the given object and keep the
     * original untouched, check for {@link #addCircuit(Circuit)}.
     * 
     * @param copy
     *            A copied circuit object, that gets emptied after injecting it
     *            to current circuit object.
     */
    public void injectCircuit(Circuit copy);

    /**
     * Adds all elements of given {@link Circuit} by copying them (including all
     * {@link Wire} references). Thus there are no connections between this and
     * the given circuit object. If you are already working with a copied
     * circuit object, you can also use {@link #injectCircuit(Circuit)}, so you
     * avoid multiple copying a circuit.
     * 
     * @param other
     *            the circuit to add
     */
    public void addCircuit(Circuit other);

    /**
     * Adds given set of elements to circuit.
     * 
     * @param elements
     *            the elements to add
     */
    public void addAll(Collection<Element> elements);

    /**
     * Connects two {@link AccessPoint}s with a {@link Wire}. Given objects must
     * not be null. AccessPoints have to refer to existing bricks within this
     * circuit, as operation has to be as atomic as possible, but may not lead
     * to inconsistent states.
     * 
     * @param p1
     *            first {@link AccessPoint}
     * @param p2
     *            first {@link AccessPoint}
     * @return the newly created wire is returned
     */
    public Wire connect(AccessPoint p1, AccessPoint p2);

    /**
     * Removes given {@link Brick} from circuit. If it is null or not contained
     * in circuit, this method does nothing. Method does not check for connected
     * wires on its access point and thus might result in an inconsistent state
     * of graph ({@link Wire}s with {@link AccessPoint}s of non-existing Brick).
     * This is to treat very carefully, as Brick is still referenced in
     * AccessPoints, but not in circuit itself.
     * 
     * @param b
     *            the Brick to remove
     */
    public void removeBrick(Brick b);

    /**
     * Removes {@link Wire} between two given {@link AccessPoint}s, that must
     * not be null. If there is no such wire to disconnect, this method does
     * nothing.
     * 
     * @param accessPoint1
     *            the first {@link AccessPoint}
     * @param accessPoint2
     *            the second {@link AccessPoint}
     */
    public void disconnect(AccessPoint accessPoint1, AccessPoint accessPoint2);

    /**
     * Removes given {@link Wire} from {@link Circuit}. If wire is null or it is
     * not contained in circuit, this method does nothing. Method does not check
     * for its reference in access points but only removes wire from circuit
     * elements set and thus might result in an inconsistent state of
     * represented graph. This is to treat very carefully and in awareness to
     * support a full atomic operation. For easy use <code>disconnect()</code>
     * is recommended.
     * 
     * @param w
     *            wire to remove
     */
    public void removeWire(Wire w);

    /**
     * Returns {@link Brick} at given position.
     * 
     * @param position
     *            Position in circuit to search for a brick.
     * @return the {@link Brick}; if more {@link Brick}s are there the first
     *         found is returned, if no {@link Brick} is found null is returned.
     */
    public Brick getBrickAtPosition(Point position);

    /**
     * Checks if there is an {@link AccessPoint} on given position.
     * 
     * @param pos
     *            the position to check
     * @param scatterFactor
     *            Tolerance value to receive nearest AccessPoint to position
     *            with this radius.
     * @return {@link AccessPoint} that was found or null if there is none.
     */
    public AccessPoint getAccessPointAtPositon(Point pos, int scatterFactor);

    /**
     * Checks if there is a Wire on given position.
     * 
     * @param pos
     *            the position to check
     * @param scatterFactor
     *            Tolerance value to receive nearest Wire to position with this
     *            radius.
     * @return Wire that was found or null if there is none.
     */
    public Wire getWireAtPosition(Point pos, int scatterFactor);

    /**
     * Returns a {@link Set} of {@link Element}s which intersect the given
     * rectangle. Intersection is a real intersection. Not fully in rectangle
     * contained elements are also considered and returned!
     * 
     * @param rectangle the rectangle to check
     * @return a {@link Set} of {@link Element}s which intersect the given
     *         rectangle
     */
    public Set<Element> getIntersectingElements(Rectangle rectangle);

    /**
     * Returns all {@link Element}s this {@link Circuit} contains. This allows
     * direct access to references set.
     * 
     * @return All {@link Element}s this {@link Circuit} contains.
     */
    public Set<Element> getElements();

    /**
     * Compares this circuit with another circuit object checking if all
     * attributes are equal. This is not a graph equality check! It only works
     * correct if the attributes are distinctive.
     * 
     * @param c
     *            Other circuit object for comparison.
     * @return true if graph of circuits with all elements are equal, false
     *         otherwise.
     */
    public boolean equalGraph(Circuit c);

    /**
     * Returns the bounding Rectangle of all Elements inside this
     * {@link Circuit}.
     * 
     * @return the bounding Rectangle of all Elements inside this
     *         {@link Circuit}
     * @since implementation
     */
    public Rectangle getBoundingRectangle();

    /**
     * Returns a deep Copy of The Circuit.
     * 
     * @return a deep Copy of The Circuit.
     */
    public Circuit getCopy();

    /**
     * Like {@link #getCopy()} but clones only the specified Elements.
     * 
     * @param elements
     *            elements to put into the new Circuit. Wires connected to
     *            outside this set will be omitted.
     * @return a Circuit containing a copy of the given elementss.
     */
    public Circuit getPartCopy(Collection<Element> elements);

    /**
     * Like {@link #getCopy()} but clones only the specified Elements.
     * 
     * @param elements
     *            elements to put into the new Circuit. Wires connected to
     *            outside this set will be omitted.
     * @return Pair:<br>
     *         right: Circuit containing a copy of the given elements. <br>
     *         left: Set of Wires which were discarded on copying because they
     *         point outside of the Circuit. Note: These Wires are also copies
     *         but have a Brick of this circuit set at one side and a Brick of
     *         the copy Circuit at the other side (but not connected), be
     *         careful!
     */
    public Pair<Set<Wire>, Circuit> getPartCopyAndDiscardedWires(
            Collection<Element> elements);

    /**
     * Like {@link #getCopy()} but also returns a map of original {@link Brick}s
     * to the corresponding newly created {@link Brick}s.
     * 
     * @return Pair:<br>
     *         right: see {@link #getCopy()} <br>
     *         left: a map of the original {@link Brick}s to the corresponding
     *         newly created {@link Brick}s
     */
    public Pair<Map<Brick, Brick>, Circuit> getBrickMapAndCopy();
}