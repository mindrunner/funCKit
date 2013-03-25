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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.simulationmodel.LightSimulationState;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;
import de.sep2011.funckit.util.GraphmodelUtil.VerticalThanHorizontalOrderComparator;
import de.sep2011.funckit.validator.LoopCheck;

/**
 *
 */
public class BehaviouralCircuitComparator {
    
    private final Circuit referenceCircuit;
    private final Circuit circuitToCompare;
    private List<Switch>  referenceSwitchList;
    private List<Switch>  toCompareSwitchList;
    private List<Light>  referenceLightList;
    private List<Light>  toCompareLightList;
    

    private Simulation referenceSimulation;
    private Simulation toCompareSimulation;
    private static final int STUPID_SIMULATION_STEPS = 100;
    
    private boolean passed;
    
    public static void main(String[] args) throws SEPFormatImportException, FileNotFoundException {
//        Circuit circuit = new SEPFormatConverter("", Mode.FUNCKITFORMAT)
//        .doImport(new FileInputStream("/home/peter/zeugs/funCKit/workspace/funCKit/src/test/resources/example-d-FlipFlop/example-dflipflop.fck"));
// 
//        Circuit circuit2 = new SEPFormatConverter("", Mode.FUNCKITFORMAT)
//        .doImport(new FileInputStream("/home/peter/zeugs/funCKit/workspace/funCKit/src/test/resources/example-d-FlipFlop/example-dflipflop.fck"));       
//        
//        Log.gl().debug("Passed: " + new BehaviouralCircuitComparator(circuit, circuit2, null).isPassed());
    }

    public BehaviouralCircuitComparator(Circuit referenceCircuit, Circuit circuitToCompare,
            List<List<Boolean>> simRows) {
        this.referenceCircuit = referenceCircuit;
        this.circuitToCompare = circuitToCompare;
        
        referenceSwitchList = new ArrayList<Switch>();
        toCompareSwitchList = new ArrayList<Switch>();
        referenceLightList = new ArrayList<Light>();
        toCompareLightList = new ArrayList<Light>();

        checkSwitchAndLightPositionsAndFillList();
        
        Collections.sort(referenceSwitchList, new VerticalThanHorizontalOrderComparator());
        Collections.sort(toCompareSwitchList, new VerticalThanHorizontalOrderComparator());
        Collections.sort(referenceLightList, new VerticalThanHorizontalOrderComparator());
        Collections.sort(toCompareLightList, new VerticalThanHorizontalOrderComparator());
        
        initSimulation();
        
        boolean referenceIsCombinatorial = new LoopCheck().perform(referenceCircuit).isPassed();
        boolean toCompareIsCombinatorial = new LoopCheck().perform(circuitToCompare).isPassed();

        if (referenceIsCombinatorial && toCompareIsCombinatorial) {
            passed = compareCombinatorial();
        } else if (!referenceIsCombinatorial && !toCompareIsCombinatorial) {
            if(simRows == null) {
                passed = compareStupid();
            } else {
                passed = compareSimlist(simRows);
            }
        } else {
            passed = false;
        }

    }
    
    private void initSimulation() {
        referenceSimulation = new SimulationImpl(referenceCircuit);
        toCompareSimulation = new SimulationImpl(circuitToCompare);

    }
 
