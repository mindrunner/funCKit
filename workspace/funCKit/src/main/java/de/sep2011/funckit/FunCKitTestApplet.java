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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.sep2011.funckit.controller.listener.project.NewProjectActionListener;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Profiler;
import de.sep2011.funckit.util.ReflectiveJSObject;
import de.sep2011.funckit.util.NoCloseFilterInputStream;


import javax.swing.*;


public class FunCKitTestApplet extends JApplet {
    
    private final static String APPLICATION_TITLE = "funCKit";

    private Application app;
    private Circuit circuitToDisplay;

    // Called when this applet is loaded into the browser.
    @Override
    public void init() {
        
        app  = new AppletApplication(this, APPLICATION_TITLE, SessionModel.ViewType.VIEW_TYPE_ELEANING_SOLVE);
        
        loadZipFile();

        ReflectiveJSObject jsObject;
        try {
            jsObject = ReflectiveJSObject.getWindow(this);
            Log.gl().debug("The Age from js: " + jsObject.eval("getAge()"));
            //jsObject.call("alert", "This is a Test alert");
            
        } catch (ClassNotFoundException e1) {
            Log.gl().info("Unable to execute JS things, no JSObject class");
        }
        
        //Execute a job on the event-dispatching thread; creating this applet's GUI.

        Profiler.ON = false;

        try {

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setRootPane(app.getView().getMainRootPane());
                    
                    if (circuitToDisplay != null) {
                        new NewProjectActionListener(app.getView(), app.getController(), "foo",
                                circuitToDisplay).actionPerformed(null);
                    }

                }
            });

        } catch (final Exception e) {
            e.printStackTrace();
            Log.gl().error(e);
        }

    }
    
    @Override
    public String[][] getParameterInfo() {
        String pinfo[][] = {
                {"appletconfarchive", "url", "zip archive containing config info for the applet"}
        };
        return pinfo;
        
    }
    
    // http://www.hccp.org/java-net-cookie-how-to.html
    // http://www.javamex.com/tutorials/compression/zip.shtml
    
    private void loadZipFile() {
        try {
            // Could be a relative URL build real one
            URL zipUrl = new URL(getCodeBase(), getParameter("appletconfarchive"));
            Log.gl().debug("URL to the zip archive: " + zipUrl);
            
            InputStream archiveStream = zipUrl.openConnection().getInputStream();
            ZipInputStream zipStream = new ZipInputStream(archiveStream);
            for (ZipEntry entry = zipStream.getNextEntry(); entry != null; entry = zipStream.getNextEntry()) {
                Log.gl().debug("current zip entry: " + entry);
                if(entry.getName().equals("circuit.fck")) {
                    circuitToDisplay = new SEPFormatConverter("", Mode.FUNCKITFORMAT)
                            .doImport(new NoCloseFilterInputStream(zipStream));
                }
                
            }
            
            zipStream.close();
            
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SEPFormatImportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    
    }
    
    public void showMessageBox(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, text);

            }
        });

    }

    public void openNewProject(final String projName) {
        // We seem to need this to give JS code enough rights to load resources
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        new NewProjectActionListener(app.getView(), app.getView().getController(),
                                projName, new CircuitImpl()).actionPerformed(null);
                    }
                });
                return null;
            }
        });

    }

}
