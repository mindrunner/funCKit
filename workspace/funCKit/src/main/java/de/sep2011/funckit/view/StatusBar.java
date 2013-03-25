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

package de.sep2011.funckit.view;

import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 * User can find messages, informations about bugs and informations about
 * simulation in the statusbar.
 */
public class StatusBar extends JLabel {

    private static final long serialVersionUID = 5690361747710189956L;

    /**
     * Create a new StatusBar.
     * 
     * @param view
     *            the associated {@link View} object
     */
    public StatusBar(View view) {
        super();
        initalize();
    }

    private void initalize() {
        setLayout(new MigLayout("align left, fill"));

    }

    public void setLabel(String text) {
        setText(" " + text);
    }

}
