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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

/**
 * utility class for the Simulation tests.
 */
class SimulationTestUtil {
    /**
     * Returns the first found Switch with the given name in the given circuit.
     * 
     * @param c
     *            circuit where the switch is in
     * @param name
     *            name of the Switch
     * @return the Switch, null if not found
     */
    public static Switch getSwitchByName(final Circuit c, final String name) {
        return new ElementDispatcher() {
            Switch sw = null;

            {
                for (Element elem : c.getElements()) {
                    elem.dispatch(this);

                    if (sw != null) {
                        break;
                    }
                }
            }

            @Override
            public void visit(IdPoint idPoint) {

            }

            @Override
            public void visit(Not not) {

            }

            @Override
            public void visit(Or or) {

            }

            @Override
            public void visit(And and) {

            }

            @Override
            public void visit(Light light) {

            }

            @Override
            public void visit(Switch s) {
                if (s.getName().equals(name)) {
                    sw = s;
                }
            }

            @Override
            public void visit(Component component) {

            }

            @Override
            public void visit(Wire wire) {
            }

            @Override
            public void visit(Element element) {

            }

            public Switch getLight() {
                return sw;
            }
        }.getLight();
    }

    /**
     * Returns the first found Light with the given name in the given circuit.
     * 
     * @param c
     *            circuit where the light is in
     * @param name
     *            name of the Light
     * @return the Light, null if not found
     */
    public static Light getLightByName(final Circuit c, final String name) {
        return new ElementDispatcher() {
            Light light = null;

            {
                for (Element elem : c.getElements()) {
                    elem.dispatch(this);

                    if (light != null) {
                        break;
                    }
                }
            }

            @Override
            public void visit(IdPoint idPoint) {

            }

            @Override
            public void visit(Not not) {

            }

            @Override
            public void visit(Or or) {

            }

            @Override
            public void visit(And and) {

            }

            @Override
            public void visit(Light light) {
                if (light.getName().equals(name)) {
                    this.light = light;
                }
            }

            @Override
            public void visit(Switch s) {

            }

            @Override
            public void visit(Component component) {

            }

            @Override
            public void visit(Wire wire) {
            }

            @Override
            public void visit(Element element) {

            }

            public Light getLight() {
                return light;
            }
        }.getLight();
    }
}
