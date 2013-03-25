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

package de.sep2011.funckit.model.simulationmodel;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentTypeImpl;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for simulation states to easily receive {@link SimulationState}
 * objects by just passing a {@link Brick} object.
 */
class SimulationStateFactory {

    /**
     * Map from {@link Brick} class to a dummy {@link SimualtionState}, used to
     * create the right {@link SimulationState} for a {@link Brick}.
     */
    private static Map<Class<? extends Brick>, SimulationState> map = null;

    /**
     * Static constructor to initialize the factory.
     */
    static {
        init();
    }

    /**
     * Initializes the map from {@link Brick} to {@link SimulationState}.
     */
    private static void init() {
        map = new HashMap<Class<? extends Brick>, SimulationState>();
        map.put(Gate.class, new GateSimulationState(new And(new Rectangle())));
        map.put(SwitchImpl.class, new SwitchSimulationState(new SwitchImpl(
                new Rectangle())));
        map.put(And.class, new GateSimulationState(new And(new Rectangle())));
        map.put(Or.class, new GateSimulationState(new Or(new Rectangle())));
        map.put(Not.class, new GateSimulationState(new Not(new Rectangle())));
        map.put(IdPoint.class, new GateSimulationState(new IdPoint(
                new Rectangle())));
        map.put(Light.class, new LightSimulationState(
                new Light(new Rectangle())));
        map.put(Component.class, new ComponentSimulationState(
                new ComponentImpl(new ComponentTypeImpl(""))));
        map.put(ComponentImpl.class, new ComponentSimulationState(
                new ComponentImpl(new ComponentTypeImpl(""))));
    }

    /**
     * Creates a new specialized {@link SimulationState} for the given
     * {@link Brick}.
     * 
     * @param b
     *            {@link Brick} to create the {@link SimulationState} for. Has
     *            to be non null and has to be associated with a supported
     *            {@link SimulationState}.
     * @return New specialized {@link SimulationState} for the given
     *         {@link Brick}.
     */
    public static SimulationState getSimulationState(Brick b) {
        if (b == null) {
            throw new IllegalArgumentException();
        }

        SimulationState state = null;
        state = map.get(b.getClass());

        if (state != null) {
            state = state.create(b);
        } else {
            throw new IllegalArgumentException();
        }
        return state;
    }

}
