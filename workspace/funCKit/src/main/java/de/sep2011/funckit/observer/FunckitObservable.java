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

package de.sep2011.funckit.observer;

import java.util.Observable;

/**
 * A class implementing this represents an observable object, or "data" in the
 * model-view paradigm. It can be implemented to represent an object that the
 * application wants to have observed. <br>
 * An observable object can have one or more observers. An observer may be any
 * object of type OBSERVER_TYPE. After an observable instance changes, an
 * application calling the Observable's notifyObservers method causes all of its
 * observers to be notified of the change by a call to their update method.
 * 
 * @param <OBSERVER_TYPE>
 *            Type of the Observer observing a instance of a class implementing
 *            this
 * @param <INFO_TYPE>
 *            Type of Info Object to pass over to the Observer
 */
public interface FunckitObservable<OBSERVER_TYPE, INFO_TYPE extends Info<INFO_TYPE>> {

    /**
     * Adds an observer to the set of observers for this object, provided that
     * it is not the same as some observer already in the set.
     * 
     * @param obs
     *            the observer to add
     */
    public void addObserver(OBSERVER_TYPE obs);

    /**
     * Deletes an observer from the set of observers of this object.
     * 
     * @param obs
     *            Observer to delete
     */
    public void deleteObserver(OBSERVER_TYPE obs);

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public void deleteObservers();

    /**
     * Returns the number of observers of this Observable object.
     * 
     * @return The Observer count
     */
    public int countObservers();

    /**
     * Marks this Observable object as having been changed; the hasChanged
     * method will now return true.
     */
    public void setChanged();

    /**
     * Indicates that this object has no longer changed, or that it has already
     * notified all of its observers of its most recent change, so that the
     * hasChanged method will now return false.
     */
    public void clearChanged();

    /**
     * Notifies observers if {@link #hasChanged()} is true.
     * 
     * @param i
     *            the info Object to give the Observer some more infos about the
     *            update reason
     */
    public void notifyObservers(INFO_TYPE i);

    /**
     * Like but uses an Internal Info Object.
     */
    public void notifyObservers();

    /**
     * Tests if this object has changed.
     * 
     * @return true or false
     */
    public boolean hasChanged();

    /**
     * Returns the current internal {@link Info} Object of this
     * {@link FunckitObservable}.
     * 
     * @return the current internal {@link Info} Object of this
     *         {@link FunckitObservable}.
     */
    public INFO_TYPE getInfo();

    /**
     * Returns if this {@link FunckitObservable} is set to Auto Notify. If this
     * is true it automatically notfies its Observers, if false this must be
     * done from outside.
     * 
     * @return if this {@link Observable} is set to Auto Notify
     */
    public boolean isAutoNotify();

    /**
     * Set what you get at {@link #isAutoNotify()}.
     * 
     * @param auto
     *            what you get at {@link #isAutoNotify()}.
     */
    public void setAutoNotify(boolean auto);

    /**
     * Sets the Internal Info to a fresh one.
     */
    public void clearInternalInfo();
}
