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

import de.sep2011.funckit.controller.listener.editpanel.EditPanelScrollPaneHorizontalScrollbarBoundedRangeModelChangeListener;
import de.sep2011.funckit.controller.listener.editpanel.EditPanelScrollPaneVerticalScrollbarBoundedRangeModelChangeListener;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.observer.EditPanelModelInfo;
import de.sep2011.funckit.observer.EditPanelModelObserver;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.util.Log;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import net.miginfocom.swing.MigLayout;

import java.awt.Adjustable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * A Scrollpane for a {@link EditPanel}. It implements ComponentListener and
 * {@link EditPanelModelObserver} to adjust the scrollbars appropriately when
 * the {@link EditPanel} or the associated {@link EditPanelModel} change.
 */
public class EditPanelScrollPane extends JPanel implements ComponentListener,
        EditPanelModelObserver, GraphModelObserver {

    /**
     * 
     */
    private static final long serialVersionUID = 3881046211805115478L;
    private JScrollBar southScrollBar;
    private JScrollBar eastScrollBar;
    private EditPanel editPanel;

    private static final int SCROLLBAR_UNIT_INCREMENT = 14;

    private boolean scrollbarsAdjustingNotFromUser = false;

    /**
     * Caches the Bounding rect of the circuit. Set it to null if it changed
     */
    private Rectangle graphRectangleCache;

    /**
     * Specifies additional pixels the scrollbars should scroll in every
     * direction (additional to the Circuit bounding rect).
     */
    private static final int ADDITIONAL_SCROLLBAR_SPACE = 15;

    /**
     * Create a new {@link EditPanelScrollPane} with no {@link EditPanel} set.
     * 
     * @param view
     *            the associated {@link View} object, should not be null
     */
    public EditPanelScrollPane(View view) {
        super();
        initialize(null, view);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        adjustScrollbarsRange();
    }

    /**
     * Create a new {@link EditPanelScrollPane}.
     * 
     * @param panel
     *            The {@link EditPanel} which will be in the center of this
     *            ScrollPane
     * @param view
     *            the associated {@link View} object, should not be null
     */
    public EditPanelScrollPane(View view, EditPanel panel) {
        super();
        initialize(panel, view);
    }

    /**
     * Create a new {@link EditPanelScrollPane}.
     * 
     * @param panel
     *            The {@link EditPanel} which will be in the center of this
     *            ScrollPane, can be null
     * @param isDoubleBuffered
     *            see {@link JPanel#JPanel(boolean)}
     * @param view
     *            the associated {@link View} object, should not be null
     */
    public EditPanelScrollPane(View view, EditPanel panel, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        initialize(panel, view);
    }

    private void initialize(EditPanel panel, View view) {
        southScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
        southScrollBar.setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        southScrollBar.getModel().addChangeListener(
                new EditPanelScrollPaneHorizontalScrollbarBoundedRangeModelChangeListener(view,
                        view.getController(), this));
        eastScrollBar = new JScrollBar(Adjustable.VERTICAL);
        eastScrollBar.setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        eastScrollBar.getModel().addChangeListener(
                new EditPanelScrollPaneVerticalScrollbarBoundedRangeModelChangeListener(view, view
                        .getController(), this));

        this.setLayout(new MigLayout("fill, insets 0"));

        add(southScrollBar, "dock south, growx");
        add(eastScrollBar, "dock east, growy");

        if (panel != null) {
            setEditPanel(panel);
        }

    }

    /**
     * Set a {@link EditPanel} to be in the center of this
     * {@link EditPanelScrollPane}. This Scrollpane Automatically registers
     * itself as observer of the {@link EditPanel} and the
     * {@link EditPanelModel} and unregisters from the old.
     * 
     * @param c
     *            {@link EditPanel} to manage, null to unset the current set one
     */
    public void setEditPanel(EditPanel c) {
        if (editPanel != null) {
            editPanel.removeComponentListener(this);
            editPanel.getPanelModel().deleteObserver(this);
            editPanel.getPanelModel().getCircuit().deleteObserver(this);
            remove(editPanel);
        }

        if (c != null) {

            editPanel = c;
            editPanel.addComponentListener(this);
            editPanel.getPanelModel().addObserver(this);
            editPanel.getPanelModel().getCircuit().addObserver(this);

            add(c, "dock center, grow");

            graphRectangleCache = null; // clear cache
            adjustScrollbarsRange();
        }
    }

    /**
     * Get the set {@link EditPanel}.
     * 
     * @return the {@link EditPanel} or null if unset
     */
    public EditPanel getEditPanel() {
        return editPanel;
    }

    @Override
    public void editPanelModelChanged(EditPanelModel source, EditPanelModelInfo i) {
        if (i.isTransformChanged()) {
            adjustScrollbarsRange();
        }

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // no interest

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // no interest

    }

    @Override
    public void componentResized(ComponentEvent e) {
        adjustScrollbarsRange();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        adjustScrollbarsRange();
    }

    private void adjustScrollbarsRange() {
        scrollbarsAdjustingNotFromUser = true;

        AffineTransform tr = editPanel.getPanelModel().getTransformation();
        if (graphRectangleCache == null) {
            graphRectangleCache = editPanel.getPanelModel().getCircuit().getBoundingRectangle();
        }

        Point viewPos1 = new Point();
        Point viewPos2 = new Point(editPanel.getSize().width, editPanel.getSize().height);
        try {
            tr.inverseTransform(viewPos1, viewPos1);
            tr.inverseTransform(viewPos2, viewPos2);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
            Log.gl().error(e1);
        }
        Rectangle viewRect = new Rectangle(viewPos1.x, viewPos1.y, viewPos2.x - viewPos1.x,
                viewPos2.y - viewPos1.y);

        Rectangle boundingRect = graphRectangleCache.createUnion(viewRect).getBounds();
        boundingRect.grow(ADDITIONAL_SCROLLBAR_SPACE, ADDITIONAL_SCROLLBAR_SPACE);

        double southMin = boundingRect.x;
        double southMax = boundingRect.x + boundingRect.width;
        double eastMin = boundingRect.y;
        double eastMax = boundingRect.y + boundingRect.height;

        int southValue = viewPos1.x;
        int eastValue = viewPos1.y;

        southScrollBar.getModel().setRangeProperties(southValue, viewRect.width, (int) southMin,
                (int) southMax, southScrollBar.getModel().getValueIsAdjusting());
        eastScrollBar.getModel().setRangeProperties(eastValue, viewRect.height, (int) eastMin,
                (int) eastMax, eastScrollBar.getModel().getValueIsAdjusting());

        scrollbarsAdjustingNotFromUser = false;
    }

    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {
        graphRectangleCache = null; // clear cache
        adjustScrollbarsRange();
    }

    /**
     * Return true if the Scrollbars are adjusted internally. (observers,...)
     * 
     * @return true if the Scrollbars are adjusted internally
     * @since implementation
     */
    public boolean isScrollbarsAdjustingNotFromUser() {
        return scrollbarsAdjustingNotFromUser;
    }

}
