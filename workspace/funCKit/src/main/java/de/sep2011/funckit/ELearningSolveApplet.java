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
import java.awt.event.ComponentListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import de.sep2011.funckit.controller.listener.project.NewProjectActionListener;
import de.sep2011.funckit.controller.listener.view.ModelFitsIntoCircuitListener;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.BehaviouralCircuitComparator;
import de.sep2011.funckit.util.GraphmodelUtil;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Profiler;
import de.sep2011.funckit.util.ReflectiveJSObject;
import de.sep2011.funckit.validator.SimulationValidatorFactory;
import de.sep2011.funckit.validator.Validator;


public class ELearningSolveApplet extends JApplet {
    
    private final static String APPLICATION_TITLE = "funCKit";

    private Application app;
    private Circuit beginCircuit;
    private Circuit solutionCircuit;
    
    private String sessionId;
    private String sessionName;
    private String projectName;
    private List<List<Boolean>> simrows;
    
    private ReflectiveJSObject jsObject;

    // Called when this applet is loaded into the browser.
    @Override
    public void init() {
        Profiler.ON = false;
        app  = new AppletApplication(this, APPLICATION_TITLE, SessionModel.ViewType.VIEW_TYPE_ELEANING_SOLVE);
        
        Settings settings = app.getSessionModel().getSettings();
        settings.set(Settings.REALTIME_VALIDATION, true);
        
        initJsObject();
        processParameters();
        
        if(solutionCircuit == null) {
            displayLoadError("Error: failed to load solution circuit!");
            return;
        }
        
        app.getView().getSessionModel().getNewBrickListManager().loadPredefinedInternalType("clock");
        app.getView().getSessionModel().getNewBrickListManager().loadPredefinedInternalType("identity");
        fixAndGenerateSwitchesAndLights();
        
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setRootPane(app.getView().getMainRootPane());

                    if (beginCircuit != null) {
                        new NewProjectActionListener(app.getView(), app.getController(),
                                projectName, beginCircuit).actionPerformed(null);

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

        loadCmps();
        loadFckBegin();    
        loadFckSolution();
        loadSimList();
    }
    
    private void initJsObject() {
        try {
            jsObject = ReflectiveJSObject.getWindow(this);
        } catch (ClassNotFoundException e1) {
            Log.gl().warn("Unable to execute JS in page.");
        }
        
    }
    
    private void loadCmps() {
        for(int i = 0; getParameter("cmp" + i) != null; i++) {
            try {
                if("".equals(getParameter("cmp" + i))) {
                    continue;
                }
                
                URL url = new URL(getCodeBase(), getParameter("cmp" + i));
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Cookie", sessionName +"="+ sessionId);
                InputStream in = connection.getInputStream();
                
                SEPFormatConverter conv = new SEPFormatConverter("", Mode.FUNCKITFORMAT);
                ComponentType t = conv.importComponentType(in);
                Validator simulationValidator = new SimulationValidatorFactory().getValidator();
                simulationValidator.validate(t.getCircuit());
                if (simulationValidator.allPassed()) {
                    app.getView().getSessionModel().getNewBrickListManager().addInternalType(t);
                }
                in.close();
            } catch (SEPFormatImportException e) {
                Log.gl().debug(e);
            } catch (FileNotFoundException e) {
                Log.gl().debug(e);
            } catch (MalformedURLException e) {
                Log.gl().debug(e);
            } catch (IOException e) {
                Log.gl().debug(e);
            }
        }  
    }
    
    private void loadFckSolution() {
        try {
            URL url = new URL(getCodeBase(), getParameter("fcksolution"));
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Cookie", sessionName +"="+ sessionId);
            InputStream in = connection.getInputStream();
            
            SEPFormatConverter sepFormatConverter = new SEPFormatConverter("", Mode.FUNCKITFORMAT);
            solutionCircuit = sepFormatConverter.doImport(in);
            projectName = sepFormatConverter.getProjectName();
            
            in.close();
            Log.gl().debug("Loaded soulution Circuit.");
        } catch (MalformedURLException e) {
            Log.gl().debug(e);
        } catch (IOException e) {
            Log.gl().debug(e);
        } catch (SEPFormatImportException e) {
            Log.gl().debug(e);
        }
    } 
    
    private void loadFckBegin() {
        if("".equals(getParameter("fckbegin"))) {
            beginCircuit = new CircuitImpl();
            Log.gl().debug("empy beginCircuit");
            return;
        }
        
        try {
            URL url = new URL(getCodeBase(), getParameter("fckbegin"));
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Cookie", sessionName + "=" + sessionId);
            InputStream in = connection.getInputStream();
            beginCircuit = new SEPFormatConverter("", Mode.FUNCKITFORMAT).doImport(in);
            in.close();
        } catch (MalformedURLException e) {
            Log.gl().debug(e);
            beginCircuit = new CircuitImpl();
        } catch (IOException e) {
            Log.gl().debug(e);
            beginCircuit = new CircuitImpl();
        } catch (SEPFormatImportException e) {
            Log.gl().debug(e);
            beginCircuit = new CircuitImpl();
        }
    }
    
    private void loadSimList() {
        if("".equals(getParameter("simassign"))) {
            Log.gl().debug("empy simassign Parameter");
            return;
        }
        
        try {
            URL url = new URL(getCodeBase(), getParameter("simassign"));
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Cookie", sessionName + "=" + sessionId);
            InputStream in = connection.getInputStream();
            simrows = BehaviouralCircuitComparator.convertSimlistToSimRows(in);
            in.close();
        } catch (MalformedURLException e) {
            Log.gl().debug(e);
        } catch (IOException e) {
            Log.gl().debug(e);
        } 
    }
    
    
    public ReflectiveJSObject getJsObject() {
        return jsObject;
    }
    
    @Override
    public String[][] getParameterInfo() {
        String pinfo[][] = {
                {"fckbegin", "url", "the begin circuit, can be empty"},
                {"fcksolution", "url", "the solution circuit"},
                {"session_name", "String", "the name of the session"},
                {"session_id", "String", "the session id"},
                {"cmp0", "url", "the first component"},
                {"cmp1", "url", "the second component"},
                {"cmpx", "url", "the x. component (Numbers must start with 0 and be in a row)"},
                {"simassign", "url", "simulation configurations for circuits"}
        };                
        return pinfo;
        
    }

    private void fixAndGenerateSwitchesAndLights() {
        for (Element elem : solutionCircuit.getElements()) {
            if (elem instanceof Switch) {
                Brick beginSwitch = GraphmodelUtil.findBrickAtSamePos(beginCircuit,
                        elem.getPosition());
                if (!(beginSwitch instanceof Switch)) {
                    beginSwitch = ((Switch) elem).getUnconnectedCopy().getLeft();
                    beginCircuit.addBrick(beginSwitch);
                }
                beginSwitch.setFixedHint(true);
            } else if (elem instanceof Light) {
                Brick beginLight = GraphmodelUtil.findBrickAtSamePos(beginCircuit,
                        elem.getPosition());
                if (!(beginLight instanceof Light)) {
                    beginLight = ((Light) elem).getUnconnectedCopy().getLeft();
                    beginCircuit.addBrick(beginLight);
                }
                beginLight.setFixedHint(true);
            }
        }
    }
    
    public Circuit getBeginCircuit() {
        return beginCircuit;
    }
    
    public Circuit getSolutionCircuit() {
        return solutionCircuit;
    }
    
    public List<List<Boolean>> getSimrows() {
        return simrows;
    }
    
    private void displayLoadError(final String err) {
        try {

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setRootPane(new JRootPane());
                    add(new JLabel(err));

                }
            });} catch (Exception e) {
                e.printStackTrace();
            }
    }
}
