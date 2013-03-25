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

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import de.sep2011.funckit.Application;
import de.sep2011.funckit.FunCKit;
import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.listener.project.SaveFileActionListener;
import de.sep2011.funckit.controller.listener.simulation.SimulationForwardButtonListener;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;
import de.sep2011.funckit.observer.SettingsInfo;
import de.sep2011.funckit.observer.SettingsObserver;
import de.sep2011.funckit.util.FunckitGuiUtil;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.validator.LiveCheckValidatorFactory;
import de.sep2011.funckit.validator.Result;

/**
 * View is an overall mediator system, encapsulating graphical user interface
 * components from an outer glance at the view-part of the application. It
 * serves as session model observer to react on changes in that model part and
 * delegate that change information to subsystems of the GUI.
 */
public class View implements SessionModelObserver, SettingsObserver, ProjectObserver {

    private FunckitRootPane mainRootPane;
    private FunckitFrame mainFrame;
    private JDialog progressDialog;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private boolean progressCanceled;
    private SessionModel session;
    private Controller controller;
    private Timer simulationTimer;
    private String windowTitle;
    private JApplet applet;
    
    /**
     * Initializes view object with given parameters.
     * 
     * @param windowTitle
     * @param session
     * @param controller
     * @param applet The applet to use, can be null for a standalone application
     */
    public View(String windowTitle, SessionModel session, Controller controller, JApplet applet) {
        initialize(windowTitle, session, controller, applet);
    }



    private void initialize(String windowTitle, SessionModel session, Controller controller, JApplet applet) {
        this.windowTitle = windowTitle;
        this.applet = applet;
        assert session != null;
        assert controller != null;

        this.session = session;
        this.controller = controller;
        this.simulationTimer = new Timer(0, new SimulationForwardButtonListener(this, controller));

        mainRootPane = new FunckitRootPane(this);

        if (applet == null) {
            createAndShowMainWindow();
        }

        initProgressBar();
        session.addObserver(this);

        for (Project pro : session.getProjects()) {
            pro.addObserver(this);
            if (pro.hasSimulation() && (pro == session.getCurrentProject())) {
                simulationTimer.setDelay(pro.getTimerDelay());
                if (pro.isSimulationPaused()) {
                    simulationTimer.stop();
                } else {
                    simulationTimer.start();
                }
            }
        }

        getSessionModel().getSettings().addObserver(this);
    }
    
    private void createAndShowMainWindow() {
        Rectangle frameBounds = this.session.getSettings().get(
                Settings.WINDOW_BOUNDS, Rectangle.class);
        if (frameBounds == null) {
            mainFrame = new FunckitFrame(windowTitle, this, mainRootPane);
        } else {
            mainFrame = new FunckitFrame(windowTitle, frameBounds, this, mainRootPane);
        }
        
        mainFrame.setVisible(true);
    }

