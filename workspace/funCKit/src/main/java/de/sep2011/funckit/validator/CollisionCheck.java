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
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.util.internationalization.Language;

import java.util.Iterator;
import java.util.Set;

/**
 * A Check which checks if a {@link Brick} collides with another {@link Brick}
 * inside a {@link Circuit}.
 */
public class CollisionCheck implements Check {

    private Brick brick;

    /**
     * Create a new {@link MultipleCollisionCheck}.
     * 
     * @param brick
     *            the Brick to test if it collides
     */
    public CollisionCheck(Brick brick) {
        assert brick != null;
        this.brick = brick;
    }

    @Override
    public String getName() {
        return Language.tr("Check." + this.getClass().getSimpleName());
    }

    @Override
    public Result perform(Circuit circuit) {
        assert circuit != null;

        Set<Element> collisions =
                circuit.getIntersectingElements(brick.getBoundingRect());

        // colliding wires don't matter => remove them
        for (Iterator<Element> i = collisions.iterator(); i.hasNext();) {
            Element e = i.next();
            if (e instanceof Wire) {
                i.remove();
            }
        }

        // construct Result object
        String message = "check." + this.getClass().getSimpleName();
        if (collisions.isEmpty()) {
            message += ".passedMessage";
        } else {
            message += ".failedMessage";
        }
        return new Result(collisions.isEmpty(), Language.tr(message, brick),
                collisions, this);
    }

}