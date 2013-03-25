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

package de.sep2011.funckit.test.util;

import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.CommandDispatcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandDispatcherTest {

    /**
     * Tests a unilimited {@link CommandDispatcher}, Performs various operations
     * (dispatch(),stepBack(), stepForward(), ...) on the
     * {@link CommandDispatcher} and checks for correct operation.
     */
    @Test
    public void testCommandDispatcherSimpleNoLimit() {
        CommandDispatcher cd = new CommandDispatcher();

        Command[] cmds = new Command[7];

        final Boolean[] cmdsRun = new Boolean[7]; // true if execute, false if
        // undo,null if untouched

        for (int i = 0; i < cmds.length; i++) {

            final int y = i;

            cmds[i] = new Command() {

                Boolean executed = false;

                @Override
                public void undo() {
                    cmdsRun[y] = false;
                    executed = false;
                }

                @Override
                public void execute() {
                    cmdsRun[y] = true;
                    executed = true;
                }

                @Override
                public boolean isExecuted() {
                    return executed;
                }

            };

            cd.dispatch(cmds[i]);

        }

        for (Boolean b : cmdsRun) {
            assertTrue("all commands should have been executed here", b);
        }

        // go forward further, nothing should happen
        cd.stepForward();
        for (Boolean b : cmdsRun) {
            assertTrue("all commands should have been executed here", b);
        }

        assertFalse(cd.canStepForward());
        assertTrue(cd.canStepBack());

        // go some steps back
        cd.stepBack();
        cd.stepBack();
        cd.stepBack();
        assertFalse(cmdsRun[6]);
        assertFalse(cmdsRun[5]);
        assertFalse(cmdsRun[4]);
        assertTrue(cmdsRun[3]);
        assertTrue(cmdsRun[2]);
        assertTrue(cmdsRun[1]);
        assertTrue(cmdsRun[0]);
        assertTrue(cd.canStepBack());
        assertTrue(cd.canStepForward());

        // go some more steps back
        cd.stepBack();
        cd.stepBack();
        cd.stepBack();
        cd.stepBack();
        for (Boolean b : cmdsRun) {
            assertFalse("all commands should have been executed here", b);
        }
        assertTrue(cd.canStepForward());
        assertFalse(cd.canStepBack());

        // go back further, nothing should happen
        for (Boolean b : cmdsRun) {
            assertFalse("all commands should have been executed here", b);
        }

        // redo 5 commands
        cd.stepForward();
        cd.stepForward();
        cd.stepForward();
        cd.stepForward();
        cd.stepForward();
        assertFalse(cmdsRun[6]);
        assertFalse(cmdsRun[5]);
        assertTrue(cmdsRun[4]);
        assertTrue(cmdsRun[3]);
        assertTrue(cmdsRun[2]);
        assertTrue(cmdsRun[1]);
        assertTrue(cmdsRun[0]);
        assertTrue(cd.canStepBack());
        assertTrue(cd.canStepForward());

        // dispatch some new command should not be abe to step forwards
        cd.dispatch(new Command() {

            @Override
            public void undo() {
                // do nothing

            }

            @Override
            public void execute() {
                // do nothing

            }

            @Override
            public boolean isExecuted() {
                return false;
            }
        });

        assertFalse(cd.canStepForward());
        assertTrue(cd.canStepBack());
    }

    public void notestCommandDispatcherIntOverflow() { // this text will
        // actually never end
        // and may heat up your
        // machine

        class noCommand extends Command {
            @Override
            public void execute() {
                // nothing to do here
            }

            @Override
            public void undo() {
                // nothing to do here
            }

            @Override
            public boolean isExecuted() {
                return false;
            }
        }

        CommandDispatcher commandDispatcher = new CommandDispatcher();
        for (int i = 0; i < Integer.MAX_VALUE; ++i) {
            noCommand c = new noCommand();
            commandDispatcher.dispatch(c);
            Log.gl().info("Added" + i + "commands");
        }

        noCommand c = new noCommand();
        commandDispatcher.dispatch(c);

    }

    /**
     * Tests if the {@link CommandDispatcher} works correctly if a limit is set.
     */
    @Test
    public void testLimit() {
        CommandDispatcher cd = new CommandDispatcher(8);
        assertEquals(8, cd.getLimit());
        cd.setLimit(4);
        assertEquals(4, cd.getLimit());

        Command[] cmds = new Command[8];

        final Boolean[] cmdsRun = new Boolean[8]; // true if execute, false if
        // undo,null if untouched

        for (int i = 0; i < cmds.length; i++) {

            final int y = i;

            cmds[i] = new Command() {

                Boolean executed = false;

                @Override
                public void undo() {
                    cmdsRun[y] = false;
                    executed = false;
                }

                @Override
                public void execute() {
                    cmdsRun[y] = true;
                    executed = true;
                }

                @Override
                public boolean isExecuted() {
                    return executed;
                }

            };

            cd.dispatch(cmds[i]);

        }

        for (Boolean b : cmdsRun) {
            assertTrue("all commands should have been executed here", b);
        }

        cd.rewind();
        assertFalse(cmdsRun[7]);
        assertFalse(cmdsRun[6]);
        assertFalse(cmdsRun[5]);
        assertFalse(cmdsRun[4]);
        assertTrue(cmdsRun[3]);
        assertTrue(cmdsRun[2]);
        assertTrue(cmdsRun[1]);
        assertTrue(cmdsRun[0]);

        cd.clear();
    }

    /**
     * Tests if the {@link CommandDispatcher} throws an
     * {@link IllegalArgumentException} on negative limit values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        new CommandDispatcher(-88);
    }

    /**
     * Tests if the {@link CommandDispatcher} throws an
     * {@link IllegalArgumentException} on negative limit values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testException2() {
        CommandDispatcher cd = new CommandDispatcher(8);
        cd.setLimit(-77);
    }
}
