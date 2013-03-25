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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.model.simulationmodel.SimulationBrick;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;
import de.sep2011.funckit.model.simulationmodel.commands.SimulateCommand;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import static de.sep2011.funckit.util.Log.gl;

/**
 * A {@link Tool} to be used during Simulation. Processes events like klicks on
 * switches.
 */
public class SimulationTool extends AbstractTool {

    /**
     * Create a new SimulationTool.
     * 
     * @param c
     *            the associated {@link Controller}, should not be null
     */
    public SimulationTool(Controller c) {
        this.controller = c;
    }

    @Override
    public SimulationTool getNewInstance(Controller c) {
        return new SimulationTool(c);
    }

    @Override
    public Cursor getToolDefaultCursor() {
        return Tool.FIVE_FINGER_HAND_CURSOR;
    }

    /**
     * Do not open it here.
     * 
     * @param event
     *            the mouse event
     */
    @Override
    protected void openContextMenu(MouseEvent event) {
    }

    @Override
    public void mouseClicked(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseClicked(e, editPanelModel);
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());
        Simulation sim = controller.getSessionModel().getCurrentSimulation();
        assert sim != null;

        Brick b = editPanelModel.getCircuit().getBrickAtPosition(click);

        if (b instanceof Switch) {
            SimulationBrick simBrick = new SimulationBrick(b, editPanelModel.getComponentStack());
            SwitchSimulationState state = (SwitchSimulationState) sim.getSimulationState(simBrick);

            gl().debug("Simulation Next action triggered from clicked Switch");
            Simulation simulation = controller.getSessionModel().getCurrentSimulation();
            if (simulation != null) {
                if (controller.getSessionModel().getSettings()
                        .getBoolean(Settings.SIMULATION_UNDO_ENABLED)) {
                    SimulateCommand command = new SimulateCommand(simulation);

                    /*
                     * We must create the SimulateCommand before this so it can
                     * create its snapshot before the switch is toggled,
                     * otherwise the switch would be on in the snapshot.
                     */
                    state.setValue(!state.getValue());
                    controller.getSessionModel().getCurrentSimulationCommandDispatcher()
                            .dispatch(command);
                } else {
                    state.setValue(!state.getValue());
                    simulation.nextStep();
                }

                gl().debug("Executed Simulation.nextStep()");
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseReleased(mouseEvent, editPanelModel);
        setCursor(mouseEvent, editPanelModel, false, false);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        super.mouseMoved(mouseEvent, editPanelModel);
        setCursor(mouseEvent, editPanelModel, false, false);
    }

    private void setCursor(MouseEvent event, EditPanelModel panelModel, boolean pressed,
            boolean isDragging) {
        
        Circuit circuit = panelModel.getCircuit();
        Point click = calculateInversePoint(event.getPoint(), panelModel.getTransformation());
        boolean brickIsSwitch = circuit.getBrickAtPosition(click) instanceof Switch;

        if (brickIsSwitch && !isDragging) {
            panelModel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else if (pressed) {
            panelModel.setCursor(ZERO_FINGER_HAND_CURSOR);
        } else {
            panelModel.setCursor(getToolDefaultCursor());
        }

    }

    @Override
    public void mousePressed(MouseEvent e, EditPanelModel editPanelModel) {
        super.mousePressed(e, editPanelModel);
        editPanelModel.setDragStartPoint(new Point(e.getX(), e.getY()));
        setCursor(e, editPanelModel, true, false);
    }

    @Override
    public void mouseDragged(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseDragged(e, editPanelModel);
        dragViewport(e, editPanelModel);
        setCursor(e, editPanelModel, true, true);
    }
}
