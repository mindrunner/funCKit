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

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.observer.AbstractObservable;
import de.sep2011.funckit.observer.SettingsInfo;
import de.sep2011.funckit.observer.SettingsObserver;
import de.sep2011.funckit.util.Log;
import net.iharder.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Settings are application properties (or configuration) that should - in
 * contrast to data from {@link SessionModel} - be persisted.
 */
public class Settings extends AbstractObservable<SettingsObserver, SettingsInfo> {

    /**
     * Must not be null.
     */
    public static final String DEFAULT_BRICK_ORIENTATION = "brick.defaultOrientation";

    /**
     * Must not be null.
     */
    public static final String DEFAULT_BRICK_WIDTH = "brick.defaultWidth";

    /**
     * Must not be null.
     */
    public static final String DEFAULT_BRICK_HEIGHT = "brick.defaultHeight";

    /**
     * Must not be null. Specifies maximum length of command dispatcher.
     */
    public static final String MAXIMUM_COMMAND_QUEUE_SIZE = "command.maximumQueueSize";

    /**
     * Must not be null.
     */
    public static final String GRID_SIZE = "ui.gridSize";

    /**
     * Must not be null.
     */
    public static final String GRID_COLOR = "ui.gridColor";

    /**
     * Must not be null. Specifies if docking to grid should be enabled.
     */
    public static final String GRID_LOCK = "ui.gridLock";

    /**
     * Must not be null. Specifies if grid should be displayed.
     */
    public static final String SHOW_GRID = "ui.gridToggle";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_FILL_COLOR = "ui.element.fillColor";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_BORDER_COLOR = "ui.element.borderColor";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_ERROR_BORDER_COLOR = "ui.element.error.borderColor";

    /**
     * Must not be null. Border color for active elements.
     */
    public static final String ELEMENT_ACTIVE_BORDER_COLOR = "ui.element.active.borderColor";

    /**
     * Must not be null. Fill color for active elements.
     */
    public static final String ELEMENT_ACTIVE_FILL_COLOR = "ui.element.active.fillColor";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_SELECTED_BORDER_COLOR = "ui.element.selected.borderColor";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_SELECTED_FILL_COLOR = "ui.element.selected.fillColor";

    /**
     * May be null.
     */
    public static final String ELEMENT_INFO_BORDER_COLOR = "ui.element.info.borderColor";

    /**
     * May be null.
     */
    public static final String ELEMENT_INFO_FILL_COLOR = "ui.element.info.fillColor";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_SIMULATED_BORDER_COLOR = "ui.element.simulated.borderColor";

    /**
     * Must not be null.
     */
    public static final String ELEMENT_SIMULATED_FILL_COLOR = "ui.element.simulated.fillColor";

    /**
     * May be null. Extra color for wires if it should differ from element
     * border color.
     */
    public static final String WIRE_COLOR = "ui.wire.color";

    /**
     * May be null. Extra color for gates if it should differ from element
     * border color.
     */
    public static final String GATE_BORDER_COLOR = "ui.brick.borderColor";

    /**
     * May be null. Extra fill color for gates if it should differ from.
     */
    public static final String GATE_FILL_COLOR = "ui.brick.fillColor";

    /**
     * Must not be null.
     */
    public static final String GHOST_BORDER_COLOR = "ui.element.ghost.borderColor";

    public static final String GHOST_TEXT_COLOR = "ui.element.ghost.textColor";

    /**
     * Must not be null.
     */
    public static final String GHOST_FILL_COLOR = "ui.element.ghost.fillColor";

    public static final String LIGHT_BORDER_COLOR = "ui.light.borderColor";
    public static final String LIGHT_FILL_COLOR = "ui.light.fillColor";
    public static final String SWITCH_BORDER_COLOR = "ui.switch.borderColor";
    public static final String SWITCH_FILL_COLOR = "ui.switch.fillColor";

    /**
     * Must not be null.
     */
    public static final String SELECTION_FILL_COLOR = "ui.selection.fillColor";

    /**
     * Must not be null.
     */
    public static final String SELECTION_BORDER_COLOR = "ui.selection.borderColor";

    /**
     * Must not be null.
     */
    public static final String SHOW_TOOLTIPS = "ui.drawToolTips";

    /**
     * Must not be null.
     */
    public static final String REALTIME_VALIDATION = "RealTimeValidation";

    /**
     * Must not be null.
     */
    public static final String OPENED_PROJECTS = "openedProjects";

    /**
     * Must not be null.
     */
    public static final String LOOK_AND_FEEL = "swingLookAndFeel";

    /**
     * Must not be null.
     */
    public static final String SIMULATION_UNDO_ENABLED = "simulation.undoEnabled";

    /**
     * Must not be null.
     */
    public static final String SCROLL_SPEED = "ui.scrollSpeed";

    /**
     * Must not be null.
     */
    public static final String Language = "language";

