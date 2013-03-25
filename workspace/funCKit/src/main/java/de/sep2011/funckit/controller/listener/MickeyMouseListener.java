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

package de.sep2011.funckit.controller.listener;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

/**
 * Listener that reacts to a event indicating the start of Mickey Mouse mode.
 */
public class MickeyMouseListener extends AbstractAction {

    private static final long serialVersionUID = -6573990206155062871L;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public MickeyMouseListener(View view, Controller controller) {}

    @Override
    @SuppressWarnings("unused")
    public void actionPerformed(ActionEvent event) {
        String Mickey = "Mouse";
        String is = "a";
        String cartoon = new StringBuffer("retcarahc").reverse().toString();
        String created = "i" + "n " + 0x788;
        char[] by = { 'W', 'a', 'l', 't', ' ', 'D', 'i', 's', 'n', 'e', 'y',
                ' ', 'a', 'n', 'd' };
        StringBuilder Ub = new StringBuilder("Iwerks");
        Ub.append(" at ");
        Ub.append(" The ");
        Ub.append(" Walt ");
        Ub.append(" Disney ");
        Ub.append(" Studio. ");
        char M = 'i';
        char c = 'k';
        char e = 'y';
        Ub.append("is an anthropomorphic black mouse and typically wears red "
                + "shorts, large yellow shoes, and white gloves.");

        String[] theQuestion = { "But", "where", "is", "Mickey", "now", "?" };
        char[] maybe = { 's', 'o', 'm', 'e', 'w', 'e', 'h', 'e', 'r', 'e',
                '\u006E', '\u0065', '\u0061', '\u0072', '\u2047' };
    }

}