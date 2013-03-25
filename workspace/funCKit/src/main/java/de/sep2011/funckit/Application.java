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

package de.sep2011.funckit;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.DefaultSettings;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.view.NewBrickList;
import de.sep2011.funckit.view.View;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.io.File;
import java.util.Locale;

/**
 * Wrapper object that contains logic of how sub systems get initialized and
 * delegated.
 */
public abstract class Application {
    /**
     * Title of GUI's window frame.
     */
    protected String applicationTitle;
    protected View view;
    protected SessionModel.ViewType viewType;
    
    /**
     * Home directory of our application for persisting components and settings.
     */
    protected String funckitHomeDirectory;
    
    /**
     * Directory for holding component files, depending on funckit home
     * directory.
     */
    protected String componentListDirectory;

    /**
     * Classification of operating systems, we work with.
     */
    public enum OperatingSystem {
        /**
         * Microsoft Windows.
         */
        WIN,
        /**
         * Mac OS X.
         */
        OSX,
        /**
         * Other operating system.
         */
        OTHER
    }

    /**
     * Identified operating system, this application is running in.
     */
    public static final OperatingSystem OS;

    /**
     * File path for settings xml.
     */
    protected String settingsFilePath;

    /**
     * Applications' settings object.
     */
    private Settings settings;

    /**
     * Session model for information, that is not persisted.
     */
    private SessionModel sessionModel;

    /**
     * Controller delegation object.
     */
    private Controller controller;

    /* Resolve operating system. */
    static {
        if (System.getProperty("os.name").contains("OS X")) {
            OS = OperatingSystem.OSX;
        } else if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            OS = OperatingSystem.WIN;
        } else {
            OS = OperatingSystem.OTHER;
        }
    }

    public Settings getSettings() {
        return settings;
    }

    /**
     * Returns the full path to the directory where the files for the
     * {@link NewBrickList} are stored.
     * 
     * @return the full path to the directory where the files for the
     *         {@link NewBrickList} are stored.
     */
    public String getComponentListDirectory() {
        return componentListDirectory;
    }

    /**
     * Returns the {@link View} associated with this {@link Application}.
     * 
     * @return the {@link View} associated with this {@link Application}
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Returns the {@link View} associated with this {@link Application}.
     * 
     * @return the {@link View} associated with this {@link Application}
     */
    public View getView() {
        return this.view;
    }

    /**
     * Initializes language system (i18n & i10n) by specifying current locale.
     */
    protected void initializeLanguage() {
        Language.setLocale(new Locale(settings.getString(Settings.Language)));
        JComponent.setDefaultLocale(Language.getCurrentLocale());
    }

    /**
     * Initializes a specified directory by checking for existence or
     * permissions.
     * 
     * @param dir
     *            Directory path.
     */
    protected void initializeDir(String dir) {
        File fi = new File(dir);
        if (!fi.isDirectory()) {
            boolean success = fi.mkdirs();

            if (!success) {
                Log.gl().warn("Could not create Directory " + dir);
            }
        }
    }

    protected void initializeController() {
        Log.gl().debug(("Initialize controller"));
        controller = new Controller(sessionModel);
    }

    protected void initializeModels() {
        Log.gl().debug(("Initialize graph and session model"));

        sessionModel = new SessionModel(this, settings, viewType);
    }

    /**
     * Initialize settings by applying default settings to existing ones,
     * without overwriting existing ones. MickeyMouseMode is always set to false
     * on start. Enabling automatic save to apply latest changes in merge from
     * default and existing settings.
     */
    protected void initializeSettings() {
        /* Inject default settings. */
        assert settingsFilePath != null;
        Log.gl().info(("Initialize setting object with " + settingsFilePath));
        settings = new Settings(settingsFilePath);
        settings.setAutosave(false);

        settings.apply(DefaultSettings.getDefaultSettings(), false);
        settings.set(Settings.MMMode, false);

        settings.setAutosave(true);
    }

    protected void loadLookAnfFeels() {
        installLaf("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
    }

    private static void installLaf(String name, String className) {

        for (LookAndFeelInfo lafinfo : UIManager.getInstalledLookAndFeels()) {
            if (lafinfo.getClassName().equals(className)) {
                return;
            }
            if (className.equals(UIManager.getLookAndFeel().getClass().getName())) {
                return;
            }
        }

        UIManager.installLookAndFeel(name, className);
    }

    protected boolean setLookAndFeel(String lookAndFeelName) {
        try {
            UIManager.setLookAndFeel(lookAndFeelName);
        } catch (ClassNotFoundException e) {
            Log.gl().debug(e.getMessage(), e);
            return false;
        } catch (InstantiationException e) {
            Log.gl().debug(e.getMessage(), e);
            return false;
        } catch (IllegalAccessException e) {
            Log.gl().debug(e.getMessage(), e);
            return false;
        } catch (UnsupportedLookAndFeelException e) {
            Log.gl().debug(e.getMessage(), e);
            return false;
        }

        return true;
    }
    
    /**
     * Returns the {@link SessionModel} associated with this {@link Application}
     * .
     * 
     * @return the {@link SessionModel} associated with this {@link Application}
     */
    public SessionModel getSessionModel() {
        return sessionModel;
    }
    
    public abstract boolean LoadInternalComponentTypes();
}
