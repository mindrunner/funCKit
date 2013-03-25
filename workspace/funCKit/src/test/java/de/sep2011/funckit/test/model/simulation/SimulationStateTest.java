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

package de.sep2011.funckit.test.model.simulation;

import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.model.simulationmodel.SwitchSimulationState;
import junit.framework.Assert;
import org.junit.Test;

import java.awt.Point;

/**
 * Tests for the various SimulationStates.
 */
public class SimulationStateTest {

    /**
     * Tests the SwitchSimulationStates equals and hashcode methods.
     */
    @Test
    public void testSwitchSimulationStateEquals() {
        SwitchImpl sw = new SwitchImpl(new Point());
        SwitchSimulationState state = new SwitchSimulationState(sw);
        SwitchSimulationState stateCopy = new SwitchSimulationState(sw);
        SwitchSimulationState state2 = new SwitchSimulationState(
                new SwitchImpl(new Point()));
        Assert.assertTrue(state.hashCode() == state.hashCode());
        Assert.assertTrue(state.equals(state));
        Assert.assertTrue(state.hashCode() == stateCopy.hashCode());
        Assert.assertTrue(state.equals(stateCopy));
        Assert.assertFalse(state.hashCode() == state2.hashCode());
        Assert.assertFalse(state.equals(state2));
    }

}
