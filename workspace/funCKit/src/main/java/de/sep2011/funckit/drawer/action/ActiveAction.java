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
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Color;

/**
 * Action invoked to modify a layout in a way that user recognize it as an
 * active element. E.g. overwrites its colors. This must happen in each
 * <code>prepare()</code>-method to overwrite previous actions!
 */
public class ActiveAction extends DefaultDrawAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Layout layout) {
        setActive(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        if (layout.getShape(Layout.OUTPUTS_SHAPE) != null) {
            layout.getShape(Layout.OUTPUTS_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input output, Layout layout) {
        if (layout.getShape(Layout.INPUTS_SHAPE) != null) {
            layout.getShape(Layout.INPUTS_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    /**
     * Sets layout to an active state by changing its standard colors.
     *
     * @param layout Layout to be modified.
     */
    private void setActive(Layout layout) {
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            Color activeColor = settings.get(Settings.ELEMENT_ACTIVE_BORDER_COLOR,
                    Color.class);
            Color fillColor = settings.get(Settings.ELEMENT_ACTIVE_FILL_COLOR,
                    Color.class);
            if (activeColor != null) {
                layout.getShape(Layout.BASE_SHAPE).setBorderColor(activeColor);
            }
            if (fillColor != null) {
                layout.getShape(Layout.BASE_SHAPE).setFillColor(fillColor);
            }
        }
    }
}
