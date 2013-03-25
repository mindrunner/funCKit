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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Composite of {@link DrawAction} objects.
 */
public class ActionComposite implements DrawAction {
    private final LinkedList<DrawAction> actions = new LinkedList<DrawAction>();

    /**
     * Adds an action to this action composition.
     *
     * @param action DrawAction object to add to chain.
     * @return Returns itself to invoke another method on {@link
     *         ActionComposite}.
     */
    public ActionComposite addAction(DrawAction action) {
        actions.add(action);
        return this;
    }

    /**
     * Getter method for action composition list. Should be only used for
     * debugging or reading list, not modifying it. Introduce new methods in
     * {@link ActionComposite} if modifications on that list are explicitly
     * needed.
     *
     * @return List of actions, that get invoked successive.
     */
    public List<DrawAction> getActions() {
        return actions;
    }

    /**
     * Removes all {@link DrawAction} of certain type.
     *
     * @param clazz Type to remove from composition.
     * @return Self reference for method invocation chaining.
     */
    public ActionComposite removeAction(Class<?> clazz) {
        for (Iterator<DrawAction> iterator = actions.iterator(); iterator.hasNext(); ) {
            DrawAction action = iterator.next();
            if (action.getClass().equals(clazz)) {
                iterator.remove();
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp(Layout layout, Settings settings, Graphics2D graphics) {
        for (DrawAction action : actions) {
            action.setUp(layout, settings, graphics);
        }
    }

    /**
     * In this special case of composing several actions, we still have to make
     * sure, that the order of method-invocation is <ol> <li>{@link
     * DrawAction#setUp()}</li> <li>{@link DrawAction#prepare()}</li> <li>{@link
     * DrawAction#prepare(dispatched element)}</li> </ol> As this
     * <code>ActionComposite</code> gets invoked in this order, too, we can not
     * perform all <code>prepare()</code>-invocations at this point, but have to
     * wait until <code>prepare(dispatched element)</code> gets invoked. Then we
     * have the possibility to invoke prepare() and prepare(dispatched element)
     * directly successively.<br /> The order of invocation can also be imagined
     * as following: <ol> <li> <ol> <li>{@link DrawAction#setUp()}</li>
     * <li>{@link DrawAction#setUp()}</li> <li>..</li> </ol> </li> <li> <ol>
     * <li>{@link DrawAction#prepare()}</li> <li>{@link DrawAction#prepare(dispatched
     * element)}</li> </ol> <ol> <li>{@link DrawAction#prepare()}</li>
     * <li>{@link DrawAction#prepare(dispatched element)}</li> </ol> <ol>
     * <li>..</li> <li>..</li> </ol> </li> </ol>
     */
    @Override
    public void prepare(Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Element element, Layout layout) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Wire wire, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(wire, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Component component, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(component, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Switch s, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(s, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Light light, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(light, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(And and, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(and, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Or or, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(or, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Not not, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(not, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(IdPoint idPoint, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(idPoint, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Input input, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(input, layout);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(Output output, Layout layout) {
        for (DrawAction action : actions) {
            action.prepare(layout);
            action.prepare(output, layout);
        }
    }
}
