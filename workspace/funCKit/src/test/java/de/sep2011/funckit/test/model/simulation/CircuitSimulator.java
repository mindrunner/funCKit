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
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.simulationmodel.LightSimulationState;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;
import de.sep2011.funckit.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a helper class to test the simulation of circuits. You can specify a
 * list of Switches and Lights which will be toggled/checked. For every step you
 * can set a input configuration which will be set to the switches and a table
 * of values with the expected output on the lights.
 */
class CircuitSimulator {

    private final Circuit circuit;
    private final List<Switch> switches;
    private final List<Light> lights;
    private final List<List<Boolean>> inputs;
    private final List<List<Boolean>> outputs;
    private Simulation simulation;

    /**
     * Create a new CircuitSimulator.
     * 
     * @param circuit
     *            The circuit to simulate on
     * @param switches
     *            the switches to toggle
     * @param lights
     *            the lights to check
     */
    public CircuitSimulator(Circuit circuit, List<Switch> switches,
            List<Light> lights) {
        assert circuit != null;
        assert switches != null;
        assert lights != null;

        this.circuit = circuit;
        simulation = null;
        this.switches = new ArrayList<Switch>(switches);
        this.lights = new ArrayList<Light>(lights);
        inputs = new LinkedList<List<Boolean>>();
        outputs = new LinkedList<List<Boolean>>();
    }

    /**
     * Set one row of the table of values. format is "0 1 0 0 1 0", where 0 is
     * false and 1 is true.
     * 
     * @param input
     *            list of input values for the switches to check against
     * @param output
     *            list of output values for the lights to check against, set to
     *            empty string if you expect no value
     */
    public void addTestRow(String input, String output) {
        String[] insplit = input.split("\\s+");
        String[] outsplit = output.split("\\s+");
        assert insplit.length == switches.size();
        assert outsplit.length == lights.size() || output.equals("");

        List<Boolean> inlist = new ArrayList<Boolean>();
        List<Boolean> outlist = new ArrayList<Boolean>();

        for (String str : insplit) {
            int value = Integer.parseInt(str);
            if (value == 0) {
                inlist.add(false);
            } else {
                inlist.add(true);
            }
        }

        if (!output.equals("")) {
            for (String str : outsplit) {
                int value = Integer.parseInt(str);
                if (value == 0) {
                    outlist.add(false);
                } else {
                    outlist.add(true);
                }
            }
        }

        if (output.equals("")) {
            addTestRow(inlist, null);
        } else {
            addTestRow(inlist, outlist);

        }

        Log.gl().debug("Added " + inlist + " " + outlist);
    }

    /**
     * Set one row of the table of values.
     * 
     * @param input
     *            list of input values for the switches to check against
     * @param output
     *            list of output values for the lights to check against, set it
     *            to null if you expect no value
     */
    void addTestRow(List<Boolean> input, List<Boolean> output) {
        assert input.size() == switches.size();
        assert output == null || output.size() == lights.size();

        inputs.add(new ArrayList<Boolean>(input));
        outputs.add(output == null ? null : new ArrayList<Boolean>(output));
    }

    private void setSimulationToRow(List<Boolean> inputValues) {
        for (int i = 0; i < inputValues.size(); i++) {
            SimulationBrick simSwitch = new SimulationBrick(switches.get(i),
                    new LinkedList<Component>());
            SwitchSimulationState switchState = (SwitchSimulationState) simulation
                    .getSimulationState(simSwitch);
            switchState.setValue(inputValues.get(i));
        }
    }

    private boolean checkOutput(List<Boolean> outputValues) {
        assert simulation != null;

        if (outputValues == null) {
            return true;
        }

        for (int i = 0; i < outputValues.size(); i++) {
            SimulationBrick simLamp = new SimulationBrick(lights.get(i),
                    new LinkedList<Component>());
            LightSimulationState lightState = (LightSimulationState) simulation
                    .getSimulationState(simLamp);
            boolean value = lightState.getValue(lights.get(i).getInputA());

            if (value != outputValues.get(i).booleanValue()) {
                Log.gl().debug("Failed at switch setting:");
                Log.gl().debug(" Inputs: " + inputs.get(i));
                Log.gl().debug(" Outputs: " + outputValues);
                return false;
            }
        }

        return true;
    }

    /**
     * Clear all input configuration / expected output rows.
     */
    public void clearRows() {
        inputs.clear();
        outputs.clear();
    }

    /**
     * Simulate all rows added with {@link #addTestRow(List, List)} or
     * {@link #addTestRow(String, String)}.
     * 
     * @return true if all have passed, else false
     */
    public boolean simulate() {
        simulation = new SimulationImpl(circuit);
        boolean passed = true;

        for (int i = 0; i < inputs.size(); i++) {
            Log.gl().debug("Simulating row " + i);
            setSimulationToRow(inputs.get(i));
            simulation.nextStep();
            boolean result = checkOutput(outputs.get(i));

            if (!result) {
                Log.gl().debug(" Failed at row " + i + ":");
                passed = false;
                break;
            }
        }

        return passed;
    }

}
