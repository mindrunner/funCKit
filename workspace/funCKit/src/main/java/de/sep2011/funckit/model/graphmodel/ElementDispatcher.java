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

package de.sep2011.funckit.model.graphmodel;

import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;

/**
 * Dispatcher (see visitor pattern) interface for dispatching elements from
 * graph model to determine their exact type.
 */
public interface ElementDispatcher {
    /**
     * General element.
     * 
     * @param element
     */
    public void visit(Element element);

    /**
     * Dispatched to element of type wire.
     * 
     * @param wire
     */
    public void visit(Wire wire);

    /**
     * Dispatched to element of type component (and thus, {@link Brick}, too).
     * 
     * @param component
     */
    public void visit(Component component);

    /**
     * Dispatched to element of type {@link Switch}.
     * 
     * @param s
     */
    public void visit(Switch s);

    /**
     * Dispatched to element of type {@link Light}.
     * 
     * @param light
     */
    public void visit(Light light);

    /**
     * Dispatched to element of type {@link And}.
     * 
     * @param and
     * @see Gate
     */
    public void visit(And and);

    /**
     * Dispatched to element of type {@link Or}.
     * 
     * @see Gate
     * @param or
     */
    public void visit(Or or);

    /**
     * Dispatched to element of type {@link Not}.
     * 
     * @see Gate
     * @param not
     */
    public void visit(Not not);

    /**
     * Dispatched to element of type {@link IdPoint}.
     * 
     * @param idPoint
     */
    public void visit(IdPoint idPoint);
}
