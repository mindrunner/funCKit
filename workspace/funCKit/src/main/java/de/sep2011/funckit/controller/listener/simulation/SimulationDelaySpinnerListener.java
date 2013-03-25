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
import de.sep2011.funckit.view.View;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This Listener is notified on events indicating that the simulation should run
 * faster.
 */
public class SimulationDelaySpinnerListener implements ChangeListener {

    private final Controller controller;
    private final JSpinner spinner;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param spinner
     * @param view
     *            associated View object, should not be null
     */
    public SimulationDelaySpinnerListener(View view, Controller controller,
            JSpinner spinner) {
        this.controller = controller;
        this.spinner = spinner;
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        int delay = (Integer) spinner.getValue();
        if (delay > 0) {
            controller.getSessionModel().getCurrentProject()
                    .setTimerDelay(delay);
            SimulationForwardButtonListener.doNextStep(controller);
        }
    }

}