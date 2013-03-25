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

package de.sep2011.funckit.test.model.graphmodel.implementation.commands;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.commands.AddBrickCommand;
import de.sep2011.funckit.util.command.CommandDispatcher;
import org.junit.Test;

import java.awt.Rectangle;

import static junit.framework.Assert.assertEquals;

/**
 * This class contains a simple test for the AddBrickCommand and the
 * CommandDispatcher.
 */
public class CommandEditingTest {

    /**
     * Performs the test.
     */
    @Test
    public void testSimpleCommandDispatchingOnCircuit() {

        Circuit cir = new CircuitImpl();

        CommandDispatcher cd = new CommandDispatcher();
        cd.dispatch(new AddBrickCommand(cir, new And(new Rectangle())));
        cd.dispatch(new AddBrickCommand(cir, new Or(new Rectangle())));
        cd.dispatch(new AddBrickCommand(cir, new Not(new Rectangle())));

        assertEquals(3, cir.getElements().size());

        cd.stepBack();
        cd.stepBack();

        assertEquals(1, cir.getElements().size());

        cd.stepForward();
        cd.stepForward();

        assertEquals(3, cir.getElements().size());

    }
}
