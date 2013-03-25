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

package de.sep2011.funckit.model.sessionmodel;

import de.sep2011.funckit.model.graphmodel.Brick;
import javax.swing.UIManager;

import java.awt.Color;
import java.util.Locale;

import static de.sep2011.funckit.model.sessionmodel.Settings.ACCESS_POINT_SCATTER_FACTOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.DEFAULT_BRICK_HEIGHT;
import static de.sep2011.funckit.model.sessionmodel.Settings.DEFAULT_BRICK_ORIENTATION;
import static de.sep2011.funckit.model.sessionmodel.Settings.DEFAULT_BRICK_WIDTH;
import static de.sep2011.funckit.model.sessionmodel.Settings.DEFAULT_TIMER_DELAY;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_ACTIVE_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_ACTIVE_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_ERROR_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_INPUT_LABEL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_OUTPUT_LABEL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_SELECTED_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_SELECTED_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_SIMULATED_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_SIMULATED_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.ELEMENT_TYPE_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.EXPERT_MODE;
import static de.sep2011.funckit.model.sessionmodel.Settings.GATE_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.GHOST_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.GHOST_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.GRID_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.GRID_LOCK;
import static de.sep2011.funckit.model.sessionmodel.Settings.GRID_SIZE;
import static de.sep2011.funckit.model.sessionmodel.Settings.INPUT_RADIUS;
import static de.sep2011.funckit.model.sessionmodel.Settings.LIGHT_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.LOOK_AND_FEEL;
import static de.sep2011.funckit.model.sessionmodel.Settings.LOW_RENDERING_QUALITY_MODE;
import static de.sep2011.funckit.model.sessionmodel.Settings.Language;
import static de.sep2011.funckit.model.sessionmodel.Settings.MAXIMUM_COMMAND_QUEUE_SIZE;
import static de.sep2011.funckit.model.sessionmodel.Settings.OUTPUT_RADIUS;
import static de.sep2011.funckit.model.sessionmodel.Settings.SCROLL_SPEED;
import static de.sep2011.funckit.model.sessionmodel.Settings.SELECTION_BORDER_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.SELECTION_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.SHOW_TOOLTIPS;
import static de.sep2011.funckit.model.sessionmodel.Settings.SIMULATION_SLIDER_FACTOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.SWITCH_FILL_COLOR;
import static de.sep2011.funckit.model.sessionmodel.Settings.WIRE_SCATTER_FACTOR;

/**
 * Wrapper class that contains default settings, accessible from all over the
 * application. Adding new default settings is very simple: just set them in
 * static-area of <code>DefaultSettings</code> and assign them any value. They
 * are automatically applied on application startup, if they have no
 * user-defined value, yet.
 */
public class DefaultSettings {
    private static final Settings DEFAULT_SETTINGS = new Settings();

    static {
        /* Do not automatically save. */
        DEFAULT_SETTINGS.setAutosave(false);

        DEFAULT_SETTINGS.set(DEFAULT_BRICK_ORIENTATION, Brick.Orientation.WEST);
        DEFAULT_SETTINGS.set(DEFAULT_BRICK_WIDTH, 40);
        DEFAULT_SETTINGS.set(DEFAULT_BRICK_HEIGHT, 40);
        DEFAULT_SETTINGS.set(MAXIMUM_COMMAND_QUEUE_SIZE, 100);
        DEFAULT_SETTINGS.set(GRID_SIZE, 20);
        DEFAULT_SETTINGS.set(GRID_COLOR, new Color(220, 220, 220));
        DEFAULT_SETTINGS.set(SELECTION_BORDER_COLOR, new Color(100, 100, 255, 200));
        DEFAULT_SETTINGS.set(SELECTION_FILL_COLOR, new Color(150, 150, 255, 100));
        DEFAULT_SETTINGS.set(ELEMENT_BORDER_COLOR, new Color(0, 0, 0));
        DEFAULT_SETTINGS.set(ELEMENT_FILL_COLOR, new Color(107, 234, 201));
        DEFAULT_SETTINGS.set(ELEMENT_ERROR_BORDER_COLOR, Color.RED);
        DEFAULT_SETTINGS.set(ELEMENT_ACTIVE_BORDER_COLOR, new Color(20, 20, 20));
        DEFAULT_SETTINGS.set(ELEMENT_ACTIVE_FILL_COLOR, new Color(220, 220, 255));
        DEFAULT_SETTINGS.set(ELEMENT_SELECTED_BORDER_COLOR, new Color(50, 50, 200));
        DEFAULT_SETTINGS.set(ELEMENT_SELECTED_FILL_COLOR, new Color(220, 220, 255));
        DEFAULT_SETTINGS.set(ELEMENT_SIMULATED_BORDER_COLOR, new Color(0, 0, 0));
        DEFAULT_SETTINGS.set(ELEMENT_SIMULATED_FILL_COLOR, new Color(222, 255, 50));
        DEFAULT_SETTINGS.set(GATE_FILL_COLOR, new Color(146, 230, 129));
        DEFAULT_SETTINGS.set(LIGHT_FILL_COLOR, new Color(160, 200, 200));
        DEFAULT_SETTINGS.set(SWITCH_FILL_COLOR, new Color(160, 200, 200 ));
        DEFAULT_SETTINGS.set(ELEMENT_INPUT_LABEL_COLOR, new Color(135, 0, 84));
        DEFAULT_SETTINGS.set(ELEMENT_OUTPUT_LABEL_COLOR, new Color(135, 0, 84));
        DEFAULT_SETTINGS.set(SCROLL_SPEED, 20);
        DEFAULT_SETTINGS.set(LOOK_AND_FEEL, UIManager.getSystemLookAndFeelClassName());
        DEFAULT_SETTINGS.set(Language, Locale.getDefault().getLanguage());
        DEFAULT_SETTINGS.set(GRID_LOCK, true);
        DEFAULT_SETTINGS.set(LOW_RENDERING_QUALITY_MODE, false);
        DEFAULT_SETTINGS.set(DEFAULT_TIMER_DELAY, 1000);
        DEFAULT_SETTINGS.set(SHOW_TOOLTIPS, true);
        DEFAULT_SETTINGS.set(ACCESS_POINT_SCATTER_FACTOR, 15);
        DEFAULT_SETTINGS.set(WIRE_SCATTER_FACTOR, 7);
        DEFAULT_SETTINGS.set(GHOST_BORDER_COLOR, Color.GRAY);
        DEFAULT_SETTINGS.set(GHOST_FILL_COLOR, new Color(1, 1, 1, 0.5f));
        DEFAULT_SETTINGS.set(ELEMENT_TYPE_COLOR, new Color(100, 0, 100));
        DEFAULT_SETTINGS.set(ELEMENT_INPUT_LABEL_COLOR, new Color(100, 0, 100));
        DEFAULT_SETTINGS.set(ELEMENT_OUTPUT_LABEL_COLOR, new Color(100, 0, 100));
        DEFAULT_SETTINGS.set(EXPERT_MODE, false);
        DEFAULT_SETTINGS.set(SIMULATION_SLIDER_FACTOR, 20.0);
        DEFAULT_SETTINGS.set(INPUT_RADIUS, 2);
        DEFAULT_SETTINGS.set(OUTPUT_RADIUS, 2);
    }

    /**
     * Returns the default settings instance.
     *
     * @return the default settings instance.
     */
    public static Settings getDefaultSettings() {
        return DEFAULT_SETTINGS;
    }

}