    /**
     * Start Comparation of a Combinatorial Circuit.
     */
    private boolean compareCombinatorial() {
        Log.gl().debug("Comparing two combinatorial circuits");
        long maxDelayRef = GraphmodelUtil
                .getTotalDelayOfCombinatorialCircuit(referenceCircuit);
        long maxDelayComp = GraphmodelUtil
                .getTotalDelayOfCombinatorialCircuit(circuitToCompare);

        for (long i = 0; i < (1l << referenceSwitchList.size()); i++) {
            setConfiguration(referenceSimulation, referenceSwitchList, i);
            setConfiguration(toCompareSimulation, toCompareSwitchList, i);
            runSimulation(referenceSimulation, maxDelayRef + 1);
            runSimulation(toCompareSimulation, maxDelayComp + 1);
            if(!compareLights()) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Compare the lights between the reference and the other circuit
     * 
     * @return if minimum one light is not right
     */
    private boolean compareLights() {
        for (int i = 0; i < referenceLightList.size(); i++) {
            SimulationBrick simLampRef = new SimulationBrick(
                    referenceLightList.get(i), new LinkedList<Component>());
            SimulationBrick simLampComp = new SimulationBrick(
                    toCompareLightList.get(i), new LinkedList<Component>());
            
            
            LightSimulationState lightStateRef = (LightSimulationState) referenceSimulation
                    .getSimulationState(simLampRef);
            LightSimulationState lightStateComp = (LightSimulationState) toCompareSimulation
                    .getSimulationState(simLampComp);
            
            boolean valueRef = lightStateRef.getValue(referenceLightList.get(i).getInputA());
            boolean valueComp = lightStateComp.getValue(toCompareLightList.get(i).getInputA());

            if(valueRef != valueComp) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * run the specified Simulation.
     * 
     * @param simulation
     * @param steps repeat specified number of steps
     */
    private void runSimulation(Simulation simulation, long steps) {
        for (int i = 0; i < steps; i++) {
            simulation.nextStep();
        }
    }
    
    private boolean compareSimlist(List<List<Boolean>> simrows) {
        // TODO: check simrows

        Log.gl().debug("Comparing with simassign");
        if(simrows.isEmpty()) {
            Log.gl().warn("no test rows in simassign! Will always return true");
        }
        
        for (List<Boolean> row : simrows) {
            Log.gl().debug("Testing row: " + row);
            setConfigurationToRow(referenceSimulation, referenceSwitchList, row);
            setConfigurationToRow(toCompareSimulation, toCompareSwitchList, row);
            runSimulation(referenceSimulation, 1);
            runSimulation(toCompareSimulation, 1);

            if (!compareLights()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Stupid method: set a configuration and simulate STUPID_SIMULATION_STEPS
     * steps on fresh simulation. Then the same on one simulation.
     */
    private boolean compareStupid() {
        Log.gl().debug("Comparing using the stupid method");
        for (long i = 0; i < (1l << referenceSwitchList.size()); i++) {
            initSimulation();
            setConfiguration(referenceSimulation, referenceSwitchList, i);
            setConfiguration(toCompareSimulation, toCompareSwitchList, i);
            runSimulation(referenceSimulation, STUPID_SIMULATION_STEPS);
            runSimulation(toCompareSimulation, STUPID_SIMULATION_STEPS);
            if(!compareLights()) {
                return false;
            }
        }
        
        initSimulation();
        for (long i = 0; i < (1l << referenceSwitchList.size()); i++) {
            setConfiguration(referenceSimulation, referenceSwitchList, i);
            setConfiguration(toCompareSimulation, toCompareSwitchList, i);
            runSimulation(referenceSimulation, STUPID_SIMULATION_STEPS);
            runSimulation(toCompareSimulation, STUPID_SIMULATION_STEPS);
            if(!compareLights()) {
                return false;
            }
        }

        return true;

    }
    
    public static List<List<Boolean>> convertSimlistToSimRows(InputStream simlist) {
        List<List<Boolean>> simrows = new ArrayList<List<Boolean>>();

        BufferedReader r = new BufferedReader(new InputStreamReader(simlist));
        try {
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                String[] insplit = line.split("\\s+");
                
                List<Boolean> inlist = new ArrayList<Boolean>();
                boolean error = false;
                
                for (String str : insplit) {
                    if(!("1".equals(str) || "0".equals(str))) {
                        error = true;
                        break;
                    }
                    
                    int value = Integer.parseInt(str);
                    inlist.add(value != 0);
                }
                
                if(!error) {
                    simrows.add(inlist);
                } else {
                    Log.gl().debug("line >>" + line + "<< is a comment or has an error, do not add");
                }
            }
        } catch (IOException e) {
            return null;
        }
        
        return simrows;
    }
    
    private void setConfigurationToRow(Simulation simulation, List<Switch> switches,
            List<Boolean> inputValues) {
        for (int i = 0; i < inputValues.size(); i++) {
            SimulationBrick simSwitch = new SimulationBrick(switches.get(i),
                    new LinkedList<Component>());
            SwitchSimulationState switchState = (SwitchSimulationState) simulation
                    .getSimulationState(simSwitch);
            switchState.setValue(inputValues.get(i));
        }
    }
    
    /**
     * Set the configuration of the switches which represent number
     * 
     * @param simulation
     * @param switches
     * @param number
     */
    private void setConfiguration(Simulation simulation, List<Switch> switches,
            long number) {
        long convnum = number;

        for (int pos = switches.size() - 1; pos >= 0; pos--) {
            SimulationBrick simSwitch = new SimulationBrick(switches.get(pos),
                    new LinkedList<Component>());
            SwitchSimulationState switchState = (SwitchSimulationState) simulation
                    .getSimulationState(simSwitch);
            switchState.setValue((convnum % 2) != 0);
            convnum /= 2;
        }
    }

    /**
     * Check if the circuit to compare has switches and lights on the same
     * position as the reference
     * 
     * @param circuitToCompare
     * @return
     */
    private void checkSwitchAndLightPositionsAndFillList() {

        for (Element element : referenceCircuit.getElements()) {
            if (element instanceof Switch) {
                Brick brick2 = GraphmodelUtil.findBrickAtSamePos(circuitToCompare, element
                        .getPosition());
                if (brick2 instanceof Switch) {
                    referenceSwitchList.add((Switch) element);
                    toCompareSwitchList.add((Switch) brick2);
                } else {
                    throw new AssertionError("Switches not at same position");
                }
            } else if (element instanceof Light) {
                Brick brick2 = GraphmodelUtil.findBrickAtSamePos(circuitToCompare, element
                        .getPosition());
                if (brick2 instanceof Light) {
                    referenceLightList.add((Light) element);
                    toCompareLightList.add((Light) brick2);
                } else {
                    throw new AssertionError("Lights not at same position");
                }
            }
        }
    }


    public boolean isPassed() {
        return passed;
    }
}
