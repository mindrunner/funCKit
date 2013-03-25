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

import java.util.Deque;
import java.util.LinkedList;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;

public class SimulationUtil {

    /**
     * Helper method to receive the non-component-brick from a {@link
     * SimulationBrick}, connected to the given output. This is needed as the
     * connected brick may be a component, containing a further brick, that is a
     * component again. To receive the non-component-brick connected to the
     * given output, this method looks that brick recursively up.
     * 
     * @param simBrick
     * @param o
     * @return a Pair<SimulationBrick, Output>
     */
    public static Pair<SimulationBrick, Output> getNonComponent(
            SimulationBrick simBrick, Output o) {
        Brick b = simBrick.getBrick();
        if (b instanceof Component) {
            Component comp = (Component) b;
            Output inner = comp.getInnerOutput(o);
            Deque<Component> stack = new LinkedList<Component>(
                    simBrick.getStack());
            stack.push(comp);
            simBrick = new SimulationBrick(inner.getBrick(), stack);
            return getNonComponent(simBrick, inner);
        }
        return new Pair<SimulationBrick, Output>(simBrick, o);
    }
}
