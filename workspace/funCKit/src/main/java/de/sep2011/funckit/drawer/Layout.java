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

package de.sep2011.funckit.drawer;

import de.sep2011.funckit.model.graphmodel.Output;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * A <code>Layout</code> object is descriptive bundle of information for drawers
 * how to paint the associated element.
 */
public class Layout {
    public final static Color DEBUG_BORDER_COLOR = Color.CYAN;
    public final static Color DEBUG_FILL_COLOR = Color.PINK;
    public final static String BASE_SHAPE = "baseShape";
    public final static String INPUTS_SHAPE = "inputsShape";
    public final static String OUTPUTS_SHAPE = "outputsShape";
    public static final String QUEUE_SHAPE = "queueShape";


    /* Information for simulated objects. */

    /**
     * Simulation state of associated element (true for logic-1 and false for
     * logic-0).
     */
    private boolean simulationState = false;

    /**
     * Map of {@see Output} to their associated queue of simulation values. Only
     * filled if {@see Layout#simulationState} is set to true. May not be null
     * if {@see Layout#simulationState} is true.
     */
    private Map<Output, Queue<Boolean>> outputQueueMap = new LinkedHashMap<Output, Queue<Boolean>>();

    /* Information for text */

    /**
     * List of objects, that represent capsuled texts with their needed
     * information about font, size etc.
     */
    private final List<LayoutText> textList = new LinkedList<LayoutText>();

    private final Map<String, LayoutShape> shapeMap = new LinkedHashMap<String, LayoutShape>();

    /**
     * Remove all texts applied on this layout object.
     */
    public void clearTextList() {
        this.textList.clear();
    }

    /**
     * Add a new {@link LayoutText} object to this element layout.
     *
     * @param text Descriptive object for text, that should be added to this
     *             layout.
     */
    public void addText(LayoutText text) {
        this.textList.add(text);
    }

    /**
     * Getter for {@link Layout#textList}. Only used for reading. Do not apply
     * changes on that list, as this method returns reference and not a copy!
     *
     * @return list of LayoutText
     */
    public List<LayoutText> getTextList() {
        return textList;
    }

    /**
     * Clears all assigned shapes of this layout.
     */
    public void clearShapeList() {
        this.shapeMap.clear();
    }

    /**
     * Assigns given shape with specified name.
     *
     * @param name  Name for mapping and receiving given shape.
     * @param shape Shape to add to this layout.
     */
    public void putShape(String name, LayoutShape shape) {
        this.shapeMap.put(name, shape);
    }

    /**
     * Getter method for a certain shape, that was previously assigned to this
     * layout. May be null.
     *
     * @param name Name of shape in map.
     * @return Associated {@link LayoutShape}.
     */
    public LayoutShape getShape(String name) {
        return shapeMap.get(name);
    }

    /**
     * Getter for {@link Layout#shapeMap}. Should only used for reading access,
     * but not for writing access. As this method returns reference and not a
     * copy, you should not apply changes on it, as it takes affect on internal
     * structures of this {@link Layout} object.
     *
     * @return Map of names of shapes with their associated shape description
     *         objects.
     */
    public Map<String, LayoutShape> getShapeMap() {
        return this.shapeMap;
    }

    /**
     * Specifies simulation state of associated element of this layout.
     *
     * @param active Boolean value for logic-0 or logic-1.
     */
    public void setSimulationState(boolean active) {
        simulationState = active;
    }

    /**
     * Getter method for simulation state.
     *
     * @return True, iff associated element is logic-1.
     */
    public boolean isSimulationState() {
        return simulationState;
    }

    /**
     * Injection method for a map of outputs and their associated queue of
     * stored simulation values. Used for displaying on UI in simulation mode.
     * May be null.
     *
     * @param outputQueueMap Map of {@link Output}s to their current simulation
     *                       queues.
     */
    public void setOutputQueueMap(Map<Output, Queue<Boolean>> outputQueueMap) {
        this.outputQueueMap = outputQueueMap;
    }

    /**
     * Getter method for injected output queue map.
     *
     * @return Map of outputs mapped to their simulation queues.
     */
    public Map<Output, Queue<Boolean>> getOutputQueueMap() {
        return outputQueueMap;
    }
}