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

package de.sep2011.funckit.model.sessionmodel;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.observer.AbstractObservable;
import de.sep2011.funckit.observer.EditPanelModelInfo;
import de.sep2011.funckit.observer.EditPanelModelObserver;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.view.EditPanel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Model object that is assigned to every editing tab for storing session
 * information like current selected elements or the circuit object that is
 * modeled on.
 */
public class EditPanelModel extends
        AbstractObservable<EditPanelModelObserver, EditPanelModelInfo> {

    private AffineTransform translation;

    private AffineTransform zoom;

    private double centerX;

    private double centerY;
    private Brick activeBrick;

    public enum ToolMode {
        DEFAULT_MODE, CREATE_WIRE_PATH_FROM_AP, CREATE_WIRE_PATH_FROM_WIRE,
        CREATE_SINGLE_WIRE_MODE, SELECT_RECT_MODE, MOVE_ELEMENT_MODE
    }

    private ToolMode toolMode;

    /**
     * Associated circuit object, that can be part of a component type and thus
     * be indirect part of another circuit.
     */
    private Circuit circuit;

    /**
     * The path of components the circuit lays in.
     */
    private Deque<Component> componentStack;

    /**
     * Current selected elements on edit panel.
     */
    private Set<Element> selectedElements;

    /**
     * Elements that are currently moved (copy of selected elements with moved
     * position).
     */
    private Set<Element> ghosts;

    /**
     * Stores the current cursor for the edit panel.
     */
    private Cursor cursor;

    /**
     * Current selected brick for the Gridlock!
     */
    private Brick selectedBrick;

    private Point selectionStart;
    private Point selectionEnd;

    private Point dragStartPoint;
    
	/**
	 * First Brick selected to connect. Here the Outputs are used.
	 */
    private Brick multiConnectBrick1;
    
    /**
     * Second Brick selected to connect. Here the Inputs are used.
     */
    private Brick multiConnectBrick2;

	/**
     * Create a new EditPanelModel.
     * 
     * @param circuit
     *            the associated circuit, not null
     * @param name
     *            name of the EditPanel.
     * @param componentStack
     */
    public EditPanelModel(Circuit circuit, Deque<Component> componentStack) {
        checkNotNull(circuit, "The Circuit is null but should not");
        assert componentStack != null;
        assert circuit != null;
        initialize(circuit, componentStack);
    }

    private void initialize(Circuit circuit, Deque<Component> componentStack) {
        translation = new AffineTransform();
        zoom = new AffineTransform();
        this.circuit = circuit;
        this.componentStack = componentStack;
        selectedElements = new LinkedHashSet<Element>();
        ghosts = new LinkedHashSet<Element>();
        toolMode = ToolMode.DEFAULT_MODE;
        initInfo(EditPanelModelInfo.getInfo());
        cursor = Cursor.getDefaultCursor();
    }

    /**
     * Returns the current {@link Cursor} for the {@link EditPanel}.
     * 
     * @return the current {@link Cursor} for the {@link EditPanel}
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * Sets the Cursor.
     * 
     * @param cursor
     *            the new cursor, if the same do not notify observers.
     */
    public void setCursor(Cursor cursor) {
        if (!this.cursor.equals(cursor)) {
            this.cursor = cursor;
            setChanged();
            getInfo().setCursorChanged(true);
            notifyObserversIfAuto();
        }
    }

    public void setActiveBrick(Brick activeBrick) {
        boolean changed = false;
        if (this.activeBrick != activeBrick) {
            changed = true;
        }
        this.activeBrick = activeBrick;
        if (changed) {
            setChanged();
            getInfo().setActiveChanged(true);
            notifyObserversIfAuto();
        }
    }

    public Brick getActiveBrick() {
        return activeBrick;
    }

    /**
     * Returns the associated {@link Circuit} of this Model.
     * 
     * @return the circuit
     */
    public Circuit getCircuit() {
        return this.circuit;
    }

    /**
     * @return The current Tool mode
     */
    public ToolMode getToolMode() {
        return toolMode;
    }

    /**
     * @param toolMode
     *            the new Tool Mode, not null
     */
    public void setToolMode(ToolMode toolMode) {
    	Log.gl().debug("Set toolmode to " + toolMode );
        this.toolMode = toolMode;
    }

    /**
     * Return the start point of the selection start and end define a rectangle.
     * 
     * @return the start point of the selection
     */
    public Point getSelectionStart() {
        return selectionStart;
    }

    /**
     * Sets the start point of the selection start and end define a rectangle.
     * 
     * @param selectionStart
     *            start point of the selection
     */
    public void setSelectionStart(Point selectionStart) {
        this.selectionStart = selectionStart;
        setChanged();
        getInfo().setSelectionChanged();
        notifyObserversIfAuto();
    }

    /**
     * Return the end point of the selection start and end define a rectangle.
     * 
     * @return the end point of the selection
     */
    public Point getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * Sets the end point of the selection start and end define a rectangle.
     * 
     * @param selectionEnd
     *            end point of the selection
     */
    public void setSelectionEnd(Point selectionEnd) {
        this.selectionEnd = selectionEnd;
        setChanged();
        getInfo().setSelectionChanged();
        notifyObserversIfAuto();
    }

    /**
     * Getter method for selected elements.
     * 
     * @return Set of selected elements.
     */
    public Set<Element> getSelectedElements() {
        return selectedElements;
    }

    /**
     * Specifies current selected elements. Has to be non null.
     * 
     * @param selectedElements
     *            set of current selected elements
     */
    public void setSelectedElements(Set<Element> selectedElements) {
        assert selectedElements != null;
        this.selectedElements = selectedElements;
        setChanged();
        getInfo().setSelectionChanged();
        notifyObserversIfAuto();
    }

    /**
     * @param b
     *            selected brick
     */
    public void setSelectedBrick(Brick b) {
        selectedBrick = b;
    }

    public Brick getSelectedBrick() {
        return selectedBrick;
    }
    
    
    public Brick getMultiConnectBrick1() {
		return multiConnectBrick1;
	}

	public void setMultiConnectBrick1(Brick multiConnectBrick1) {
		this.multiConnectBrick1 = multiConnectBrick1;
	}
	
    public Brick getMultiConnectBrick2() {
		return multiConnectBrick2;
	}

	public void setMultiConnectBrick2(Brick multiConnectBrick2) {
		this.multiConnectBrick2 = multiConnectBrick2;
	}

    /**
     * Getter method for current moving ghosts.
     * 
     * @return current moving ghosts.
     */
    public Set<Element> getGhosts() {
        return ghosts;
    }

    /**
     * Returns the current ghost Elements to accept. (e.g. on moving Elements)
     * 
     * @param ghosts
     *            the current ghost Elements to accept
     */
    public void setGhosts(Set<Element> ghosts) {
        assert ghosts != null;
        this.ghosts = ghosts;
        setChanged();
        getInfo().setGhostsChanged();
        notifyObserversIfAuto();
    }

    /**
     * Get the path of components the circuit lays in.
     * 
     * @return parentComponent of the circuit.
     */
    public Deque<Component> getComponentStack() {
        return componentStack;
    }

    /**
     * Returns a {@link AffineTransform} to control drawing of the
     * {@link EditPanel}. If you change it, call
     * {@link #transformationChanged()}
     * 
     * @return The current {@link AffineTransform} for this {@link EditPanel}
     * @since implementation
     */
    public AffineTransform getTransformation() {

        AffineTransform inverseCenter =
                AffineTransform.getTranslateInstance(-centerX, -centerY);
        AffineTransform transformation = inverseCenter; // 4. move back to edge
        // transformation.concatenate(AffineTransform.getShearInstance(0,
        // -0.2));
        transformation.concatenate(zoom); // 3. zoom
        transformation.concatenate(AffineTransform.getTranslateInstance(
                centerX, centerY)); // 2. move to center
        transformation.concatenate(translation); // 1. move to position
        return transformation;
    }

    public void translate(double tx, double ty) {
        translation.translate(-tx, -ty);
        transformationChanged();
    }

    public void setTranslationX(double tx) {
        translation.setToTranslation(-tx, translation.getTranslateY());
        transformationChanged();
    }

    public void setTranslationY(double ty) {
        translation.setToTranslation(translation.getTranslateX(), -ty);
        transformationChanged();
    }

    public void zoom(double zoom) {
        this.zoom.scale(zoom, zoom);
        transformationChanged();
    }

    public void setZoom(double newZoom) {
        zoom = AffineTransform.getScaleInstance(newZoom, newZoom);
        transformationChanged();
    }

    public void setCenter(double x, double y) {

        double zoom_mat[] = new double[6];
        double dx = 0;
        double dy = 0;
        zoom.getMatrix(zoom_mat);

        /*
         * calculate translation correction so that the new center doesn't
         * affect the current view (the new transformation matrix equals the old
         * one - be aware that this has a loss of precision!)
         */
        if (zoom_mat[0] != 0) {
            dx = centerX + x + (-x - centerX) / zoom_mat[0];
        }
        if (zoom_mat[3] != 0) {
            dy = centerY + y + (-y - centerY) / zoom_mat[3];
        }

        translation.translate(dx, dy);

        centerX = -x;
        centerY = -y;

        transformationChanged();
    }

    public Point getTranslation() {
        return new Point((int) translation.getTranslateX(),
                (int) translation.getTranslateY());
    }

    // public void setRotation(...) ??

    /**
     * This method has to be called after doing some change on the
     * {@link AffineTransform} object.
     * 
     * @since implementation
     */
    private void transformationChanged() {
        setChanged();
        getInfo().setTransformChanged(true);
        notifyObserversIfAuto();
    }

    /**
     * returns the Point where the viewport dragging started.
     * 
     * @return the Point where the viewport dragging started, null indicates
     *         that dragging is not enabled
     * @since implementation
     */
    public Point getDragStartPoint() {
        return dragStartPoint;
    }

    /**
     * set the Point where the viewport dragging started.
     * 
     * @param dragStartPoint
     *            the Point where the viewport dragging started, null indicates
     *            that dragging is not enabled
     * @since implementation
     */
    public void setDragStartPoint(Point dragStartPoint) {
        this.dragStartPoint = dragStartPoint;
    }

    public boolean hasMainCircuit() {
        return componentStack.isEmpty();
    }

    @Override
    public void notifyObserver(EditPanelModelInfo i, EditPanelModelObserver obs) {
        obs.editPanelModelChanged(this, i);
    }

}