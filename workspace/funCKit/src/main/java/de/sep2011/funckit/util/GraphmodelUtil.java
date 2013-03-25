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

import static java.lang.Math.max;
import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.graphmodel.implementations.WireImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ConnectCommand;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.CommandDispatcher;
import de.sep2011.funckit.util.command.SimpleCommandCombiner;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Utility class for some logic belonging to the graphmodel, but which is better
 * not integrated directly in the graphmodel itself.
 */
public class GraphmodelUtil {

    /**
     * Converts the given {@link Circuit} to an {@link ComponentType} by
     * converting all {@link Switch}es and {@link Light}s in the {@link Circuit}
     * to {@link IdPoint}s and their {@link Input}/{@link Output} gets the
     * {@link ComponentType} {@link Input}/{@link Output} with the name of the
     * {@link Switch}/{@link Light}. This can be reverted with .
     * 
     * @param circuit
     *            the {@link Circuit} to convert.
     * @param typeName
     *            the name of the generated {@link ComponentType}.
     * @param copyCircuit
     * @return a ComponentType
     */
    public static ComponentType convertToComponentType(Circuit circuit, String typeName,
            boolean copyCircuit) {
        if (copyCircuit) {
            circuit = circuit.getCopy();
        }
        Set<Input> inputs = new LinkedHashSet<Input>();
        Set<Output> outputs = new LinkedHashSet<Output>();
        Map<AccessPoint, String> names = new LinkedHashMap<AccessPoint, String>();
        Set<Element> elements = new LinkedHashSet<Element>(circuit.getElements());
        List<Element> switches = new LinkedList<Element>();
        List<Element> lights = new LinkedList<Element>();
        for (Element e : elements) {
            if (e instanceof SwitchImpl) {
                switches.add(e);
            } else if (e instanceof Light) {
                lights.add(e);
            }
        }
        Collections.sort(switches, new VerticalThanHorizontalOrderComparator());
        Collections.sort(lights, new VerticalThanHorizontalOrderComparator());
        for (Element e : switches) {
            SwitchImpl sw = (SwitchImpl) e;
            IdPoint in = new IdPoint(sw.getPosition());
            in.setName(sw.getName());
            in.setOrientation(in.getOrientation());
            for (Wire w : sw.getOutputO().getWires()) {
                if (w.getFirstAccessPoint() == sw.getOutputO()) {
                    w.setFirstAccessPoint(in.getOutputO());
                } else {
                    w.setSecondAccessPoint(in.getOutputO());
                }
                in.getOutputO().addWire(w);
            }
            circuit.removeBrick(sw);
            circuit.addBrick(in);
            inputs.add(in.getInputA());
            names.put(in.getInputA(), sw.getName());
        }

        for (Element e : lights) {
            Light li = (Light) e;
            IdPoint out = new IdPoint(li.getPosition());
            out.setName(li.getName());
            out.setOrientation(li.getOrientation());
            for (Wire w : li.getInputA().getWires()) {
                if (w.getFirstAccessPoint() == li.getInputA()) {
                    w.setFirstAccessPoint(out.getInputA());
                } else {
                    w.setSecondAccessPoint(out.getInputA());
                }
                out.getInputA().addWire(w);
            }
            circuit.removeBrick(li);
            circuit.addBrick(out);
            outputs.add(out.getOutputO());
            names.put(out.getOutputO(), li.getName());
        }

        ComponentType type = new ComponentTypeImpl(circuit, typeName, inputs, outputs);
        for (Entry<AccessPoint, String> entry : names.entrySet()) {
            type.setName(entry.getKey(), entry.getValue());
        }
        return type;
    }

