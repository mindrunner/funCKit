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

package de.sep2011.funckit.circuitfactory;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * An abstract factory class for {@link Circuit}s. Every instance of a class
 * inheriting from this generates one instance of a specific Circuit. The class
 * has to specify the behavior of the {@link Circuit} it generates and can add
 * its own Methods as necessary.
 */
public abstract class AbstractCircuitFactory {

    /**
     * To use the predefined {@link #getCircuit()} method store the circuit
     * here.
     */
    protected Circuit circuit;

    /**
     * Returns the generated Circuit.
     * 
     * @return the generated Circuit.
     */
    public Circuit getCircuit() {
        return circuit;
    }

    /**
     * Returns the {@link Light}s this {@link Circuit} contains.
     * 
     * @return the Lights this {@link Circuit} consists of.
     */
    public List<Light> getLights() {
        List<Light> lights = new LinkedList<Light>();
        for (Element l : circuit.getElements()) {
            if (l instanceof Light) {
                lights.add((Light) l);
            }
        }
        return lights;

    }

    /**
     * Return the {@link Switch}es this {@link Circuit} consists of.
     * 
     * @return the {@link Switch}es this {@link Circuit} consists of.
     */
    public List<Switch> getSwitches() {
        List<Switch> switches = new LinkedList<Switch>();
        for (Element l : circuit.getElements()) {
            if (l instanceof Switch) {
                switches.add((Switch) l);
            }
        }
        return switches;
    }

    /**
     * This class generates a {@link ComponentType} for this {@link Circuit}.
     * The default implementation uses all connected {@link AccessPoint}s to
     * generate a {@link ComponentType}. Subclasses may provide a more
     * appropriate implementation.
     * 
     * @return a {@link ComponentType} for this {@link Circuit}
     */
    public ComponentType getComponentTypeForCircuit() {
        ComponentType ct1 = new ComponentTypeImpl(circuit, "CompType1",
                getUnconnectedInputs(circuit), getUnconnectedOutputs(circuit));
        int i = 0;
        for (Input input : getUnconnectedInputs(circuit)) {
            ct1.setName(input, "i" + i);
            i++;
        }
        i = 0;
        for (Output output : getUnconnectedOutputs(circuit)) {
            ct1.setName(output, "o" + i);
            i++;
        }

        return ct1;
    }

    /**
     * Helper to get unconnected Inputs of a {@link Circuit}.
     * 
     * @param c
     *            the circuit
     * @return the unconnected Inputs
     */
    private static Set<Input> getUnconnectedInputs(Circuit c) {
        Set<Input> inputs = new LinkedHashSet<Input>();
        for (Element e : c.getElements()) {
            if (e instanceof Brick) {
                for (Input i : ((Brick) e).getInputs()) {
                    if (i.getWires().isEmpty()) {
                        inputs.add(i);
                    }
                }
            }
        }
        return inputs;
    }

    /**
     * Helper to get unconnected {@link Output}s of a {@link Circuit}.
     * 
     * @param c
     *            the circuit
     * @return the unconnected {@link Output}s
     */
    private static Set<Output> getUnconnectedOutputs(Circuit c) {
        Set<Output> outputs = new LinkedHashSet<Output>();
        for (Element e : c.getElements()) {
            if (e instanceof Brick) {
                for (Output o : ((Brick) e).getOutputs()) {
                    if (o.getWires().isEmpty()) {
                        outputs.add(o);
                    }
                }
            }
        }
        return outputs;
    }
}
