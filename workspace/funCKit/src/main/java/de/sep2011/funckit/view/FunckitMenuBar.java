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

import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;
import de.sep2011.funckit.controller.listener.AboutFunckitActionListener;
import de.sep2011.funckit.controller.listener.ExitApplicationActionListener;
import de.sep2011.funckit.controller.listener.HelpActionListener;
import de.sep2011.funckit.controller.listener.edit.CopyActionListener;
import de.sep2011.funckit.controller.listener.edit.CutActionListener;
import de.sep2011.funckit.controller.listener.edit.DeleteSelectedElementsActionListener;
import de.sep2011.funckit.controller.listener.edit.PasteActionListener;
import de.sep2011.funckit.controller.listener.edit.RedoActionListener;
import de.sep2011.funckit.controller.listener.edit.SelectAllActionListener;
import de.sep2011.funckit.controller.listener.edit.UndoActionListener;
import de.sep2011.funckit.controller.listener.edit.WizardActionListener;
import de.sep2011.funckit.controller.listener.project.ExportComponentActionListener;
import de.sep2011.funckit.controller.listener.project.OpenActionListener;
import de.sep2011.funckit.controller.listener.project.SaveFileActionListener;
import de.sep2011.funckit.controller.listener.project.SaveFileAsActionListener;
import de.sep2011.funckit.controller.listener.settings.ChangeColorActionListener;
import de.sep2011.funckit.controller.listener.settings.GridLockActionListener;
import de.sep2011.funckit.controller.listener.settings.GridOnOffActionListener;
import de.sep2011.funckit.controller.listener.settings.LafChangeActionListener;
import de.sep2011.funckit.controller.listener.settings.LanguageChangeActionListener;
import de.sep2011.funckit.controller.listener.settings.LowQualityOnOffActionListener;
import de.sep2011.funckit.controller.listener.settings.RealTimeValidationOnOffActionListener;
import de.sep2011.funckit.controller.listener.settings.RestoreDefaultActionListener;
import de.sep2011.funckit.controller.listener.settings.SimulationUndoActionListener;
import de.sep2011.funckit.controller.listener.settings.ToggleTooltipsActionListener;
import de.sep2011.funckit.controller.listener.view.ModelFitsIntoCircuitListener;
import de.sep2011.funckit.controller.listener.view.Zoom100PercentListener;
import de.sep2011.funckit.controller.listener.view.ZoomInActionListener;
import de.sep2011.funckit.controller.listener.view.ZoomOutActionListener;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.observer.EditPanelModelInfo;
import de.sep2011.funckit.observer.EditPanelModelObserver;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.observer.SettingsInfo;
import de.sep2011.funckit.observer.SettingsObserver;
import de.sep2011.funckit.util.FunckitGuiUtil;
import de.sep2011.funckit.util.internationalization.Language;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static de.sep2011.funckit.util.internationalization.Language.tr;
import static java.awt.event.KeyEvent.ALT_MASK;
import static java.awt.event.KeyEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_F1;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_PLUS;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;
import static javax.swing.KeyStroke.getKeyStroke;

/**
 * Represents menu bar object for access to most actions like opening, saving or
 * importing projects or change settings.
 */