    private void initProgressBar() {
        progressDialog = new JDialog(mainFrame, Language.tr("view.progressBar"), true);
        progressDialog.setUndecorated(true);
        progressDialog.setResizable(false);
        // progressDialog.setSize(300, 50);
        progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        JButton cancelButton = new JButton(Language.tr("view.progressBar.cancelButton"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                progressCanceled = true;
            }
        });
        progressLabel = new JLabel();
        progressDialog.setLayout(new MigLayout());
        progressDialog.add(progressBar);
        progressDialog.add(cancelButton, "wrap");
        progressDialog.add(progressLabel);
        progressDialog.pack();
        if(mainFrame != null ) {
            mainFrame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
    
                @Override
                public void ancestorResized(HierarchyEvent e) {
                    FunckitGuiUtil.centerDialogInWindow(progressDialog, mainFrame);
                }
    
                @Override
                public void ancestorMoved(HierarchyEvent e) {
                    FunckitGuiUtil.centerDialogInWindow(progressDialog, mainFrame);
                }
            });
    
            /* grey out the Frame */
            mainFrame.setGlassPane(new JPanel() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 550111350341542349L;
    
                {
                    setOpaque(false);
                }
    
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Color ppColor = new Color(88, 88, 88, 100); // r,g,b,alpha
                    g.setColor(ppColor);
                    g.fillRect(0, 0, getSize().width, getSize().height);
                }
    
            });
        }

    }

    public void openNewProject(Circuit circuit) {
        NewProjectDialog newProjectFrame = new NewProjectDialog(this, circuit);
        newProjectFrame.setLocationRelativeTo(mainFrame);
        newProjectFrame.setVisible(true);
    }

    public void setStatusText(String text) {
        mainRootPane.setStatusText(text);
    }

    /**
     * Gets the {@link Timer} used for the
     * {@link de.sep2011.funckit.model.simulationmodel.Simulation}.
     * 
     * @return the {@link Timer} used for the
     *         {@link de.sep2011.funckit.model.simulationmodel.Simulation}.
     */
    public Timer getSimulationTimer() {
        return simulationTimer;
    }

    public FunckitRootPane getMainRootPane() {
        return mainRootPane;
    }
    
    /**
     * @return the main frame, can be null if no main frame
     */
    public FunckitFrame getMainFrame() {
        return mainFrame;
    }

    public void prepareProgress(String label) {
        progressLabel.setText(label);
        progressLabel.revalidate();
        progressLabel.repaint();
        progressDialog.pack();

        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressCanceled = false;
        if (mainFrame != null) {
            mainFrame.getGlassPane().setVisible(true);
            FunckitGuiUtil.centerDialogInWindow(progressDialog, mainFrame);
        }

    }

    public void showProgress() {
        // workaround for displaying the wrong label text because of missing
        // repaint.
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressLabel.repaint();
            }
        });
        progressDialog.setVisible(true);
    }

    public void setProgress(int value) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(value);
    }

    public void hideProgress() {
        mainRootPane.getGlassPane().setVisible(false);
        progressDialog.setVisible(false);
    }

    public void setProgressMax(int max) {
        progressBar.setMaximum(max);
    }

    public boolean isProgressCanceled() {
        return progressCanceled;
    }

    public void setProgressCanceled(boolean canceled) {
        this.progressCanceled = canceled;
    }

    /**
     * returns the current active {@link EditPanel}, null if no
     * {@link EditPanel} is active.
     * 
     * @return the current active {@link EditPanel}, null if no
     *         {@link EditPanel} is active
     */
    public EditPanel getCurrentActiveEditPanel() {
        java.awt.Component ac = mainRootPane.getTabbedPane().getSelectedComponent();
        if (ac instanceof EditPanelScrollPane) {
            return ((EditPanelScrollPane) ac).getEditPanel();
        }
        return null;
    }

    public List<EditPanel> getOpenEditPanels() {
        java.awt.Component comps[] = mainRootPane.getTabbedPane().getComponents();
        List<EditPanel> eps = new ArrayList<EditPanel>(comps.length);

        for (java.awt.Component ac : comps) {
            if (ac instanceof EditPanelScrollPane) {
                eps.add(((EditPanelScrollPane) ac).getEditPanel());
            }
        }

        return eps;
    }

    /**
     * Delegates exiting application event.
     */
    public void exit() {
        if (askForSave()) {
            if (mainFrame != null) {
                mainFrame.setVisible(false);
                mainFrame.dispose();
            }
            
            progressDialog.setVisible(false);
            progressDialog.dispose();
            simulationTimer.stop();
        }
    }

    /**
     * Returns current session model object. May never be null.
     * 
     * @return Current session model.
     */
    public SessionModel getSessionModel() {
        return session;
    }

    /**
     * Current mediating controller object. May never be null.
     * 
     * @return mediating controller object. May never be null.
     */
    public Controller getController() {
        return controller;
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {
        if (i.isPrepareExit()) {
            session.getSettings().set(Settings.WINDOW_BOUNDS, mainRootPane.getBounds());
            exit();
        }
        if (i.hasCurrentProjectChanged()) {
            if (this.getSessionModel().getSettings().getBoolean(Settings.REALTIME_VALIDATION)) {
                LiveCheckValidatorFactory liveFactory = new LiveCheckValidatorFactory();
                List<Result> results = liveFactory.getValidator().validate(
                        i.getChangedProject().getCircuit());

                // NOTE: the notifying circuit has to be the main circuit of the
                // current project.
                this.getSessionModel().getCurrentProject().setCheckResults(results);
            }

            /* update simulation timer */
            if (i.getChangedProject().hasSimulation()
                    && (i.getChangedProject() == session.getCurrentProject())) {
                simulationTimer.setDelay(i.getChangedProject().getTimerDelay());
                if (i.getChangedProject().isSimulationPaused()) {
                    simulationTimer.stop();
                } else {
                    simulationTimer.start();
                }
            } else {
                simulationTimer.stop();
            }

        }
        if (i.hasProjectAdded()) {
            i.getChangedProject().addObserver(this);
        }
        if (i.hasProjectRemoved()) {
            i.getChangedProject().deleteObserver(this);
        }

    }

    private boolean askForSave() {
        boolean askForSave = false;
        for (Project p : controller.getSessionModel().getProjects()) {
            if (p.isModified()) {
                askForSave = true;
                break;
            }
        }

        if (askForSave) {
            int ret = askForSaveUnsavedProjects();
            if (ret == JOptionPane.YES_OPTION) {
                for (Project p : controller.getSessionModel().getProjects()) {
                    if (p.isModified()) {
                        controller.getSessionModel().setCurrentProject(p);
                        if (!SaveFileActionListener.saveProject(p, this)) {
                            return false;
                        }
                    }
                }
            } else if (ret == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }

        return true;
    }

    /**
     * Rebuilds the view by starting a new {@link Application} instance.
     */
    public void rebuildView() {
        if (askForSave()) {
            if (mainFrame != null) {
                mainFrame.setVisible(false);
                mainFrame.dispose();
            }
            
            simulationTimer.stop();

            gl().info("Restating Application");
            FunCKit.start();
        }
    }

    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showWarningMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public int showWarningOptionDialog(String title, String message) {
        return JOptionPane.showOptionDialog(getMainRootPane(), message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
    }

    /**
     * Shows dialog for saving unsaved projects and returns its result depending
     * on users choice.
     * 
     * @return {@link JOptionPane#YES_OPTION} to confirm saving unsaved
     *         projects, {@link JOptionPane#NO_OPTION} to deny.
     */
    public int askForSaveUnsavedProjects() {
        if (mainFrame != null) {
            mainFrame.setExtendedState(Frame.NORMAL);
            mainFrame.toFront();
        }

        return JOptionPane.showOptionDialog(getMainRootPane(),
                Language.tr("view.saveUnsavedProjects.message"),
                Language.tr("view.saveUnsavedProjects.title"), JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, null, null);
    }

    public void showValidatorResults(List<Result> results) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><table>");
        for (Result result : results) {
            if (!result.isPassed()) {
                builder.append("<tr>");
                builder.append("<td>");
                builder.append(result.getMessage());
                builder.append("</td>");
                builder.append("</tr>");
            }
        }
        builder.append("</table></html>");
        showErrorMessage(Language.tr("view.validationFailedDialog.title"), builder.toString());
    }

    @Override
    public void settingsChanged(Settings source, SettingsInfo i) {
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if ((i.isSimulationControlStateModified() || i.isSimulationChanged())
                && source.hasSimulation() && source == session.getCurrentProject()) {
            simulationTimer.setDelay(source.getTimerDelay());
            if (source.isSimulationPaused()) {
                simulationTimer.stop();
            } else {
                simulationTimer.start();
            }
        } else if (i.isSimulationChanged() && !source.hasSimulation()) {
            simulationTimer.stop();
        }

        if (i.isCircuitChanged() && source.getCircuit() == null) {
            simulationTimer.stop();
        }
    }
    
    public JApplet getApplet() {
        return applet;
    }

}
