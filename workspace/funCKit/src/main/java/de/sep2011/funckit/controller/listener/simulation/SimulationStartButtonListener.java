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

package de.sep2011.funckit.controller.listener.simulation;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationImpl;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.SimulationValidatorFactory;
import de.sep2011.funckit.validator.Validator;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.util.List;

import static de.sep2011.funckit.util.Log.gl;

/**
 * Listener object for action that is triggered to start simulation on current
 * circuit object.
 */
public class SimulationStartButtonListener extends AbstractAction {

    private static final long serialVersionUID = -8158627482224783866L;
    private final Controller controller;
    private final View view;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public SimulationStartButtonListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Validator simulationValidator = (new SimulationValidatorFactory())
                .getValidator();
        Project project = controller.getSessionModel().getCurrentProject();
        if (project == null) {
            return;
        }
        Circuit circuit = project.getCircuit();
        if (circuit == null) {
            return;
        }
        List<Result> res = simulationValidator.validate(circuit);

        controller.getSessionModel().getCurrentProject().setCheckResults(res);

        if (simulationValidator.allPassed()) {
            project.getSimulationCommandDispatcher().clear();
            Simulation simulation = new SimulationImpl(circuit);
            controller.getSessionModel().getCurrentProject()
                    .setSimulation(simulation);
            controller.enterSimulationMode();
        } else {
        	view.showValidatorResults(res);
            gl().debug("Simulation validation failed.");
        }
    }
}