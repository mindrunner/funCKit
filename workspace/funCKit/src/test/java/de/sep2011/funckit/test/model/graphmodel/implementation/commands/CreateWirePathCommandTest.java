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

package de.sep2011.funckit.test.model.graphmodel.implementation.commands;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.commands.CreateWirePathCommand;
import de.sep2011.funckit.test.factory.circuit.TestWirePathCommandCircuitFactory;
import de.sep2011.funckit.validator.SimulationValidatorFactory;
import de.sep2011.funckit.validator.Validator;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.ArrayList;

/**
 * This class contains various tests for the {@link CreateWirePathCommand}.
 */
public class CreateWirePathCommandTest {

    private static final int TEST_PATH_LENGTH = 10;
    private TestWirePathCommandCircuitFactory cf;
    private Circuit c;

    @Before
    public void setUp() {
        cf = new TestWirePathCommandCircuitFactory();
        c = cf.getCircuit();
    }

    /**
     * This test simply connects two {@link AccessPoint}s directly using
     * {@link CreateWirePathCommand}.
     */
    @Test
    public void testConnectTwoAps() {
        new CreateWirePathCommand(c, cf.getLonelyIdPoint().getInputA(), new ArrayList<Point>(), cf
                .getOnlyToInputConnectedIdPoint().getOutputO(), false).execute();
        Validator validator = new SimulationValidatorFactory().getValidator();
        validator.validate(c);
        Assert.assertTrue(validator.allPassed());
    }

    /**
     * This test simply connects two {@link AccessPoint}s via a path of points
     * using {@link CreateWirePathCommand} and use {@link Validator} from
     * {@link SimulationValidatorFactory} to check if it is still a valid
     * circuit.
     */
    @Test
    public void testConnectPath() {
        ArrayList<Point> path = new ArrayList<Point>();
        for (int i = 0; i < TEST_PATH_LENGTH; i++) {
            path.add(new Point());
        }
        new CreateWirePathCommand(c, cf.getLonelyIdPoint().getInputA(), path, cf
                .getOnlyToInputConnectedIdPoint().getOutputO(), false).execute();
        Validator validator = new SimulationValidatorFactory().getValidator();
        validator.validate(c);
        Assert.assertTrue(validator.allPassed());
    }

    /**
     * This test simply splits a {@link Wire} and start a Path Creation via a
     * path of points using {@link CreateWirePathCommand} and use
     * {@link Validator} from {@link SimulationValidatorFactory} to check if it
     * is still a valid circuit.
     */
    @Test
    public void testConnectPathFromWire() {
        ArrayList<Point> path = new ArrayList<Point>();
        for (int i = 0; i < TEST_PATH_LENGTH; i++) {
            path.add(new Point());
        }
        new CreateWirePathCommand(c, cf.getWire(), path, cf.getOnlyToInputConnectedIdPoint()
                .getOutputO(), false).execute();
        Validator validator = new SimulationValidatorFactory().getValidator();
        validator.validate(c);
        Assert.assertTrue(validator.allPassed());
    }
}
