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

package de.sep2011.funckit.observer;

/**
 * This Class stores infos for the {@link SimulationModelObserver}s notify
 * method. Create an empty Instance with {@link #getInfo()}.
 */
public class SimulationModelInfo extends Info<SimulationModelInfo> {

    private boolean simulationChanged;

    private SimulationModelInfo() {
    }

    /**
     * Factory Method to get a new instance of this Info where every property is
     * false or unset.
     * 
     * @return the new instance
     */
    public static SimulationModelInfo getInfo() {
        return new SimulationModelInfo();
    }

    /**
     * Set this to true if the simulation changed.
     * 
     * @param changed
     *            true or false
     * @return this for convenience
     */
    public SimulationModelInfo setSimulationChanged(boolean changed) {
        simulationChanged = changed;
        return this;
    }

    /**
     * Returns the value set by {@link #setSimulationChanged(boolean)}.
     * 
     * @return the value set by {@link #setSimulationChanged(boolean)}
     */
    public boolean isSimulationChanged() {
        return simulationChanged;
    }

    @Override
    public SimulationModelInfo getNewInstance() {
        return getInfo();
    }
}
