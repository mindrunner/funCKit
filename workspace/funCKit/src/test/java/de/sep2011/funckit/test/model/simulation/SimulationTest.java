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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.test.factory.circuit.ComplexComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.ComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import org.junit.Test;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertEquals;

/**
 * This class contains various Simulation tests.
 */
public class SimulationTest {

    /**
     * Test simulation (if it runs without errors) on Circuit from
     * {@link SimpleCircuit1Factory}.
     */
    @Test
    public void testSimpleCircuit() {
        simulate(new SimpleCircuit1Factory(true).getCircuit());
    }

    /**
     * Test simulation (if it runs without errors) on Circuit from
     * {@link ComponentCircuit1Factory}.
     */
    @Test
    public void testComponentCircuit() {
        simulate((new ComponentCircuit1Factory(true)).getCircuit());
    }

    /**
     * Test simulation (if it runs without errors) on Circuit from
     * {@link SimpleCircuit1Factory}.
     */
    @Test
    public void testSimpleFeedBackCircuit() {
        simulate(new SimpleCircuit1Factory(true).getCircuit());
    }

    /**
     * Tests the correct creation of simulation Snapshots used to undo
     * Simulation steps.
     */
    @Test
    public void testSimulationSnapshot() {
        Simulation s = new SimulationImpl(
                new ComplexComponentCircuit1Factory().getCircuit());
        Simulation snapshot1 = s.createSnapshot();
        assertEquals(s, snapshot1);
        s.nextStep();
        Simulation snapshot2 = s.createSnapshot();
        assertEquals(s, snapshot2);
        s.nextStep();
        Simulation snapshot3 = s.createSnapshot();
        assertEquals(s, snapshot3);
        s.restoreFromSnapshot(snapshot1);
        assertEquals(snapshot1, s);
        s.restoreFromSnapshot(snapshot3);
        assertEquals(snapshot3, s);
        s.restoreFromSnapshot(snapshot2);
        assertEquals(snapshot2, s);
        snapshot1.restoreFromSnapshot(s);
        assertEquals(s, snapshot1);
        assertEquals(snapshot1, snapshot2);
    }

    private static void simulate(Circuit c) {
        Simulation s = new SimulationImpl(c);
        s.nextStep();
        gl().debug(s);

        s.nextStep();
        gl().debug(s);
    }

}
