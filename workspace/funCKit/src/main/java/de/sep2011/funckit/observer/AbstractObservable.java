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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Abstract Implementation of {@link FunckitObservable}. Classes Subclassing
 * this must implement
 * 
 * @param <OBSERVER_TYPE>
 *            Type of the Observer observing a instance of a class implementing
 *            this
 * @param <INFO_TYPE>
 *            Type of Info Object to pass over to the Observer
 */
public abstract class AbstractObservable<OBSERVER_TYPE, INFO_TYPE extends Info<INFO_TYPE>>
        implements FunckitObservable<OBSERVER_TYPE, INFO_TYPE> {

    private final Set<OBSERVER_TYPE> observers = new CopyOnWriteArraySet<OBSERVER_TYPE>();
    private boolean changed = false;
    private INFO_TYPE info;
    private boolean autoNotify = true;

    /**
     * Initializes the internal {@link Info} object. All subtypes must call this
     * from constructor.
     * 
     * @param i
     *            a fresh {@link Info} object
     */
    protected void initInfo(INFO_TYPE i) {
        info = i;
    }

    @Override
    public INFO_TYPE getInfo() {
        if (info == null) {
            throw new IllegalStateException("No info set. Did you call initInfo()?");
        }
        return info;
    }

    @Override
    public void addObserver(OBSERVER_TYPE obs) {
        assert obs != null;
        observers.add(obs);

    }

    @Override
    public void deleteObserver(OBSERVER_TYPE obs) {
        observers.remove(obs);

    }

    @Override
    public void deleteObservers() {
        observers.clear();

    }

    @Override
    public int countObservers() {
        return observers.size();
    }

    @Override
    public void setChanged() {
        changed = true;

    }

    @Override
    public void clearChanged() {
        changed = false;
    }

    @Override
    public void notifyObservers(INFO_TYPE i) {
        if (hasChanged()) {
            for (OBSERVER_TYPE p : observers) {
                notifyObserver(i, p);
            }
        }
    }

    @Override
    public void clearInternalInfo() {
        info = getInfo().getNewInstance();
    }

    @Override
    public void notifyObservers() {
        INFO_TYPE localInfo = getInfo();
        clearInternalInfo();
        notifyObservers(localInfo);
    }

    /**
     * This method calls {@link #notifyObservers()} if {@link #isAutoNotify()}
     * is true.
     */
    protected void notifyObserversIfAuto() {
        if (isAutoNotify()) {
            notifyObservers();
        }
    }

    /**
     * This method has to be implemented by the concrete Observable to call the
     * Observers Corresponding update method. <br>
     * Example: obs.graphModelChanged(this, i)
     * 
     * @param i
     *            the Info Object
     * @param obs
     *            The observer
     */
    abstract protected void notifyObserver(INFO_TYPE i, OBSERVER_TYPE obs);

    @Override
    public boolean hasChanged() {
        return changed;
    }

    @Override
    public boolean isAutoNotify() {
        return autoNotify;
    }

    @Override
    public void setAutoNotify(boolean auto) {
        autoNotify = auto;
    }

}
