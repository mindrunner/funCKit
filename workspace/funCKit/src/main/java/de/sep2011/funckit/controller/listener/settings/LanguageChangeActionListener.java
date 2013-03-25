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

import static de.sep2011.funckit.util.internationalization.Language.tr;
import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * Listener which listens to a Language Change request from the View.
 */
public class LanguageChangeActionListener implements ActionListener {

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
    public LanguageChangeActionListener(View view, Controller controller) {
        this.view = view;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Settings settings = controller.getSessionModel().getSettings();
        boolean oldAndNewTheSame = settings.getString(Settings.Language).equals(
                event.getActionCommand());

        settings.set(Settings.Language, event.getActionCommand());

        if (!oldAndNewTheSame
                && JOptionPane.showConfirmDialog(view.getMainRootPane(), tr("View.restartQuestion"),
                        tr("View.restartDialogTitle"), JOptionPane.YES_NO_OPTION) == 0) {
            view.rebuildView();
        }
    }
}
