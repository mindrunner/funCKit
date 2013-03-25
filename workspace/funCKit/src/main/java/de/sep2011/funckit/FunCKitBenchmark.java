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

package de.sep2011.funckit;

import de.sep2011.funckit.controller.SimulationTool;
import de.sep2011.funckit.controller.listener.view.ModelFitsIntoCircuitListener;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Profiler;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Level;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Bootstrap class to kick off application and thus controller, view and models.
 */
public class FunCKitBenchmark {
    private final static String APPLICATION_TITLE = "FCK Benchmark";
    private final static String HOME_DIRECTORY = System.getProperty("user.home") + File.separator
            + ".funckit";

    private static long simulationTime = 0;
    private static long renderTime = 0;
    private static long lowRenderTime = 0;
    private static long completeTime = 0;

    private static final long simulationSteps = 250;

    private static SessionModel model = null;
    private static Project project = null;
    private static Application application = null;
    private static Circuit circuit = null;

    // private static List<InputStream> benchmarkFileList = new
    // LinkedList<InputStream>();
    private static final List<Circuit> benchmarkCircuitList = new LinkedList<Circuit>();
    private static Circuit currentBenchmark = null;
    private static int currentBenchmarkElementCount = 0;

    private static Iterator<Circuit> benchIterator = null;

    private static EditPanelModel editPanelModel = null;

    /**
     * Program arguments, passed on command line.
     */
    private static String[] arguments;

    /**
     * Bootstrap method for FunCKit. Passed arguments might influence state of
     * application (e.g. load projects from instance).
     * 
     * @param args
     *            Program arguments
     */
    public static void main(String[] args) {
        /* For possible restart. */
        FunCKitBenchmark.arguments = args;
        FunCKitBenchmark.start();
    }

