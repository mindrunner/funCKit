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

import java.util.List;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.AccessPoint;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.validator.Result;

/**
 * Resolver object, that builds an {@link ElementState} with a given {@link
 * Element} upon information collected from {@link SessionModel} and {@link
 * EditPanelModel} (and thus {@link Settings}, too).
 */
public class ElementStateResolver {
    /**
     * Breakpoint value to switch from fancy to simple drawing.
     */
    private final static double ZOOM_BREAKPOINT_DRAW_SIMPLE = 0.4;

    private final EditPanelModel panelModel;
    private final SessionModel sessionModel;

    public ElementStateResolver(EditPanelModel panelModel,
            SessionModel sessionModel) {
        this.panelModel = panelModel;
        this.sessionModel = sessionModel;
    }

    public ElementState resolve(Element element) {
        ElementState elementState = new ElementState();
        elementState.setMode(ElementState.Mode.NORMAL);

        double zoomLevel = panelModel.getTransformation().getScaleX();

        /* Check if element should be drawn very simple - depending on zoom. */
        if (zoomLevel < ZOOM_BREAKPOINT_DRAW_SIMPLE) {
            elementState.setSimple();
        }

        /* Check for ghost or selection mode. */
        if (panelModel.getSelectedElements().contains(element)) {
            elementState.setMode(ElementState.Mode.SELECTED);
        } else if (panelModel.getGhosts().contains(element)) {
            elementState.setMode(ElementState.Mode.GHOST);
        }
        elementState.setSimulated(sessionModel.getCurrentSimulation() != null);

        /* Mark erroneous elements from validations. */
        List<Result> results = sessionModel.getCurrentCheckResults();
        if (results != null) {
            for (Result r : results) {
                if (r.getFlawElements().contains(element)) {
                    elementState.setHasError(true);
                }
            }
        }

        /* Mark erroneous ghosts. */
        Set<Element> erroneousGhosts = sessionModel.getCurrentProject()
                .getErrorGhosts();
        if (erroneousGhosts != null && erroneousGhosts.contains(element)) {
                elementState.setHasError(true);
        }

        /* Check if element is active one (if zoom is near enough). */
        if (panelModel.getActiveBrick() != null
                && panelModel.getActiveBrick().equals(element)) {
            elementState.setActive();
        }

        return elementState;
    }

    public ElementState resolve(AccessPoint accessPoint) {
        Brick brick = accessPoint.getBrick();
        ElementState elementState = new ElementState();
        elementState.setMode(ElementState.Mode.NORMAL);

        double zoomLevel = panelModel.getTransformation().getScaleX();
        if (zoomLevel < ZOOM_BREAKPOINT_DRAW_SIMPLE) {
            elementState.setSimple();
        }

        if (panelModel.getGhosts().contains(brick)) {
            elementState.setMode(ElementState.Mode.GHOST);
        }
        elementState.setSimulated(sessionModel.getCurrentSimulation() != null);

        /* Check if element is active one (if zoom is near enough). */
        if (panelModel.getActiveBrick() != null
                && panelModel.getActiveBrick().equals(brick)) {
            elementState.setActive();
        }

        return elementState;
    }
}
