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
import de.sep2011.funckit.drawer.LayoutText;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Action that modifies layout objects, if element is in an ghost-state (dragged
 * mode). E.g. this changes colors to gray or enables transparency on some
 * existing colors.
 */
public class GhostAction extends DefaultDrawAction {

    private Color ghostBorder;
    private Color ghostFill;
    private Color ghostText;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);

        ghostBorder = settings.get(Settings.GHOST_BORDER_COLOR,
                Color.class);
        ghostFill = settings.get(Settings.GHOST_FILL_COLOR, Color.class);

        assert ghostBorder != null; // make that sure in Application
        assert ghostFill != null; // make that sure in Application

        ghostText = settings.get(Settings.GHOST_TEXT_COLOR, Color.class);
        if (ghostText == null) {
            ghostText = ghostBorder;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Layout layout) {
        makeGhost(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input input, Layout layout) {
        makeGhost(layout);
        if (layout.getShape(Layout.INPUTS_SHAPE) != null) {
            layout.getShape(Layout.INPUTS_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        makeGhost(layout);
        if (layout.getShape(Layout.OUTPUTS_SHAPE) != null) {
            layout.getShape(Layout.OUTPUTS_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    /**
     * Assigns ghost colors to given layout (text, border and fill colors).
     *
     * @param layout Layout object to apply modifications on.
     */
    private void makeGhost(Layout layout) {
        for (LayoutText layoutText : layout.getTextList()) {
            layoutText.setColor(ghostText);
        }

        for (String shapeName : layout.getShapeMap().keySet()) {
            if (layout.getShape(shapeName) != null) {
                layout.getShape(shapeName).setBorderColor(ghostBorder);
                layout.getShape(shapeName).setFillColor(ghostFill);
            }
        }
    }
}
