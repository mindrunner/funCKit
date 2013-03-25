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

import de.sep2011.funckit.controller.CreateTool;
import de.sep2011.funckit.controller.DragViewportTool;
import de.sep2011.funckit.controller.MultiConnectTool;
import de.sep2011.funckit.controller.SelectTool;
import de.sep2011.funckit.controller.Tool;
import de.sep2011.funckit.controller.WireTool;
import de.sep2011.funckit.controller.listener.ELearningSubmitActionListener;
import de.sep2011.funckit.controller.listener.EditToolSelectionActionListener;
import de.sep2011.funckit.controller.listener.edit.RedoActionListener;
import de.sep2011.funckit.controller.listener.edit.UndoActionListener;
import de.sep2011.funckit.controller.listener.project.OpenActionListener;
import de.sep2011.funckit.controller.listener.project.SaveFileActionListener;
import de.sep2011.funckit.controller.listener.settings.GridLockActionListener;
import de.sep2011.funckit.controller.listener.settings.GridOnOffActionListener;
import de.sep2011.funckit.controller.listener.settings.RealTimeValidationOnOffActionListener;
import de.sep2011.funckit.controller.listener.settings.SimulationUndoActionListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationBackButtonListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationDelaySpinnerListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationFasterButtonListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationForwardButtonListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationPauseButtonListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationSlowerButtonListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationSpeedSliderListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationStartButtonListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationStopButtonListener;
import de.sep2011.funckit.controller.listener.view.ModelFitsIntoCircuitListener;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.observer.SettingsInfo;
import de.sep2011.funckit.observer.SettingsObserver;
import de.sep2011.funckit.observer.SimulationModelInfo;
import de.sep2011.funckit.observer.SimulationModelObserver;
import de.sep2011.funckit.util.SpinnerWheelListener;
import de.sep2011.funckit.util.command.CommandDispatcher;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static de.sep2011.funckit.util.FunckitGuiUtil.addButtonClickKeystroke;
import static de.sep2011.funckit.util.FunckitGuiUtil.iconFromResource;
import static de.sep2011.funckit.util.internationalization.Language.tr;

import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_3;
import static java.awt.event.KeyEvent.VK_4;
import static java.awt.event.KeyEvent.VK_5;
import static java.lang.Math.round;
import static de.sep2011.funckit.util.FunckitGuiUtil.ctrlOrMeta;
import static de.sep2011.funckit.model.sessionmodel.SessionModel.ViewType.*;

/**
 * Main toolbar of the application. Displays various Controls for editing and
 * simulation.
 */
