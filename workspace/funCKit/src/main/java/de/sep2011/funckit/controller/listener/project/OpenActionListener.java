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

package de.sep2011.funckit.controller.listener.project;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.ImportWorker;
import de.sep2011.funckit.controller.ImportWorker.WorkerMode;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.util.GraphmodelUtil;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.view.View;
import de.sep2011.funckit.view.filechooser.FunckitFileChooser;
import de.sep2011.funckit.view.filechooser.FunckitFileChooser.FileFormat;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A Listener that is triggered if the user wants to open a File with a Dialog.
 */
public class OpenActionListener extends AbstractAction implements PropertyChangeListener {

    private static final long serialVersionUID = 1941693337608557995L;
    private final View view;
    private final Controller controller;
    private Object result;
    private File[] files;
    private int currentFile;
    private FunckitFileChooser fileChooser;
    private Project project;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public OpenActionListener(View view, Controller controller) {
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        result = null;
        fileChooser = new FunckitFileChooser(view);
        files = fileChooser.openFile();
        if (files == null) {
            return;
        }

        currentFile = 0;
        batchLoad();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getPropertyName();
        if (property.equals("progress")) {
            view.setProgress((Integer) event.getNewValue());
        } else if (property.equals("dataClose")) {
            view.setStatusText(Language.tr("status.loadingImport"));
        } else if (property.equals("result")) {
            result = event.getNewValue();
        } else if (property.equals("converted")) {
            SEPFormatConverter converter = (SEPFormatConverter) event
                    .getNewValue();
            Circuit circuit;
            String name = converter.getProjectName();
            if (result instanceof ComponentType) {
                name = ((ComponentType) result).getName();
            }
            if (name == null || name.equals("")) {
                name = files[currentFile - 1].getName();
            }
            if (result instanceof Circuit) {
                circuit = (Circuit) result;
            } else {
                circuit = GraphmodelUtil.revertToCircuit(
                        (ComponentType) result, false);
            }
            if (project == null) {
                project = new Project(circuit, name, view.getSessionModel()
                        .getSettings());
                if (fileChooser.getSelectedFileFormat(files[currentFile - 1]) == FileFormat.FCK) {
                    project.setPath(files[currentFile - 1].getAbsolutePath());
                } else {
                    project.setModified(true);
                }
                controller.openProject(project, true);
            } else { // project existed, just load the circuit
                project.setCircuit(circuit);
                controller.openProject(project, false);
                // project.setName(converter.getProjectName());
            }
            Log.gl().debug("Circuit with " + project.getCircuit().getElements().size() + "Elements loaded");

            batchLoad();
        } else if (property.equals("canceled")
                || property.equals("interrupted")) {
            // do nothing, abort batch import
        } else if (property.equals("error")) {

            // continue opening in batch
            batchLoad();
        }
    }

    private void batchLoad() {
        boolean loaded = false;
        while (!loaded && currentFile < files.length) {
            currentFile++;
            loaded = loadFile(files[currentFile - 1]);
        }
    }

    private boolean loadFile(File file) {
        // skip already loaded projects
        this.project = null;
        for (Project project : controller.getSessionModel().getProjects()) {
            String path = project.getAbsolutePath();
            if (!(path == null || path.equals(""))
                    && file.compareTo(new File(path)) == 0) {
                if (project.getCircuit() == null) {
                    this.project = project; // load project's circuit
                } else { // already loaded project => just select it
                    controller.getSessionModel().setCurrentProject(project);
                    return false;
                }
            }
        }

        if (file.canRead()) {
            view.setProgressCanceled(false);
            view.setProgressMax(100);

            view.setProgress(0);

            // import in another thread
            ImportWorker importWorker;
            FileFormat format = fileChooser.getSelectedFileFormat(file);
            WorkerMode mode = format == FileFormat.CMP ? WorkerMode.COMPONENTTYPE
                    : WorkerMode.CIRCUIT;

            try {
                importWorker = new ImportWorker(view,
                        new FileInputStream(file), mode);
            } catch (FileNotFoundException e1) {
                view.showErrorMessage(
                        Language.tr("loadError.title.fileNotFoundException"),
                        e1.getLocalizedMessage());
                return false;
            }
            importWorker.addPropertyChangeListener(this);
            view.setStatusText(Language.tr("status.loadingData"));
            view.prepareProgress(file.getName());
            importWorker.execute();

            // blocks while importing through only showing the modal progressbar
            // dialog
            view.showProgress();
        } else {
            view.showErrorMessage(Language.tr("loadError.title.cannotRead"),
                    "LoadError.message.CannotRead");
            return false;
        }
        return true;
    }
}