    /**
     * Must not be null.
     */
    public static final String MMMode = "mmmode";

    /**
     * Render in low quality.
     */
    public static final String LOW_RENDERING_QUALITY_MODE = "rendering.lowQuality";
    /**
     * Default delay for the simulation timer.
     */
    public static final String DEFAULT_TIMER_DELAY = "timer.defaultDelay";
    /**
     * Scatter factor for clicking onto {@link AccessPoint}s.
     */
    public static final String ACCESS_POINT_SCATTER_FACTOR = "tool.accessPointScatterFactor";
    /**
     * Scatter factor for clicking onto {@link Wire}s.
     */
    public static final String WIRE_SCATTER_FACTOR = "tool.wireScatterFactor";
    public static final String ELEMENT_TYPE_COLOR = "ui.element.type.color";
    public static final String ELEMENT_INPUT_LABEL_COLOR = "ui.element.input.label.color";
    public static final String ELEMENT_OUTPUT_LABEL_COLOR = "ui.element.output.label.color";
    public static final String EXPERT_MODE = "funckit.expertMode";
    public static final String SIMULATION_SLIDER_FACTOR = "toolbar.simulationSliderFactor";
    public static final String WINDOW_BOUNDS = "window_bounds";
    public static final String QUEUE_BORDER_COLOR = "ui.element.simulated.queue.borderColor";
    public static final String QUEUE_FILL_COLOR = "ui.element.simulated.queue.fillColor";
    public static final String QUEUE_TEXT_FONT = "ui.element.simulated.queue.font";
    public static final String QUEUE_TEXT_COLOR = "ui.element.simulated.queue.textColor";
    public static final String QUEUE_MARGIN = "ui.element.simulated.queue.margin";
    /**
     * Must not be null.
     */
    public static final String INPUT_RADIUS = "ui.element.input.radius";

    /**
     * Must not be null.
     */
    public static final String OUTPUT_RADIUS = "ui.element.output.radius";

    /**
     * May be null.
     */
    public static final String OUTPUT_FONT = "ui.element.output.font";

    /**
     * May be null.
     */
    public static final String ELEMENT_DELAY_FONT = "ui.element.delay.font";

    /**
     * May be null.
     */
    public static final String ELEMENT_DELAY_COLOR = "ui.element.delay.color";

    /**
     * May be null.
     */
    public static final String ELEMENT_NAME_FONT = "ui.element.name.font";

    /**
     * May be null.
     */
    public static final String ELEMENT_NAME_COLOR = "ui.element.name.color";

    /**
     * Path to associated persistence file.
     */
    private String settingsFile;

    /**
     * Object, settings are stored in (to decouple persistence file and direct
     * access).
     */
    private final Properties properties;

    /**
     * Flag to determine if changed settings should be saved automatically.
     */
    private boolean autosave;

    /**
     * Create a new {@link Settings} object.
     */
    public Settings() {
        properties = new Properties();
        initInfo(SettingsInfo.getInfo());
        setAutosave(false);
    }

    /**
     * Create a new {@link Settings} object using a File.
     *
     * @param settingsFile the path to the settings File.
     */
    public Settings(String settingsFile) {
        this.settingsFile = settingsFile;
        properties = new Properties();

        initInfo(SettingsInfo.getInfo());
        reload();
    }

