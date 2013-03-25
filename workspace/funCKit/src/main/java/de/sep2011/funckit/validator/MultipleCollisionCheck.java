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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * A Check which checks if a set of {@link Brick}s collides with another
 * {@link Brick} inside a {@link Circuit}. Does not return any affected
 * Elements.
 */
public class MultipleCollisionCheck implements Check {

    private final Collection<Element> elements;

    /**
     * Create a new {@link CollisionCheck}.
     * 
     * @param bricks
     *            the Bricks to test if it collides
     */
    public MultipleCollisionCheck(Collection<? extends Element> bricks) {
        this.elements = new LinkedList<Element>(bricks);
    }

    @Override
    public String getName() {
        return Language.tr("Check." + this.getClass().getSimpleName());
    }

    @Override
    public Result perform(Circuit c) {
        boolean passed = true;

        Set<Element> cols;
        for (Element element : elements) {
            if (element instanceof Brick) {
                Brick b = (Brick) element;
                cols = c.getIntersectingElements(b.getBoundingRect());
                for (Element e : cols) {
                    if (!(e instanceof Wire)) {
                        passed = false;
                    }
                }
                if (!passed) {
                    break;
                }
            }
        }

        // construct Result object
        String message = "check." + this.getClass().getSimpleName();
        if (passed) {
            message += ".passedMessage";
        } else {
            message += ".failedMessage";
        }
        Result result = new Result(passed, Language.tr(message),
                new LinkedList<Element>(), this);
        return result;
    }

}