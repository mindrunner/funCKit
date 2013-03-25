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

package de.sep2011.funckit.validator;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.util.internationalization.Language;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Check if a {@link Circuit} has loops. Can be used to detect if a circuit is a
 * combinatorial circuit.
 */
public class LoopCheck implements Check {

    /**
     * Elements detected as flaws in the definition of this {@link Check}.
     */
    private final Set<Element> flawElements = new LinkedHashSet<Element>();

    private final Map<ComponentType, Boolean> typeCheckResult = new HashMap<ComponentType, Boolean>();

    /**
     * Visited {@link SimulationBrick}s in the depth-search.
     */
    private final Set<SimulationBrick> visited = new LinkedHashSet<SimulationBrick>();

    /**
     * Visited {@link SimulationBrick}s in the depth-search.
     */
    private final Set<SimulationBrick> finished = new LinkedHashSet<SimulationBrick>();

    /**
     * The current path of the depth search.
     */
    private final Deque<Object> currentPath = new LinkedList<Object>();

    private final boolean checkForZeroDelay;
    
    /**
     * Create a new {@link LoopCheck}
     */
    public LoopCheck() {
        checkForZeroDelay = false;
    }

    /**
     * Construct a new {@link LoopCheck}.
     * 
     * @param checkForZeroDelay
     *            true: only check for loops with zero delay, false: check for
     *            all loops
     */
    LoopCheck(boolean checkForZeroDelay) {
        this.checkForZeroDelay = checkForZeroDelay;
    }

    /**
     * {@inheritDoc} In this case look if the {@link Circuit} has loops with a
     * by performing a depth-search on the flat {@link Circuit}.
     */
    @Override
    public Result perform(Circuit c) {
        flawElements.clear();
        typeCheckResult.clear();
        visited.clear();
        finished.clear();

        checkCircuit(c);

        // construct Result object
        String message = "check." + this.getClass().getSimpleName();
        if (flawElements.isEmpty()) {
            message += ".passedMessage";
        } else {
            message += ".failedMessage";
        }
        return new Result(flawElements.isEmpty(), Language.tr(message),
                flawElements, this);
    }

    /**
     * Performs a depth-search on the {@link Circuit} and looks for loops with 0
     * delay.
     * 
     * @param c
     *            {@link Circuit} to check.
     * @return
     */
    private boolean checkCircuit(Circuit c) {
        boolean hasFlaw = false;
        currentPath.clear();

        // check all elements
        for (Element e : c.getElements()) {
            if (e instanceof Brick) {
                Brick b = (Brick) e;
                if (doDepthSearch(new SimulationBrick(b,
                        new LinkedList<Component>()))) {
                    hasFlaw = true;
                }
            }

            // check component's type additionally
            if (e instanceof Component) {
                Component comp = (Component) e;
                ComponentType type = comp.getType();
                if (!typeCheckResult.containsKey(type)) {
                    typeCheckResult.put(type, checkCircuit(type.getCircuit()));
                }
                boolean typeHasFlaw = typeCheckResult.get(type);
                if (typeHasFlaw) {
                    hasFlaw = true;
                    flawElements.add(comp);
                }
            }
        }
        return hasFlaw;
    }

    /**
     * Performs the actual depth-search.
     * 
     * @param simBrick
     *            {@link SimulationBrick} to search on.
     * @return
     */
    private boolean doDepthSearch(SimulationBrick simBrick) {
        boolean flawFound = false;

        // ignore already finished bricks (and those with delay)
        if (finished.contains(simBrick)
                || (simBrick.getBrick().hasDelay() && checkForZeroDelay)) {
            return false;
        }

        if (visited.contains(simBrick)) {

            // loop detected
            flawElements.add(simBrick.getBrick());
            for (Object o : currentPath) {
            	if (o instanceof SimulationBrick) {
            		flawElements.add(((SimulationBrick) o).getBrick());
            	} else {
            		assert o instanceof Wire;
            		flawElements.add((Element)o);
            	}
            	if (simBrick.equals(o)) {
            		break;
            	}
            }
            flawFound = true;
        } else {
            visited.add(simBrick);
            currentPath.push(simBrick);

            // search deeper on all outputs
            for (Output o : simBrick.getBrick().getOutputs()) {
                if (traverseOutput(o, simBrick.getStack())
                        | traverseUpwards(o, simBrick.getStack())) {
                    flawFound = true;
                }
            }
            finished.add(simBrick);
            currentPath.pop();
        }
        return flawFound;
    }

