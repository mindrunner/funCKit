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

package de.sep2011.funckit.model.graphmodel.implementations.commands;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.util.command.CommandDispatcher;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static de.sep2011.funckit.util.Log.gl;

class CircuitReorganizeUtility {

    private final Circuit circuit;
    private final List<Wire> knownWires = new LinkedList<Wire>();
    private final List<Wire> switchedWires = new LinkedList<Wire>();
    private final List<Brick> fixedBricks = new LinkedList<Brick>();
    private final List<IdPoint> knownIdPoints = new LinkedList<IdPoint>();

    private int lonelyBrickCount = 0;

    private final CommandDispatcher dispatcher;

    private CircuitReorganizeUtility(Circuit c, CommandDispatcher dispatcher) {
        this.circuit = c;
        this.dispatcher = dispatcher;
    }

    public static void reorganizeCircuit(Circuit circuit,
            CommandDispatcher disp, boolean hint) {
        if (hint) {
            new CircuitReorganizeUtility(circuit, disp).reorganize()
                    .removeLonelyBricks();
        }

    }

    boolean isLonelyBrick(Brick b) {
        for (AccessPoint accessPoint : b.getOutputs()) {
            if (accessPoint.getWires().size() > 0)
                return false;
        }

        for (AccessPoint accessPoint : b.getInputs()) {
            if (accessPoint.getWires().size() > 0)
                return false;
        }
        lonelyBrickCount++;
        return true;
    }

    private void removeLonelyBricks() {
        Set<Element> elements = new LinkedHashSet<Element>(
                circuit.getElements());
        for (Element e : elements) {
            if (e instanceof IdPoint) {
                IdPoint idPoint = (IdPoint) e;
                if (isLonelyBrick(idPoint)) {
                    dispatcher.dispatch(new RemoveBrickCommand(circuit, idPoint).setNotifyObserversHint(false));
                }
            }
        }
        gl().info("Removed " + this.lonelyBrickCount + " lonely IdPoints");
        lonelyBrickCount = 0;
    }

    private CircuitReorganizeUtility reorganize() {
        Set<Element> elements = new LinkedHashSet<Element>(
                circuit.getElements());

        // look for IdPoints to start reorganizing
        for (Element e : elements) {
            if (e instanceof IdPoint && !knownIdPoints.contains(e)
                    && !fixedBricks.contains(e)) {
                knownIdPoints.add((IdPoint) e);
                reorganizeWirePath((IdPoint) e); // reorganize the IdPoint
            }
        }
        gl().info("Reordered " + this.fixedBricks.size() + " connections");
        gl().info("Checked " + this.knownWires.size() + " wires");
        gl().info("Checked " + this.knownIdPoints.size() + " idpoints");
        return this;
    }

    private void reorganizeWirePath(IdPoint p) {
        List<Output> outputs = getGraphOutputs(p);
        
        // use this IdPoints Output if no other is found
        if (outputs.isEmpty()) {
        	outputs.add(p.getOutputO());
        }
        if (outputs.size() == 1) { // this seems to be a valid graph
            Output o = outputs.get(0);

            // reorganize all connected IdPoints to the found Output
            Set<Wire> wires = new LinkedHashSet<Wire>(o.getWires());
            for (Wire wire : wires) {
                Brick b = wire.getOther(o).getBrick();
                if (b instanceof IdPoint) {
                	fixWire(o, ((IdPoint)b).getInputA(), wire);
                	this.reorganizeWirePath((IdPoint) b, wire);
                }
            }

        } else { // more than 1 is invalid, and 0??
            // for now, do nothing, because reorganizeWirePath works only with
            // correct connected wires ;)
        }
    }
    
    private Wire fixWire(AccessPoint output, AccessPoint input, Wire w) {
        switchedWires.add(w);
		dispatcher.dispatch(new RemoveWireCommand(circuit, w)
				.setNotifyObserversHint(false));
		BareConnectCommand command = new BareConnectCommand(circuit, output, input);
		dispatcher.dispatch(command.setNotifyObserversHint(false));
		return command.getWire();
    }

    private void reorganizeWirePath(IdPoint p, Wire inputWire) {
    	
    	// switch all wires that are connected to the input expect the given wire
        AccessPoint inputPoint = p.getInputA();
        if (inputPoint.getWires().size() > 1) {
            Set<Wire> wires = new LinkedHashSet<Wire>(inputPoint.getWires());

            for (Wire wire : wires) {
                if (!wire.equals(inputWire)) {
                    Brick b = wire.getOther(inputPoint).getBrick();
                    if (b instanceof IdPoint) {
                        fixWire(p.getOutputO(), ((IdPoint) b).getInputA(), wire);
                    }
                }
            }
        }

        // goto next AccessPoints
        Set<Wire> wires = new LinkedHashSet<Wire>(p.getOutputO().getWires());

        for (Wire wire : wires) {
            AccessPoint nextAp = wire.getOther(p.getOutputO());
            if (nextAp.getBrick() instanceof IdPoint) {
                IdPoint idp = (IdPoint) nextAp.getBrick();
                if (!fixedBricks.contains(idp)) {
                    fixedBricks.add(idp);
                	wire = fixWire(p.getOutputO(), idp.getInputA(), wire);
                    reorganizeWirePath(idp, wire);
                }
            }
        }
    }

    private List<Output> getGraphOutputs(IdPoint point) {
    	
    	// search through the input
        List<Output> outputList = new LinkedList<Output>();
        for (Wire w : point.getInputA().getWires()) {
            if (!knownWires.contains(w)) {
                knownWires.add(w);
                AccessPoint ap = w.getOther(point.getInputA());
                if (ap.getBrick() instanceof IdPoint) {
                    outputList.addAll(getGraphOutputs((IdPoint) ap.getBrick()));
                } else if (ap instanceof Output){
                    outputList.add((Output) ap);
                }
            }
        }
        
        // search through the output
        for (Wire w : point.getOutputO().getWires()) {
            if (!knownWires.contains(w)) {
                knownWires.add(w);
                AccessPoint ap = w.getOther(point.getOutputO());
                if (ap.getBrick() instanceof IdPoint) {
                    outputList.addAll(getGraphOutputs((IdPoint) ap.getBrick()));
                } else if (ap instanceof Output){
                    outputList.add((Output) ap);
                }
            }
        }
        return outputList;
    }
}
