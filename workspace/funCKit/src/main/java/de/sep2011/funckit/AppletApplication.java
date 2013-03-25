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

import javax.swing.JApplet;
import static com.google.common.base.Preconditions.*;

import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.view.View;

public class AppletApplication extends Application {

    private JApplet applet;

    /**
     * @param applet The associated applet for this application
     * @param applicationTitle the tiltle of the application
     */
    public AppletApplication(JApplet applet, String applicationTitle, SessionModel.ViewType viewType) {
        initialize(applet, applicationTitle, viewType);
    }

    private void initialize(JApplet applet, String applicationTitle, SessionModel.ViewType viewType) {
        checkNotNull(viewType);
        checkNotNull(applet);
        this.viewType = viewType;
        this.applet = applet;
        this.applicationTitle = applicationTitle;
        this.funckitHomeDirectory = null;
        loadLookAnfFeels();
        initializeSettings();
        initializeLanguage();
        initializeModels();
        initializeController();
        initializeView();
    }
    
    @Override
    public String getComponentListDirectory() {
        return null;
    }

    /**
     * @return The associated applet
     */
    public JApplet getApplet() {
        return applet;
    }

    private void initializeView() {

        view = new View(applicationTitle, getSessionModel(), getController(),
                applet);
    }

    @Override
    public boolean LoadInternalComponentTypes() {
        return false;
    }
}
