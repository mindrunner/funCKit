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

package de.sep2011.funckit.drawer;

import de.sep2011.funckit.drawer.action.DrawAction;
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

import java.awt.Graphics;

/**
 * Drawer interface to accept certain objects from {@link Circuit} on injected
 * graphics object.<br /> Standard drawing routine is: <p>
 * <pre>
 *     drawer.setGraphics(graphicsObject)
 *     drawer.setLayout(layoutObject)
 *     drawer.setAction(action)
 *     drawer.accept(element)
 * </pre>
 * </p> The configuration objects are injected via setter injection to keep the
 * drawing method clean and simple. Furthermore the visit() method is used for
 * double dispatching from graphmodel and the already undesired coupling between
 * drawing and graphmodel should be kept simple.
 */
public interface Drawer extends ElementDispatcher {
    /**
     * Injects graphics object needed for drawing.
     *
     * @param graphics Graphic object this drawer should draw on.
     */
    public void setGraphics(Graphics graphics);

    /**
     * Injects {@link DrawAction} object needed to adjust layout object.
     *
     * @param action Action object, this drawer should use to modify layout
     *               object in next element-drawer-phase.
     */
    public void setAction(DrawAction action);

    /**
     * Injects default {@link Layout} object to pass to action object.
     *
     * @param layout Layout descriptive object, that should be modified and
     *               drawn on graphics object in next drawing phase.
     */
    public void setLayout(Layout layout);

    /**
     * Method for drawing a general element object (if no further pattern
     * matching applied). {@inheritDoc}
     */
    @Override
    public void visit(Element element);

    /**
     * Draws given wire. {@inheritDoc}
     */
    @Override
    public void visit(Wire wire);

    /**
     * Draws the component. {@inheritDoc}
     */
    @Override
    public void visit(Component component);

    /**
     * Draws the switch. {@inheritDoc}
     */
    @Override
    public void visit(Switch s);

    /**
     * Draws the light. {@inheritDoc}
     */
    @Override
    public void visit(Light light);

    /**
     * Draws the and element. {@inheritDoc}
     */
    @Override
    public void visit(And and);

    /**
     * Draws the or gate. {@inheritDoc}
     */
    @Override
    public void visit(Or or);

    /**
     * Draws the not gate. {@inheritDoc}
     */
    @Override
    public void visit(Not not);

    /**
     * Draws an IdPoint / connection point. {@inheritDoc}
     */
    @Override
    public void visit(IdPoint idPoint);
}