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

import de.sep2011.funckit.controller.AbstractTool;
import de.sep2011.funckit.drawer.DecisionTable;
import de.sep2011.funckit.drawer.Drawer;
import de.sep2011.funckit.drawer.ElementState;
import de.sep2011.funckit.drawer.ElementStateResolver;
import de.sep2011.funckit.drawer.FancyDrawer;
import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.drawer.LayoutResolver;
import de.sep2011.funckit.drawer.SimpleDrawer;
import de.sep2011.funckit.drawer.SlimmedDrawer;
import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.BrickWireDistinguishDispatcher;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.observer.EditPanelModelInfo;
import de.sep2011.funckit.observer.EditPanelModelObserver;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.observer.SettingsInfo;
import de.sep2011.funckit.observer.SettingsObserver;
import de.sep2011.funckit.observer.SimulationModelInfo;
import de.sep2011.funckit.observer.SimulationModelObserver;
import de.sep2011.funckit.util.DrawUtil;
import de.sep2011.funckit.util.Profiler;
import de.sep2011.funckit.validator.LiveCheckValidatorFactory;
import de.sep2011.funckit.validator.Result;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.sep2011.funckit.util.Log.gl;
import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * Panel for displaying and editing {@link Circuit}s and interacting with a
 * {@link Simulation} of a {@link Circuit}.
 */
