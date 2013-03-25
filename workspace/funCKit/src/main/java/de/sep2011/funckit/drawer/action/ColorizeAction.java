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
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Action to initialize standard colors for {@link Layout.BASE_SHAPE} border
 * and fill.
 */
public class ColorizeAction extends DefaultDrawAction {
    private static final Color COLOR_WARN = Color.PINK;
    private Color defaultFillColor;
    private Color defaultBorderColor;
    private Color lightBorderColor;
    private Color lightFillColor;
    private Color switchBorderColor;
    private Color switchFillColor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);

        /* Cache default element fill color. */
        defaultFillColor = settings
                .get(Settings.ELEMENT_FILL_COLOR, Color.class);
        if (defaultFillColor == null) {
            defaultFillColor = COLOR_WARN; // For notice on UI
        }

        /* Cache default element border color. */
        defaultBorderColor = settings.get(Settings.ELEMENT_BORDER_COLOR,
                Color.class);
        if (defaultBorderColor == null) {
            defaultBorderColor = COLOR_WARN; // For notice on UI
        }

        /* Cache light border color. */
        lightBorderColor = settings.get(Settings.LIGHT_BORDER_COLOR,
                Color.class);
        if (lightBorderColor == null) {
            lightBorderColor = defaultBorderColor;
        }

        /* Cache light fill color. */
        lightFillColor = settings.get(Settings.LIGHT_FILL_COLOR,
                Color.class);
        if (lightFillColor == null) {
            lightFillColor = defaultFillColor;
        }

        /* Cache switch border color. */
        switchBorderColor = settings.get(Settings.SWITCH_BORDER_COLOR,
                Color.class);
        if (switchBorderColor == null) {
            switchBorderColor = defaultBorderColor;
        }

        /* Cache switch fill color. */
        switchFillColor = settings.get(Settings.SWITCH_FILL_COLOR,
                Color.class);
        if (switchFillColor == null) {
            switchFillColor = defaultFillColor;
        }
    }

    /**
     * Specifies default color for all elements.
     *
     * @param layout With base shape filled layout object.
     */
    @Override
    public void prepare(Layout layout) {
        /* Define colors, if shape is given. */
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setBorderColor(defaultBorderColor);
            layout.getShape(Layout.BASE_SHAPE).setFillColor(defaultFillColor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Wire wire, Layout layout) {
        Color alternativeColor = settings.get(Settings.ELEMENT_BORDER_COLOR, Color.class);
        if (alternativeColor == null) {
            alternativeColor = COLOR_WARN;
        }
        setBorderColor(layout, Settings.WIRE_COLOR, alternativeColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(IdPoint idPoint, Layout layout) {
        Color alternativeColor = settings.get(Settings.ELEMENT_BORDER_COLOR, Color.class);
        if (alternativeColor == null) {
            alternativeColor = COLOR_WARN;
        }
        setBorderColor(layout, Settings.WIRE_COLOR, alternativeColor);

        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {
        prepareGate(and, layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        prepareGate(or, layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        prepareGate(not, layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Light light, Layout layout) {
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setBorderColor(lightBorderColor);
            layout.getShape(Layout.BASE_SHAPE).setFillColor(lightFillColor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setBorderColor(switchBorderColor);
            layout.getShape(Layout.BASE_SHAPE).setFillColor(switchFillColor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input input, Layout layout) {
        if (layout.getShape(Layout.INPUTS_SHAPE) != null) {
            layout.getShape(Layout.INPUTS_SHAPE).setBorderColor(defaultBorderColor);
            layout.getShape(Layout.INPUTS_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        if (layout.getShape(Layout.OUTPUTS_SHAPE) != null) {
            layout.getShape(Layout.OUTPUTS_SHAPE).setBorderColor(defaultBorderColor);
            layout.getShape(Layout.OUTPUTS_SHAPE).setFillColor(DrawUtil.TRANSPARENT);
        }
    }

    private void prepareGate(Gate gate, Layout layout) {
        Color alternativeColor = settings.get(Settings.ELEMENT_BORDER_COLOR, Color.class);
        if (alternativeColor == null) {
            alternativeColor = COLOR_WARN;
        }
        setBorderColor(layout, Settings.GATE_BORDER_COLOR, alternativeColor);

        alternativeColor = settings.get(Settings.ELEMENT_FILL_COLOR, Color.class);
        if (alternativeColor == null) {
            alternativeColor = COLOR_WARN;
        }
        setFillColor(layout, Settings.GATE_FILL_COLOR, alternativeColor);
    }

    /**
     * Sets border color of {@link Layout.BASE_SHAPE} of given {@link Layout}
     * object to color, that is configured with given settings key and falls
     * back to given default color, if there is no color in settings specified.
     *
     * @param layout           Layout object to modify.
     * @param colorSettingsKey Keyword to search in settings for configured
     *                         color.
     * @param defaultColor     Alternative color object.
     */
    private void setBorderColor(Layout layout, String colorSettingsKey, Color defaultColor) {
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            Color color = settings.get(colorSettingsKey, Color.class);
            if (color == null) {
                color = settings.get(colorSettingsKey, Color.class);
                if (color == null) {
                    color = defaultColor;
                }
            }
            layout.getShape(Layout.BASE_SHAPE).setBorderColor(color);
        }
    }

    /**
     * Sets fill color of {@link Layout.BASE_SHAPE} of given {@link Layout}
     * object to color, that is configured with given settings key and falls
     * back to given default color, if there is no color in settings specified.
     *
     * @param layout           Layout object to modify.
     * @param colorSettingsKey Keyword to search in settings for configured
     *                         color.
     * @param defaultColor     Alternative color object.
     */
    private void setFillColor(Layout layout, String colorSettingsKey, Color defaultColor) {
        if (layout.getShape(Layout.BASE_SHAPE) != null) {
            Color color = settings.get(colorSettingsKey, Color.class);
            if (color == null) {
                color = settings.get(colorSettingsKey, Color.class);
                if (color == null) {
                    color = defaultColor;
                }
            }
            layout.getShape(Layout.BASE_SHAPE).setFillColor(color);
        }
    }
}
