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

package de.sep2011.funckit.controller.listener.edit;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.PasteTool;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.commands.MoveBunchOfElementsCommand;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.view.GenerateDialog;
import de.sep2011.funckit.view.View;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static de.sep2011.funckit.util.internationalization.Language.tr;

public class GenerateActionListener implements ActionListener {
    private static final int MAXIMUM_NUMBER_TO_GENERATE = 500;

    private final View view;
    private final Controller controller;
    private final GenerateDialog generateDialog;

    public GenerateActionListener(View view, GenerateDialog generateDialog,
            Controller controller) {
        this.view = view;
        this.controller = controller;
        this.generateDialog = generateDialog;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        view.setStatusText(tr("generate.generating"));
        JTextField brickSpaceField = generateDialog.getBrickSpaceField();
        JTextField brickNumberField = generateDialog.getBrickNumberField();

        /* Accept space number for duplicate gaps from user input. */
        int spaceBetweenDuplicates = 0;
        try {
            spaceBetweenDuplicates = Integer.valueOf(brickSpaceField.getText());
        } catch (NumberFormatException e) {
        }
        spaceBetweenDuplicates =
                spaceBetweenDuplicates < 0 ? 0 : spaceBetweenDuplicates;

        /* Accept number of duplicates from user input. */
        int numberOfDuplicates = 0;
        try {
            numberOfDuplicates = Integer.valueOf(brickNumberField.getText());
        } catch (NumberFormatException e) {
        }
        numberOfDuplicates = numberOfDuplicates < 0 ? 0 : numberOfDuplicates;
        if (numberOfDuplicates > MAXIMUM_NUMBER_TO_GENERATE) {
            view.setStatusText(tr("generate.maximumNumberToGenerateExceeded",
                    MAXIMUM_NUMBER_TO_GENERATE));
            return;
        }

        Circuit generated = new CircuitImpl();
        JComboBox brickTypeField = generateDialog.getBrickTypeField();
        if (brickTypeField == null) {
            /*
             * Try to generate circuit via selected bricks, as there is no brick
             * type field given.
             */
            EditPanelModel selectedEditPanelModel =
                    view.getSessionModel().getSelectedEditPanelModel();

            if (selectedEditPanelModel != null) {
                Circuit template = new CircuitImpl();
                template.addAll(selectedEditPanelModel.getSelectedElements());
                int templateHeight = template.getBoundingRectangle().height;
                for (int i = 0; i < numberOfDuplicates; i++) {
                    Circuit copy = template.getCopy();
                    Command moveCommand =
                            new MoveBunchOfElementsCommand(
                                    copy,
                                    copy.getElements(),
                                    0,
                                    i
                                            * (templateHeight + spaceBetweenDuplicates));
                    moveCommand.execute();
                    generated.injectCircuit(copy);
                }
            } else {
                view.setStatusText(tr("generate.noEditPanelSelected"));
            }
        } else {
            /* Generate circuit via one brick, that gets duplicated. */
            final NewBrickListManager newBrickListManager =
                    view.getSessionModel().getNewBrickListManager();
            final List<Brick> brickList = newBrickListManager.getNewBrickList();
            Brick selectedBrick =
                    brickList.get(brickTypeField.getSelectedIndex());

            if (selectedBrick != null) {
                Brick template = selectedBrick;
                for (int i = 0; i < numberOfDuplicates; i++) {
                    Brick brick =
                            template.getNewInstance(new Point(
                                    0,
                                    i
                                            * (template.getBoundingRect().height + spaceBetweenDuplicates)));
                    brick.setName(template.getName() + i);
                    generated.addBrick(brick);
                }
            } else {
                view.setStatusText(tr("generate.noBrickSelected"));
            }
        }

        view.getSessionModel().setCopyBuffer(generated);

        PasteTool pasteTool = new PasteTool(controller);
        view.getSessionModel().saveTool();
        view.getSessionModel().setTool(pasteTool);
        view.setStatusText(tr("generate.done"));

        view.setStatusText(tr("generate.errorGeneratingCircuit"));

        generateDialog.setVisible(false);
        generateDialog.dispose();
    }
}
