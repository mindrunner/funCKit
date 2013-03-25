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

package de.sep2011.funckit.benchmark.simulation;

import de.sep2011.funckit.circuitfactory.ShiftRegisterFactory;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.util.Log;

public class SimulationBenchmark {

    private static final int AMOUNT_OF_STEPS_TO_EXECUTE = 1000;
    private Circuit circuit;
    private Simulation simulation;
    private int steps = 0;

    private SimulationBenchmark(Circuit circuit) {
        assert circuit != null;
        this.circuit = circuit;
        loadSimulation();
    }

    long measureTimeForSteps(int maximumSteps) {
        long startTime = System.currentTimeMillis();
        // Log.gl().debug("Starting simulation at " + startTime + " with " +
        // maximumSteps + "steps...");
        for (int i = 0; i < maximumSteps; i++) {
            simulate();
        }
        long endTime = System.currentTimeMillis();
        // Log.gl().debug("Simulation ended at " + endTime);
        return endTime - startTime;
    }

    public int measureStepsForTime(long timeInMillis) {
        steps = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        while (endTime - startTime < timeInMillis) {
            simulate();
            endTime = System.currentTimeMillis();
        }
        return steps - 1;
    }

    private void loadSimulation() {
        // Log.gl().debug("Loading Simulation...");
        simulation = new SimulationImpl(circuit);
        // Log.gl().debug("Simulation loaded.");
    }

    private void simulate() {
        if (steps % 100 == 0) {
            // Log.gl().debug("Simulation step: " + (steps+1));
        }
        simulation.nextStep();
        steps++;
    }

    public static void main(String[] args) {
        Log.gl().debug("Starting Simulation-Benchmark...");

        /*
         * SimulationBenchmark benchmark = new
         * SimulationBenchmark(loadCircuit()); Log.gl().debug("Time for " +
         * AMOUNT_OF_STEPS_TO_EXECUTE + " steps of '" + CIRCUIT_RESOURCE +
         * "' simulation in Milliseconds: " + benchmark
         * .measureTimeForSteps(AMOUNT_OF_STEPS_TO_EXECUTE));
         */

        for (int i = 50; i < 10000; i *= 1.5) {
            Circuit c = new ShiftRegisterFactory(i).getCircuit();
            SimulationBenchmark benchmark2 = new SimulationBenchmark(c);
            long time = benchmark2
                    .measureTimeForSteps(AMOUNT_OF_STEPS_TO_EXECUTE);
            Log.gl()
                    .debug("Shiftregister("
                            + i
                            + ") (size:"
                            + c.getElements().size()
                            + ") simulation speed: "
                            + (AMOUNT_OF_STEPS_TO_EXECUTE * 1000 / time)
                            + "steps per second. RegisterSteps/second: "
                            + (((long) AMOUNT_OF_STEPS_TO_EXECUTE) * 1000 * i / time)
                            + ". ElementSteps/second: "
                            + (((long) AMOUNT_OF_STEPS_TO_EXECUTE) * 1000
                                    * c.getElements().size() / time));
            System.gc();
        }

        Log.gl().debug("Simulation-Benchmark done.");
    }
}
