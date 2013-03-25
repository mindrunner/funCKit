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

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.util.internationalization.Language;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Checks if all Elements in circuit are on positive positions.
 */
public class PositivePositionCheck implements Check {

    private final Set<Element> flawElements = new LinkedHashSet<Element>();
    private final Map<ComponentType, Boolean> typeCheckResult = new HashMap<ComponentType, Boolean>();

    @Override
    public Result perform(Circuit c) {
        flawElements.clear();
        typeCheckResult.clear();

        checkCircuit(c);

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

    private boolean checkCircuit(Circuit circuit) {
        boolean hasFlaw = false;

        // look for bricks with a negative position
        for (Element e : circuit.getElements()) {
            if (e instanceof Brick) {
                int x = e.getPosition().x;
                int y = e.getPosition().y;
                if (x < 0 || y < 0) {
                    flawElements.add(e);
                    hasFlaw = true;
                }
            }

            // check component's type additionally
            if (e instanceof Component) {
                Component comp = (Component) e;
                ComponentType type = comp.getType();
                if (!typeCheckResult.containsKey(type)) {
                    typeCheckResult.put(type, checkCircuit(type.getCircuit()));
                }
                boolean typeHasFlaw = typeCheckResult.get(type);
                if (typeHasFlaw) {
                    hasFlaw = true;
                    flawElements.add(comp);
                }
            }
        }
        return hasFlaw;
    }

    @Override
    public String getName() {
        return Language.tr("Check." + this.getClass().getSimpleName());
    }
}
