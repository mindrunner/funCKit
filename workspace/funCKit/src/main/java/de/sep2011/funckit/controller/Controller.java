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

package de.sep2011.funckit.controller;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static de.sep2011.funckit.util.Log.gl;

/**
 * Bootstrapping controller object to initialize cooperation between view and
 * listener objects and mediate between controller systems and model or view
 * systems.
 */
public class Controller {

    private final Tool DEFAULT_TOOL = new SelectTool(this);

    /**
     * Current session model, containing project information or view-related
     * data.
     */
    private SessionModel sessionModel;

    /**
     * Map a String to a specific tool. The String should be the Class Name of
     * the Tool.
     */
    private Map<String, Tool> toolMap;

    /**
     * Creates a new {@link Controller}.
     * 
     * @param sessionModel
     *            the associated {@link SessionModel}, not null
     */
    public Controller(SessionModel sessionModel) {
        initialize(sessionModel);
    }

    private void initialize(SessionModel sessionModel) {
        this.sessionModel = sessionModel;
        
        switch (sessionModel.getViewType()) {
        case VIEW_TYPE_PRESENTER:
            sessionModel.setTool(new DragViewportTool(this));
            break;

        default:
            sessionModel.setTool(DEFAULT_TOOL);
            break;
        }


        initToolMap();
    }

    /**
     * Add new Tools here.
     */
    private void initToolMap() {
        toolMap = new HashMap<String, Tool>();
        toolMap.put(CreateTool.class.getName(), new CreateTool(this));
        toolMap.put(DragViewportTool.class.getName(), new DragViewportTool(this));
        toolMap.put(SelectTool.class.getName(), new SelectTool(this));
        toolMap.put(SimulationTool.class.getName(), new SimulationTool(this));
        toolMap.put(WireTool.class.getName(), new WireTool(this));
        toolMap.put(PasteTool.class.getName(), new PasteTool(this));
        toolMap.put(MultiConnectTool.class.getName(), new MultiConnectTool(this));
    }

    /**
     * Get a Tool by its Name.
     * 
     * @param name
     *            usually {@link Class#getName()} of the Tool
     * @return Corresponding Tool, or {@code null} if no tool is found
     */
    public Tool getToolByName(String name) {
        Tool tool = toolMap.get(name);

        if (tool == null) {
            gl().debug(
                    "There is no Tool for this String! "
                            + "Assure that all Tools are in Controllers toolMap");
        }

        return tool;
    }

    /**
     * switch to simulation tool.
     */
    public void enterSimulationMode() {
        sessionModel.saveTool();
        sessionModel.forceTool(new SimulationTool(this));
    }

    /**
     * Sets the tool if edit mode is entered.
     * 
     * @param editTools Switch to default tool if true, else use saved tool
     */
    public void enterEditMode(boolean editTools) {
        if (editTools) {
            // switch tool back
            sessionModel.restoreTool();
        } else {
            // switch to drag viewport tool
            sessionModel.saveTool();
            sessionModel.forceTool(new DragViewportTool(this));
        }
    }

    public void openProject(Project p, boolean addProject) {
        if (addProject) {
            sessionModel.setAutoNotify(false);
            sessionModel.addProject(p);
            sessionModel.setAutoNotify(true);
        }
        sessionModel.setCurrentProject(p);
        openMainCircuitTab(p);
        if (p.hasSimulation()) {
            enterSimulationMode();
        } else {
            EditPanelModel panelModel = p.getSelectedEditPanelModel();
            enterEditMode(panelModel != null && panelModel.hasMainCircuit());
        }
    }

    public void openMainCircuitTab(Project project) {
        // setup of panelmodel: open main circuit as tab if it exists
        Circuit circuit = project.getCircuit();
        if (circuit != null) {
            gl().debug("Opening Main Tab for loaded project...");
            EditPanelModel panelModel = new EditPanelModel(project.getCircuit(),
                    new LinkedList<Component>());
            project.addEditPanelModel(panelModel);
            gl().debug("Selecting Main Tab for loaded project...");
            project.setSelectedEditPanelModel(panelModel);
        }
    }

    /**
     * Provides a getter method for controller systems to access session model.
     * 
     * @return SessionModel the associated {@link SessionModel}
     */
    public SessionModel getSessionModel() {
        return sessionModel;
    }
}