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

import de.sep2011.funckit.drawer.action.ActionComposite;
import de.sep2011.funckit.drawer.action.ActiveAction;
import de.sep2011.funckit.drawer.action.ColorizeAction;
import de.sep2011.funckit.drawer.action.DebugAction;
import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.drawer.action.ErrorAction;
import de.sep2011.funckit.drawer.action.FancyShapeAction;
import de.sep2011.funckit.drawer.action.GhostAction;
import de.sep2011.funckit.drawer.action.InfoAction;
import de.sep2011.funckit.drawer.action.QueueAction;
import de.sep2011.funckit.drawer.action.SelectedAction;
import de.sep2011.funckit.drawer.action.SimpleShapeAction;
import de.sep2011.funckit.drawer.action.SimulationAction;
import de.sep2011.funckit.drawer.action.TextDelayAction;
import de.sep2011.funckit.drawer.action.TextInputLabelAction;
import de.sep2011.funckit.drawer.action.TextNameAction;
import de.sep2011.funckit.drawer.action.TextOutputLabelAction;
import de.sep2011.funckit.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class DecisionTable {
    /**
     * This map is the actual decision table, that gets created statically, so
     * we have to initialize it just once (performance). In this map each {@link
     * ElementState} is associated with a {@link DrawAction}, that is used to
     * build a ready layout object for the {@link Element}, which is mapped by
     * its <code>ElementState</code> to it.
     */
    private static final Map<ElementState, DrawAction> decisionTable = new LinkedHashMap<ElementState, DrawAction>();

    static {
        /*
         * Default action to add to all fancy drawings. Do NOT (NOT!) add any
         * actions to this ActionComposite during constructing the decision
         * table as this would modify the SAME object, that is used for all
         * states using this default action!
         */
        ActionComposite defaultAction = new ActionComposite()
                .addAction(new FancyShapeAction())
                .addAction(new ColorizeAction())
                .addAction(new TextNameAction())
                .addAction(new TextInputLabelAction());
        ActionComposite additionalInfoAction = new ActionComposite()
                .addAction(new TextDelayAction())
                .addAction(new TextOutputLabelAction());

        /* FancyDrawer: Normal */
        ElementState e1 = addModeAction(ElementState.Mode.NORMAL, defaultAction, additionalInfoAction);

        /* FancyDrawer: Normal & has info */
        ElementState e1info = e1.clone().setHasInfo();
        add(e1info, defaultAction, additionalInfoAction, new InfoAction());

        /* FancyDrawer: Normal & has error */
        ElementState e1error = e1.clone().setHasError(true);
        add(e1error, defaultAction, additionalInfoAction, new ErrorAction());

        /* FancyDrawer: Normal & is active */
        ElementState e1active = e1.clone().setActive();
        add(e1active, defaultAction, additionalInfoAction, new ActiveAction());

        /* FancyDrawer: Normal & has info & has error */
        ElementState e1infoError = e1info.clone().setHasError(true);
        add(e1infoError, defaultAction, additionalInfoAction, new ErrorAction());

        /* FancyDrawer: Normal & has info & is active */
        ElementState e1infoActive = e1info.clone().setActive();
        add(e1infoActive, defaultAction, additionalInfoAction, new InfoAction());

        /* FancyDrawer: Normal & has error & is active */
        ElementState e1errorActive = e1error.clone().setActive();
        add(e1errorActive, defaultAction, additionalInfoAction, new ErrorAction());

        /* FancyDrawer: Normal & simulated */
        ElementState e1simulated = e1.clone().setSimulated(true);
        add(e1simulated, defaultAction, additionalInfoAction, new SimulationAction());

        /* FancyDrawer: Normal & simulated & active */
        ElementState e1simulatedActive = e1simulated.clone().setActive();
        add(e1simulatedActive, defaultAction, new SimulationAction(),
                new QueueAction());

        /* FancyDrawer: Selected */
        ElementState e2 = addModeAction(
                ElementState.Mode.SELECTED,
                defaultAction,
                additionalInfoAction,
                new SelectedAction());

        /* FancyDrawer: Selected & active */
        ElementState e2active = e2.clone().setActive();
        add(e2active, defaultAction, additionalInfoAction, new SelectedAction());

        /* FancyDrawer: Selected & has error */
        ElementState e2error = e2.clone().setHasError(true);
        add(e2error, defaultAction, additionalInfoAction, new SelectedAction());

        /* FancyDrawer: Selected & simulated */
        ElementState e2simulated = e2.clone().setSimulated(true);
        add(e2simulated, defaultAction, additionalInfoAction, new SimulationAction());

        /* FancyDrawer: Selected & has error & active */
        ElementState e2errorActive = e2error.clone().setActive();
        add(e2errorActive, defaultAction, additionalInfoAction, new SelectedAction(),
                new ErrorAction());

        /* FancyDrawer: Selected & simulated & active */
        ElementState e2simulatedActive = e2simulated.clone().setActive();
        add(e2simulatedActive, defaultAction, new ActiveAction(), new SimulationAction(),
                new QueueAction());

        /* FancyDrawer: Ghost */
        ElementState e3 = addModeAction(
                ElementState.Mode.GHOST,
                defaultAction,
                additionalInfoAction,
                new GhostAction());

        /* FancyDrawer: Ghost & simulated */
        ElementState e3ghostSimulated = e3.clone();
        e3ghostSimulated.setSimulated(true);
        add(e3ghostSimulated, defaultAction, additionalInfoAction, new GhostAction());

        /* FancyDrawer: Ghost & error */
        ElementState e3ghostError = e3.clone().setHasError(true);
        add(e3ghostError, defaultAction, additionalInfoAction, new GhostAction(), new ErrorAction());

        /* FancyDrawer: Ghost & error & simulated */
        ElementState e3ghostErrorSimulated = e3ghostSimulated.clone()
                .setHasError(true);
        add(e3ghostErrorSimulated, defaultAction, additionalInfoAction, new GhostAction(),
                new ErrorAction());

        /*
         * Default action to add to all simple drawings.
         */
        defaultAction = new ActionComposite()
                .addAction(new SimpleShapeAction())
                .addAction(new ColorizeAction())
                .addAction(new TextNameAction());
        additionalInfoAction = new ActionComposite();

        /* SimpleDrawer: Normal */
        ElementState s1 = e1.clone().setSimple();
        add(s1, defaultAction, additionalInfoAction);

        /* SimpleDrawer: Normal & has info */
        ElementState s1info = s1.clone().setHasInfo();
        add(s1info, defaultAction, additionalInfoAction, new InfoAction());

        /* SimpleDrawer: Normal & has error */
        ElementState s1error = s1.clone().setHasError(true);
        add(s1error, defaultAction, additionalInfoAction, new ErrorAction());

        /* SimpleDrawer: Normal & is active */
        ElementState s1active = s1.clone().setActive();
        add(s1active, defaultAction, additionalInfoAction, new ActiveAction());

        /* SimpleDrawer: Normal & has info & has error */
        ElementState s1infoError = s1info.clone().setHasError(true);
        add(s1infoError, defaultAction, additionalInfoAction, new ErrorAction());

        /* SimpleDrawer: Normal & has info & is active */
        ElementState s1infoActive = s1info.clone().setActive();
        add(s1infoActive, defaultAction, additionalInfoAction, new InfoAction());

        /* SimpleDrawer: Normal & has error & is active */
        ElementState s1errorActive = s1error.clone().setActive();
        add(s1errorActive, defaultAction, additionalInfoAction, new ErrorAction());

        /* SimpleDrawer: Normal & simulated */
        ElementState s1simulated = s1.clone().setSimulated(true);
        add(s1simulated, defaultAction, additionalInfoAction, new SimulationAction());

        /* SimpleDrawer: Normal & simulated & active */
        ElementState s1simulatedActive = s1simulated.clone().setActive();
        add(s1simulatedActive, defaultAction, additionalInfoAction, new ActiveAction(), new SimulationAction());

        /* SimpleDrawer: Selected */
        ElementState s2 = e2.clone().setSimple();
        add(s2, defaultAction, additionalInfoAction, new SelectedAction());

        /* SimpleDrawer: Selected & active */
        ElementState s2active = s2.clone().setActive();
        add(s2active, defaultAction, additionalInfoAction, new SelectedAction(),
                new SimulationAction());

        /* SimpleDrawer: Selected & simulated */
        ElementState s2simulated = s2.clone().setSimulated(true);
        add(s2simulated, defaultAction, additionalInfoAction, new SimulationAction());

        /* SimpleDrawer: Selected & simulated & active */
        ElementState s2simulatedActive = s2simulated.clone().setActive();
        add(s2simulatedActive, defaultAction, additionalInfoAction, new ActiveAction(), new SimulationAction());

        /* SimpleDrawer: Ghost */
        ElementState s3 = e3.clone().setSimple();
        add(s3, defaultAction, additionalInfoAction, new GhostAction());

        /* SimpleDrawer: Ghost & simulated */
        ElementState s3ghostSimulated = s3.clone().setSimulated(true);
        add(s3ghostSimulated, additionalInfoAction, defaultAction, new GhostAction());

        /* SimpleDrawer: Ghost & error */
        ElementState s3ghostError = s3.clone().setHasError(true);
        add(s3ghostError, defaultAction, additionalInfoAction, new GhostAction(), new ErrorAction());

        /* SimpleDrawer: Ghost & error & simulated */
        ElementState s3ghostErrorSimulated = s3ghostSimulated.clone()
                .setHasError(true);
        add(s3ghostErrorSimulated, additionalInfoAction, defaultAction, new GhostAction(),
                new ErrorAction());
    }

    private static ElementState addModeAction(ElementState.Mode mode, DrawAction... actions) {
        ElementState elementState = new ElementState();
        elementState.setMode(mode);
        ActionComposite action = new ActionComposite();
        for (int i = 0, num = actions.length; i < num; i++) {
            action.addAction(actions[i]);
        }
        decisionTable.put(elementState, action);
        return elementState.clone();
    }

    private static void add(ElementState state, DrawAction... actions) {
        ActionComposite c = new ActionComposite();
        for (int i = 0, num = actions.length; i < num; i++) {
            c.addAction(actions[i]);
        }
        decisionTable.put(state, c);
    }

    /**
     * Returns a distinct {@link DrawAction} from our statically created
     * decision table by using a given {@link ElementState} to identify it
     * (distinct path in decision table / map).
     *
     * @param state Identifies state of element to resolve a {@link
     *              DrawAction}.
     * @return The resolved DrawAction.
     */
    public static DrawAction resolve(ElementState state) {
        DrawAction action = decisionTable.get(state);
        if (action == null) {
            Log.gl().error(
                    "Undefined action in decision table for ElementState "
                            + state);
            action = new ActionComposite().addAction(new FancyShapeAction())
                    .addAction(new DebugAction());
        }
        return action;
    }
}
