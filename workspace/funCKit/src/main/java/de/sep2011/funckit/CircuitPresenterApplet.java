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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.sep2011.funckit.controller.listener.project.NewProjectActionListener;
import de.sep2011.funckit.controller.listener.view.ModelFitsIntoCircuitListener;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Profiler;


public class CircuitPresenterApplet extends JApplet {
    
    private final static String APPLICATION_TITLE = "funCKit";

    private Application app;
    private Circuit circuitToDisplay;
    
    private String sessionId;
    private String sessionName;
    private String projectName;

    // Called when this applet is loaded into the browser.
    @Override
    public void init() {
        Profiler.ON = false;
        app  = new AppletApplication(this, APPLICATION_TITLE, SessionModel.ViewType.VIEW_TYPE_PRESENTER);
        
        Settings settings = app.getSessionModel().getSettings();
        settings.set(Settings.REALTIME_VALIDATION, false);
        
        processParameters();
        
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    if (circuitToDisplay != null) {
                        setRootPane(app.getView().getMainRootPane());
                        new NewProjectActionListener(app.getView(), app.getController(),
                                projectName, circuitToDisplay).actionPerformed(null);

                        // hack to fit circuit into view on load of applet
                        getRootPane().addComponentListener(new ComponentAdapter() {
                            private boolean done;

                            @Override
                            public void componentShown(ComponentEvent e) {
                                done = true;

                            }

                            public void componentResized(ComponentEvent e) {
                                if (e.getComponent().getWidth() > 0
                                        && e.getComponent().getHeight() > 0 && !done) {
                                    new ModelFitsIntoCircuitListener(app.getView(), app
                                            .getController()).actionPerformed(null);
                                }
                            };
                        });
                    } else {
                        add(new JLabel("Unable to load circuit!"));                                
                    }
                }
            });

        } catch (final Exception e) {
            e.printStackTrace();
            Log.gl().error(e);
        }

    }
    
    private void processParameters() {
        sessionId = getParameter("session_id");
        sessionName = getParameter("session_name");
        
        loadCircuit();    
    }
    
    private void loadCircuit() {
        try {
            URL url = new URL(getCodeBase(), getParameter("circuit"));
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Cookie", sessionName + "=" + sessionId);
            InputStream in = connection.getInputStream();
            SEPFormatConverter sepFormatConverter = new SEPFormatConverter("", Mode.FUNCKITFORMAT);
            circuitToDisplay = sepFormatConverter.doImport(in);
            projectName = sepFormatConverter.getProjectName();
            in.close();
        } catch (MalformedURLException e) {
            Log.gl().debug(e);
        } catch (IOException e) {
            Log.gl().debug(e);
        } catch (SEPFormatImportException e) {
            Log.gl().debug(e);
        }
    }    
    
    @Override
    public String[][] getParameterInfo() {
        String pinfo[][] = {
                {"circuit", "url", "the circuit to display"},
                {"session_name", "String", "the name of the session"},
                {"session_id", "String", "the session id"},
        };
        return pinfo;
        
    }
}