    /**
     * Remove a setting.
     *
     * @param key setting to remove
     */
    public void remove(String key) {
        properties.remove(key);
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * (Re-)loads settings from file stream.
     *
     * @return true if successful, else false
     */
    public boolean reload() {
        if (settingsFile == null) {
            Log.gl().warn("Can not load settings of uninitialized settings object.");
            return false;
        }

        Log.gl().info("Loading settings ..");
        try {
            InputStream is = new FileInputStream(settingsFile);
            properties.loadFromXML(is);
            Log.gl().info("Settings loaded!");
            return true;
        } catch (FileNotFoundException e) {
            Log.gl().info("Settings file '" + settingsFile + "' does not exist, yet.");
        } catch (InvalidPropertiesFormatException e) {
            Log.gl().error("Settings has '" + settingsFile + "' invalid format.");
        } catch (IOException e) {
            Log.gl().error("Could not save settings.");
        }

        return false;
    }

    /**
     * Returns flag if automatic saving is enabled.
     *
     * @return true if automatic saving is enabled.
     */
    public boolean isAutosave() {
        return autosave;
    }

    /**
     * Specifies if automatic saving should be enabled or disabled.
     *
     * @param autosave True iff automatic save should be enabled.
     */
    public void setAutosave(boolean autosave) {
        this.autosave = autosave;
        if (autosave && hasChanged()) {
            save();
        }
    }

    /**
     * Wrapper for save-method to save only if autosave is enabled.
     */
    private void autosave() {
        if (autosave) {
            save();
        }
    }

    /**
     * Initializes saving settings from property object to settings file.
     *
     * @return true if save was successful
     */
    public boolean save() {
        if (settingsFile == null) {
            Log.gl().warn("Can not save settings for uninitialized settings object.");
            return false;
        }

        Log.gl().info("Saving settings ..");
        try {
            OutputStream outputStream = new FileOutputStream(settingsFile);
            properties.storeToXML(outputStream, "Last updated " + new java.util.Date(), "UTF-8");
            outputStream.close();
            Log.gl().info("Settings saved!");
            return true;
        } catch (FileNotFoundException e) {
            Log.gl().error("Settings file '" + settingsFile + "' not found for saving.");
        } catch (IOException e) {
            Log.gl().error("Could not save settings.");
        }

        return false;
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting.
     * @param value a {@link Serializable} object
     */
    public <T> void set(String key, T value) {
        assert value != null;
        assert value instanceof Serializable;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(value);
            outputStream.close();
            properties.setProperty(key, Base64.encodeBytes(outputStream.toByteArray()));
            autosave();
        } catch (IOException e) {
            Log.gl().warn("Catched IOException in setSetting(" + key + ", " + value + ")");
        }

        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting.
     * @param value a String
     */
    public void set(String key, String value) {
        assert value != null;
        properties.setProperty(key, value);
        autosave();
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting.
     * @param value a boolean
     */
    public void set(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
        autosave();
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting.
     * @param value an int
     */
    public void set(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
        autosave();
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting.
     * @param value a double
     */
    public void set(String key, double value) {
        properties.setProperty(key, String.valueOf(value));
        autosave();
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting.
     * @param value a long
     */
    public void set(String key, long value) {
        properties.setProperty(key, String.valueOf(value));
        autosave();
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Set the Setting of key to the specified value.
     *
     * @param key   a unique string identifying the setting
     * @param value a float
     */
    public void set(String key, float value) {
        properties.setProperty(key, String.valueOf(value));
        autosave();
        this.getInfo().setChangedSetting(key);
        this.setChanged();
        this.notifyObserversIfAuto();
    }

    /**
     * Returns the setting corresponding to the given key.
     *
     * @param key  a unique string identifying the setting
     * @param type because of generics you need to specify a class object of the
     *             type you want get
     * @return the value of the setting corresponding to the given key, null if
     *         no setting exists
     */
    public <T> T get(String key, Class<T> type) {
        String property = properties.getProperty(key);
        if (property == null) {
            // Log.gl().warn("Unknown setting " + key);
            return null;
        }

        byte[] value = null;
        try {
            value = Base64.decode(property);
        } catch (IOException e) {
            Log.gl().warn("Could not decode " + key);
            e.printStackTrace();
        }

        // TODO julian cache serialization
        if (value != null) {
            InputStream inputStream = new ByteArrayInputStream(value);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object object = objectInputStream.readObject();
                objectInputStream.close();
                return type.cast(object);
            } catch (ClassNotFoundException e) {
                Log.gl().warn("Tried to load unknown serialized object");
                e.printStackTrace();
            } catch (IOException e) {
                Log.gl().warn("Catched IOException in get(" + key + ", " + type + ")");
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Returns the corresponding int value to the given key.
     *
     * @param key a unique string identifying the setting
     * @return the corresponding int value to the given key
     */
    public int getInt(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            return 0;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns the corresponding long value to the given key.
     *
     * @param key a unique string identifying the setting
     * @return the corresponding long value to the given key
     */
    public long getLong(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            return 0L;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Returns the corresponding String value to the given key.
     *
     * @param key a unique string identifying the setting
     * @return the corresponding String value to the given key
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns the corresponding float value to the given key.
     *
     * @param key a unique string identifying the setting
     * @return the corresponding float value to the given key
     */
    public float getFloat(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            return 0f;
        }

        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    /**
     * Returns the corresponding double value to the given key.
     *
     * @param key a unique string identifying the setting
     * @return the corresponding double value to the given key
     */
    public double getDouble(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            return 0d;
        }

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    /**
     * Returns the corresponding boolean value to the given key.
     *
     * @param key a unique string identifying the setting
     * @return the corresponding boolean value to the given key
     */
    public boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.valueOf(value) : false;
    }

    @Override
    public void notifyObserver(SettingsInfo i, SettingsObserver obs) {
        obs.settingsChanged(this, i);
    }

    /**
     * Applys the content of the other settings object.
     *
     * @param other     the other settings object to apply
     * @param overwrite if true overwrite existing settings
     */
    public void apply(Settings other, boolean overwrite) {
        for (String key : other.properties.stringPropertyNames()) {
            if (overwrite || getString(key) == null) {
                properties.setProperty(key, other.properties.getProperty(key));
                this.setChanged();
                getInfo().setChangedSetting(key);
                this.notifyObservers();
            }
        }

    }
}
