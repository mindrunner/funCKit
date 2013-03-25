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

package de.sep2011.funckit.controller.listener.settings;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener to perform enable/disable of real time Validation.
 */
public class RealTimeValidationOnOffActionListener implements ActionListener {

    private final View view;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param view
     *            associated View object, should not be null
     */
    public RealTimeValidationOnOffActionListener(View view) {
        this.view = view;
    }

    /**
     * Method to toggle real time validation during editing.
     * 
     * @param event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Settings settings = view.getSessionModel().getSettings();
        Boolean current = settings.getBoolean(Settings.REALTIME_VALIDATION);
        settings.set(Settings.REALTIME_VALIDATION, !current);
    }
}