public class FunckitMenuBar extends JMenuBar implements SessionModelObserver, SettingsObserver,
        ProjectObserver, EditPanelModelObserver {

    private static final long serialVersionUID = 957124217216842770L;

    public static final String[] EDITABLE_COLORS = new String[]{
            Settings.ELEMENT_BORDER_COLOR,
            Settings.ELEMENT_FILL_COLOR,
            Settings.ELEMENT_NAME_COLOR,
            Settings.ELEMENT_ERROR_BORDER_COLOR,
            Settings.ELEMENT_SELECTED_BORDER_COLOR,
            Settings.ELEMENT_SELECTED_FILL_COLOR,
            Settings.ELEMENT_OUTPUT_LABEL_COLOR,
            Settings.ELEMENT_INFO_BORDER_COLOR,
            Settings.ELEMENT_INFO_FILL_COLOR,
            Settings.ELEMENT_ACTIVE_BORDER_COLOR,
            Settings.ELEMENT_ACTIVE_FILL_COLOR,
            Settings.WIRE_COLOR,
            Settings.GATE_FILL_COLOR,
            Settings.GATE_BORDER_COLOR,
            Settings.SWITCH_BORDER_COLOR,
            Settings.SWITCH_FILL_COLOR,
            Settings.LIGHT_BORDER_COLOR,
            Settings.LIGHT_FILL_COLOR,
            Settings.GHOST_BORDER_COLOR,
            Settings.GHOST_FILL_COLOR,
            Settings.GHOST_TEXT_COLOR,
            Settings.ELEMENT_INPUT_LABEL_COLOR,
            Settings.ELEMENT_OUTPUT_LABEL_COLOR,
            Settings.SELECTION_BORDER_COLOR,
            Settings.SELECTION_FILL_COLOR
    };

    private final View view;

    /* Menu items for file menu. */

    /**
     * Item for creating a new project.
     */
    private JMenuItem newProjectItem;

    /**
     * Item for loading an existing project.
     */
    private JMenuItem openItem;

    /**
     * Item for saving current project.
     */
    private JMenuItem saveItem;

    /**
     * Item for saving as current project.
     */
    private JMenuItem saveAsItem;

    /**
     * Item for importing circuits.
     */
    // JMenuItem importComponentItem;

    /**
     * Item for exporting circuits.
     */
    private JMenuItem exportComponentItem;

    /**
     * Button to quit application.
     */
    private JMenuItem exitItem;

    /* Items for edit menu. */

    /**
     * Item for undoing edit actions.
     */
    private JMenuItem undoItem;

    /**
     * Item for redoing edit actions.
     */
    private JMenuItem redoItem;

    /**
     * Item for deletion of elements on the current panel model.
     */
    private JMenuItem deleteElementsItem;

    /* Items for help menu. */

    /**
     * Item for opening a help system.
     */
    private JMenuItem helpItem;

    /**
     * Item for zoom 100 percent.
     */
    private JMenuItem hundredProZoomItem;

    /**
     * Item for displaying author and version information.
     */
    private JMenuItem aboutItem;

    /* Menu items for settings menu. */

    /**
     * Item for de- or activating live check.
     */
    private JCheckBoxMenuItem liveCheckItem;

    /**
     * Item for de- or activating the gridlock.
     */
    private JCheckBoxMenuItem gridLockItem;

    /**
     * Item for de- or activating the step back in simulation.
     */
    private JCheckBoxMenuItem simulationUndoItem;

    /* Items for view edit menu. */

    /**
     * Item for displaying elements bigger.
     */
    private JMenuItem biggerItem;

    /**
     * Item for displaying elements smaller.
     */
    private JMenuItem smallerItem;

    /**
     * Ja fuer was wohl, alla?
     */
    private JMenuItem modelFitsInView;

    /**
     * Item for toggling grid on edit panel.
     */
    private JCheckBoxMenuItem toggleGridItem;

    private Set<JCheckBoxMenuItem> languageItems;

    private JMenuItem copyItem;

    private JMenuItem pasteItem;

    private JMenuItem cutItem;

    private JMenu lafMenu;

    private JCheckBoxMenuItem toggleLowQualityItem;

    private JMenuItem selectAllItem;

    private JMenuItem wizardItem;

    private JCheckBoxMenuItem toggleTooltipsItem;

    private JMenu colorMenu;
    
    /**
     * Create a new {@link FunckitMenuBar}.
     *
     * @param view the associated View object
     */
    public FunckitMenuBar(View view) {
        this.view = view;
        createFileMenu();        
        createEditMenu();
        createSettingsMenu();
        createViewMenu();
        createHelpMenu();
        updateItemState();
        initObserver();
    }

    private void createFileMenu() {
        /*
         * Following the menu with keyname "file" is created. It is structured
         * in several items separated by whitespaces. Sometimes gui separators
         * are inserted between them. For better overview keep construction of
         * common items in one context (without whitespaces).
         */
        JMenu fileMenu = new JMenu(tr("menuBar.file"));
        fileMenu.setMnemonic(tr("menuBar.Mnemonic.file").charAt(0));

        switch (view.getSessionModel().getViewType()) {
        case VIEW_TYPE_STANDALONE:
            add(fileMenu);
            break;
        }

        newProjectItem = new JMenuItem(tr("menuBar.file.newProject"));
        newProjectItem.setAccelerator(getKeyStroke(VK_N, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        newProjectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                view.openNewProject(null);
            }
        });
        newProjectItem.setMnemonic(tr("menuBar.Mnemonic.file.newProject").charAt(0));

        openItem = new JMenuItem(tr("menuBar.file.open..."));
        openItem.addActionListener(new OpenActionListener(view, view.getController()));
        openItem.setAccelerator(getKeyStroke(VK_O, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        openItem.setMnemonic(tr("menuBar.Mnemonic.file.open...").charAt(0));

        saveItem = new JMenuItem(tr("menuBar.file.save"));
        saveItem.addActionListener(new SaveFileActionListener(view, view.getController()));
        saveItem.setAccelerator(getKeyStroke(VK_S, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        saveItem.setMnemonic(tr("menuBar.Mnemonic.file.save").charAt(0));

        saveAsItem = new JMenuItem(tr("menuBar.file.saveAs..."));
        saveAsItem.addActionListener(new SaveFileAsActionListener(view, view.getController()));
        saveAsItem.setAccelerator(getKeyStroke(VK_S, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask() | SHIFT_MASK));
        saveAsItem.setMnemonic(tr("menuBar.Mnemonic.file.saveAs...").charAt(0));

        exportComponentItem = new JMenuItem(tr("menuBar.file.exportComponent..."));
        exportComponentItem.setAccelerator(getKeyStroke(VK_E, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        exportComponentItem.addActionListener(new ExportComponentActionListener(view, view
                .getController()));
        exportComponentItem.setMnemonic(tr("menuBar.Mnemonic.file.exportComponent...").charAt(0));

        exitItem = new JMenuItem(tr("menuBar.file.exit"));
        exitItem.setAccelerator(getKeyStroke(VK_Q, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        exitItem.addActionListener(new ExitApplicationActionListener(view, view.getController()));
        exitItem.setMnemonic(tr("menuBar.Mnemonic.file.exit").charAt(0));
        
        switch (view.getSessionModel().getViewType()) {
        case VIEW_TYPE_STANDALONE:
            fileMenu.add(newProjectItem);
            fileMenu.addSeparator();
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
            fileMenu.addSeparator();
            fileMenu.add(exportComponentItem);
            fileMenu.addSeparator();
            fileMenu.add(exitItem);
            break;
        }
    }

    private void createEditMenu() {
        // create edit menu
        JMenu editMenu = new JMenu(tr("menuBar.edit"));
        editMenu.setMnemonic(tr("menuBar.Mnemonic.edit").charAt(0));
        switch (view.getSessionModel().getViewType()) {
        case VIEW_TYPE_PRESENTER:
            break;

        default:
            add(editMenu);
            break;
        }
        

        undoItem = new JMenuItem(tr("menuBar.edit.undo"));
        undoItem.addActionListener(new UndoActionListener(view, view.getController()));
        undoItem.setAccelerator(getKeyStroke(VK_Z, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        undoItem.setMnemonic(tr("menuBar.Mnemonic.edit.undo").charAt(0));

        redoItem = new JMenuItem(tr("menuBar.edit.redo"));
        redoItem.addActionListener(new RedoActionListener(view, view.getController()));
        redoItem.setAccelerator(getKeyStroke(VK_Y, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        redoItem.setMnemonic(tr("menuBar.Mnemonic.edit.redo").charAt(0));

        cutItem = new JMenuItem(tr("menuBar.edit.cut"));
        cutItem.addActionListener(new CutActionListener(view, view.getController()));
        cutItem.setAccelerator(getKeyStroke(VK_X, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        cutItem.setMnemonic(tr("menuBar.Mnemonic.edit.cut").charAt(0));

        copyItem = new JMenuItem(tr("menuBar.edit.copy"));
        copyItem.addActionListener(new CopyActionListener(view, view.getController()));
        copyItem.setAccelerator(getKeyStroke(VK_C, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        copyItem.setMnemonic(tr("menuBar.Mnemonic.edit.copy").charAt(0));

        pasteItem = new JMenuItem(tr("menuBar.edit.paste"));
        pasteItem.addActionListener(new PasteActionListener(view, view.getController()));
        pasteItem.setAccelerator(getKeyStroke(VK_V, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        pasteItem.setMnemonic(tr("menuBar.Mnemonic.edit.paste").charAt(0));

        deleteElementsItem = new JMenuItem(tr("menuBar.edit.delete"));
        deleteElementsItem.addActionListener(new DeleteSelectedElementsActionListener(view, view
                .getController()));
        if (Application.OS == OperatingSystem.OSX) {
            FunckitGuiUtil.addButtonClickKeystroke(deleteElementsItem, getKeyStroke(VK_BACK_SPACE, 0));
        }
        deleteElementsItem.setAccelerator(getKeyStroke(VK_DELETE, 0));
        deleteElementsItem.setMnemonic(tr("menuBar.Mnemonic.edit.delete").charAt(0));

        selectAllItem = new JMenuItem(tr("menuBar.edit.selectAll"));
        selectAllItem.addActionListener(new SelectAllActionListener(view, view.getController()));
        selectAllItem.setAccelerator(getKeyStroke(VK_A, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        selectAllItem.setMnemonic(tr("menuBar.Mnemonic.edit.selectAll").charAt(0));

        wizardItem = new JMenuItem(tr("menuBar.edit.wizard"));
        wizardItem.addActionListener(new WizardActionListener(view, view.getController()));
        wizardItem.setAccelerator(getKeyStroke(VK_ENTER, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask() | ALT_MASK));
        wizardItem.setMnemonic(tr("menuBar.Mnemonic.edit.wizard").charAt(0));
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(deleteElementsItem);
        editMenu.add(selectAllItem);
        editMenu.addSeparator();
        editMenu.add(wizardItem);
    }

    private void createSettingsMenu() {
        Settings settings = view.getSessionModel().getSettings();
        // create settings menu
        JMenu settingsMenu = new JMenu(tr("menuBar.settings"));
        settingsMenu.setMnemonic(tr("menuBar.Mnemonic.settings").charAt(0));
        add(settingsMenu);

        JMenu languageMenu = new JMenu(tr("menuBar.settings.language"));
        languageMenu.setMnemonic(tr("menuBar.Mnemonic.settings.language").charAt(0));

        lafMenu = new JMenu(tr("menuBar.settings.lafSubMenu"));
        lafMenu.setMnemonic(tr("menuBar.Mnemonic.settings.lafSubMenu").charAt(0));
        fillLafSubmenu();

        languageItems = new LinkedHashSet<JCheckBoxMenuItem>();
        for (Locale loc : Language.getAvailableLocales()) {
            JCheckBoxMenuItem languageItem = new JCheckBoxMenuItem(loc.getDisplayLanguage());
            languageMenu.add(languageItem);
            languageItem.setActionCommand(loc.getLanguage());
            languageItem.addActionListener(new LanguageChangeActionListener(view, view
                    .getController()));
            languageItem.setSelected(loc.equals(Language.getCurrentLocale()));

            languageItems.add(languageItem);
        }


        liveCheckItem = new JCheckBoxMenuItem(tr("menuBar.settings.liveCheck"));
        liveCheckItem.setMnemonic(tr("menuBar.Mnemonic.settings.liveCheck").charAt(0));
        liveCheckItem.addActionListener(new RealTimeValidationOnOffActionListener(view));
        liveCheckItem.setSelected(view.getSessionModel().getSettings()
                .getBoolean(Settings.REALTIME_VALIDATION));

        gridLockItem = new JCheckBoxMenuItem(tr("menuBar.settings.lockGrid"));
        gridLockItem.addActionListener(new GridLockActionListener(view, view.getController()));
        gridLockItem.setSelected(view.getSessionModel().getSettings()
                .getBoolean(Settings.GRID_LOCK));
        gridLockItem.setMnemonic(tr("menuBar.Mnemonic.settings.lockGrid").charAt(0));

        simulationUndoItem = new JCheckBoxMenuItem(tr("menuBar.settings.simulationUndo"));
        simulationUndoItem.addActionListener(new SimulationUndoActionListener(view, view
                .getController()));
        simulationUndoItem.setSelected(settings
                .getBoolean(Settings.SIMULATION_UNDO_ENABLED));
        simulationUndoItem.setMnemonic(tr("menuBar.Mnemonic.settings.simulationUndo").charAt(0));

        toggleLowQualityItem = new JCheckBoxMenuItem(tr("menuBar.settings.toggleLowQuality"));
        toggleLowQualityItem.setSelected(view.getSessionModel().getSettings()
                .getBoolean(Settings.LOW_RENDERING_QUALITY_MODE));
        toggleLowQualityItem
                .setMnemonic(tr("menuBar.Mnemonic.settings.toggleLowQuality").charAt(0));
        toggleLowQualityItem.addActionListener(new LowQualityOnOffActionListener(view, view
                .getController()));
        toggleTooltipsItem = new JCheckBoxMenuItem(
                tr("menuBar.settings.toggleTooltips"));
        toggleTooltipsItem.setSelected(view.getSessionModel().getSettings()
                .getBoolean(Settings.SHOW_TOOLTIPS));
        toggleTooltipsItem.setMnemonic(tr("menuBar.Mnemonic.settings.toggleTooltips").charAt(0));
        toggleTooltipsItem.addActionListener(new ToggleTooltipsActionListener(view, view
                .getController()));


        JMenuItem restoreDefaultItem = new JMenuItem(tr("menuBar.settings.restoreDefault"));
        restoreDefaultItem.addActionListener(new RestoreDefaultActionListener(view, view
                .getController()));

        if (settings.getBoolean(Settings.EXPERT_MODE)) {
            colorMenu = new JMenu(tr("menuBar.settings.colorMenu"));
            colorMenu.setMnemonic(tr("menuBar.Mnemonic.settings.colorMenu").charAt(0));
            fillColorMenu();
            settingsMenu.add(colorMenu);
        }


        switch (view.getSessionModel().getViewType()) {
        case VIEW_TYPE_ELEANING_SOLVE:
            settingsMenu.add(liveCheckItem);
            settingsMenu.add(gridLockItem);
            settingsMenu.add(simulationUndoItem);
            settingsMenu.add(toggleLowQualityItem);
            settingsMenu.add(toggleTooltipsItem);
            break;
            
        case VIEW_TYPE_STANDALONE :
            settingsMenu.add(languageMenu);
            settingsMenu.add(lafMenu);
            settingsMenu.addSeparator();
            settingsMenu.add(liveCheckItem);
            settingsMenu.add(gridLockItem);
            settingsMenu.add(simulationUndoItem);
            settingsMenu.add(toggleLowQualityItem);
            settingsMenu.add(toggleTooltipsItem);
            settingsMenu.addSeparator();
            settingsMenu.add(restoreDefaultItem);
            break;
        case VIEW_TYPE_PRESENTER:
            settingsMenu.add(gridLockItem);
            settingsMenu.add(simulationUndoItem);
            settingsMenu.add(toggleLowQualityItem);
            settingsMenu.add(toggleTooltipsItem);
            break;
        }
        
    }

    private void fillColorMenu() {
        for (String color : EDITABLE_COLORS) {
            JMenuItem menuItem = new JMenuItem(tr(color));
            menuItem.setActionCommand(color);
            menuItem.addActionListener(new ChangeColorActionListener(view, view.getController()));
            colorMenu.add(menuItem);
        }
    }

    private void createViewMenu() {
        // create view menu
        JMenu viewMenu = new JMenu(tr("menuBar.view"));
        viewMenu.setMnemonic(tr("menuBar.Mnemonic.view").charAt(0));
        add(viewMenu);

        biggerItem = new JMenuItem(tr("menuBar.view.zoomIn"));
        biggerItem.addActionListener(new ZoomInActionListener(view, view.getController()));
        biggerItem.setAccelerator(getKeyStroke(VK_PLUS, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        biggerItem.setMnemonic(tr("menuBar.Mnemonic.view.zoomIn").charAt(0));

        smallerItem = new JMenuItem(tr("menuBar.view.zoomOut"));
        smallerItem.addActionListener(new ZoomOutActionListener(view, view.getController()));
        smallerItem.setAccelerator(getKeyStroke(VK_MINUS, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        smallerItem.setMnemonic(tr("menuBar.Mnemonic.view.zoomOut").charAt(0));

        hundredProZoomItem = new JMenuItem(tr("menuBar.view.zoom100pro"));
        hundredProZoomItem
                .addActionListener(new Zoom100PercentListener(view, view.getController()));
        hundredProZoomItem.setAccelerator(getKeyStroke(VK_0, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        hundredProZoomItem.setMnemonic(tr("menuBar.Mnemonic.view.zoom100pro").charAt(0));

        modelFitsInView = new JMenuItem(tr("menuBar.view.modelFitsInView"));
        modelFitsInView.addActionListener(new ModelFitsIntoCircuitListener(view, view
                .getController()));
        // modelFitsInView.setAccelerator();
        modelFitsInView.setMnemonic(tr("menuBar.Mnemonic.view.modelFitsInView").charAt(0));

        toggleGridItem = new JCheckBoxMenuItem(tr("menuBar.view.toggleGrid"));
        toggleGridItem.addActionListener(new GridOnOffActionListener(view, view.getController()));
        toggleGridItem.setAccelerator(getKeyStroke(VK_G, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        toggleGridItem.setSelected(view.getSessionModel().getSettings()
                .getBoolean(Settings.SHOW_GRID));
        toggleGridItem.setMnemonic(tr("menuBar.Mnemonic.view.toggleGrid").charAt(0));

        viewMenu.add(biggerItem);
        viewMenu.add(smallerItem);
        viewMenu.add(hundredProZoomItem);
        viewMenu.addSeparator();
        viewMenu.add(modelFitsInView);
        viewMenu.addSeparator();
        viewMenu.add(toggleGridItem);
    }

    private void createHelpMenu() {
        // create help menu
        JMenu helpMenu = new JMenu(tr("menuBar.help"));
        helpMenu.setMnemonic(tr("menuBar.Mnemonic.help").charAt(0));
        add(helpMenu);

        helpItem = new JMenuItem(tr("menuBar.help.funckitHelp"));
        helpItem.setAccelerator(getKeyStroke(VK_F1, 0));
        helpItem.addActionListener(new HelpActionListener(view, view.getController()));
        helpItem.setMnemonic(tr("menuBar.Mnemonic.help.funckitHelp").charAt(0));

        aboutItem = new JMenuItem(tr("menuBar.help.about..."));
        aboutItem.addActionListener(new AboutFunckitActionListener(view, view.getController()));
        aboutItem.setMnemonic(tr("menuBar.Mnemonic.help.about...").charAt(0));

        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
    }

    private void fillLafSubmenu() {
        for (LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
            JMenuItem mi = new JMenuItem(lafi.getName());
            mi.setActionCommand(lafi.getClassName());
            mi.addActionListener(new LafChangeActionListener(view, view.getController()));
            lafMenu.add(mi);
        }

    }

    private void initObserver() {
        SessionModel sm = view.getSessionModel();

        sm.addObserver(this);
        sm.getSettings().addObserver(this);

        for (Project pro : sm.getProjects()) {
            pro.addObserver(this);
            for (EditPanelModel epm : pro.getOpenedEditPanelModels()) {
                epm.addObserver(this);
            }
        }
    }

    /**
     * Grey out item and related.
     */
    private void updateItemState() {
        SessionModel sm = view.getSessionModel();
        Project currentProject = sm.getCurrentProject();
        EditPanelModel editPanelModel = currentProject == null ? null : currentProject
                .getSelectedEditPanelModel();

        saveItem.setEnabled(currentProject != null);
        saveAsItem.setEnabled(currentProject != null);

        /* first handle simulation */
        if (currentProject != null && currentProject.getSimulation() != null) {
            newProjectItem.setEnabled(true);
            openItem.setEnabled(true);
            saveItem.setEnabled(true);
            saveAsItem.setEnabled(true);
            // importComponentItem.setEnabled(false);
            exportComponentItem.setEnabled(false);
            copyItem.setEnabled(false);
            cutItem.setEnabled(false);
            pasteItem.setEnabled(false);
            undoItem.setEnabled(false);
            redoItem.setEnabled(false);
            selectAllItem.setEnabled(false);
            wizardItem.setEnabled(false);

        } else { // here we handle edit mode
            newProjectItem.setEnabled(true);
            openItem.setEnabled(true);
            saveItem.setEnabled(true);
            saveAsItem.setEnabled(true);
            // importComponentItem.setEnabled(true);
            exportComponentItem.setEnabled(true);
            copyItem.setEnabled(true);
            cutItem.setEnabled(true);
            pasteItem.setEnabled(true);
            wizardItem.setEnabled(true);

            if (currentProject == null) {
                undoItem.setEnabled(false);
                redoItem.setEnabled(false);
                saveItem.setEnabled(false);
                saveAsItem.setEnabled(false);
                wizardItem.setEnabled(false);
            } else {
                undoItem.setEnabled(currentProject.getGraphCommandDispatcher().canStepBack());
                redoItem.setEnabled(currentProject.getGraphCommandDispatcher().canStepForward());
                saveItem.setEnabled(true);
                saveAsItem.setEnabled(true);
            }

            if (editPanelModel == null) {
                copyItem.setEnabled(false);
                cutItem.setEnabled(false);
                pasteItem.setEnabled(false);
                deleteElementsItem.setEnabled(false);
                biggerItem.setEnabled(false);
                smallerItem.setEnabled(false);
                hundredProZoomItem.setEnabled(false);
                modelFitsInView.setEnabled(false);
                selectAllItem.setEnabled(false);
                wizardItem.setEnabled(false);
            } else {
                copyItem.setEnabled(!editPanelModel.getSelectedElements().isEmpty());
                cutItem.setEnabled(!editPanelModel.getSelectedElements().isEmpty());
                pasteItem.setEnabled(!sm.getCopyBuffer().getElements().isEmpty());
                deleteElementsItem.setEnabled(!editPanelModel.getSelectedElements().isEmpty());
                biggerItem.setEnabled(true);
                smallerItem.setEnabled(true);
                hundredProZoomItem.setEnabled(true);
                modelFitsInView.setEnabled(true);
                selectAllItem.setEnabled(true);
            }
        }

    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.hasProjectAdded()) {
            i.getChangedProject().addObserver(this);
            for (EditPanelModel epm : i.getChangedProject().getOpenedEditPanelModels()) {
                epm.addObserver(this);
            }
        }

        if (i.hasProjectRemoved()) {
            i.getChangedProject().deleteObserver(this);
            for (EditPanelModel epm : i.getChangedProject().getOpenedEditPanelModels()) {
                epm.deleteObserver(this);
            }

        }

        updateItemState();

    }

    @Override
    public void settingsChanged(Settings source, SettingsInfo i) {
        if (Settings.Language.equals(i.getChangedSetting())) {
            Locale loc = new Locale(source.getString(Settings.Language));

            for (JCheckBoxMenuItem langi : languageItems) {
                langi.setSelected(loc.equals(new Locale(langi.getActionCommand())));
            }
        }

        if (Settings.LOW_RENDERING_QUALITY_MODE.equals(i.getChangedSetting())) {
            toggleLowQualityItem
                    .setSelected(source.getBoolean(Settings.LOW_RENDERING_QUALITY_MODE));
        }

        if (i.getChangedSetting().equals(Settings.SIMULATION_UNDO_ENABLED)) {
            simulationUndoItem.setSelected(source.getBoolean(Settings.SIMULATION_UNDO_ENABLED));
        }

        if (i.getChangedSetting().equals(Settings.GRID_LOCK)) {
            gridLockItem.setSelected(source.getBoolean(Settings.GRID_LOCK));
        }

        if (i.getChangedSetting().equals(Settings.SHOW_GRID)) {
            toggleGridItem.setSelected(source.getBoolean(Settings.SHOW_GRID));
        }

        if (i.getChangedSetting().equals(Settings.REALTIME_VALIDATION)) {
            liveCheckItem.setSelected(view.getSessionModel().getSettings()
                    .getBoolean(Settings.REALTIME_VALIDATION));
        }

        if (i.getChangedSetting().equals(Settings.SHOW_TOOLTIPS)) {
            toggleTooltipsItem.setSelected(view.getSessionModel().getSettings()
                    .getBoolean(Settings.SHOW_TOOLTIPS));
        }

    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (i.hasEditPanelModelAdded()) {
            i.getChangedModel().addObserver(this);
        }

        if (i.hasEditPanelModelRemoved()) {
            i.getChangedModel().deleteObserver(this);
        }

        updateItemState();

    }

    @Override
    public void editPanelModelChanged(EditPanelModel source, EditPanelModelInfo i) {
        updateItemState();

    }
    
}
