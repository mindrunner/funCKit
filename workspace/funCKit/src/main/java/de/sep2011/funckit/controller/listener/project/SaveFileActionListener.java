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
import de.sep2011.funckit.converter.ExportException;
import de.sep2011.funckit.converter.GIFExporter;
import de.sep2011.funckit.converter.JPGExporter;
import de.sep2011.funckit.converter.PNGExporter;
import de.sep2011.funckit.converter.StreamExporter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatExportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.validator.Result;
import de.sep2011.funckit.validator.SEPExportValidatorFactory;
import de.sep2011.funckit.validator.Validator;
import de.sep2011.funckit.view.View;
import de.sep2011.funckit.view.filechooser.FunckitFileChooser;
import de.sep2011.funckit.view.filechooser.FunckitFileChooser.FileFormat;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class SaveFileActionListener extends AbstractAction {

    private static final long serialVersionUID = -4014999458022162688L;
    private final View view;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public SaveFileActionListener(View view, Controller controller) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Project project = view.getSessionModel().getCurrentProject();
        saveProject(project, view);
    }

    public static boolean saveProject(Project project, View view) {
        if (project == null || project.getCircuit() == null) {
            return false;
        }

        File file;
        FunckitFileChooser.FileFormat fileFormat =
                FunckitFileChooser.FileFormat.FCK;

        // project path exists? => save there
        String path = project.getAbsolutePath();
        if (path != null && !path.equals("")) {
            file = new File(path);
        } else { // no project path => ask for path to save
            FunckitFileChooser fileChooser = new FunckitFileChooser(view);
            file = fileChooser.saveFile();
            if (file == null) {
                return false;
            }

            fileFormat = fileChooser.getSelectedFileFormat(file);
        }

        return saveProjectInFile(file, view.getSessionModel()
                .getCurrentProject(), view, fileFormat);
    }

    public static boolean saveProjectInFile(File file, Project project,
            View view, FunckitFileChooser.FileFormat format) {

        if (project == null) {
            return false;
        }

        StreamExporter converter = null;
        String projectName = project.getName();

        switch (format) {
        case FCK:
            converter = new SEPFormatConverter(projectName, Mode.FUNCKITFORMAT);
            break;
        case SEP:
            converter = new SEPFormatConverter(projectName, Mode.SEPFORMAT);
            break;
        case GIF:
            converter = new GIFExporter(view.getSessionModel().getSettings());
            break;
        case JPG:
            converter = new JPGExporter(view.getSessionModel().getSettings());
            break;
        case PDF:
            try {
                Class<?> pdfExport = Class.forName("de.sep2011.funckit.converter.PDFExporter");
                Constructor<?> pdfConstructor = pdfExport.getConstructor(Settings.class);
                converter = (StreamExporter) pdfConstructor.newInstance(view.getSessionModel()
                        .getSettings());
            } catch (ClassNotFoundException e) {
                Log.gl().debug(e);
                Log.gl().info("Exporter Not Found");
            } catch (InstantiationException e) {
                Log.gl().debug(e);
            } catch (IllegalAccessException e) {
                Log.gl().debug(e);
            } catch (IllegalArgumentException e) {
                Log.gl().debug(e);
            } catch (InvocationTargetException e) {
                Log.gl().debug(e);
            } catch (NoSuchMethodException e) {
                Log.gl().debug(e);
            } catch (SecurityException e) {
                Log.gl().debug(e);
            }
            break;
        case PNG:
            converter = new PNGExporter(view.getSessionModel().getSettings());
            break;
        case SVG:
            try {
                Class<?> svgExport = Class.forName("de.sep2011.funckit.converter.SVGExporter");
                Constructor<?> svgConstructor = svgExport.getConstructor(Settings.class);
                converter = (StreamExporter) svgConstructor.newInstance(view.getSessionModel()
                        .getSettings());
            } catch (ClassNotFoundException e) {
                Log.gl().debug(e);
                Log.gl().info("Exporter Not Found");
            } catch (InstantiationException e) {
                Log.gl().debug(e);
            } catch (IllegalAccessException e) {
                Log.gl().debug(e);
            } catch (IllegalArgumentException e) {
                Log.gl().debug(e);
            } catch (InvocationTargetException e) {
                Log.gl().debug(e);
            } catch (NoSuchMethodException e) {
                Log.gl().debug(e);
            } catch (SecurityException e) {
                Log.gl().debug(e);
            }
            break;
        }

        if (converter == null) {
            throw new IllegalStateException("converter should not be null here");
        }

        Circuit circuit = project.getCircuit();
        if (circuit == null) {
            return false;
        }

        if (format == FileFormat.SEP) {

            // validate before exporting in sep format
            Validator sepFormatValidator =
                    new SEPExportValidatorFactory().getValidator();
            List<Result> results = sepFormatValidator.validate(circuit);
            if (!sepFormatValidator.allPassed()) {
                project.setCheckResults(results);
                StringBuilder messageBuilder = new StringBuilder();
                for (Result result : results) {
                    if (!result.isPassed()) {
                        messageBuilder.append(" ");
                        messageBuilder.append(result.getMessage());
                    }
                }
                view.showErrorMessage(Language
                        .tr("export.sepformat.validationFailed.title"),
                        Language.tr(
                                "export.sepformat.validationFailed.message",
                                messageBuilder.toString()));
                return false;
            }
        }

        // open file for writing
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {
            view.showErrorMessage(
                    Language.tr("SaveError.title.FileNotFoundException"),
                    e1.getLocalizedMessage());
            return false;
        }

        // actual export
        try {
            converter.doExport(circuit, outputStream);
        } catch (SEPFormatExportException e1) {
            view.showErrorMessage(
                    Language.tr("SaveError.title.SEPFormatExportException"),
                    e1.getLocalizedMessage());
            return false;
        } catch (ExportException e) {
            view.showErrorMessage(
                    Language.tr("SaveError.title.ExportException"),
                    e.getLocalizedMessage());
            return false;
        }

        if (format == FileFormat.FCK) {
            project.setPath(file.getAbsolutePath());
            project.setModified(false);
            view.getSessionModel().persistProjects();
        }
        return true;
    }

}