    /**
     * Reverts a {@link ComponentType} that was generated by
     * {@link #convertToComponentType(Circuit, String, boolean)} to the original
     * circuit.
     * 
     * @param type
     *            the {@link ComponentType} that was generated by
     *            {@link #convertToComponentType(Circuit, String, boolean)}.
     * @param copyCircuit
     *            copy the circuit of the given {@link ComponentType}?
     * @return the reverted {@link Circuit}.
     */
    public static Circuit revertToCircuit(ComponentType type, boolean copyCircuit) {
        Circuit circuit = type.getCircuit();
        Map<Brick, Brick> oldToNewBrickMap = null;
        if (copyCircuit) {
            Pair<Map<Brick, Brick>, Circuit> pair = circuit.getBrickMapAndCopy();
            circuit = pair.getRight();
            oldToNewBrickMap = pair.getLeft();
        }
        for (Input in : type.getInputs()) {
            Brick brick = in.getBrick();
            if (copyCircuit) {
                assert oldToNewBrickMap != null;
                brick = oldToNewBrickMap.get(brick);
            }
            if (brick instanceof IdPoint) {
                IdPoint point = (IdPoint) brick;
                SwitchImpl sw = new SwitchImpl(point.getPosition());
                sw.setName(point.getName());
                sw.setOrientation(point.getOrientation());
                for (Wire w : point.getOutputO().getWires()) {
                    if (w.getFirstAccessPoint() == point.getOutputO()) {
                        w.setFirstAccessPoint(sw.getOutputO());
                    } else {
                        w.setSecondAccessPoint(sw.getOutputO());
                    }
                    sw.getOutputO().addWire(w);
                }
                circuit.removeBrick(point);
                assert !circuit.getElements().contains(point);
                circuit.addBrick(sw);
            }
        }
        for (Output out : type.getOutputs()) {
            Brick brick = out.getBrick();
            if (copyCircuit) {
                assert oldToNewBrickMap != null;
                brick = oldToNewBrickMap.get(brick);
            }
            if (brick instanceof IdPoint) {
                IdPoint point = (IdPoint) brick;
                Light li = new Light(point.getPosition());
                li.setName(point.getName());
                li.setOrientation(point.getOrientation());
                for (Wire w : point.getInputA().getWires()) {
                    if (w.getFirstAccessPoint() == point.getInputA()) {
                        w.setFirstAccessPoint(li.getInputA());
                    } else {
                        w.setSecondAccessPoint(li.getInputA());
                    }
                    li.getInputA().addWire(w);
                }
                circuit.removeBrick(point);
                assert !circuit.getElements().contains(point);
                circuit.addBrick(li);
            }
        }

        return circuit;
    }

    /**
     * Comparator which compares the y-Coordinate. When they are equal it
     * compares the x-Coordinate.
     */
    public static class VerticalThanHorizontalOrderComparator implements Comparator<Element>,
            Serializable {

        private static final long serialVersionUID = 4784080254850188924L;

        @Override
        public int compare(Element o1, Element o2) {
            int vertical = o1.getPosition().y - o2.getPosition().y;
            return vertical == 0 ? o1.getPosition().x - o2.getPosition().x : vertical;
        }

    }
    
    /**
     * Comparator which compares the y-Coordinate. When they are equal it
     * compares the x-Coordinate.
     */
    private static class VerticalThanHorizontalOrderComparatorForAccessPoints implements Comparator<AccessPoint>,
            Serializable {

        private static final long serialVersionUID = 4784080254850188924L;

        @Override
        public int compare(AccessPoint o1, AccessPoint o2) {
            int vertical = o1.getPosition().y - o2.getPosition().y;
            return vertical == 0 ? o1.getPosition().x - o2.getPosition().x : vertical;
        }

    }
    
    /**
     * Connects the {@link Output}s of the first {@link Brick} with the {@link Input}s of the second {@link Brick}.
     * This is done pairwise, starting with the topmost {@link AccessPoint}s. If one {@link Brick} has more
     * {@link AccessPoint}s than the other the remain ones are not connected.
     * @param circuit the circuit the {@link Brick}s are an.
     * @param dispatcher the {@link CommandDispatcher} to use for connecting.
     * @param firstBrick the first {@link Brick} to connect. Here the {@link Output}s get connected.
     * @param secondBrick the second {@link Brick} to connect. Here the {@link Input}s get connected.
     * @param onlyEmpty only connect unconnected {@link AccessPoint}s?
     * @param offset the offset to use for the {@link Brick} with less {@link AccessPoint}s. If the offset is too big
     * 		the maximum that can be used without reducing the amount of connected {@link Wire}s is used.
     * @return {@link Pair} of the actually used offset and the created {@link Wire}s.
     */
    public static void connectBricks(Circuit circuit, CommandDispatcher dispatcher, Brick firstBrick, Brick secondBrick, boolean onlyEmpty, int offset) {
    	int[] intResults = new int[3];
    	Pair<List<AccessPoint>, List<AccessPoint>> listPair = connectBricksHelper(firstBrick, secondBrick, onlyEmpty, offset, intResults);
    	List<Command> commands = new ArrayList<Command>(intResults[0]);
    	for(int i = 0; i < intResults[0]; i++) {
    		Command connectCommand = new ConnectCommand(circuit, listPair.getLeft().get(i+intResults[1]), listPair.getRight().get(i+intResults[2]));
    		commands.add(connectCommand);
    	}
    	if (commands.size() > 0) {
    		dispatcher.dispatch(new SimpleCommandCombiner(commands));
    	}
    }
    
