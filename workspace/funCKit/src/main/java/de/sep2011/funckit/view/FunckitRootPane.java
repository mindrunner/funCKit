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

import static de.sep2011.funckit.util.Log.gl;
import static de.sep2011.funckit.util.internationalization.Language.tr;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.jidesoft.swing.JideTabbedPane;

import net.miginfocom.swing.MigLayout;

import de.sep2011.funckit.controller.listener.editpanel.TabbedPaneChangeListener;
import de.sep2011.funckit.controller.listener.project.ProjectTreeListener;
import de.sep2011.funckit.controller.listener.settings.ToggleExpertModeActionListener;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.observer.SettingsInfo;
import de.sep2011.funckit.observer.SettingsObserver;

/**
 * FunckitFrame is the main-view of the program. Creates the layout of the view.
 */
public class FunckitRootPane extends JRootPane implements SessionModelObserver, ProjectObserver,
        SettingsObserver {
    /**
     * Unique identifier for serialization.
     */
    private static final long serialVersionUID = -8480228957905762293L;

    private static final JideTabbedPane EMPTY_TABS = new JideTabbedPane();

    final private View view;

    private Map<Project, ProjectTabs> projectsTabs;

    private FunckitMenuBar menuBar;

    private JPanel projectExplorerPanel;

    private NewBrickListPanel newBrickListPanel;

    private StatusBar statusBar;

    private FunckitToolBar toolBar;

    private JSplitPane mainSplitPane;

    /**
     * Create a new {@link FunckitRootPane}.
     * @param view the view this is part of
     *
     */
    public FunckitRootPane(View view) {
        super();
        this.view = view;
        switch (view.getSessionModel().getViewType()) {
        case VIEW_TYPE_ELEANING_SOLVE:
            initializeElearningSolveRootPane();
            break;
        case VIEW_TYPE_STANDALONE:
            initializeStandaloneRootPane();
            break;
        case VIEW_TYPE_PRESENTER:
            initializeCircuitPresenterRootPane();
        default:
            new RuntimeException("ViewType " + view.getSessionModel().getViewType()
                    + " Not implemented.");
            break;
        }
    }

    /**
     * Initializes object with the given parameters.
     *
     * @param boundingRect
     *            The rectangle of the frame
     */
    private void initializeStandaloneRootPane() {
        projectsTabs = new LinkedHashMap<Project, ProjectTabs>();

        // create menu bar
        FunckitMenuBar menuBar = new FunckitMenuBar(view);
        setFunckitMenuBar(menuBar);

        // create the toolbar
        toolBar = new FunckitToolBar(view);

        // create project explorer
        createProjectTreePanel();

        // create new brick list panel
        newBrickListPanel = new NewBrickListPanel(view);

        // create status bar
        statusBar = new StatusBar(view);

        Container c = getContentPane();
        c.setLayout(new MigLayout("fill"));
        c.add(toolBar, "dock north, growx");
        c.add(statusBar, "dock south, growx");
        // c.add(editToolBar, "dock east, growy");
        c.add(newBrickListPanel, "dock east, growy");
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, EMPTY_TABS);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setLeftComponent(projectExplorerPanel);
        c.add(mainSplitPane, "grow");

        view.getSessionModel().addObserver(this);
        view.getSessionModel().getSettings().addObserver(this);

        // init cursor from model
        handleCursorChange(view.getSessionModel());

        createToogleExpertModeShortcut();

    }
    
    private void initializeCircuitPresenterRootPane() {
        projectsTabs = new LinkedHashMap<Project, ProjectTabs>();

        // create menu bar
        FunckitMenuBar menuBar = new FunckitMenuBar(view);
        setFunckitMenuBar(menuBar);

        // create the toolbar
        toolBar = new FunckitToolBar(view);

        // create project explorer
        createProjectTreePanel();
        
        // create new brick list panel
        newBrickListPanel = new NewBrickListPanel(view);

        // create status bar
        statusBar = new StatusBar(view);

        Container c = getContentPane();
        c.setLayout(new MigLayout("fill"));
        c.add(toolBar, "dock north, growx");
        c.add(statusBar, "dock south, growx");
        // c.add(editToolBar, "dock east, growy");
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, EMPTY_TABS);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setLeftComponent(projectExplorerPanel);
        
        // collapse splitpane
        mainSplitPane.setDividerLocation(0.3d);
        mainSplitPane.getLeftComponent().setMinimumSize(new Dimension());
        mainSplitPane.setDividerLocation(0.0d);

        c.add(mainSplitPane, "grow");

        view.getSessionModel().addObserver(this);
        view.getSessionModel().getSettings().addObserver(this);

        // init cursor from model
        handleCursorChange(view.getSessionModel());

        createToogleExpertModeShortcut();

    }
    
    private void initializeElearningSolveRootPane() {
        projectsTabs = new LinkedHashMap<Project, ProjectTabs>();

        // create menu bar
        FunckitMenuBar menuBar = new FunckitMenuBar(view);
        setFunckitMenuBar(menuBar);

        // create the toolbar
        toolBar = new FunckitToolBar(view);

        // create project explorer
        createProjectTreePanel();

        // create new brick list panel
        newBrickListPanel = new NewBrickListPanel(view);

        // create status bar
        statusBar = new StatusBar(view);

        Container c = getContentPane();
        c.setLayout(new MigLayout("fill"));
        c.add(toolBar, "dock north, growx");
        c.add(statusBar, "dock south, growx");
        // c.add(editToolBar, "dock east, growy");
        c.add(newBrickListPanel, "dock east, growy");
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, EMPTY_TABS);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setLeftComponent(projectExplorerPanel);
        
        // collapse splitpane
        mainSplitPane.setDividerLocation(0.3d);
        mainSplitPane.getLeftComponent().setMinimumSize(new Dimension());
        mainSplitPane.setDividerLocation(0.0d);

        c.add(mainSplitPane, "grow");

        view.getSessionModel().addObserver(this);
        view.getSessionModel().getSettings().addObserver(this);

        // init cursor from model
        handleCursorChange(view.getSessionModel());

        createToogleExpertModeShortcut();

    }
    
    private void createToogleExpertModeShortcut() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK
                        | InputEvent.SHIFT_DOWN_MASK), "expertMode");
        getRootPane().getActionMap().put("expertMode",
                new ToggleExpertModeActionListener(view, view.getController()));
    }

    private void createProjectTreePanel() {
        ProjectTree projectTree = new ProjectTree(view);
        for (Project project : view.getSessionModel().getProjects()) {
            project.addObserver(this);
        }
        ProjectTreeListener projectTreeListener = new ProjectTreeListener(view,
                view.getController(), projectTree);
        projectTree.addMouseListener(projectTreeListener);
        projectTree.addMouseMotionListener(projectTreeListener);
        // projectTree.addMouseWheelListener(projectTreeListener);
        projectTree.addKeyListener(projectTreeListener);
        projectTree.addTreeExpansionListener(projectTreeListener);
        projectTree.addTreeSelectionListener(projectTreeListener);

        projectExplorerPanel = new JPanel(new MigLayout("insets 0, wrap", "[grow]", "[][grow]"));
        JScrollPane projectTreeScrollList = new JScrollPane(projectTree);
        projectExplorerPanel.add(new JLabel(tr("FunckitFrame.project")));
        projectExplorerPanel.add(projectTreeScrollList, "grow 100 100");
    }

    /**
     * Returns the main menu bar of the Application.
     *
     * @return the main menu bar of the Application
     */
    public FunckitMenuBar getFunckitMenuBar() {
        return menuBar;
    }

    /**
     * Sets the main menu bar of the Application.
     *
     * @param menuBar
     *            the menubar
     */
    public void setFunckitMenuBar(FunckitMenuBar menuBar) {
        this.setJMenuBar(menuBar);
        this.menuBar = menuBar;
    }

    /**
     * Sets the test of the status bar.
     *
     * @param text
     *            the text that will be displayed inside the statusbar.
     */
    public void setStatusText(String text) {
        statusBar.setLabel(text);
    }

    /**
     * Returns the main tabbed pane (EditPanels as tabs).
     *
     * @return the main tabbed pane
     */
    public JTabbedPane getTabbedPane() {
        return (JTabbedPane) mainSplitPane.getRightComponent();
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {

        if (i.hasCursorChanged()) {
            handleCursorChange(source);
        }

        if (i.hasProjectAdded()) {
            Project p = i.getChangedProject();
            p.addObserver(this);
            openProjectTabs(p);
        }

        if (i.hasProjectRemoved()) {
            Project p = i.getChangedProject();
            p.deleteObserver(this);
            closeProjectTabs(p);
        }

        if (i.hasCurrentProjectChanged()) {
            Project p = source.getCurrentProject();
            ProjectTabs tabs;
            if (p == null) {
                mainSplitPane.setRightComponent(EMPTY_TABS);
                return;
            }
            tabs = projectsTabs.get(p);
            if (tabs == null) {
                tabs = openProjectTabs(p);
            }

            mainSplitPane.setRightComponent(tabs);
            gl().debug("tabs set");
            if (p.hasSimulation()) {
                enableSimulationMode();
            } else if (p.getSelectedEditPanelModel() != null) {
                enableEditMode(p);
            }

        }
    }

    private void enableSimulationMode() {
        newBrickListPanel.hidePanel();
        toolBar.showSimulationTools();
    }

    private void enableEditMode(Project p) {

        if (p.getSelectedEditPanelModel() == null) {
            newBrickListPanel.hidePanel();
            toolBar.showEverytimeTools();
        } else if (p.getSelectedEditPanelModel() != null
                && p.getSelectedEditPanelModel().hasMainCircuit()) {
            newBrickListPanel.showPanel();
            toolBar.showEditTools();
        } else {
            newBrickListPanel.hidePanel();
            toolBar.showOnlyStartTool();
        }
    }

    private void closeProjectTabs(Project p) {
        ProjectTabs tabs = projectsTabs.get(p);

        // currently opened?
        if (tabs == mainSplitPane.getRightComponent()) {
            mainSplitPane.setRightComponent(EMPTY_TABS);
        }

        if (tabs != null) {
            tabs.cleanObserved();
        }

        projectsTabs.remove(p);
    }

    private ProjectTabs openProjectTabs(Project p) {
        gl().debug("Opening Tabs for loaded project...");
        // setup of tabs
        ProjectTabs tabs = new ProjectTabs(p, view);
        tabs.setCloseTabOnMouseMiddleButton(true);
        tabs.setShowCloseButtonOnSelectedTab(true);
        tabs.setShowCloseButtonOnTab(true);
        tabs.setTabShape(JideTabbedPane.SHAPE_ROUNDED_VSNET);
        TabbedPaneChangeListener listener = new TabbedPaneChangeListener(view,
                view.getController(), tabs);
        tabs.addChangeListener(listener);
        tabs.setCloseAction(listener);

        projectsTabs.put(p, tabs);
        return tabs;
    }

    private void handleCursorChange(SessionModel source) {
        setCursor(source.getCurrentCursor());
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (!(source == view.getSessionModel().getCurrentProject())) {
            return;
        }

        if (i.isSimulationChanged() || i.isActiveEditPanelModelChanged()) {

            if (source.hasSimulation()) {
                enableSimulationMode();
            } else {
                enableEditMode(source);
            }
        } 

        if (i.isCircuitChanged() && source.getCircuit() == null) {
            enableEditMode(source);
        }
    }

    @Override
    public void settingsChanged(Settings source, SettingsInfo i) {

    }
}
