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
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatExportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.util.GraphmodelUtil;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.view.View;
import de.sep2011.funckit.view.filechooser.FunckitFileChooser;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Listener object for events that indicate a component export.
 */
public class ExportComponentActionListener extends AbstractAction {

    private static final long serialVersionUID = 6657552222822374603L;
    private static final String I18N_FILE_NOT_FOUND_EXCEPTION = "SaveError.title.FileNotFoundException";
    private static final String I18N_SEPFORMAT_EXPORT_EXCEPTION = "SaveError.title.SEPFormatExportException";
    private final View view;
    private final Controller controller;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public ExportComponentActionListener(View view, Controller controller) {
        this.view = view;
        this.controller = controller;
    }

    /**
     * Action method to export current project as component.
     * 
     * @param event
     *            Additional event information.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Project project = view.getSessionModel().getCurrentProject();
        if (project == null) {
            return;
        }
        Circuit circuit = project.getCircuit();
        if (circuit == null) {
            return;
        }

        // ask for path to save
        FunckitFileChooser fileChooser = new FunckitFileChooser(view);
        File file = fileChooser.saveComponent();
        if (file == null) {
            return;
        }


        SEPFormatConverter converter = new SEPFormatConverter("",
                Mode.FUNCKITFORMAT);
        ComponentType type = GraphmodelUtil.convertToComponentType(circuit,
                project.getName(), true);

        // open file for writing
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {
            view.showErrorMessage(Language.tr(I18N_FILE_NOT_FOUND_EXCEPTION),
                    e1.getLocalizedMessage());
            return;
        }

        try {
            converter.exportComponentType(type, outputStream);
        } catch (SEPFormatExportException e1) {
            view.showErrorMessage(Language.tr(I18N_SEPFORMAT_EXPORT_EXCEPTION),
                    e1.getLocalizedMessage());
            return;
        }

        NewBrickListManager manager = controller.getSessionModel()
                .getNewBrickListManager();
        
        /* reload new brick list if saved in component dir */
        if (file.getAbsolutePath().contains(manager.getExternalTypePath())) {
            manager.loadExternalTypes();
        }
    }

}