    /**
     * Encapsulates logic to initialize application (here could argument
     * interpreter get injected).
     */
    private static void start() {
        if (Arrays.asList(arguments).contains("nodebug")) {
            Log.gl().setLevel(Level.INFO);
        }

        try {
            final Application application = new StandaloneApplication(APPLICATION_TITLE, HOME_DIRECTORY, "");
            final SessionModel model = application.getSessionModel();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    FunCKitBenchmark.bench(application, model);
                }
            });

        } catch (final Exception e) {
            e.printStackTrace();
            Log.gl().error(e);
        }
    }

    private static void bench(Application application, SessionModel model) {

        FunCKitBenchmark.model = model;
        FunCKitBenchmark.application = application;

        simulationTime = 0;
        renderTime = 0;
        lowRenderTime = 0;
        completeTime = 0;

        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(10, 10));
        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(20, 20));
        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(30, 30));
        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(50, 50));
        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(100, 100));
        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(150, 150));
        benchmarkCircuitList.add(new BenchmarkCircuitFactory().generateCircuit(200, 200));

        benchIterator = benchmarkCircuitList.iterator();
        currentBenchmark = benchIterator.next();

        beginBench();

    }

    private static void beginBench() {
        String projectName = "Benchmark";
        /* BEGIN LOADING */
        completeTime = System.currentTimeMillis();

        circuit = currentBenchmark;
        currentBenchmarkElementCount = circuit.getElements().size();

        Settings notSavingSettings = new Settings("");
        notSavingSettings.setAutosave(false);

        project = new Project(circuit, projectName, notSavingSettings);
        model.addProject(project);
        model.setCurrentProject(project);

        editPanelModel = new EditPanelModel(circuit, new LinkedList<Component>());
        project.addEditPanelModel(editPanelModel);
        project.setSelectedEditPanelModel(editPanelModel);

        /* END LOADING */

        /* BEGIN RENDERING */
        renderTime = System.currentTimeMillis();
        new ModelFitsIntoCircuitListener(application.getView(), application.getController())
                .actionPerformed(null);

        final Timer renderTimer = new Timer(0, new ActionListener() {
            private float zoomLevel = 1;
            private boolean zoomIn = false;
            private boolean bothZoomed = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (zoomIn)
                    zoomLevel += 0.01;
                else
                    zoomLevel -= 0.01;

                double centerX = FunCKitBenchmark.application.getView().getCurrentActiveEditPanel()
                        .getBounds().getCenterX();
                double centerY = FunCKitBenchmark.application.getView().getCurrentActiveEditPanel()
                        .getBounds().getCenterY();

                editPanelModel.setCenter(centerX, centerY);
                editPanelModel.setZoom(zoomLevel);

                if (zoomLevel <= 0.01) {
                    zoomIn = true;
                }

                if (zoomLevel >= 2.40) {
                    zoomIn = false;
                    bothZoomed = true;
                }

                if (zoomLevel == 1 && bothZoomed) {
                    ((Timer) e.getSource()).stop();
                    renderTimerFinished();
                }
            }
        });
        renderTimer.start();

    }

    private static void lowRenderTimerFinished() {
        lowRenderTime = System.currentTimeMillis() - lowRenderTime;
        /* BEGIN SIMULATING */
        simulationTime = System.currentTimeMillis();
        final SimulationImpl simulation = new SimulationImpl(circuit);

        project.setSimulationPaused(true);
        model.setTool(new SimulationTool(application.getController()));
        project.setSimulation(simulation);

        final Timer simulationTimer = new Timer(0, new ActionListener() {
            private int simulationCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                ++simulationCount;
                simulation.nextStep();
                if (simulationCount >= simulationSteps) {
                    ((Timer) e.getSource()).stop();
                    simulationTimerFinished();
                }
            }
        });
        simulationTimer.start();
    }

    private static void renderTimerFinished() {
        new ModelFitsIntoCircuitListener(application.getView(), application.getController())
                .actionPerformed(null);
        renderTime = System.currentTimeMillis() - renderTime;
        /* END RENDERING */
        /* BEGIN LOWRENDERING */
        lowRenderTime = System.currentTimeMillis();
        new ModelFitsIntoCircuitListener(application.getView(), application.getController())
                .actionPerformed(null);

        application.getSessionModel().getSettings().set(Settings.LOW_RENDERING_QUALITY_MODE, true);

        final Timer renderTimer = new Timer(0, new ActionListener() {
            private float zoomLevel = 1;
            private boolean zoomIn = false;
            private boolean bothZoomed = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (zoomIn)
                    zoomLevel += 0.01;
                else
                    zoomLevel -= 0.01;

                double centerX = FunCKitBenchmark.application.getView().getCurrentActiveEditPanel()
                        .getBounds().getCenterX();
                double centerY = FunCKitBenchmark.application.getView().getCurrentActiveEditPanel()
                        .getBounds().getCenterY();

                editPanelModel.setCenter(centerX, centerY);
                editPanelModel.setZoom(zoomLevel);

                if (zoomLevel <= 0.01) {
                    zoomIn = true;
                }

                if (zoomLevel >= 2.40) {
                    zoomIn = false;
                    bothZoomed = true;
                }

                if (zoomLevel == 1 && bothZoomed) {
                    ((Timer) e.getSource()).stop();
                    lowRenderTimerFinished();
                }
            }
        });
        renderTimer.start();
    }

    private static void simulationTimerFinished() {
        project.setSimulation(null);
        simulationTime = System.currentTimeMillis() - simulationTime;
        /* END SIMULATING */

        Profiler.benchmark(Profiler.BENCHMARK_RENDERING, renderTime, currentBenchmarkElementCount);
        Profiler.benchmark(Profiler.BENCHMARK_LOW_RENDERING, lowRenderTime,
                currentBenchmarkElementCount);
        Profiler.benchmark(Profiler.BENCHMARK_SIMULATION, simulationTime,
                currentBenchmarkElementCount);
        Profiler.benchmark(Profiler.BENCHMARK_COMPLETE, System.currentTimeMillis() - completeTime,
                currentBenchmarkElementCount);
        if (benchIterator.hasNext()) {
            currentBenchmark = benchIterator.next();
            beginBench();
        } else {
            model.prepareExit();
        }
    }
}
