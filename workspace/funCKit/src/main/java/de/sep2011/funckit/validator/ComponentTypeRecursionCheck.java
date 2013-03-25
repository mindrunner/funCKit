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

package de.sep2011.funckit.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.util.internationalization.Language;

/**
 * This check verifies that there is no recursion in any {@link ComponentType}
 * of any {@link Component} in a {@link Circuit}. This means no {@link Circuit}
 * can (over some {@link Component}s and {@link ComponentType}s) reference
 * itself.
 */
public class ComponentTypeRecursionCheck implements Check {

    /**
     * Already visited circuits by the depth search.
     */
    private final Set<Circuit> visitedCircutis = new HashSet<Circuit>();

    /**
     * {@link Component}s that have a {@link ComponentType} with a
     * {@link Circuit} that somehow references up in the hierarchy of
     * {@link Component}s.
     */
    private final Set<Element> flawElements = new LinkedHashSet<Element>();

    /**
     * {@inheritDoc} In this case this verifies that there is no recursion in
     * any {@link ComponentType} of any {@link Component} in a {@link Circuit}.
     * This means no {@link Circuit} can (over some {@link Component}s and
     * {@link ComponentType}s) reference itself. This is done by a depth search.
     * If this check fails the {@link Result} contains {@link Component}s that
     * have a {@link ComponentType} with a {@link Circuit} that somehow
     * references up in the hierarchy of {@link Component}s.
     */
    @Override
    public Result perform(Circuit c) {
        flawElements.clear();
        visitedCircutis.clear();

        // look for flaws
        doDepthSearch(c);

        // construct Result object
        String message = "check." + this.getClass().getSimpleName();
        if (flawElements.isEmpty()) {
            message += ".passedMessage";
        } else {
            message += ".failedMessage";
        }
        return new Result(flawElements.isEmpty(), Language.tr(message),
                flawElements, this);
    }

    /**
     * Perform a depth search on the given {@link Circuit} by looking for
     * {@link Component}s in it going recursively deeper in the
     * {@link ComponentType} and the {@link Circuit} it has.
     * 
     * @param c
     *            {@link Circuit} to search on.
     */
    private void doDepthSearch(Circuit c) {
        visitedCircutis.add(c);

        // sum up all Circuits of the Components at this layer
        Set<Circuit> localCircuits = new HashSet<Circuit>();
        Map<Circuit, Set<Component>> circuitOwners = new HashMap<Circuit, Set<Component>>();
        for (Element e : c.getElements()) {
            if (e instanceof Component) {
                Component component = (Component) e;
                Circuit localCircuit = component.getType().getCircuit();
                localCircuits.add(localCircuit);
                if (!circuitOwners.containsKey(localCircuit)) {
                    Set<Component> components = new LinkedHashSet<Component>();
                    circuitOwners.put(localCircuit, components);
                }
                Set<Component> components = circuitOwners.get(localCircuit);
                components.add(component);
            }
        }

        // check for all found circuits if they are already visited
        for (Circuit localCircuit : localCircuits) {

            // recursion found? => add all components with the circuit to
            // flawElements
            if (visitedCircutis.contains(localCircuit)) {
                flawElements.addAll(circuitOwners.get(localCircuit));
            } else { // keep on searching
                doDepthSearch(localCircuit);
            }
        }
    }

    @Override
    public String getName() {
        return Language.tr("Check." + this.getClass().getSimpleName());
    }

}