public class FunckitToolBar extends JToolBar implements SessionModelObserver, ProjectObserver,
        SettingsObserver, SimulationModelObserver {
    private static final long serialVersionUID = -6374770619968509711L;

    private JButton startButton;

    private JToggleButton newBrickButton;
    private JToggleButton moveViewButton;
    private JToggleButton selectMoveButton;
    private JToggleButton invisibleDummyButton;
    private Map<Class<? extends Tool>, JToggleButton> toolToButtonMap;
    private JToggleButton wireButton;
    private JToggleButton multiConnectButton;

    final private View view;

    private JButton stopButton;
    private JToggleButton pauseButton;
    private JButton stepBackButton;
    private JButton stepNextButton;
    private JToggleButton recordButton;
    private JToggleButton gridlockButton;
    private JToggleButton showGridButton;

    private JButton openButton;
    private JButton saveButton;
    private JButton newProjectButton;

    private JButton undoButton;
    private JButton redoButton;
    private JToggleButton liveCheckButton;

    private JButton fitsInWindowButton;
    private JButton submitButton;

    private static final Icon NEW_PROJECT_ICON = iconFromResource("/icons/toolbar/newProject.png");
    private static final Icon OPEN_ICON = iconFromResource("/icons/toolbar/open.png");
    private static final Icon SAVE_ICON = iconFromResource("/icons/toolbar/disk.png");

    private static final Icon SELECT_ICON = iconFromResource("/icons/toolbar/select_new.png");
    private static final Icon NEW_BRICK_ICON = iconFromResource("/icons/toolbar/newBrick1.png");
    private static final Icon WIRE_TOOL_ICON = iconFromResource("/icons/toolbar/wiretool.png");
    private static final Icon MOVE_VIEWPORT_ICON = iconFromResource("/icons/toolbar/moveViewport.png");

    private static final Icon START_ICON = iconFromResource("/icons/toolbar/control_play_blue.png");
    private static final Icon STOP_ICON = iconFromResource("/icons/toolbar/control_stop_blue.png");
    private static final Icon PAUSE_ICON = iconFromResource("/icons/toolbar/control_pause_blue.png");
    private static final Icon BACKWARD_ICON = iconFromResource("/icons/toolbar/control_rewind_blue.png");
    private static final Icon FORWARD_ICON = iconFromResource("/icons/toolbar/control_rewind_blue_TURNED.png");
    private static final Icon RECORD_ICON = iconFromResource("/icons/toolbar/record.png");

    private static final Icon FITS_IN_WINDOW_ICON = iconFromResource("/icons/toolbar/fitsInWindow.png");
    private static final Icon GRIDLOCK_ICON = iconFromResource("/icons/toolbar/gridlock.png");

    private static final Icon GRID_ICON = iconFromResource("/icons/toolbar/gridNew.png");
    private static final Icon UNDO_ICON = iconFromResource("/icons/toolbar/stepBack.png");
    private static final Icon REDO_ICON = iconFromResource("/icons/toolbar/stepForward.png");
    private static final Icon LIVECHECK_ICON = iconFromResource("/icons/toolbar/livecheck.png");

    private JSpinner timerDelaySpinner;
    private JSlider simulationSpeedSlider;
    private ChangeListener simulationSpeedListener;
    private Multimap<Object, SessionModel.ViewType> viewTypeObjectMap;


    /**
     * Creates a new toolbar.
     * 
     * @param view the associated View, not null.
     */
    public FunckitToolBar(View view) {
        super();
        assert view != null;
        this.view = view;
        viewTypeObjectMap = HashMultimap.create();
        initEditItems();
        initSimulationItems();
        initViewItems();
        initFileItems();
        initSettingsItems();
        initEditItems();
        initElearningItems();
        addEverytimeTools();

        view.getSessionModel().addObserver(this);
        view.getSessionModel().getSettings().addObserver(this);

        for (Project pro : view.getSessionModel().getProjects()) {
            pro.addObserver(this);
        }
    }

    private void initFileItems() {
        saveButton = new JButton();
        saveButton.addActionListener(new SaveFileActionListener(view, view.getController()));
        saveButton.setIcon(SAVE_ICON);
        saveButton.setToolTipText(tr("menuBar.file.save"));
        viewTypeObjectMap.put(saveButton, VIEW_TYPE_STANDALONE);
            
        newProjectButton = new JButton();
        newProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                view.openNewProject(null);
            }
        });
        newProjectButton.setIcon(NEW_PROJECT_ICON);
        newProjectButton.setToolTipText(tr("menuBar.file.newProject"));
        viewTypeObjectMap.put(newProjectButton, VIEW_TYPE_STANDALONE);

        openButton = new JButton();
        openButton.addActionListener(new OpenActionListener(view, view.getController()));
        openButton.setIcon(OPEN_ICON);
        openButton.setToolTipText(tr("menuBar.file.open..."));
        viewTypeObjectMap.put(openButton, VIEW_TYPE_STANDALONE);
    }

    private void initSettingsItems() {
        Settings settings = view.getSessionModel().getSettings();
        gridlockButton = new JToggleButton();
        gridlockButton.addActionListener(new GridLockActionListener(view, view.getController()));
        gridlockButton.setIcon(GRIDLOCK_ICON);
        gridlockButton.setSelected(true);
        gridlockButton.setSelected(settings.getBoolean(Settings.GRID_LOCK));
        gridlockButton.setToolTipText(tr("menuBar.settings.lockGrid"));
        viewTypeObjectMap.put(gridlockButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(gridlockButton, VIEW_TYPE_ELEANING_SOLVE);
    }

    private void initViewItems() {
        Settings settings = view.getSessionModel().getSettings();
        fitsInWindowButton = new JButton();
        fitsInWindowButton.addActionListener(new ModelFitsIntoCircuitListener(view, view
                .getController()));
        fitsInWindowButton.setIcon(FITS_IN_WINDOW_ICON);
        fitsInWindowButton.setToolTipText(tr("menuBar.view.modelFitsInView"));
        viewTypeObjectMap.put(fitsInWindowButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(fitsInWindowButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(fitsInWindowButton, VIEW_TYPE_PRESENTER);

        showGridButton = new JToggleButton();
        showGridButton.addActionListener(new GridOnOffActionListener(view, view.getController()));
        showGridButton.setIcon(GRID_ICON);
        showGridButton.setSelected(settings.getBoolean(Settings.SHOW_GRID));
        showGridButton.setToolTipText(tr("menuBar.view.toggleGrid"));
        viewTypeObjectMap.put(showGridButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(showGridButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(showGridButton, VIEW_TYPE_PRESENTER);



    }

    private void initSimulationItems() {
        Settings settings = view.getSessionModel().getSettings();
        Project pro = view.getSessionModel().getCurrentProject();

        startButton = new JButton();
        startButton.setToolTipText(tr("simulationToolbar.start"));
        startButton
                .addActionListener(new SimulationStartButtonListener(view, view.getController()));
        startButton.setIcon(START_ICON);
        viewTypeObjectMap.put(startButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(startButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(startButton, VIEW_TYPE_PRESENTER);

        stopButton = new JButton();
        stopButton.addActionListener(new SimulationStopButtonListener(view, view.getController()));
        stopButton.setIcon(STOP_ICON);
        stopButton.setToolTipText(tr("simulationToolbar.stop"));
        viewTypeObjectMap.put(stopButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(stopButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(stopButton, VIEW_TYPE_PRESENTER);

        pauseButton = new JToggleButton();
        pauseButton
                .addActionListener(new SimulationPauseButtonListener(view, view.getController()));
        pauseButton.setIcon(PAUSE_ICON);
        pauseButton.setSelected(pro == null ? false : pro.isSimulationPaused());
        pauseButton.setToolTipText(tr("simulationToolbar.pause"));
        viewTypeObjectMap.put(pauseButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(pauseButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(pauseButton, VIEW_TYPE_PRESENTER);

        JButton fasterButton = new JButton(tr("simulationToolbar.faster"));
        fasterButton.addActionListener(new SimulationFasterButtonListener(view, view
                .getController()));
        viewTypeObjectMap.put(fasterButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(fasterButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(fasterButton, VIEW_TYPE_PRESENTER);

        JButton slowerButton = new JButton(tr("simulationToolbar.slower"));
        slowerButton.addActionListener(new SimulationSlowerButtonListener(view, view
                .getController()));
        viewTypeObjectMap.put(slowerButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(slowerButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(slowerButton, VIEW_TYPE_PRESENTER);
        
        recordButton = new JToggleButton();
        recordButton
                .addActionListener(new SimulationUndoActionListener(view, view.getController()));
        recordButton.setIcon(RECORD_ICON);
        recordButton.setSelected(true);
        recordButton.setSelected(settings.getBoolean(Settings.SIMULATION_UNDO_ENABLED));
        recordButton.setToolTipText(tr("menuBar.settings.simulationUndo"));
        viewTypeObjectMap.put(recordButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(recordButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(recordButton, VIEW_TYPE_PRESENTER);

        stepBackButton = new JButton();
        stepBackButton.addActionListener(new SimulationBackButtonListener(view, view
                .getController()));
        stepBackButton.setIcon(BACKWARD_ICON);
        stepBackButton.setEnabled(settings.getBoolean(Settings.SIMULATION_UNDO_ENABLED));
        stepBackButton.setToolTipText(tr("simulationToolbar.stepBack"));
        viewTypeObjectMap.put(stepBackButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(stepBackButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(stepBackButton, VIEW_TYPE_PRESENTER);

        stepNextButton = new JButton();
        stepNextButton.addActionListener(new SimulationForwardButtonListener(view, view
                .getController()));
        stepNextButton.setIcon(FORWARD_ICON);
        stepNextButton.setToolTipText(tr("simulationToolbar.stepForward"));
        viewTypeObjectMap.put(stepNextButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(stepNextButton, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(stepNextButton, VIEW_TYPE_PRESENTER);

        int spinnerValue = pro == null ? 1 : pro.getTimerDelay();

        timerDelaySpinner = new JSpinner(new SpinnerNumberModel(spinnerValue, 1, Integer.MAX_VALUE,
                10));
        final JSpinner.NumberEditor delayEditor = new JSpinner.NumberEditor(timerDelaySpinner);
        delayEditor.getTextField().setFormatterFactory(
                new JFormattedTextField.AbstractFormatterFactory() {

                    @Override
                    public AbstractFormatter getFormatter(JFormattedTextField tf) {
                        return new DelayFormater();
                    }
                });
        timerDelaySpinner.setEditor(delayEditor);
        timerDelaySpinner.addMouseWheelListener(new SpinnerWheelListener(timerDelaySpinner));
        timerDelaySpinner.addChangeListener(new SimulationDelaySpinnerListener(view, view
                .getController(), timerDelaySpinner));
        timerDelaySpinner.setMaximumSize(new Dimension(delayEditor.getTextField()
                .getPreferredSize().width, timerDelaySpinner.getMaximumSize().height));
        viewTypeObjectMap.put(timerDelaySpinner, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(timerDelaySpinner, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(timerDelaySpinner, VIEW_TYPE_PRESENTER);
        
        simulationSpeedSlider = new JSlider(getOrientation(), 0, 100, 50);
        simulationSpeedListener = new SimulationSpeedSliderListener(view, view.getController(),
                simulationSpeedSlider.getModel());
        simulationSpeedSlider.setMinorTickSpacing(5);
        simulationSpeedSlider.setMajorTickSpacing(20);
        simulationSpeedSlider.setPaintTicks(true);
        simulationSpeedSlider.setPaintLabels(true);
        double sliderFactor = settings.getDouble(Settings.SIMULATION_SLIDER_FACTOR);
        int speedValue = pro == null ? 50 : (int) round(pro.getTimerDelay() / sliderFactor);
        simulationSpeedSlider.getModel().setValue(speedValue);
        simulationSpeedSlider.getModel().addChangeListener(simulationSpeedListener);
        viewTypeObjectMap.put(simulationSpeedSlider, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(simulationSpeedSlider, VIEW_TYPE_ELEANING_SOLVE);
        viewTypeObjectMap.put(simulationSpeedSlider, VIEW_TYPE_PRESENTER);
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                simulationSpeedSlider.setOrientation(getOrientation());

            }

            @Override
            public void componentResized(ComponentEvent e) {
                simulationSpeedSlider.setOrientation(getOrientation());

            }

            @Override
            public void componentMoved(ComponentEvent e) {
                simulationSpeedSlider.setOrientation(getOrientation());

            }

        });

    }

    private void initEditItems() {
        Settings settings = view.getSessionModel().getSettings();
        toolToButtonMap = new HashMap<Class<? extends Tool>, JToggleButton>();
        Project pro = view.getSessionModel().getCurrentProject();

        createToolButtonGroup();

        // init from model
        selectButtonFromTool(view.getSessionModel().getTool().getClass());

        // UNDO REDO
        undoButton = new JButton();
        undoButton.addActionListener(new UndoActionListener(view, view.getController()));
        undoButton.setIcon(UNDO_ICON);
        undoButton.setToolTipText(tr("menuBar.edit.undo"));
        undoButton.setEnabled(pro != null && pro.getGraphCommandDispatcher().canStepBack());
        viewTypeObjectMap.put(undoButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(undoButton, VIEW_TYPE_ELEANING_SOLVE);

        redoButton = new JButton();
        redoButton.addActionListener(new RedoActionListener(view, view.getController()));
        redoButton.setIcon(REDO_ICON);
        redoButton.setToolTipText(tr("menuBar.edit.redo"));
        redoButton.setEnabled(pro != null && pro.getGraphCommandDispatcher().canStepForward());
        viewTypeObjectMap.put(redoButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(redoButton, VIEW_TYPE_ELEANING_SOLVE);

        //LIVECHECK
        liveCheckButton = new JToggleButton();
        liveCheckButton.addActionListener(new RealTimeValidationOnOffActionListener(view));
        liveCheckButton.setIcon(LIVECHECK_ICON);
        liveCheckButton.setToolTipText(tr("menuBar.settings.liveCheck"));
        liveCheckButton.setSelected(settings.getBoolean(Settings.REALTIME_VALIDATION));
        viewTypeObjectMap.put(liveCheckButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(liveCheckButton, VIEW_TYPE_ELEANING_SOLVE);
    }
    
    private void initElearningItems() {
        submitButton = new JButton(tr("toolbar.elearning.submit"));
        submitButton.addActionListener(new ELearningSubmitActionListener(view, view.getController()));
        submitButton.setToolTipText(tr("toolbar.elearning.submit.tooltip"));
        submitButton.setBackground(Color.GREEN);
        viewTypeObjectMap.put(submitButton, VIEW_TYPE_ELEANING_SOLVE);
    }

    private void selectButtonFromTool(Class<? extends Tool> toolc) {
        JToggleButton b = toolToButtonMap.get(toolc);
        if (b == null) {
            invisibleDummyButton.setSelected(true);
        } else {
            b.setSelected(true);
        }
    }

    private void createToolButtonGroup() {

        selectMoveButton = new JToggleButton();
        selectMoveButton.setToolTipText(tr("editToolBar.selectMove"));
        selectMoveButton.setActionCommand(SelectTool.class.getName());
        toolToButtonMap.put(SelectTool.class, selectMoveButton);
        selectMoveButton.addActionListener(new EditToolSelectionActionListener(view, view
                .getController()));
        selectMoveButton.setIcon(SELECT_ICON);
        viewTypeObjectMap.put(selectMoveButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(selectMoveButton, VIEW_TYPE_ELEANING_SOLVE);
        addButtonClickKeystroke(selectMoveButton, KeyStroke.getKeyStroke(VK_1, ctrlOrMeta()));

        newBrickButton = new JToggleButton();
        newBrickButton.setActionCommand(CreateTool.class.getName());
        toolToButtonMap.put(CreateTool.class, newBrickButton);
        newBrickButton.addActionListener(new EditToolSelectionActionListener(view, view
                .getController()));
        newBrickButton.setIcon(NEW_BRICK_ICON);
        newBrickButton.setToolTipText(tr("editToolBar.newBrick"));
        viewTypeObjectMap.put(newBrickButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(newBrickButton, VIEW_TYPE_ELEANING_SOLVE);
        addButtonClickKeystroke(newBrickButton, KeyStroke.getKeyStroke(VK_2, ctrlOrMeta()));

        wireButton = new JToggleButton();
        wireButton.setActionCommand(WireTool.class.getName());
        toolToButtonMap.put(WireTool.class, wireButton);
        wireButton
                .addActionListener(new EditToolSelectionActionListener(view, view.getController()));
        wireButton.setIcon(WIRE_TOOL_ICON);
        wireButton.setToolTipText(tr("editToolBar.wireTool"));
        viewTypeObjectMap.put(wireButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(wireButton, VIEW_TYPE_ELEANING_SOLVE);
        addButtonClickKeystroke(wireButton, KeyStroke.getKeyStroke(VK_3, ctrlOrMeta()));
        
        multiConnectButton = new JToggleButton("Multi");
        multiConnectButton.setActionCommand(MultiConnectTool.class.getName());
        toolToButtonMap.put(MultiConnectTool.class, multiConnectButton);
        multiConnectButton
                .addActionListener(new EditToolSelectionActionListener(view, view.getController()));
        //multiConnectButton.setIcon(WIRE_TOOL_ICON);
        multiConnectButton.setToolTipText(tr("editToolBar.multiConnectTool"));
        addButtonClickKeystroke(multiConnectButton, KeyStroke.getKeyStroke(VK_5, ctrlOrMeta()));
        viewTypeObjectMap.put(multiConnectButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(multiConnectButton, VIEW_TYPE_ELEANING_SOLVE);

        moveViewButton = new JToggleButton();
        moveViewButton.setActionCommand(DragViewportTool.class.getName());
        toolToButtonMap.put(DragViewportTool.class, moveViewButton);
        moveViewButton.addActionListener(new EditToolSelectionActionListener(view, view
                .getController()));
        moveViewButton.setIcon(MOVE_VIEWPORT_ICON);
        moveViewButton.setToolTipText(tr("editToolBar.moveViewport"));
        viewTypeObjectMap.put(moveViewButton, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(moveViewButton, VIEW_TYPE_ELEANING_SOLVE);
        //addButtonClickKeystroke(moveViewButton, KeyStroke.getKeyStroke(VK_4, CTRL_DOWN_MASK));
        addButtonClickKeystroke(moveViewButton, KeyStroke.getKeyStroke(VK_4, ctrlOrMeta()));

        invisibleDummyButton = new JToggleButton("Dummy");

        ButtonGroup group = new ButtonGroup();
        group.add(selectMoveButton);
        group.add(newBrickButton);
        group.add(wireButton);
        group.add(moveViewButton);
        group.add(invisibleDummyButton);
        group.add(multiConnectButton);
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.isToolChanged()) {
            selectButtonFromTool(source.getTool().getClass());
        }

        if (i.hasProjectAdded()) {
            i.getChangedProject().addObserver(this);
        }

        if (i.hasProjectRemoved()) {
            i.getChangedProject().deleteObserver(this);
        }

        if (i.hasCurrentProjectChanged()) {
            pauseButton.setSelected(source.getCurrentProject().isSimulationPaused());
            timerDelaySpinner.setValue(source.getCurrentProject().getTimerDelay());
            timerDelaySpinner.setEnabled(!source.getCurrentProject().isSimulationPaused());
            updateSlider(source.getCurrentProject());
        }
    }
    
    public void addViewTypeDependant(java.awt.Component component) {
        if(viewTypeObjectMap.containsEntry(component, view.getSessionModel().getViewType())) {
            add(component);
        }
    }
    
    public void addSeparatorOnlyIfNotNeedless() {
        if (getComponentCount() == 0) {
            return;
        }

        if (getComponentAtIndex(getComponentCount() - 1) instanceof Separator) {
            return;
        }
        
        addSeparator();
    }
    
    private void addELearningTools() {
        addSeparatorOnlyIfNotNeedless();
        addViewTypeDependant(submitButton);
    }

    public void showEditTools() {
        removeAll();
        addEverytimeTools();
        addViewTypeDependant(startButton);
        addViewTypeDependant(pauseButton);
        addSeparatorOnlyIfNotNeedless();
        addViewTypeDependant(selectMoveButton);
        addViewTypeDependant(newBrickButton);
        addViewTypeDependant(wireButton);
        addViewTypeDependant(moveViewButton);
        if (view.getSessionModel().getSettings().getBoolean(Settings.EXPERT_MODE)) {
            addViewTypeDependant(multiConnectButton);
        }
        addSeparatorOnlyIfNotNeedless();
        addViewTypeDependant(liveCheckButton);
        addViewTypeDependant(undoButton);
        addViewTypeDependant(redoButton);
        addELearningTools();
        actualizeView();
    }

    public void showSimulationTools() {
        removeAll();
        addEverytimeTools();
        addViewTypeDependant(stopButton);
        addViewTypeDependant(pauseButton);
        addViewTypeDependant(stepBackButton);
        addViewTypeDependant(stepNextButton);
        addSeparatorOnlyIfNotNeedless();
        if (view.getSessionModel().getSettings().getBoolean(Settings.EXPERT_MODE)) {
            addViewTypeDependant(timerDelaySpinner);
        } else {
            addViewTypeDependant(simulationSpeedSlider);
        }
        actualizeView();
    }

    private void addEverytimeTools() {
        addViewTypeDependant(newProjectButton);
        addViewTypeDependant(openButton);
        addViewTypeDependant(saveButton);
        addSeparatorOnlyIfNotNeedless();
        addViewTypeDependant(fitsInWindowButton);
        addViewTypeDependant(gridlockButton);
        addViewTypeDependant(showGridButton);
        addSeparatorOnlyIfNotNeedless();
        addViewTypeDependant(recordButton);
        addSeparatorOnlyIfNotNeedless();
    }

    public void showEverytimeTools() {
        removeAll();
        addEverytimeTools();
        actualizeView();
    }

    public void showOnlyStartTool() {
        removeAll();
        addEverytimeTools();
        addViewTypeDependant(startButton);
        addViewTypeDependant(pauseButton);
        actualizeView();
    }

    void actualizeView() {
        if (view.getMainRootPane() != null) {
            view.getMainRootPane().validate();
        }
        revalidate();
        repaint();
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (i.isSimulationControlStateModified()) {
            pauseButton.setSelected(source.isSimulationPaused());
            timerDelaySpinner.setValue(source.getTimerDelay());
            timerDelaySpinner.setEnabled(!source.isSimulationPaused());
            updateSlider(source);
        }

        if (i.isSimulationChanged()) {
            if (i.getOldSimulation() != null) {
                i.getOldSimulation().deleteObserver(this);
            }

            Simulation simulation = source.getSimulation();
            if (simulation != null) {
                simulation.addObserver(this);
                updateStepBackButton();
            }

        }

        undoButton.setEnabled(source.getGraphCommandDispatcher().canStepBack());
        redoButton.setEnabled(source.getGraphCommandDispatcher().canStepForward());
    }

    private void updateSlider(Project project) {
        if (!simulationSpeedSlider.getModel().getValueIsAdjusting()) {
            simulationSpeedSlider.getModel().removeChangeListener(simulationSpeedListener);

            if (project.isSimulationPaused()) {
                simulationSpeedSlider.setValue(0);
            } else {

                double sliderFactor = view.getSessionModel().getSettings()
                        .getDouble(Settings.SIMULATION_SLIDER_FACTOR);
                simulationSpeedSlider.getModel().setValue(
                        100 - ((int) round(project.getTimerDelay() / sliderFactor)));
            }
            simulationSpeedSlider.getModel().addChangeListener(simulationSpeedListener);
        }
    }

    private static class DelayFormater extends JFormattedTextField.AbstractFormatter {

        private static final long serialVersionUID = 600115871763311301L;
        private static final String prestr = "Delay: ";
        private static final String poststr = " ms";

        @Override
        public Object stringToValue(String text) throws ParseException {
            try {
                return Integer.parseInt(text.replace(prestr, "").replace(poststr, ""));
            } catch (NumberFormatException e) {
                throw new ParseException(e.toString(), -1);
            }
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            return prestr + value + poststr;

        }

    }

    @Override
    public void settingsChanged(Settings source, SettingsInfo i) {
        if (i.getChangedSetting().equals(Settings.SIMULATION_UNDO_ENABLED)) {
            recordButton.setSelected(source.getBoolean(Settings.SIMULATION_UNDO_ENABLED));
            updateStepBackButton();
        }

        if (i.getChangedSetting().equals(Settings.GRID_LOCK)) {
            gridlockButton.setSelected(source.getBoolean(Settings.GRID_LOCK));
        }

        if (i.getChangedSetting().equals(Settings.SHOW_GRID)) {
            showGridButton.setSelected(source.getBoolean(Settings.SHOW_GRID));
        }
        if (i.getChangedSetting().equals(Settings.REALTIME_VALIDATION)){
        	liveCheckButton.setSelected(source.getBoolean(Settings.REALTIME_VALIDATION));
        }

    }

    private void updateStepBackButton() {
        boolean enable;
        Settings settings = view.getSessionModel().getSettings();
        CommandDispatcher disp = view.getSessionModel().getCurrentSimulationCommandDispatcher();
        enable = settings.getBoolean(Settings.SIMULATION_UNDO_ENABLED);

        if (disp != null) {
            enable = enable && disp.canStepBack();
        }

        stepBackButton.setEnabled(enable);

    }

    @Override
    public void simulationModelChanged(Simulation source, SimulationModelInfo i) {
        updateStepBackButton();
    }

}
