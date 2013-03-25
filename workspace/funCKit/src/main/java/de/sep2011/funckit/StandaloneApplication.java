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

import java.io.File;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.SystemInformation;
import de.sep2011.funckit.view.View;

public class StandaloneApplication extends Application {
    
    private static final String COMPONENT_LIST_DIR_NAME = "components";

    /**
     * Creation of new application object. This initializes all dependent
     * systems.
     * 
     * @param applicationTitle
     *            The title of the application e.g. FunCKit
     * @param homeDirectory
     *            the home directory of funckit, usually something like System
     *            .getProperty("user.home") + File.separator + ".funckit"
     */
    public StandaloneApplication(String applicationTitle, String homeDirectory) {
        initialize(applicationTitle, homeDirectory, "settings.xml");
    }

    /**
     * Creation of new application object. This initializes all dependent
     * systems.
     * 
     * @param applicationTitle
     *            The title of the application e.g. FunCKit
     * @param homeDirectory
     *            the home directory of funckit, usually something like System
     *            .getProperty("user.home") + File.separator + ".funckit"
     * @param settingsFile
     *            name of the settings file inside homeDirectory e.g.
     *            settings.xml
     */
    public StandaloneApplication(String applicationTitle, String homeDirectory, String settingsFile) {
        initialize(applicationTitle, homeDirectory, settingsFile);
    }

    private void initialize(String applicationTitle, String homeDirectory, String settingsFileName) {
        viewType = SessionModel.ViewType.VIEW_TYPE_STANDALONE;
        this.applicationTitle = applicationTitle;
        this.funckitHomeDirectory = homeDirectory;
        initializeEnvironment(funckitHomeDirectory, settingsFileName);
        loadLookAnfFeels();
        initializeSettings();
        initializeLanguage();
        initializeModels();
        initializeController();
        initializeUserInterface();
    }
    
    /**
     * Returns The title of the application e.g. FunCKit.
     * 
     * @return The title of the application e.g. FunCKit
     */
    String getApplicationTitle() {
        return applicationTitle;
    }

    private void initializeEnvironment(String homeDirectory, String settingsFileName) {
        funckitHomeDirectory = homeDirectory;
        componentListDirectory = homeDirectory + File.separator + COMPONENT_LIST_DIR_NAME;
        settingsFilePath = funckitHomeDirectory + File.separator + settingsFileName;
        initializeDir(funckitHomeDirectory);
        initializeDir(componentListDirectory);
    }

    /**
     * Initializes user interface by creating all major delegating systems and
     * apply dependencies.
     */
    protected void initializeUserInterface() {
        final SessionModel sessionModel = getSessionModel();
        final Controller controller = getController();
        
        assert sessionModel != null;
        assert controller != null;

        Log.gl().debug(("Initialize user interface"));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Settings settings = getSettings();
                System.setProperty("apple.laf.useScreenMenuBar", "true");

                boolean success = false;
                String settingLaf = settings.getString(Settings.LOOK_AND_FEEL);

                /*
                 * unset look and feel so we can run on default next time if jvm
                 * crashes during setting laf
                 */
                settings.remove(Settings.LOOK_AND_FEEL);

                if (!success) {
                    success = setLookAndFeel(settingLaf);
                    Log.gl().info("Unable to set LookAndFeel from settings: " + settingLaf);
                }

                if (!success) {
                    success = setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }

                if (!success) {
                    success = setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                }

                if (!success) {
                    Log.gl().error("Unable to set Look And Feel");
                }

                if (success) {
                    /*
                     * set laf succeeded so we again store the currently used in
                     * settings
                     */
                    LookAndFeel laf = UIManager.getLookAndFeel();

                    String currLafName = laf == null ? "" : laf.getClass()
                            .getName();

                    settings.set(Settings.LOOK_AND_FEEL, currLafName);
                }

                new SystemInformation().printAll();

                view = new View(getApplicationTitle(), sessionModel, controller, null);

            }
        });
    }

    @Override
    public boolean LoadInternalComponentTypes() {
        return true;
    }
    
}