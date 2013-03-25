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

package de.sep2011.funckit.view;

import javax.swing.AbstractListModel;

import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;

/**
 * The data model feeding {@link NewBrickList}.
 */
public class NewBrickListModel extends AbstractListModel implements
        SessionModelObserver {

    private static final long serialVersionUID = 5974321931883797194L;

    private View view;
    
    /**
     * Create a new {@link NewBrickListModel}.
     * 
     * @param view
     *            the associated {@link View} object
     */
    public NewBrickListModel(View view) {
        super();
        init(view);
    }

    private void init(View view) {
        this.view = view;
        this.view.getSessionModel().addObserver(this);
    }

    @Override
    public Object getElementAt(int arg0) {
        return view.getSessionModel().getNewBrickList().get(arg0).getName();


    }

    @Override
    public int getSize() {
        return view.getSessionModel().getNewBrickList().size();
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.isNewBrickListChanged()
                && i.getNewBrickListRemovedIndices() != null) {
            
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (int index : i.getNewBrickListRemovedIndices()) {
                if(index < min) {
                    min = index;
                }
                
                if(index > max) {
                    max = index;
                }
            }
            
            fireIntervalRemoved(this, min, max);
        }
        
        if (i.isNewBrickListChanged()
                && i.getNewBrickListAddedIndices() != null) {
            
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (int index : i.getNewBrickListAddedIndices()) {
                if(index < min) {
                    min = index;
                }
                
                if(index > max) {
                    max = index;
                }
            }
            
            fireIntervalAdded(this, min, max);
        }
    }

}