    /**
     * Traverses the given {@link Output} by following its {@link Wire}s and
     * going deeper in Components of necessary.
     * 
     * @param o
     *            {@link Output} to traverse
     * @param stack
     *            The path to the current {@link Component} in which we are at
     *            this step in the Check.
     * @return
     */
    private boolean traverseOutput(Output o, Deque<Component> stack) {
        boolean flawFound = false;
        for (Wire w : o.getWires()) {
        	currentPath.push(w);
            AccessPoint otherPoint = w.getOther(o);
            Brick otherBrick = otherPoint.getBrick();
            Deque<Component> stack2 = new LinkedList<Component>(stack);
            SimulationBrick otherSimBrick = new SimulationBrick(otherBrick,
                    stack2);

            // go deeper in components if necessary
            int pathSize = currentPath.size();
            otherSimBrick = getNonComponent(otherPoint, otherSimBrick);

            // keep on searching
            if (doDepthSearch(otherSimBrick)) {
                flawFound = true;
            }
			for (int i = 0; i < currentPath.size() - pathSize; i++) {
				currentPath.pop();
			}
            currentPath.pop();
        }
        return flawFound;
    }

    /**
     * Traverse the given {@link Output} by going upwards and traverse the
     * connected wires there if this {@link Output} is part of a
     * {@link ComponentType}.
     * 
     * @param o
     *            {@link Output} to traverse
     * @param stack
     *            The path to the current {@link Component} in which we are at
     *            this step in the Check.
     * @return
     */
    private boolean traverseUpwards(Output o, Deque<Component> stack) {
        boolean flawFound = false;

        // traverse to the outer component outputs if appropriate
        if (!stack.isEmpty()) {
            Component component = stack.peek();
            Output outer = component.getOuterOutput(o);
            if (outer != null && !(component.hasDelay() && checkForZeroDelay)) { // stop when component
                                                          // has delay in delay mode
                Deque<Component> stack2 = new LinkedList<Component>(stack);
                stack2.pop();

                // traverse through all connected wires
                if (traverseOutput(outer, stack2)) {
                    flawFound = true;
                }

                // traverse further upwards if necessary
                if (traverseUpwards(outer, stack2)) {
                    flawFound = true;
                }
            }
        }
        return flawFound;
    }

    /**
     * Goes deeper in the {@link Component} if the given {@link SimulationBrick}
     * has a {@link Component}.
     * 
     * @param a
     *            {@link AccessPoint} to go deeper with.
     * @param simBrick
     *            {@link SimulationBrick} to go deeper in.
     * @return {@link SimulationBrick} which has a non {@link Component}.
     */
    private SimulationBrick getNonComponent(AccessPoint a,
            SimulationBrick simBrick) {
        if (simBrick.getBrick() instanceof Component) {
            Component component = (Component) simBrick.getBrick();
            currentPath.push(simBrick);
            AccessPoint inner = component.getInner(a);
            Deque<Component> stack2 = new LinkedList<Component>(
                    simBrick.getStack());
            stack2.push(component);
            SimulationBrick innerBrick = new SimulationBrick(inner.getBrick(),
                    stack2);
            return getNonComponent(inner, innerBrick);
        }
        return simBrick;
    }

    @Override
    public String getName() {
        return Language.tr("Check." + this.getClass().getSimpleName());
    }

}
