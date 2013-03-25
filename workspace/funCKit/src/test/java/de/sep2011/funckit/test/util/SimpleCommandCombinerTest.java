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

import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.SimpleCommandCombiner;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link SimpleCommandCombiner}.
 */
public class SimpleCommandCombinerTest {

    /**
     * Adds dummy {@link Command}s to the {@link SimpleCommandCombiner} and
     * check if all were executed und undone.
     */
    @Test
    public void test() {
        Command[] cmds = new Command[7];

        for (int i = 0; i < cmds.length; i++) {

            // final int y = i;

            cmds[i] = new Command() {

                Boolean executed = false;

                @Override
                public void undo() {
                    executed = false;
                }

                @Override
                public void execute() {
                    executed = true;
                }

                @Override
                public boolean isExecuted() {
                    return executed;
                }

            };
        }

        Command combiner = new SimpleCommandCombiner(Arrays.asList(cmds));
        combiner.execute();
        for (Command cmd : cmds) {
            assertTrue(cmd.isExecuted());
        }

        combiner.undo();
        for (Command cmd : cmds) {
            assertFalse(cmd.isExecuted());
        }
    }

}