public class EditPanel extends JPanel implements EditPanelModelObserver,
        GraphModelObserver, SimulationModelObserver, ProjectObserver,
        SettingsObserver, SessionModelObserver {

    private static final long serialVersionUID = -3211767963743871988L;

    /**
     * Stroke object to accept our selection with.
     */
    private static final Stroke SELECTION_STROKE = new BasicStroke(1.0f, // Width
            BasicStroke.CAP_SQUARE, // End cap
            BasicStroke.JOIN_MITER, // Join style
            100.0f, // Miter limit
            new float[]{3.0f, 5.0f}, // Dash pattern
            0.0f); // Dash phase

    /**
     * Name for image layer, grid gets drawn in.
     */
    private static final String LAYER_GRID = "grid";
    /**
     * Name for layer, model (all elements) gets drawn in.
     */
    private static final String LAYER_MODEL = "model";
    /**
     * Name for layer, ghost elements get drawn in.
     */
    private static final String LAYER_GHOSTS = "ghosts";
    /**
     * Name for layer, selection feedback gets drawn in.
     */
    private static final String LAYER_SELECTION = "selection";
    /**
     * Name for layer, tooltips are drawn in.
     */
    private static final String LAYER_TOOLTIPS = "tooltips";

    /**
     * TODO comment new volatile image feature.
     */
    private final Map<String, BufferedImage> imageLayers =
            new LinkedHashMap<String, BufferedImage>();

    /**
     * List that defines the order of {@link EditPanel#imageLayers}. First layer
     * in list is deepest layer, last layer the image on top.
     */
    private List<String> imageLayerOrder;

    /**
     * Model object that is associated with this edit panel tab.
     */
    private final EditPanelModel panelModel;

    private boolean lowQualityMode;

    /**
     * Parent view object.
     */
    private final View view;

    private final Drawer drawer;

    /* Drawer without using decision table and element state resolver. */
    private final Drawer simpleDrawer;
    private static final double ZOOM_BREAKPOINT_SIMPLE_DRAWER = 0.25;
    private final Drawer slimmedDrawer;
    private static final double ZOOM_BREAKPOINT_SLIMMED_DRAWER = 0.1;

    private static final LayoutResolver LAYOUT_RESOLVER = new LayoutResolver();
    private final ElementStateResolver elementStateResolver;
    private final ContextMenu contextMenu;

    private int layerWidth = 0;
    private int layerHeight = 0;

    /**
     * Constructs a new edit panel with parent view object and the edit panel's
     * associated panel model.
     *
     * @param view
     * @param panelModel
     */
    public EditPanel(View view, EditPanelModel panelModel) {
        this.view = view;

        this.panelModel = panelModel;
        panelModel.addObserver(this);
        panelModel.getCircuit().addObserver(this);

        view.getSessionModel().getSettings().addObserver(this);
        view.getSessionModel().addObserver(this);

        elementStateResolver = new ElementStateResolver(panelModel,
                view.getSessionModel());
        drawer = new FancyDrawer(
                view.getSessionModel().getSettings(),
                LAYOUT_RESOLVER,
                elementStateResolver
        );
        simpleDrawer = new SimpleDrawer(view.getSessionModel().getSettings());
        slimmedDrawer = new SlimmedDrawer(view.getSessionModel().getSettings());

        initializeImageLayerOrder();
        initializeImageLayers();

        this.setLayout(null);

        addComponentListener(new EditPanelResizeListener());
        setBackground(Color.WHITE);

        setFocusable(true);

        this.contextMenu = new ContextMenu(view, panelModel);
        this.contextMenu.setInvoker(this);

        this.setFocusable(true);

        lowQualityMode = view.getSessionModel().getSettings()
                .getBoolean(Settings.LOW_RENDERING_QUALITY_MODE);
        setCursor(panelModel.getCursor());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        if (width != layerWidth || height != layerHeight) {
            resizeLayers();
            layerWidth = width;
            layerHeight = height;
        }
    }

    public void showContextMenu(int x, int y) {
        this.contextMenu.show(this, x, y);
    }

    public void hideContextMenu() {
        this.contextMenu.setVisible(false);
    }

    /**
     * Initializes layers with their name. If we want to use further image
     * layers, we just have to add them here.
     */
    private void initializeImageLayerOrder() {
        imageLayerOrder = new LinkedList<String>();
        imageLayerOrder.add(LAYER_GRID);
        imageLayerOrder.add(LAYER_MODEL);
        imageLayerOrder.add(LAYER_TOOLTIPS);
        imageLayerOrder.add(LAYER_GHOSTS);
        imageLayerOrder.add(LAYER_SELECTION);
    }

    /**
     * (Re-)Initializes image layer map with list of image layers to directly
     * access a layer by given name.
     */
    private void initializeImageLayers() {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        assert imageLayerOrder != null;
        imageLayers.clear();
        for (String key : imageLayerOrder) {
            imageLayers.put(key, createLayer());
        }

        if (Profiler.ON) {
            Profiler.rendering(Profiler.INITIALIZE_LAYERS,
                    System.currentTimeMillis() - time);
        }
    }

    /**
     * Creates a new layer to accept on. Important condition is, that layers
     * support (semi-)transparent colors to accept several layers.
     *
     * @return
     */
    private BufferedImage createLayer() {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        // Create an image that supports transparent pixels
        int width = Math.max(this.getWidth(), 1);
        int height = Math.max(this.getHeight(), 1);
        BufferedImage bufferedImage = gc.createCompatibleImage(width, height,
                Transparency.TRANSLUCENT);
        if (bufferedImage.getCapabilities(gc).isAccelerated()) {
            gl().debug("YEAH! HW accelerated image!");
        }
        return bufferedImage;
    }

    /**
     * Observing method for graph model. If graph model gets changed, this
     * method is called and we can react on it.
     *
     * @param source the source {@link Circuit}
     * @param i      the Info Object
     */
    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {
        if (panelModel != view.getSessionModel().getSelectedEditPanelModel()) {
            return; // only draw if visible
        }

        if (!i.isElementBoundsChanged()
                && (i.getAddedBricks().size() > 0 || i.getAddedWires().size() > 0)
                && i.getChangedBricks().size() == 0
                && i.getChangedWires().size() == 0
                && i.getRemovedBricks().size() == 0
                && i.getRemovedWires().size() == 0) {
            /* Only added elements, so just draw new elements. */
            // drawModel();
            Set<Element> elements = new LinkedHashSet<Element>();
            elements.addAll(i.getAddedBricks());
            elements.addAll(i.getAddedWires());
            Graphics2D graphics = imageLayers.get(LAYER_MODEL).createGraphics();
            // Graphics2D graphics =
            // volatileImageLayers.get(LAYER_MODEL).createGraphics();
            drawElements(graphics, elements);
        } else {
            drawModel();
        }
        updateRealTimeValidation(source);

        repaint();
    }
    
    private void updateRealTimeValidation(Circuit c) {
        if (view.getSessionModel().getSettings()
                .getBoolean(Settings.REALTIME_VALIDATION)) {
            LiveCheckValidatorFactory liveCheckValidatorFactory = new LiveCheckValidatorFactory();
            List<Result> results = liveCheckValidatorFactory.getValidator()
                    .validate(c);

            // NOTE: the notifying circuit has to be the main circuit of the
            // current project.
            view.getSessionModel().getCurrentProject().setCheckResults(results);
        } else {
            view.getSessionModel().getCurrentProject().setCheckResults(null);
        }

    }

    /**
     * Observing method for associated edit panel model. If it gets changed,
     * this method is called and we can react on the changed state. For better
     * performance it is reasonable to react on the type of changed data to
     * repaint only parts of user interface.
     *
     * @param source the source Observable
     * @param i      the Info Object
     */
    @Override
    public void editPanelModelChanged(EditPanelModel source,
                                      EditPanelModelInfo i) {
        if (i.isCursorChanged()) {
            setCursor(source.getCursor());
        }

        if (panelModel != view.getSessionModel().getSelectedEditPanelModel()) {
            return; // only draw if visible
        }

        boolean drawGhosts = false;
        boolean drawModel = false;
        boolean drawGrid = false;
        boolean drawSelection = false;
        boolean drawToolTips = false;

        if (i.isGhostsChanged()) {
            drawGhosts = true;
        }

        if (i.isSelectionChanged()) {
            Point selectionStart = source.getSelectionStart();
            Point selectionEnd = source.getSelectionEnd();
            if (selectionStart != null && selectionEnd != null) {
                /*
                 * Selection start and end points are given, so accept
                 * selection.
                 */
                drawSelection(selectionStart, selectionEnd);
                drawSelection = true;
            } else {
                /* Otherwise clear background and repaint model. */
                clearBackground(imageLayers.get(LAYER_SELECTION)
                        .createGraphics());
                // clearBackground(volatileImageLayers.get(LAYER_SELECTION).createGraphics());
                drawModel = true;
            }
        }

        if (i.isTransformChanged()) {
            drawGrid = true;
            drawModel = true;
            drawGhosts = true;
            source.setActiveBrick(null);
        }

        if (i.isActiveChanged()) {
            drawModel = true;
        }

        /* Draw grid if it has been changed. */
        if (drawGrid) {
            drawGrid();
        }

        /* Draw tooltips. */
        if (i.isActiveChanged()) {
            clearBackground(imageLayers.get(LAYER_TOOLTIPS).createGraphics());
            if (view.getSessionModel().getSettings()
                    .getBoolean(Settings.SHOW_TOOLTIPS)) {
                if (source.getActiveBrick() != null) {
                    drawToolTip(source.getActiveBrick());
                    drawToolTips = true;
                }
            }
        }

        /* Draw elements from model if changed. */
        if (drawModel) {
            drawModel();
        }

        /* Possibly draw ghosts */
        if (drawGhosts) {
            drawGhosts();
        }

        /* If anything changed, repaint! */
        if (drawGhosts || drawGrid || drawModel || drawSelection
                || drawToolTips) {
            repaint();
        }

    }

    /**
     * Observer method for {@link Simulation}. If simulation changes, this method
     * gets invoked and we can react on its changed data.
     *
     * @param source the source Observable
     * @param i      the Info Object
     */
    @Override
    public void simulationModelChanged(Simulation source, SimulationModelInfo i) {
        if (panelModel != view.getSessionModel().getSelectedEditPanelModel()) {
            return; // only draw if visible
        }

        if (i.isSimulationChanged()) {
            drawModel();
            repaint();
        }
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (i.isSimulationChanged()) {
            if (source.hasSimulation()) {

                // simulation started => observe it
                source.getSimulation().addObserver(this);
            } else {

                // simulation stopped => repaint
                drawModel();
                repaint();
            }
        }

        if (panelModel != view.getSessionModel().getSelectedEditPanelModel()) {
            return; // only draw if visible
        }

        if (i.isActiveEditPanelModelChanged()) {
            drawGrid();
            drawModel();
            drawGhosts();
            repaint();
        } else if (i.isResultsChanged()) {
            drawModel();
            repaint();
        }
    }

    /**
     * Method for event when something in settings has changed. Given source
     * object is observed settings object, given SettingsInfo specifies which
     * settings exactly have changed.
     *
     * @param source Settings object.
     * @param i      Info object for setting changes.
     */
    @Override
    public void settingsChanged(Settings source, SettingsInfo i) {
        boolean isVisible = panelModel == view.getSessionModel()
                .getSelectedEditPanelModel();

        boolean drawGrid = false;
        boolean drawModel = false;
        boolean drawGhosts = false;
        boolean drawToolTips = false;
        
        /* Handle Valisation Change */
        if (i.getChangedSetting().equals(Settings.REALTIME_VALIDATION)) {
            drawGhosts = true;
            updateRealTimeValidation(panelModel.getCircuit());
        }

        /* On toggling grid on or off, repaint it. */
        if (i.getChangedSetting().equals(Settings.SHOW_GRID) && isVisible) {
            drawGrid = true;
        }

        /* If rendering mode changed, repaint edit panel. */
        if (i.getChangedSetting().equals(Settings.LOW_RENDERING_QUALITY_MODE)
                && isVisible) {
            lowQualityMode = source
                    .getBoolean(Settings.LOW_RENDERING_QUALITY_MODE);
            drawGrid = true;
            drawModel = true;
            drawGhosts = true;
            drawToolTips = true;
        }

        /* If tooltips changed, reset them and paint them again. */
        if (i.getChangedSetting().equals(Settings.SHOW_TOOLTIPS)) {
            clearBackground(imageLayers.get(LAYER_TOOLTIPS).createGraphics());
            drawToolTips = true;

            if (view.getSessionModel().getSettings()
                    .getBoolean(Settings.SHOW_TOOLTIPS)) {
                if (panelModel.getActiveBrick() != null) {
                    drawToolTip(panelModel.getActiveBrick());
                }
            }
        }

        /* Only draw ghost elements if they changed. */
        if (drawGhosts) {
            drawGhosts();
        }
        /* Only draw grid layer if it has changed due to zooming or similar. */
        if (drawGrid) {
            drawGrid();
        }
        /* Only repaint elements on model layer if they sth. has changed. */
        if (drawModel) {
            drawModel();
        }

        /* Only repaint if anything has changed. */
        if (drawGhosts || drawGrid || drawModel || drawToolTips) {
            repaint();
        }
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.hasCurrentProjectChanged()
                && panelModel == source.getSelectedEditPanelModel()) {
            drawGrid();
            drawModel();
            drawGhosts();
            repaint();
        }
        if (i.isToolChanged()) {
        	drawSelection(new Point(), new Point()); // clear selection
        }
    }

    /**
     * Get the {@link EditPanelModel} of this {@link EditPanel}.
     *
     * @return the model
     */
    public EditPanelModel getPanelModel() {
        return panelModel;
    }

    private void resizeLayers() {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        initializeImageLayers();

        if (panelModel != view.getSessionModel().getSelectedEditPanelModel()) {
            return; // only draw if visible
        }

        drawGrid();
        drawModel();
        drawGhosts();
        repaint();

        if (Profiler.ON) {
            Profiler.rendering(Profiler.RESIZE_LAYERS,
                    System.currentTimeMillis() - time);
        }
    }

    /**
     * Called when component should be repainted. Uses parent implementation and
     * draws additionally all defined {@code imageLayers}. ImageLayers are used
     * for redrawing only certain layers on certain update events.
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        Graphics2D graphics = (Graphics2D) g;
        super.paintComponent(graphics);

        render(graphics);

        graphics.dispose();

        if (Profiler.ON) {
            Profiler.rendering(Profiler.PAINT_COMPONENT,
                    System.currentTimeMillis() - time);
        }
    }

    private void render(Graphics2D graphics) {
        for (String key : imageLayerOrder) {
            graphics.drawImage(imageLayers.get(key), 0, 0, null);
        }
    }

    /**
     * Initiates (re-)drawing ghost layer.
     */
    private void drawGhosts() {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        drawGhosts(imageLayers.get(LAYER_GHOSTS).createGraphics());

        if (Profiler.ON) {
            Profiler.rendering(Profiler.DRAW_GHOSTS, System.currentTimeMillis()
                    - time);
        }
    }

    /**
     * Draws ghosts from associated panel model on given Graphics2D object.
     *
     * @param graphics Graphics2D object from a layer.
     */
    private void drawGhosts(Graphics2D graphics) {
        clearBackground(graphics);

        drawElements(graphics, panelModel.getGhosts());

        graphics.dispose();
    }

    /**
     * Initiates (re-)drawing grid.
     */
    private void drawGrid() {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        drawGrid(imageLayers.get(LAYER_GRID).createGraphics());

        if (Profiler.ON) {
            Profiler.rendering(Profiler.DRAW_GRID, System.currentTimeMillis()
                    - time);
        }
    }

    /**
     * Draws a grid with grid information from panel model on given graphics
     * object.
     *
     * @param graphics
     */
    private void drawGrid(Graphics2D graphics) {
        clearBackground(graphics);
        setRenderingHints(graphics);

        double zoomLevel = panelModel.getTransformation().getScaleX();

        if (view.getSessionModel().getSettings().getBoolean(Settings.SHOW_GRID)
                && zoomLevel >= ZOOM_BREAKPOINT_SIMPLE_DRAWER) {
            AffineTransform at = panelModel.getTransformation();

            int width = getWidth();
            int height = getHeight();

            int gridSize = view.getSessionModel().getSettings()
                    .getInt(Settings.GRID_SIZE);
            Color gridColor = view.getSessionModel().getSettings()
                    .get(Settings.GRID_COLOR, Color.class);

            DrawUtil.drawGrid(graphics, gridSize, width, height, at, gridColor);

            graphics.dispose();
        }
    }

    /**
     * Initiates redrawing model layer (that layer, that contains all
     * elements).
     */
    private void drawModel() {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        drawModel(imageLayers.get(LAYER_MODEL).createGraphics());

        if (Profiler.ON) {
            Profiler.rendering(Profiler.DRAW_MODEL, System.currentTimeMillis()
                    - time);
        }
    }

    /**
     * Draws elements from model on given graphics object.
     *
     * @param graphics Graphics2D object to accept on.
     */
    private void drawModel(Graphics2D graphics) {
        clearBackground(graphics);
        drawElements(graphics, panelModel.getCircuit().getElements());
        graphics.dispose();
    }

    /**
     * Initiates drawing a selection feedback rectangle with given points.
     *
     * @param selectionStart First point from which selection starts.
     * @param selectionEnd   Second point on which selection ends.
     */
    private void drawSelection(Point selectionStart, Point selectionEnd) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        drawSelection(selectionStart, selectionEnd,
                imageLayers.get(LAYER_SELECTION).createGraphics());

        if (Profiler.ON) {
            Profiler.rendering(Profiler.DRAW_SELECTION,
                    System.currentTimeMillis() - time);
        }
    }

    /**
     * Draws a selection rectangle on given Graphics2D object.
     *
     * @param selectionStart Start point of selection.
     * @param selectionEnd   End point of selection.
     * @param graphics       Graphics2D object (e.g. from {@link
     *                       EditPanel#imageLayers}
     */
    private void drawSelection(Point selectionStart, Point selectionEnd,
                               Graphics2D graphics) {
        clearBackground(graphics);
        graphics.transform(panelModel.getTransformation());
        setRenderingHints(graphics);

        Color borderColor = view.getSessionModel().getSettings()
                .get(Settings.SELECTION_BORDER_COLOR, Color.class);
        graphics.setColor(borderColor);
        graphics.setStroke(SELECTION_STROKE);

        int x = selectionStart.x > selectionEnd.x ? selectionEnd.x
                : selectionStart.x;
        int width = Math.abs(selectionStart.x - selectionEnd.x);
        int height = Math.abs(selectionStart.y - selectionEnd.y);
        int y = selectionStart.y > selectionEnd.y ? selectionEnd.y
                : selectionStart.y;

        graphics.drawRect(x, y, width, height);

        /*
         * Fill rectangle (WATCH OUT: multi layer images must support
         * Transparency.TRANSLUCENT !)
         */
        /*
         * Color fillColor = new Color(SELECTION_COLOR.getRed(),
         * SELECTION_COLOR.getGreen(), SELECTION_COLOR.getBlue(), 100);
         */
        Color fillColor = view.getSessionModel().getSettings()
                .get(Settings.SELECTION_FILL_COLOR, Color.class);
        graphics.setColor(fillColor);
        graphics.fillRect(x + 1, y + 1, width - 1, height - 1);

        graphics.dispose();
    }

    /**
     * Clears background of given Graphics2D object by filling its background
     * white and with full transparency. For this to work, given Graphics2D
     * object from layer (BufferedImage) must support transparent colors! {@link
     * Transparency#TRANSLUCENT}
     *
     * @param graphics
     */
    private void clearBackground(Graphics2D graphics) {
        /*
         * Define transparent background and clear whole image with background
         * color, so that image is empty.
         */
        graphics.setBackground(new Color(255, 255, 255, 0));
        graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
    }

    private class EditPanelResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            if (e.getSource() instanceof EditPanel) {
                resizeLayers();
            }
        }
    }

    /**
     * Draws given elements on given graphics object by looking up each element
     * state, resolving via decision table its layout and then performing a
     * drawing routine on it. This method may NOT clear background or existing
     * drawings on graphics object. For clearing the graphics object first, use
     * {@see EditPanel#clearBackground}.
     *
     * @param graphics
     * @param elements
     */
    private void drawElements(Graphics2D graphics, Set<Element> elements) {
        graphics.transform(panelModel.getTransformation());
        setRenderingHints(graphics);

        Point start = AbstractTool.calculateInversePoint(new Point(0, 0),
                panelModel.getTransformation());
        Point end = AbstractTool.calculateInversePoint(new Point(getWidth(),
                getHeight()), panelModel.getTransformation());
        final Rectangle visualRange = new Rectangle(start.x, start.y, end.x
                - start.x, end.y - start.y);

        final double zoomLevel = panelModel.getTransformation().getScaleX();

        /* Inject graphics object to used drawer. */
        if (zoomLevel < ZOOM_BREAKPOINT_SLIMMED_DRAWER) {
            slimmedDrawer.setGraphics(graphics);
        } else if (zoomLevel < ZOOM_BREAKPOINT_SIMPLE_DRAWER) {
            simpleDrawer.setGraphics(graphics);
        } else {
            drawer.setGraphics(graphics);
        }

        /* Draw Wires first, then Bricks but only in high quality mode */
        if (lowQualityMode) {
            for (Element element : elements) {
                chooseDrawerAndDrawElement(element, zoomLevel, visualRange);
            }
        } else {
            for (final Element element : elements) {
                new BrickWireDistinguishDispatcher() {

                    {
                        element.dispatch(this);
                    }

                    @Override
                    protected void visitWire(Wire w) {

                    }

                    @Override
                    protected void visitBrick(Brick b) {
                        chooseDrawerAndDrawElement(element, zoomLevel,
                                visualRange);

                    }
                };
            }
            for (final Element element : elements) {
                new BrickWireDistinguishDispatcher() {

                    {
                        element.dispatch(this);
                    }

                    @Override
                    protected void visitWire(Wire w) {
                        chooseDrawerAndDrawElement(element, zoomLevel,
                                visualRange);

                    }

                    @Override
                    protected void visitBrick(Brick b) {

                    }
                };
            }

        }

    }

    private void chooseDrawerAndDrawElement(Element element, double zoomLevel,
            Rectangle visualRange) {
        if (element.intersects(visualRange)) {
            if (zoomLevel < ZOOM_BREAKPOINT_SLIMMED_DRAWER) {
                /* Draw element slimmed. */
                drawElement(element, slimmedDrawer);
            } else if (zoomLevel < ZOOM_BREAKPOINT_SIMPLE_DRAWER) {
                /* Simple drawer without fancy stuff. */
                drawElement(element, simpleDrawer);
            } else {
                /*
                 * Complex drawing with decision table and layout / state
                 * resolver.
                 */
                drawElementFancy(element);
            }
        }

    }

    private static void drawElement(Element element, Drawer drawer) {
        element.dispatch(drawer);
    }

    private void drawElementFancy(Element element) {
        ElementState state = elementStateResolver.resolve(element);
        Layout layout = new Layout();
        LAYOUT_RESOLVER.setComponentStack(panelModel.getComponentStack());
        LAYOUT_RESOLVER.setSimulation(view.getSessionModel()
                .getCurrentSimulation());
        LAYOUT_RESOLVER.setLayout(layout);
        element.dispatch(LAYOUT_RESOLVER);
        DrawAction action = DecisionTable.resolve(state);
        drawer.setLayout(layout);
        drawer.setAction(action);
        element.dispatch(drawer);
    }

    /**
     * Draws a tool tip for the given (active) brick by using its information as
     * tool tip text and {@see DrawUtil#drawToolTip} to draw the actual tool
     * tip.
     *
     * @param activeBrick
     */
    private void drawToolTip(Brick activeBrick) {
        long time = 0;
        if (Profiler.ON) {
            time = System.currentTimeMillis();
        }

        /* Construct tool tip text with a StringBuilder object. */
        String name = activeBrick.getName();
        int delay = activeBrick.getDelay();
        Brick.Orientation orientation = activeBrick.getOrientation();
        Point position = activeBrick.getPosition();
        StringBuilder text = new StringBuilder();
        text.append(tr("brick.name"));
        text.append(": ");
        text.append(name);
        text.append("\n");
        text.append(tr("brick.type"));
        text.append(": ");
        if (activeBrick instanceof Component) {
            text.append(((Component) activeBrick).getType().getName());
        } else {
            text.append(activeBrick.getClass().getSimpleName());
        }
        if (activeBrick instanceof Gate || activeBrick instanceof Component) {
            text.append("\n");
            text.append(tr("brick.delay"));
            text.append(": ");
            text.append(delay);
        }
        text.append("\n");
        text.append(tr("brick.position"));
        text.append(": ");
        text.append(position.x);
        text.append(".");
        text.append(position.y);
        text.append("\n");
        text.append(tr("brick.orientation"));
        text.append(": ");
        text.append(tr("orientation." + orientation));
        
        List<Result> checkResults = view.getSessionModel().getCurrentProject().getCheckResults();
        if (checkResults != null && !checkResults.isEmpty()) {
        	StringBuilder errorText = new StringBuilder();
            boolean hasError = false;
            for (Result r : view.getSessionModel().getCurrentProject()
                    .getCheckResults()) {
                if (r.getFlawElements().contains(activeBrick)) {
                	hasError = true;
                	errorText.append("* ");
                	errorText.append(r.getMessage());
                	errorText.append("\n");
                }
            }
            if (hasError) {
            	text.append("\n");
                text.append(tr("brick.error"));
                text.append(":\n");
            	text.append(errorText);
            }
        }

        /* Calculate start position for tooltip. */
        Point startPoint = new Point();
        panelModel.getTransformation().transform(
                new Point(activeBrick.getBoundingRect().x
                        + activeBrick.getBoundingRect().width, activeBrick.getBoundingRect().y
                        + (int) Math.round(0.5 * activeBrick.getBoundingRect().height)), startPoint);

        /* Draw with util method actual fancy tool tip on tooltip layer. */
        Font font = new Font("Helvetica", Font.PLAIN, 12); // TODO hardcoded
        int shiftingX = 20;
        int shiftingY = 20;
        int width = 200;
        int marginTop = 5;
        int marginLeft = 4;
        int marginRight = 4;
        int marginBottom = 0;
        Color tooltipBorderColor = new Color(150, 150, 220);
        Color tooltipFillColor = new Color(200, 200, 255, 200);
        Color tooltipTextColor = Color.black;

        DrawUtil.drawToolTip(
                imageLayers.get(LAYER_TOOLTIPS).createGraphics(),
                startPoint,
                width,
                text.toString(),
                tooltipBorderColor,
                tooltipFillColor,
                tooltipTextColor,
                font,
                shiftingX,
                shiftingY,
                marginTop,
                marginLeft,
                marginRight,
                marginBottom
        );

        if (Profiler.ON) {
            Profiler.rendering(Profiler.DRAW_TOOLTIP,
                    System.currentTimeMillis() - time);
        }
    }

    /**
     * Specifies rendering hints for a given graphics object depending on
     * current performance settings.
     *
     * @param graphics
     */
    private void setRenderingHints(Graphics2D graphics) {
        if (lowQualityMode) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_SPEED);
            graphics.setRenderingHint(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_DISABLE);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        } else {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
    }
}