    /**
     * Connects the {@link Output}s of the first {@link Brick} with the {@link Input}s of the second {@link Brick}.
     * This is done pairwise, starting with the topmost {@link AccessPoint}s. If one {@link Brick} has more
     * {@link AccessPoint}s than the other the remain ones are not connected.
     * This method only creates the needed {@link Wire}s
     * @param firstBrick the first {@link Brick} to connect. Here the {@link Output}s get connected.
     * @param secondBrick the second {@link Brick} to connect. Here the {@link Input}s get connected.
     * @param onlyEmpty only connect unconnected {@link AccessPoint}s?
     * @param offset the offset to use for the {@link Brick} with less {@link AccessPoint}s. If the offset is too big
     * 		the maximum that can be used without reducing the amount of connected {@link Wire}s is used.
     * @return {@link Pair} of the actually used offset and the created {@link Wire}s.
     */
    public static Pair<Integer, Set<Element>> createWiresforBricks(Brick firstBrick, Brick secondBrick, boolean onlyEmpty, int offset) {
    	int[] intResults = new int[3];
    	Pair<List<AccessPoint>, List<AccessPoint>> listPair = connectBricksHelper(firstBrick, secondBrick, onlyEmpty, offset, intResults);
    	Set<Element> wires = new LinkedHashSet<Element>();
    	for(int i = 0; i < intResults[0]; i++) {
    		wires.add(new WireImpl(listPair.getLeft().get(i+intResults[1]), listPair.getRight().get(i+intResults[2])));
    	}
		return new Pair<Integer, Set<Element>>(Math.max(intResults[1], intResults[2]), wires);
    }
    
    /**
     * Helper to connect the {@link Output}s of the first {@link Brick} with the {@link Input}s of the second {@link Brick}.
     * This is done pairwise, starting with the topmost {@link AccessPoint}s. If one {@link Brick} has more
     * {@link AccessPoint}s than the other the remain ones are not connected.
     * @param firstBrick the first {@link Brick} to connect. Here the {@link Output}s get connected.
     * @param secondBrick the second {@link Brick} to connect. Here the {@link Input}s get connected.
     * @param onlyEmpty only connect unconnected {@link AccessPoint}s?
     * @param offset the offset to use for the {@link Brick} with less {@link AccessPoint}s. If the offset is too big
     * 		the maximum that can be used without reducing the amount of connected {@link Wire}s is used.
     * @param intResults int array to store the results. First is the amount of {@link AccessPoint}s to connect,
     * 			second is the offset for the first brick, third is the offset for the second brick.
     * @return {@link Pair} of the two {@link AccessPoint} lists. Left are the {@link Output}s of the 
     * 			first {@link Brick}, right are the {@link Input}s of the second {@link Brick}.
     */
    private static Pair<List<AccessPoint>, List<AccessPoint>> connectBricksHelper(Brick firstBrick, Brick secondBrick,
    		boolean onlyEmpty, int offset, int[] intResults) {
    	assert intResults.length == 3;
    	List<AccessPoint> outputs = new ArrayList<AccessPoint>(firstBrick.getOutputs());
    	List<AccessPoint> inputs = new ArrayList<AccessPoint>(secondBrick.getInputs());
    	if (onlyEmpty) {
    		filterConnectedAccessPoints(outputs);
    		filterConnectedAccessPoints(inputs);
    	}
    	Collections.sort(outputs, new VerticalThanHorizontalOrderComparatorForAccessPoints());
    	Collections.sort(inputs, new VerticalThanHorizontalOrderComparatorForAccessPoints());
    	int amount = Math.min(outputs.size(), inputs.size());
    	int firstOffset = 0;
    	int secondOffset = 0;
    	offset = Math.min(offset, Math.abs(inputs.size() - outputs.size()));
    	if (outputs.size() > inputs.size()) {
    		firstOffset = offset;
    	} else {
    		secondOffset = offset;
    	}
    	intResults[0] = amount;
    	intResults[1] = firstOffset;
    	intResults[2] = secondOffset;
    	return new Pair<List<AccessPoint>, List<AccessPoint>>(outputs, inputs);
    }
    
