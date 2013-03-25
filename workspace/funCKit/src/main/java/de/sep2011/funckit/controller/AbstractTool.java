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

package de.sep2011.funckit.controller;

import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;
import de.sep2011.funckit.controller.listener.edit.CopyActionListener;
import de.sep2011.funckit.controller.listener.edit.PasteActionListener;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.view.EditPanel;
import javax.swing.Timer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

import static java.lang.Math.abs;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Implements all methods of tool-interface with empty body to make concrete
 * tool implementations more lightweight, as they mostly need only few action
 * methods for their implementation.
 */
public abstract class AbstractTool implements Tool {

    private final static double ZOOM_BREAKPOINT_ELEMENT_ACTIVE = 0.2;

    private long spaceReleaseTimeCode = 0;
    private final Timer spaceReleaseTimer = new Timer(1, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.getSessionModel().restoreTool();
        }
    });

    /**
     * Current mediating controller object with access to model and view
     * objects.
     */
    Controller controller;

    /**
     * Creates a new AbstractTool
     */
    protected AbstractTool() {
        spaceReleaseTimer.setRepeats(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent keyEvent, EditPanelModel editPanelModel) {

    }

    @Override
    public Cursor getToolDefaultCursor() {
        return Cursor.getDefaultCursor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent keyEvent, EditPanelModel editPanelModel) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
            controller.getSessionModel().saveTool();
            controller.getSessionModel().setTool(new SelectTool(controller));
            // controllGotPressed = true;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            long timecode = keyEvent.getWhen();
            spaceReleaseTimer.stop();
            if (timecode == spaceReleaseTimeCode) {
                // ignore auto repeat
            } else {
                controller.getSessionModel().saveTool();
                controller.getSessionModel().setTool(new DragViewportTool(controller));
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.cancelCurrentAction(editPanelModel);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_T
                && isPlatformCtrlOrBlumenkohlDown(keyEvent)) {
            Circuit c = editPanelModel.getCircuit();
            Project p = controller.getSessionModel().getCurrentProject();
            if (p != null) {
                p.addEditPanelModel(new EditPanelModel(c, new LinkedList<Component>(editPanelModel
                        .getComponentStack())));
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_W
                && isPlatformCtrlOrBlumenkohlDown(keyEvent)) {
            Project p = controller.getSessionModel().getCurrentProject();
            p.removeEditPanelModel(editPanelModel);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_M
                && isPlatformCtrlOrBlumenkohlDown(keyEvent)) {
            Settings settings = controller.getSessionModel().getSettings();
            if (settings.getBoolean(Settings.MMMode)) {
                /* Reset environment to normal. */
                settings.set(Settings.MMMode, false);
                controller.getSessionModel().setCurrentCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                editPanelModel.setCursor(getToolDefaultCursor());
            } else {
                /* Define mickey mouse environment. */
                settings.set(Settings.MMMode, true);
                controller.getSessionModel().setCurrentCursor(MM_CURSOR);
                editPanelModel.setCursor(MM_CURSOR);
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_I
                && isPlatformCtrlOrBlumenkohlDown(keyEvent)) {
            Settings settings = controller.getSessionModel().getSettings();
            boolean current = settings.getBoolean(Settings.SHOW_TOOLTIPS);
            settings.set(Settings.SHOW_TOOLTIPS, !current);
        }

    }

    @Override
    public void keyReleased(KeyEvent keyEvent, EditPanelModel editPanelModel) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
            controller.getSessionModel().restoreTool();
            // controllGotPressed = false;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceReleaseTimeCode = keyEvent.getWhen();
            spaceReleaseTimer.restart();
            // spaceGotPressed = false;
        }

    }

    /**
     * Opens the context menu of the {@link EditPanel} at the position of the
     * event.
     * 
     * @param event
     *            the mouse event
     */
    protected void openContextMenu(MouseEvent event) {
        // Makes contextmenu
        if (event.getComponent() instanceof EditPanel) {
            EditPanel ep = (EditPanel) event.getComponent();
            ep.showContextMenu(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e, EditPanelModel editPanelModel) {
        Settings settings = controller.getSessionModel().getSettings();

        if (isRightMouseButton(e)) {
            this.cancelCurrentAction(editPanelModel);

            // position clicked on in the model
            Point clickPointInModel = calculateInversePoint(e.getPoint(),
                    editPanelModel.getTransformation());
            Circuit c = editPanelModel.getCircuit();
            Set<Element> selected = editPanelModel.getSelectedElements();

            // brick we clicked on
            Brick clickedBrick = c.getBrickAtPosition(clickPointInModel);

            // if we did not click on a brick look for a near wire, else take
            // the brick
            Element clickedElement = (clickedBrick == null ? c.getWireAtPosition(clickPointInModel,
                    settings.getInt(Settings.WIRE_SCATTER_FACTOR)) : clickedBrick);

            // clear selection if clicked on empty space
            if (clickedElement == null) {
                selected.clear();
                editPanelModel.setSelectedElements(selected);
            }
            // single click on an element with right mouse button
            else {
                if (!selected.contains(clickedElement)) {
                    if (!(e.isControlDown())) {
                        selected.clear();
                    }
                    selected.add(clickedElement);
                }

                // update selected elements
                editPanelModel.setSelectedElements(selected);
            }

            openContextMenu(e);

            // middle taste mouse
        }
        if (e.getButton() == 2) {
            CopyActionListener.fillCopyBuffer(controller.getSessionModel());
            PasteActionListener.paste(controller, e.getPoint());
        }

        // check for double click on a component to open it in a new tab
        if (e.getClickCount() == 2) {

            // position clicked on in the model
            Point clickPointInModel = calculateInversePoint(e.getPoint(),
                    editPanelModel.getTransformation());
            Circuit c = editPanelModel.getCircuit();

            // brick we clicked on
            Brick brick = c.getBrickAtPosition(clickPointInModel);

            if (brick instanceof Component) {
                Component component = (Component) brick;
                ComponentType type = component.getType();
                Circuit circuit = type.getCircuit();
                Deque<Component> oldStack = editPanelModel.getComponentStack();
                Deque<Component> stack = new LinkedList<Component>(oldStack);
                stack.push(component);
                EditPanelModel panelModel = new EditPanelModel(circuit, stack);

                // open new tab
                controller.getSessionModel().getCurrentProject().addEditPanelModel(panelModel);

                // select the new tab
                controller.getSessionModel().getCurrentProject()
                        .setSelectedEditPanelModel(panelModel);
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        editPanelModel.setActiveBrick(null);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent, EditPanelModel editPanelModel) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent, EditPanelModel editPanelModel) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent, EditPanelModel editPanelModel) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent, EditPanelModel editPanelModel) {
        double zoomLevel = editPanelModel.getTransformation().getScaleX();
        if (zoomLevel > ZOOM_BREAKPOINT_ELEMENT_ACTIVE) {
            Point position = calculateInversePoint(mouseEvent.getPoint(),
                    editPanelModel.getTransformation());
            Brick activeBrick = editPanelModel.getCircuit().getBrickAtPosition(position);
            editPanelModel.setActiveBrick(activeBrick);
        } else {
            editPanelModel.setActiveBrick(null);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent, EditPanelModel editPanelModel) {
        if (mouseWheelEvent.getWheelRotation() < 0
                && isPlatformCtrlOrBlumenkohlDown(mouseWheelEvent)) {

            // center on mouse position
            Point position = mouseWheelEvent.getPoint();
            editPanelModel.setAutoNotify(false);
            editPanelModel.setCenter(position.x, position.y);
            editPanelModel.setAutoNotify(true);

            // actual zoom
            editPanelModel.zoom(1.1);
        } else if (isPlatformCtrlOrBlumenkohlDown(mouseWheelEvent)) {

            // center on mouse position
            Point position = mouseWheelEvent.getPoint();
            editPanelModel.setAutoNotify(false);
            editPanelModel.setCenter(position.x, position.y);
            editPanelModel.setAutoNotify(true);

            // actual zoom
            editPanelModel.zoom(0.9);
        }// scrolling vertical
        else /* if(mouseWheelEvent.getWheelRotation() != 0) */{
            int scroll = mouseWheelEvent.getWheelRotation();
            double scrollSpeed = controller.getSessionModel().getSettings()
                    .getDouble(Settings.SCROLL_SPEED);
            int scrollDiff = (int) (scroll * scrollSpeed);
            Point scrolledPoint = mouseWheelEvent.isAltDown() ? (new Point(scrollDiff, 0))
                    : (new Point(0, scrollDiff));
            AffineTransform transformation = editPanelModel.getTransformation();
            Point nullPointInModel = calculateInversePoint(new Point(), transformation);
            Point scrolledPointInModel = calculateInversePoint(scrolledPoint, transformation);
            editPanelModel.translate(scrolledPointInModel.x - nullPointInModel.x,
                    scrolledPointInModel.y - nullPointInModel.y);
        }

        if (editPanelModel.getToolMode() == EditPanelModel.ToolMode.SELECT_RECT_MODE) {
            editPanelModel.setSelectionEnd(calculateInversePoint(mouseWheelEvent.getPoint(),
                    editPanelModel.getTransformation()));
        }
    }

    public static boolean isPlatformCtrlOrBlumenkohlDown(InputEvent event) {
        return (Application.OS == OperatingSystem.OSX ? event.isMetaDown() : event.isControlDown());
    }

    /**
     * Calculates the inverse point using the given {@link AffineTransform}.
     * 
     * @param p
     *            point to transform
     * @param at
     *            the transformation object
     * @return the inverse transformed point
     */
    public static Point calculateInversePoint(Point p, AffineTransform at) {
        Point inv = new Point();

        try {
            at.inverseTransform(p, inv);
        } catch (NoninvertibleTransformException e1) {
            Log.gl().warn(e1.toString());
        }

        return inv;
    }

    /**
     * Clears the ghosts.
     * 
     * @param editPanelModel
     *            the editpanelmodel
     * @return the empty ghost set
     */
    protected Set<Element> clearGhosts(EditPanelModel editPanelModel) {
        Project project = controller.getSessionModel().getCurrentProject();

        Set<Element> ghosts = editPanelModel.getGhosts();

        ghosts.clear();
        if (project != null) {
            project.setErrorGhosts(null);
        }
        editPanelModel.setGhosts(ghosts);
        return ghosts;
    }

    /**
     * move a Element so that its leftTop Point will be its center Point. If
     * GridLock is on move it appropriate
     * 
     * @param elem
     *            element to move
     * @param gridlock
     *            true to lock to grif
     * @return the Element for convenience
     */
    protected Element moveElementForPlacement(Element elem, boolean gridlock) {
        Point pos = elem.getPosition();
        Dimension dim = elem.getDimension();

        pos.x -= dim.width / 2;
        pos.y -= dim.height / 2;
        elem.setPosition(pos);

        if (gridlock) {
            elem.setPosition(lockPointOnGrid(pos));
        }

        return elem;
    }

    /**
     * Creates a new point which is locked on the grid based on the given point.
     * 
     * @param point
     *            the point to convert to lock position
     * @return the converted point
     */
    protected Point lockPointOnGrid(Point point) {
        Point result = new Point(point);
        int gridSize = controller.getSessionModel().getSettings().getInt(Settings.GRID_SIZE);
        int deltaX = point.x >= 0 ? point.x % gridSize : gridSize - abs(point.x % gridSize);
        int deltaY = point.y >= 0 ? point.y % gridSize : gridSize - abs(point.y % gridSize);
        if (deltaX < gridSize / 2) {
            result.x -= deltaX;
        } else {
            result.x += gridSize - deltaX;
        }

        if (deltaY < gridSize / 2) {
            result.y -= deltaY;
        } else {
            result.y += gridSize - deltaY;
        }
        return result;
    }

    /**
     * Helper Method to drag the viewport.
     * 
     * @param e
     *            the mouse event
     * @param editPanelModel
     *            the EditPanelModel
     */
    protected static void dragViewport(MouseEvent e, EditPanelModel editPanelModel) {
        if (editPanelModel.getDragStartPoint() != null) {
            AffineTransform transformation = editPanelModel.getTransformation();
            Point dragMousePressedPoint = editPanelModel.getDragStartPoint();
            Point dragMouseNewPoint = e.getPoint();
            Point transformedNewPoint = calculateInversePoint(dragMouseNewPoint, transformation);
            Point transformedDragStartPoint = calculateInversePoint(dragMousePressedPoint,
                    transformation);

            editPanelModel.setDragStartPoint(dragMouseNewPoint);
            editPanelModel.translate(transformedDragStartPoint.x - transformedNewPoint.x,
                    transformedDragStartPoint.y - transformedNewPoint.y);

        }
    }

    /**
     * Cancels the current action.
     * 
     * @param editPanelModel
     *            the panel model
     */
    protected void cancelCurrentAction(EditPanelModel editPanelModel) {

    }
}
