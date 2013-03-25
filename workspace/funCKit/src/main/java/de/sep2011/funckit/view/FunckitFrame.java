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

import static de.sep2011.funckit.util.FunckitGuiUtil.iconFromResource;
import static de.sep2011.funckit.util.internationalization.Language.tr;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;
import de.sep2011.funckit.controller.listener.ExitApplicationActionListener;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.observer.ProjectInfo;
import de.sep2011.funckit.observer.ProjectObserver;
import de.sep2011.funckit.observer.SessionModelInfo;
import de.sep2011.funckit.observer.SessionModelObserver;

public class FunckitFrame extends JFrame implements SessionModelObserver,
        ProjectObserver {

    private static final long serialVersionUID = 4614359088166852606L;

    private View view;

    private String windowTitleBase;

    private String windowTitlePath = "";
    
    /**
     * The default width of the window (set at the beginning).
     */
    private static final int DEFAULT_WINDOW_WIDTH = 1024;

    /**
     * The default height of the window (set at the beginning).
     */
    private static final int DEFAULT_WINDOW_HEIGHT = 768;

    /**
     * Create a new {@link FunckitRootPane}.
     * 
     * @param windowTitle
     *            title of the Frame
     * @param view
     *            associated {@link View} instance
     * @param rootPane
     *            The root pane
     */
    public FunckitFrame(String windowTitle, View view, FunckitRootPane rootPane) {
        super();
        initialize(getDefaultRect(), view, windowTitle, rootPane);
    }

    /**
     * Create a new {@link FunckitRootPane}.
     * 
     * @param windowTitle
     *            title of the Frame
     * @param boundingRect
     *            see {@link #setBounds(Rectangle)}
     * @param view
     *            associated {@link View} instance
     * @param rootPane
     *            The root pane
     */
    public FunckitFrame(String windowTitle, Rectangle boundingRect, View view, FunckitRootPane rootPane) {
        super();
        initialize(boundingRect, view, windowTitle, rootPane);
    }

    /**
     * Initializes object with the given parameters.
     *
     * @param boundingRect
     *            The rectangle of the frame
     * @param view
     *            The view object
     * @param windowTitle
     */
    private void initialize(Rectangle boundingRect, View view, String windowTitle, FunckitRootPane rootPane) {
        this.view = view;
        setRootPane(rootPane);
        
        setBounds(boundingRect);
        
        this.windowTitleBase = windowTitle;
        
        /* Setting program icons */
        List<Image> appIcons = new ArrayList<Image>(3);
        appIcons.add(iconFromResource("/logo/funckit_20_20.png").getImage());
        appIcons.add(iconFromResource("/logo/funckit_32_32.png").getImage());
        appIcons.add(iconFromResource("/logo/funckit_64_64.png").getImage());
        setIconImages(appIcons);
        
        view.getSessionModel().addObserver(this);
        
        // create osx handler only when not on applet
		if (Application.OS == OperatingSystem.OSX && view.getApplet() != null) {
			new MRJHandler(view);
		}

        updateWindowTitle();
        initWindowClose();
    }

    private void updateWindowTitle() {
        Project pro = view.getSessionModel().getCurrentProject();
        if (pro != null && pro.hasSimulation()) {
            setTitle(windowTitleBase + " - " + tr("FunckitFrame.simulationModeWindowTitle"));
        } else {
            if (windowTitlePath == null || windowTitlePath.equals("")) {
                setTitle(windowTitleBase);
            } else {
                setTitle(windowTitleBase + " - " + windowTitlePath);
            }
        }

    }
    
    private void setWindowTitlePath(String path) {
        windowTitlePath = path;
    }
    
    private void initWindowClose() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
                new ExitApplicationActionListener(view, view.getController())
                        .actionPerformed(new ActionEvent(this, winEvt.getID(),
                                "addQuitActionListener"));
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    
    private static Rectangle getDefaultRect() {
        Rectangle result = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        if (frameSize.height > screenSize.height) {
            result.height = screenSize.height;
        } else
            result.height = frameSize.height;
        if (frameSize.width > screenSize.width) {
            result.width = screenSize.width;
        } else
            result.width = frameSize.width;
        result.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        return result;
    }

    @Override
    public void sessionModelChanged(SessionModel source, SessionModelInfo i) {

        if (i.hasProjectAdded()) {
            Project p = i.getChangedProject();
            p.addObserver(this);
            updateWindowTitle();
        }

        if (i.hasProjectRemoved()) {
            Project p = i.getChangedProject();
            p.deleteObserver(this);
            updateWindowTitle();
        }

        if (i.hasCurrentProjectChanged()) {
            Project p = source.getCurrentProject();

            setWindowTitlePath(p.getAbsolutePath());
            updateWindowTitle();
        }
    }

    @Override
    public void projectChanged(Project source, ProjectInfo i) {
        if (!(source == view.getSessionModel().getCurrentProject())) {
            return;
        }

        if (i.isSimulationChanged() || i.isActiveEditPanelModelChanged()) {
            updateWindowTitle();

        } else if (i.isPathChanged()) {
            setWindowTitlePath(source.getAbsolutePath());
            updateWindowTitle();
        }

    }
   
}
