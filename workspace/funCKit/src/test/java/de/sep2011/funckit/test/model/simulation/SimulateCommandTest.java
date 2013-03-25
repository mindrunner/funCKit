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

package de.sep2011.funckit.test.model.simulation;

import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.model.simulationmodel.commands.SimulateCommand;
import de.sep2011.funckit.test.factory.circuit.ComplexComponentCircuit1Factory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the {@link SimulateCommand}.
 */
public class SimulateCommandTest {

    /**
     * Tests if the {@link SimulateCommand} does {@link Simulation#nextStep()}
     * correctly and creates and restores the snapshot correctly.
     */
    @Test
    public void test() {
        Simulation sim = new SimulationImpl(
                new ComplexComponentCircuit1Factory(true).getCircuit());
        Simulation simOriginal = sim.createSnapshot();
        assertEquals(simOriginal, sim);
        SimulateCommand command = new SimulateCommand(sim);
        command.execute();
        assertFalse(sim.equals(simOriginal));
        command.undo();
        assertEquals(simOriginal, sim);
        command.execute();
        assertFalse(sim.equals(simOriginal));
    }

}
