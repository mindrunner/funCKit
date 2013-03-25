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
 * <p><code>DrawAction</code> is an object that applies modifications on an
 * injected layout object. A concrete implementation of <code>DrawAction</code>
 * has a specific duty like only applying modifications of a certain context.
 * That can be changes for elements with error notifications or elements, that
 * are currently simulated. Changes can be overwritten by other action
 * objects.<br /> It is important to maintain the following invocation order, to
 * make sure, that actions can rely on each other. For example colorizing
 * actions assume, that other actions have defined certain shapes before.</p>
 * <p><ol> <li> <ol> <li>{@link DrawAction#setUp()}</li> <li>{@link
 * DrawAction#setUp()}</li> <li>..</li> </ol> </li> <li> <ol> <li>{@link
 * DrawAction#prepare()}</li> <li>{@link DrawAction#prepare(dispatched
 * element)}</li> </ol> <ol> <li>{@link DrawAction#prepare()}</li> <li>{@link
 * DrawAction#prepare(dispatched element)}</li> </ol> <ol> <li>..</li>
 * <li>..</li> </ol> </li> </ol></p> <p>This invocation order dictates, that all
 * <code>setUp()</code>-methods have to be called before other methods of the
 * same <code>DrawAction</code> gets invoked. Basically the order of
 * <code>setUp()</code> is not important. Incidentally this means, that you can
 * not assume any modifications on the layout object, yet. After setting up
 * (injecting dependencies etc.) the actions, each action has two methods, that
 * get invoked: first a general <code>prepare()</code>-method to apply changes
 * to the layout independent of a concrete element and second a concrete
 * prepare()-method for a (probably dispatched) element. As actions must
 * sometimes assume modifications of previously performed actions, the order of
 * prepare()-calls must be met.<br /> If you use a {@link ActionComposite} for
 * chaining several <code>DrawAction</code> objects, this object handles the
 * right invocation order of the appended actions. You just have to call</p>
 * <p><pre>
 *     actionComposite.setUp()
 *     actionComposite.prepare(layout)
 *     actionComposite.prepare(element, layout)
 * </pre></p>
 */
public interface DrawAction {
    /**
     * Method called before any <code>prepare</code>-method gets invoked to
     * setting up the action and perform general layout modifications for any
     * type of element. At this point you can not assume, that any modification
     * is done on the given layout, yet. This is used for object injection,
     * caching and general calculations before preparing the layout object by
     * invoking concrete prepare(Element)-methods.
     *
     * @param layout   Layout object to modify.
     * @param settings Current settings object. A lot of settings are assumed to
     *                 be not null see ({@link Settings})!
     * @param graphics Graphics object for reading purpose (no drawing).
     */
    public void setUp(Layout layout, Settings settings, Graphics2D graphics);

    /**
     * General preparation method without knowledge about the concrete object,
     * which layout object is going to be modified. This can be used to use
     * properties, that are defined by previous prepare-element-methods from
     * other actions. For example you can assume, that colorizing actions are
     * performed after basic shape actions, so basic shapes are defined in given
     * layout object in this method. This assumption can not be made in the
     * {@link DrawAction#setUp()}-method, as shapes are not defined in a
     * setUp()-routine. Only after concrete prepare(Element)-calls shapes are
     * set.
     *
     * @param layout A layout object that got modified by previous action
     *               invocations.
     */
    public void prepare(Layout layout);

    /**
     * Prepares a general (non-dispatched) element.
     *
     * @param element Element from graphmodel.
     * @param layout To element associated layout object.
     */
    public void prepare(Element element, Layout layout);

    /**
     * Preparation method for dispatched wires. Depending on concrete action,
     * certain modifications, that apply only for {@link Wire}s can be done
     * here.
     *
     * @param wire Dispatched wire object from graphmodel.
     * @param layout Associated layout object.
     */
    public void prepare(Wire wire, Layout layout);

    /**
     * Preparation method for dispatched {@link Component}s.
     *
     * @param component Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Component component, Layout layout);

    /**
     * Preparation method for dispatched {@link Switch}es.
     *
     * @param s Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Switch s, Layout layout);

    /**
     * Preparation method for dispatched lights.
     *
     * @param light Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Light light, Layout layout);

    /**
     * Preparation method for dispatched {@link And} gates.
     *
     * @param and Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(And and, Layout layout);

    /**
     * Preparation method for dispatched {@link Or} gates.
     *
     * @param or Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Or or, Layout layout);

    /**
     * Preparation method for dispatched {@link Not} gates.
     *
     * @param not Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Not not, Layout layout);

    /**
     * Preparation method for dispatched {@link IdPoint}s.
     *
     * @param idPoint Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(IdPoint idPoint, Layout layout);

    /**
     * Preparation method for dispatched {@link Input}s.
     *
     * @param input Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Input input, Layout layout);

    /**
     * Preparation method for dispatched {@link Output}s.
     *
     * @param output Dispatched element.
     * @param layout Layout object, on which modifications should be applied.
     */
    public void prepare(Output output, Layout layout);
}
