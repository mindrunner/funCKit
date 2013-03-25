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

package de.sep2011.funckit.controller.listener;

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Gate;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ChangeElementSizeCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.ElementSetNameCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.SetBrickDelayCommand;
import de.sep2011.funckit.model.graphmodel.implementations.commands.SetSwitchValueCommand;
import de.sep2011.funckit.util.command.Command;
import de.sep2011.funckit.util.command.CommandDispatcher;
import de.sep2011.funckit.util.command.SimpleCommandCombiner;
import de.sep2011.funckit.view.ElementPropertyDialog;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Listener that reacts save on property dialog.
 * 
 * @since implementation
 */
public class PropertyDialogSaveActionListener extends AbstractAction {

    private static final long serialVersionUID = 280332843293642029L;

    /**
     * Current mediating controller object.
     */
    private final Controller controller;

    private final Element element;

    private final Circuit circuit;

    private final ElementPropertyDialog dialog;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param elem
     * @param circuit
     * @param dialog
     * @param view
     *            associated View object, should not be null
     */
    public PropertyDialogSaveActionListener(View view, Controller controller,
            Element elem, Circuit circuit, ElementPropertyDialog dialog) {
        this.controller = controller;
        this.element = elem;
        this.circuit = circuit;
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        CommandDispatcher dispatcher = controller.getSessionModel()
                .getCurrentGraphCommandDispatcher();

        final List<Command> commands = new LinkedList<Command>();

        new ElementDispatcher() {

            {
                element.dispatch(this);
            }

            @Override
            public void visit(IdPoint idPoint) {
                visit((Gate) idPoint);
            }

            private void visit(Gate g) {
                commands.add(new SetBrickDelayCommand(g, dialog.getDelay(),
                        circuit));
                visit((Brick) g);
            }

            private void visit(Brick b) {
                commands.add(new ChangeElementSizeCommand(b, dialog
                        .getDimension(), circuit));
                visit((Element) b);
            }

            @Override
            public void visit(Not not) {
                visit((Gate) not);
            }

            @Override
            public void visit(Or or) {
                visit((Gate) or);
            }

            @Override
            public void visit(And and) {
                visit((Gate) and);
            }

            @Override
            public void visit(Light light) {
                visit((Brick) light);

            }

            @Override
            public void visit(Switch s) {
                visit((Brick) s);
                commands.add(new SetSwitchValueCommand(circuit, s, dialog
                        .getSwitchOn()));
            }

            @Override
            public void visit(Component component) {
                commands.add(new SetBrickDelayCommand(component, dialog
                        .getDelay(), circuit));
                visit((Brick) component);

            }

            @Override
            public void visit(Wire wire) {
                visit((Element) wire);

            }

            @Override
            public void visit(Element element) {
                commands.add(new ElementSetNameCommand(element, dialog
                        .getName(), circuit));
            }
        };

        dispatcher.dispatch(new SimpleCommandCombiner(commands));
    }

}
