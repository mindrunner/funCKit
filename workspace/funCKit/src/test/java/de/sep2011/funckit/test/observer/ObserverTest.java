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

package de.sep2011.funckit.test.observer;

import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.observer.EditPanelModelInfo;
import de.sep2011.funckit.observer.EditPanelModelObserver;
import de.sep2011.funckit.observer.FunckitObservable;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link FunckitObservable}.
 */
public class ObserverTest implements EditPanelModelObserver {

    private boolean gotit = false;

    @Before
    public void setUp() {
        gotit = false;
    }

    @Override
    public void editPanelModelChanged(EditPanelModel source, EditPanelModelInfo i) {
        gotit = true;

    }

    /**
     * Test if we recive a notification by using a {@link EditPanelModel} as
     * observable.
     */
    @Test
    public void testEditPanelObserver() {
        EditPanelModel editPanelModel = new EditPanelModel(new CircuitImpl(),
                new LinkedList<Component>());
        editPanelModel.addObserver(this);
        editPanelModel.setChanged();
        editPanelModel.notifyObservers(EditPanelModelInfo.getInfo());
        assertTrue(gotit);

        // Test removing observer
        gotit = false;
        editPanelModel.deleteObserver(this);
        editPanelModel.setChanged();
        editPanelModel.notifyObservers(EditPanelModelInfo.getInfo());
        assertFalse(gotit);
    }

}
