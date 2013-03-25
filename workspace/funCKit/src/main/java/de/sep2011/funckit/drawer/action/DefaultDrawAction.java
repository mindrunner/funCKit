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

import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;

import java.awt.Graphics2D;

/**
 * Abstract default implementation for {@link DrawAction}. Override and call
 * <code>super.setUp()</code> to automatically inject settings and graphics
 * object to <code>DrawAction</code> implementation.
 */
public abstract class DefaultDrawAction implements DrawAction {
    Settings settings;
    Graphics2D graphics;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        this.graphics = graphics;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Element element, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Wire wire, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Component component, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Light light, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(IdPoint idPoint, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input input, Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {

    }
}
