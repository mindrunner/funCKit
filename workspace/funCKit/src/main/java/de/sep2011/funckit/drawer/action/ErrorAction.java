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

package de.sep2011.funckit.drawer.action;

import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.model.sessionmodel.Settings;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Action, that is performed to mark an elements layout as defective.
 */
public class ErrorAction extends DefaultDrawAction {
    private Color errorColor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);

        errorColor = settings.get(Settings.ELEMENT_ERROR_BORDER_COLOR, Color.class);
        if (errorColor == null) {
            errorColor = Color.RED;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Layout layout) {
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setBorderColor(errorColor);
        }
    }
}
