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

import de.sep2011.funckit.observer.FunckitObservable;
import de.sep2011.funckit.observer.SimulationModelInfo;
import de.sep2011.funckit.observer.SimulationModelObserver;

/**
 * Represents the simulation of a certain circuit. Concrete implementations of a
 * simulation must generate the next discrete step of the simulation, create
 * snapshots of the current state, restore the simulation with a former snapshot
 * and return simulation states of specific bricks.
 */
public interface Simulation extends
        FunckitObservable<SimulationModelObserver, SimulationModelInfo> {

    /**
     * Calculates exactly one simulation step on the circuit (one discrete step
     * without delay).
     */
    void nextStep();

    /**
     * Create snapshot from current simulation state.
     * 
     * @return snapshot from current simulation state.
     */
    Simulation createSnapshot();

    /**
     * Restore former simulation state from the given snapshot.
     * 
     * @param snapshot
     *            snapshot to restore from. Has to be non null.
     */
    void restoreFromSnapshot(Simulation snapshot);

    /**
     * Returns simulated state of a specific {@link SimulationBrick}. If
     * currently no {@link SimulationState} for the {@link SimulationBrick}
     * exists a new initial one is created, associated and returned.
     * 
     * @param identifier
     *            the SimulationBrick to get the SimulationState for.
     * @return The SimulationState of the given SimulationBrick
     */
    SimulationState getSimulationState(SimulationBrick identifier);

}