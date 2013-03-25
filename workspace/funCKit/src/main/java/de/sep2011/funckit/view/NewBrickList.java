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

import de.sep2011.funckit.controller.listener.RemoveBrickFromNewBrickListActionListener;
import de.sep2011.funckit.controller.listener.project.OpenNewProjFromNewBrickListActionListener;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager.BrickListType;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import static de.sep2011.funckit.util.internationalization.Language.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * NewBrickList presents the user a List of {@link Brick}s and {@link Component}
 * s when in add new Brick mode.
 */
public class NewBrickList extends JList implements SessionModelObserver {

    private static final long serialVersionUID = 425258243387120757L;

    private View view;
    private JPopupMenu popupMenu;
    private JMenuItem openAsNewProjectItem;

    private JMenuItem removeItem;

    /**
     * Create a new {@link NewBrickList}.
     * 
     * @param view
     *            the associated view object
     */
    public NewBrickList(View view) {
        super();
        initialize(view);

    }

    private void initialize(View view) {
        this.view = view;
        popupMenu = new JPopupMenu();
        openAsNewProjectItem =
                new JMenuItem(tr("NewBrickList.OpenAsNewProjectMenuItem"));
        openAsNewProjectItem
                .addActionListener(new OpenNewProjFromNewBrickListActionListener(
                        view, view.getController(), this));
        removeItem = new JMenuItem(tr("NewBrickList.RemoveMenuItem"));
        removeItem
                .addActionListener(new RemoveBrickFromNewBrickListActionListener(
                        view, view.getController(), this));

        popupMenu.add(openAsNewProjectItem);
        popupMenu.addSeparator();
        popupMenu.add(removeItem);

        setModel(new NewBrickListModel(view));

        getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        Brick curr = view.getSessionModel().getCurrentBrick();
        setSelectedIndex(view.getSessionModel().getNewBrickList().indexOf(curr));

        addMouseListener(new PopupMenuListener());
        view.getSessionModel().addObserver(this);
    }

    private class PopupMenuListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            check(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            check(e);
        }

        public void check(MouseEvent e) {
            if (e.isPopupTrigger()) { // if the event shows the menu
                setSelectedIndex(locationToIndex(e.getPoint())); // select
                popupMenu.show(NewBrickList.this, e.getX(), e.getY()); // show
                NewBrickListManager mgr =
                        view.getSessionModel().getNewBrickListManager();
                int index = getSelectedIndex();
                BrickListType type = mgr.getType(index);

                removeItem.setEnabled(false);
                openAsNewProjectItem.setEnabled(false);

                if (type != BrickListType.TYPE_BRICK
                        && view.getSessionModel().getViewType() == SessionModel.ViewType.VIEW_TYPE_STANDALONE) {
                    openAsNewProjectItem.setEnabled(true);
                }

                if (type == BrickListType.TYPE_EXTERNAL_COMPONENT
                        && view.getSessionModel().getViewType() == SessionModel.ViewType.VIEW_TYPE_STANDALONE) {
                    removeItem.setEnabled(true);
                }
            }
        }

    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.isCurrentBrickChanged()) {
            Brick curr = view.getSessionModel().getCurrentBrick();
            setSelectedIndex(view.getSessionModel().getNewBrickList()
                    .indexOf(curr));
        }
    }
}
