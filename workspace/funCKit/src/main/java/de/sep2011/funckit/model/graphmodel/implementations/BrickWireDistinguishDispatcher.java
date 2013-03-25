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

package de.sep2011.funckit.model.graphmodel.implementations;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;

/**
 * Class that lets you easily distinguish between {@link Wire}s and
 * {@link Brick}s.
 */
public abstract class BrickWireDistinguishDispatcher implements ElementDispatcher {

    /**
     * Called if Wire or one of its Subclasses.
     * 
     * @param w the Wire
     */
    protected abstract void visitWire(Wire w);
    
    /**
     * Called if Brick or one of its Subclasses.
     * 
     * @param b the Brick
     */
    protected abstract void visitBrick(Brick b);

    @Override
    public void visit(Element element) {
    }

    @Override
    public void visit(Wire wire) {
        visitWire(wire);

    }

    @Override
    public void visit(Component component) {
        visitBrick(component);

    }

    @Override
    public void visit(Switch s) {
        visitBrick(s);

    }

    @Override
    public void visit(Light light) {
        visitBrick(light);

    }

    @Override
    public void visit(And and) {
        visitBrick(and);

    }

    @Override
    public void visit(Or or) {
        visitBrick(or);

    }

    @Override
    public void visit(Not not) {
        visitBrick(not);

    }

    @Override
    public void visit(IdPoint idPoint) {
        visitBrick(idPoint);

    }

}
