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

package de.sep2011.funckit.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSpinner;

/**
 * A {@link MouseWheelListener} which can be added to a {@link JSpinner} so the
 * {@link JSpinner} can be controlled with the mouse wheel.
 */
public class SpinnerWheelListener extends MouseAdapter {

    private final JSpinner sp;

    /**
     * Creates a new {@link SpinnerWheelListener}.
     * 
     * @param sp
     *            the associated {@link JSpinner}
     */
    public SpinnerWheelListener(JSpinner sp) {
        this.sp = sp;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rot = e.getWheelRotation();
        rot *= e.isControlDown() ? 2 : 1;

        if (rot < 0) {
            for (int i = -rot; i > 0; i--) {
                Object next = sp.getNextValue();
                if (next != null) {
                    sp.setValue(next);
                }
            }
        } else if (rot > 0) {
            for (int i = rot; i > 0; i--) {
                Object prev = sp.getPreviousValue();
                if (prev != null) {
                    sp.setValue(prev);
                }
            }
        }

    }
}