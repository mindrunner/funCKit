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

package de.sep2011.funckit.util;

import org.apache.log4j.Logger;

public class Profiler {

    /*
     * Render operations
     */
    public static final String DRAW_GHOSTS = "drawGhosts";
    public static final String DRAW_GRID = "drawGrid";
    public static final String DRAW_MODEL = "drawModel";
    public static final String DRAW_SELECTION = "drawSelection";
    public static final String DRAW_TOOLTIP = "drawTooltip";
    public static final String INITIALIZE_LAYERS = "initializeLayers";
    public static final String PAINT_COMPONENT = "paintComponent";
    public static final String RESIZE_LAYERS = "resizeLayers";

    /*
     * Simulation operations
     */
    public static final String SIMULATION_STEP = "simulationStep";
    public static final String RESTORE_SIMULATION = "restoreSimulation";
    public static final String CREATE_SIMULATION = "creatSimulation";
    public static final String COPY_SIMULATION = "copySimulation";
    public static final String SIMULATION_TEST = "simulationTest";

    /*
     * Benchmark operations
     */
    public static final String BENCHMARK_LOADING = "loadingFile";
    public static final String BENCHMARK_SIMULATION = "simulating";
    public static final String BENCHMARK_LOW_RENDERING = "lowRendering";
    public static final String BENCHMARK_RENDERING = "rendering";
    public static final String BENCHMARK_COMPLETE = "complete";

    /*
     * The logger names.
     */
    private static final String RENDER_LOGGER_NAME = "funCKit.profiler.render";
    private static final String SIMULATION_LOGGER_NAME = "funCKit.profiler.simulation";
    private static final String BENCHMARK_LOGGER_NAME = "funCKit.profiler.benchmark";

    /*
     * Global logger objects
     */
    private static Logger renderLogger;
    private static Logger simulationLogger;
    private static Logger benchmarkLogger;

    private static final long startTime = System.currentTimeMillis();

    /**
     * Profiling enabled?
     */
    public static boolean ON = true;

    static {
        if (ON) {
            renderLogger = Logger.getLogger(RENDER_LOGGER_NAME);
            renderLogger
                    .info("%Starting new Session\nSystemTime 	Operation 	Duration");

            simulationLogger = Logger.getLogger(SIMULATION_LOGGER_NAME);
            simulationLogger
                    .info("%Starting new Session\nSystemTime 	Operation 	Duration");

            benchmarkLogger = Logger.getLogger(BENCHMARK_LOGGER_NAME);
            benchmarkLogger
                    .info("%Starting new Session\nOperation 	Duration  ElementCount");
        }
    }

    public static void rendering(String operation, long duration) {
        if (!ON) {
            return;
        }
        renderLogger.info(System.currentTimeMillis() - startTime + " 	"
                + operation + " 	" + duration);
    }

    public static void simulation(String operation, long duration) {
        if (!ON) {
            return;
        }
        simulationLogger.info(System.currentTimeMillis() - startTime + " 	"
                + operation + " 	" + duration);
    }

    public static void benchmark(String operation, long duration,
            int elementCount) {
        if (!ON) {
            return;
        }
        benchmarkLogger.info(operation + " 	" + duration + "  " + elementCount);
    }

}
