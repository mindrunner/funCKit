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

package de.sep2011.funckit.model.simulationmodel.commands;

import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.command.Command;

/**
 * Performs next {@link Simulation} calculation step and undoes it.
 */
public class SimulateCommand extends Command {

    /**
     * The {@link Simulation} this command performs on.
     */
    private final Simulation simulation;

    /**
     * This is used to save the state the {@link Simulation} was before
     * executing this command.
     */
    private final Simulation oldState;

    /**
     * This is used to save the state the {@link Simulation} is after this
     * command was executed.
     */
    private Simulation newState;

    /**
     * This constructor initializes the command with the given
     * {@link Simulation} Creating this Command causes an immediate creation of
     * the simulation snapshot.
     * 
     * @param simulation
     *            The {@link Simulation} this command performs on. Has to be non
     *            null.
     */
    public SimulateCommand(Simulation simulation) {
        this.simulation = simulation;
        oldState = simulation.createSnapshot();
        newState = null;
    }

    /**
     * {@inheritDoc} If this method is called the first time the current
     * {@link Simulation} state is saved.
     */
    @Override
    public void execute() {
        checkAndUpdateExecutedOnExecute();

        if (newState != null) {
            simulation.restoreFromSnapshot(newState);
            Log.gl().debug("did next step from snapshot");
        } else {
            simulation.nextStep();
            Log.gl().debug("Did next simulation step");
        }
    }

    /**
     * {@inheritDoc} Restores old simulation state, that was previously
     * calculated and overwritten. {@link SimulateCommand#execute()} has to be
     * called before.
     */
    @Override
    public void undo() {
        checkAndUpdateExecutedOnUndo();

        newState = simulation.createSnapshot();
        simulation.restoreFromSnapshot(oldState);
        Log.gl().debug("undone simulation step");
    }
}
