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

package de.sep2011.funckit.util.command;

/**
 * This Interface declares a Command inside the command pattern. Commands can
 * also be undone
 */
public abstract class Command {

    private boolean executed = false;
    private boolean notifyObserverHint = true;

    /**
     * Executes the concrete implemented command. If it was previously undone,
     * it recovers the state (redo). Method should try to prevent changing any
     * state on performing command several times without undoing (calling
     * multiple times <code>execute()</code>). In certain cases it is possible
     * to influence this logic from outside (for example performing operations
     * on circuit) and thus making it impossible for implemented command to keep
     * a consistent state. Each command should describe its concrete behavior
     * for this issue.
     */
    abstract public void execute();

    /**
     * Undoes the command.
     */
    abstract public void undo();

    /**
     * All overriding Subclasses must call this first inside their execute
     * Method or implement their own {@link #isExecuted()}.
     */
    protected void checkAndUpdateExecutedOnExecute() {
        if (executed) {
            throw new IllegalStateException(
                    "Command has been executed multiple times");
        }

        executed = true;
    }

    /**
     * All overriding Subclasses must call this first inside their undo Method
     * or implement their own {@link #isExecuted()}.
     */
    protected void checkAndUpdateExecutedOnUndo() {
        if (!executed) {
            throw new IllegalStateException("Cannot undo a Unexecuted Command");
        }

        executed = false;
    }

    /**
     * Returns true if the Command has been executed (this means
     * {@link #execute()} was called). If {@link #undo()} was called afterwards
     * this returns false again.
     * 
     * @return true if the Command has been executed
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * If this is true it gives a hint to the Command that it should call
     * notifyObservers() it changes some type of observable.
     * 
     * @return the value, default is true
     */
    protected boolean isNotifyObserversHint() {
        return notifyObserverHint;
    }

    /**
     * @param notifyObserverHint
     *            the notifyObserverHint
     * @return this for convenience
     * @see #isNotifyObserversHint()
     */
    public Command setNotifyObserversHint(boolean notifyObserverHint) {
        this.notifyObserverHint = notifyObserverHint;
        return this;
    }

}