    /**
     * Removes all {@link AccessPoint}s that have {@link Wire}s connected from the given {@link Collection}.
     * @param points the {@link Collection} of {@link AccessPoint}s to filter.
     */
    private static void filterConnectedAccessPoints(Collection<AccessPoint> points) {
    	for(Iterator<AccessPoint> i = points.iterator(); i.hasNext();) {
    		if (i.next().getWires().size() != 0) {
    			i.remove();
    		}
    	}
    }

    /**
     * like {@link #getTotalDelayOfCombinatorialCircuit(Set, Set)} but finds the
     * switches and lights by its own.
     * 
     */
    public static long getTotalDelayOfCombinatorialCircuit(final Circuit c) {
        final Set<Switch> switches = new HashSet<Switch>();
        final Set<Light> lights = new HashSet<Light>();
        
        new ElementDispatcher() {
            
            {
                for(Element elem : c.getElements()) {
                    elem.dispatch(this);
                }
            }
            
            @Override
            public void visit(IdPoint idPoint) {
                                
            }
            
            @Override
            public void visit(Not not) {
                
            }
            
            @Override
            public void visit(Or or) {
                
            }
            
            @Override
            public void visit(And and) {
                
            }
            
            @Override
            public void visit(Light light) {
                lights.add(light);
            }
            
            @Override
            public void visit(Switch s) {
                switches.add(s);
            }
            
            @Override
            public void visit(Component component) {
                
            }
            
            @Override
            public void visit(Wire wire) {
                
            }
            
            @Override
            public void visit(Element element) {
                
            }
        };
        
        return getTotalDelayOfCombinatorialCircuit(switches, lights);
    }
    
    
    /**
     * Get the total delay of a combinatorial circuit. Strating from switches,
     * ending at lights
     * 
     */
    public static long getTotalDelayOfCombinatorialCircuit(Set<Switch> switches, Set<Light> lights) {
        Set<Input> stopInputs = new HashSet<Input>();
        for (Switch sw : switches) {
            Output output = sw.getOutputs().iterator().next();
            for(Wire wire : output.getWires()) {
                Input input = (Input) wire.getOther(output);
                stopInputs.add(input);
            }
        }
        
        Set<Output> startOutputs = new HashSet<Output>();
        for (Light light : lights) {
            Input input = light.getInputA();
            if (!input.getWires().isEmpty()) {
                Output output = (Output) input.getWires().iterator().next().getOther(input);

                // filter direct switch light connections
                if (!stopInputs.contains(input)) {
                    startOutputs.add(output);
                }
            }
        }
        
        return getTotalDelayHelper(startOutputs, stopInputs);
    }

    /**
     * Helper for {@link #getTotalDelayOfCombinatorialCircuit(Set, Set)}
     * 
     * @param startOutputs
     * @param stopInputs
     * @return
     */
    private static long getTotalDelayHelper(Set<Output> startOutputs, Set<Input> stopInputs) {
        long currentDelay = 0;

        for (Output output : startOutputs) {
            
            Brick currentBrick = output.getBrick();
            Set<Output> nextOutputs = new HashSet<Output>();
            for (Input input : currentBrick.getInputs()) {
                if (!stopInputs.contains(input) && input.getWires().size() != 0) {
                    nextOutputs.add((Output) input.getWires().iterator().next()
                            .getOther(input));
                }            
            }
            
            long myDelay = currentBrick.getDelay();
            
            if(currentBrick instanceof Component) {
                Component currentComponent = (Component) currentBrick;
                ComponentType currentType = currentComponent.getType();
                Log.gl().debug("Entering: " + currentComponent.getType().getName());
                myDelay += getTotalDelayHelper(currentType.getOutputs(), currentType.getInputs());
                Log.gl().debug("Leaveing: " + currentComponent.getType().getName());
            }
            
            currentDelay = max(currentDelay, myDelay + getTotalDelayHelper(nextOutputs, stopInputs));
            
        }
        
        return currentDelay;
    }
    
    public static Brick findBrickAtSamePos(Circuit c, Point p) {
        for(Element e: c.getElements()) {
            if (e instanceof Brick && e.getPosition().equals(p)) {
                return ((Brick) e);
            }
        }
        
        return null;
    }
    

}
