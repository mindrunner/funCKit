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

import de.sep2011.funckit.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Dispatches {@link Command}s and adds them to a History to be able to undo,
 * redo them.
 */
public class CommandDispatcher {

    private List<Command> commands;

    private int limit;

    private int lastExecutedCommand;

    /**
     * Create a new {@link CommandDispatcher} with a maximum size history.
     */
    public CommandDispatcher() {
        initialize(Integer.MAX_VALUE);
    }

    /**
     * Create a new {@link CommandDispatcher}.
     * 
     * @param limit
     *            length of the History
     */
    public CommandDispatcher(int limit) {
        initialize(limit);
    }

    /**
     * Initializes object with all needed parameters.
     * 
     * @param limit
     */
    private void initialize(int limit) {
        checkArgument(limit >= 0, "Limit must be >= 0");
        this.limit = limit;
        commands = new ArrayList<Command>();
        lastExecutedCommand = -1;
    }

    /**
     * Returns the current set limit for this command dispatcher.
     * 
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Specifies a limit of possible commands for this dispatcher.
     * 
     * @param limit
     *            use Integer.MAX_VALUE for unlimited history, value must be
     *            non-negative
     */
    public void setLimit(int limit) {
        checkArgument(limit >= 0, "Limit must be >= 0");
        this.limit = limit;
        removeOldCommands();
    }

    /**
     * Remove old commands as long as history size exceeds limit.
     */
    private void removeOldCommands() {
        while (commands.size() >= limit) {
            commands.remove(0);
            lastExecutedCommand--;
        }
    }

    /**
     * Dispatches a new command.
     * 
     * @param command
     *            Command object to execute, should not be null
     */
    public void dispatch(Command command) {
        removeOldCommands();

        /* Dispatch new command and save it in history. */
        commands.add(lastExecutedCommand + 1, command);
        lastExecutedCommand++;

        /*
         * Remove all commands after the newly added command as they are invalid
         * then
         */
        int beginObsolete = commands.indexOf(command) + 1;
        int endObsolete = commands.size() - 1;
        // Log.gl().debug("Removing commands " + beginObsolete + " to " +
        // endObsolete);
        for (int i = endObsolete; i >= beginObsolete; --i) {
            commands.remove(i);
        }

        command.execute();
        if (!command.isExecuted()) {
            Log.gl().debug("Removing not executed command");
            commands.remove(command);
            lastExecutedCommand--;
        }
    }

    /**
     * Test if the Dispatcher is able to step Back one {@link Command} in the
     * history.
     * 
     * @return true if the Dispatcher is able to step Back one {@link Command}
     *         in the history
     */
    public boolean canStepBack() {
        return (lastExecutedCommand > -1);
    }

    /**
     * Test if the Dispatcher is able to step forward one {@link Command} in the
     * history.
     * 
     * @return true if the Dispatcher is able to step forward one
     *         {@link Command} in the history
     */
    public boolean canStepForward() {
        return (lastExecutedCommand < (commands.size() - 1));
    }

    /**
     * Undoes a previously executed command.
     */
    public void stepBack() {
        /* Only step back in history if last executed command exists. */
        if (lastExecutedCommand > -1) {
            Command command = commands.get(lastExecutedCommand);
            lastExecutedCommand--;
            Log.gl().debug("stepping backwards");
            command.undo();
        }
    }

    /**
     * Executes a command, that was previously performed and then undone.
     */
    public void stepForward() {
        /*
         * Only step forward in history if an index in command list greater than
         * last executed command exists.
         */
        if (lastExecutedCommand < (commands.size() - 1)) {
            Command command = commands.get(lastExecutedCommand + 1);
            lastExecutedCommand++;
            Log.gl().debug("stepping forward");
            command.execute();
        }
    }

    /**
     * Undoes all commands dispatched by this command dispatcher.
     */
    public void rewind() {
        /*
         * Step back as long as lastExecutedCommand exists (index greater or
         * equal zero).
         */
        while (lastExecutedCommand > -1) {
            stepBack();
        }
    }

    /**
     * Redoes all commands in the Queue.
     */
    public void replay() {
        while (canStepForward()) {
            stepForward();
        }
    }

    /**
     * Clears the complete history of this {@link CommandDispatcher}.
     */
    public void clear() {
        commands.clear();
        lastExecutedCommand = -1;
    }
}