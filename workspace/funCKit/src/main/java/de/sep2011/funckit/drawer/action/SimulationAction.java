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

package de.sep2011.funckit.drawer.action;

import java.awt.Color;
import java.awt.Graphics2D;

import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.sessionmodel.Settings;

/**
 * Action that prepares certain elements for drawing in a simulating state.
 * These routines are connected to DefaultShapeAction and should
 * probably not be connected with other shape actions. To use other shape
 * actions than the default one, another simulation action may be needed.
 */
public class SimulationAction extends DefaultDrawAction {
    private Color simulationColor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        super.setUp(layout, settings, graphics);
        simulationColor = settings.get(Settings.ELEMENT_SIMULATED_FILL_COLOR,
                Color.class);
        if (simulationColor == null) {
            simulationColor = Color.YELLOW;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Wire wire, Layout layout) {
        setBaseShapeBorderSimulated(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Light light, Layout layout) {
        setBaseShapeFillSimulated(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {
        setBaseShapeFillSimulated(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(IdPoint idPoint, Layout layout) {
        setBaseShapeFillSimulated(layout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input input, Layout layout) {
        assert simulationColor != null; // U did not call setUp()
        if (layout.isSimulationState() && layout.getShape(Layout.INPUTS_SHAPE) != null) {
            layout.getShape(Layout.INPUTS_SHAPE).setFillColor(simulationColor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        assert simulationColor != null; // U did not call setUp()
        if (layout.isSimulationState() && layout.getShape(Layout.OUTPUTS_SHAPE) != null) {
            layout.getShape(Layout.OUTPUTS_SHAPE).setFillColor(simulationColor);
        }
    }

    private void setBaseShapeBorderSimulated(Layout layout) {
        assert simulationColor != null; // U did not call setUp()

        if (layout.isSimulationState() && layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setBorderColor(simulationColor);
        }
    }

    private void setBaseShapeFillSimulated(Layout layout) {
        assert simulationColor != null; // U did not call setUp()

        if (layout.isSimulationState() && layout.getShape(Layout.BASE_SHAPE) != null) {
            layout.getShape(Layout.BASE_SHAPE).setFillColor(simulationColor);
        }
    }
}
