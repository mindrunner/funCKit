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

import static de.sep2011.funckit.util.internationalization.Language.tr;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import de.sep2011.funckit.controller.listener.PropertyDialogSaveActionListener;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.ElementDispatcher;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.observer.GraphModelInfo;
import de.sep2011.funckit.observer.GraphModelObserver;
import de.sep2011.funckit.util.FunckitGuiUtil;
import de.sep2011.funckit.util.SpinnerWheelListener;

/**
 * Dialog to edit the Properties of an {@link Element}, like name and size.
 */
public class ElementPropertyDialog extends JDialog implements
        GraphModelObserver {

    /**
     * 
     */
    private static final long serialVersionUID = 8200193808711746487L;
    private JLabel nameLabel;
    private JTextField nameTextField;
    // private JLabel orientationLabel;
    // private JComboBox orientationCombo;
    private JLabel delayLabel;
    private JSpinner delaySpinner;
    private Element element;
    private Circuit circuit;
    private JLabel widthLabel;
    private JSpinner widthSpinner;
    private JLabel heightLabel;
    private JSpinner heightSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private View view;
    private ActionListener saveButtonAl;
    private Set<Object> doAddSet;
    private JLabel switchOnOffLabel;
    private JCheckBox switchOnOffCheckBox;

    /**
     * See {@link JDialog}.
     */
    public ElementPropertyDialog() {
        super();
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}.
     * @param modal
     *            See {@link JDialog}.
     */
    public ElementPropertyDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     * @param modal
     *            See {@link JDialog}
     * @param gc
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Dialog owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     * @param modal
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Dialog owner) {
        super(owner);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param modal
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     * @param modal
     *            See {@link JDialog}
     * @param gc
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Frame owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     * @param modal
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Frame owner) {
        super(owner);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param modalityType
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     * @param modalityType
     *            See {@link JDialog}
     * @param gc
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Window owner, String title,
            ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     * @param modalityType
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Window owner, String title,
            ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     * @param title
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Window owner, String title) {
        super(owner, title);
        init();
    }

    /**
     * See {@link JDialog}.
     * 
     * @param owner
     *            See {@link JDialog}
     */
    public ElementPropertyDialog(Window owner) {
        super(owner);
        init();
    }

    private void init() {
        setLayout(new MigLayout());
        doAddSet = new HashSet<Object>();

        setTitle(tr("ElementPropertyDialog.Title"));

        nameLabel = new JLabel(tr("ElementPropertyDialog.BrickNameLabel"));
        nameTextField = new JTextField(20);

        delayLabel = new JLabel(tr("ElementPropertyDialog.DelayLabel"));
        delaySpinner = new JSpinner(new SpinnerNumberModel(0, 0,
                Integer.MAX_VALUE, 1));
        delaySpinner.addMouseWheelListener(new SpinnerWheelListener(
                delaySpinner));

        widthLabel = new JLabel(tr("ElementPropertyDialog.BrickWithLabel"));
        widthSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
                Integer.MAX_VALUE, 1));
        widthSpinner.addMouseWheelListener(new SpinnerWheelListener(
                widthSpinner));

        heightLabel = new JLabel(tr("ElementPropertyDialog.BrickHeightLabel"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
                Integer.MAX_VALUE, 1));
        heightSpinner.addMouseWheelListener(new SpinnerWheelListener(
                heightSpinner));

        switchOnOffLabel = new JLabel(
                tr("ElementPropertyDialog.switchOnOffLabel"));
        switchOnOffCheckBox = new JCheckBox();

        saveButton = new JButton(tr("ElementPropertyDialog.SaveButton"));
        cancelButton = new JButton(tr("ElementPropertyDialog.CancelButton"));

        saveButton.addActionListener(new CloseListener());
        saveButton.setMnemonic(tr("ElementPropertyDialog.Mnemonic.SaveButton")
                .charAt(0));
        cancelButton.addActionListener(new CloseListener());
        cancelButton.setMnemonic(tr(
                "ElementPropertyDialog.Mnemonic.CancelButton").charAt(0));

        FunckitGuiUtil.addButtonClickKeystroke(saveButton,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        FunckitGuiUtil.addButtonClickKeystroke(cancelButton,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

        setResizable(false);

    }

    private void condAddBrickNameRow() {
        condAdd(nameLabel);
        condAdd(nameTextField, "wrap, growx");
    }

    private void condAddDelayRow() {
        condAdd(delayLabel);
        condAdd(delaySpinner, "growx, wrap");
    }

    private void condAddSwitchOnOffRow() {
        condAdd(switchOnOffLabel);
        condAdd(switchOnOffCheckBox, "growx, wrap");
    }

    private void condAddWidthHeightRow() {
        condAdd(widthLabel);
        condAdd(widthSpinner, "growx, wrap");
        condAdd(heightLabel);
        condAdd(heightSpinner, "growx, wrap");
    }

    /**
     * Sets the associated view. Must be called first befor {@link #setElement(Element, Circuit)}.
     * 
     * @param v the associated view.
     */
    public void setView(View v) {
        this.view = v;

    }

    private void condAdd(java.awt.Component comp, Object constraints) {
        if (doAddSet.contains(comp)) {
            add(comp, constraints);
        }
    }

    private void condAdd(java.awt.Component comp) {
        if (doAddSet.contains(comp)) {
            add(comp);
        }
    }

    /**
     * Sets the Element to edit and Creates the dialog according to this.
     * 
     * @param b
     *            the Element to Edit, not null
     * @param c
     *            the circuit the Element is in
     */
    public void setElement(Element b, Circuit c) {
        assert view != null;

        if (element != null) {
            circuit.deleteObserver(this);
            element = null;
            circuit = null;
            removeAll();
            saveButton.removeActionListener(saveButtonAl);
            saveButtonAl = null;

        }

        if (b != null && c != null) {
            doAddSet.clear();
            this.element = b;
            this.circuit = c;

            new ElementDispatcher() {

                {
                    element.dispatch(this);
                }

                private void addDelaySpinner(Brick b) {
                    doAddSet.add(delayLabel);
                    doAddSet.add(delaySpinner);
                    delaySpinner.setValue(b.getDelay());
                }

                private void addWithHeight(Brick b) {
                    doAddSet.add(widthLabel);
                    doAddSet.add(widthSpinner);
                    doAddSet.add(heightSpinner);
                    doAddSet.add(heightLabel);
                    widthSpinner.setValue(b.getDimension().width);
                    heightSpinner.setValue(b.getDimension().height);
                }

                private void addName(Element element) {
                    doAddSet.add(nameTextField);
                    doAddSet.add(nameLabel);
                    nameTextField.setText(element.getName());
                }

                @Override
                public void visit(IdPoint idPoint) {
                    addName(idPoint);
                    addDelaySpinner(idPoint);
                    widthSpinner.setValue(idPoint.getDimension().width);
                    heightSpinner.setValue(idPoint.getDimension().height);
                }

                @Override
                public void visit(Not not) {
                    addName(not);
                    addDelaySpinner(not);
                    addWithHeight(not);
                }

                @Override
                public void visit(Or or) {
                    addName(or);
                    addDelaySpinner(or);
                    addWithHeight(or);
                }

                @Override
                public void visit(And and) {
                    addName(and);
                    addDelaySpinner(and);
                    addWithHeight(and);
                }

                @Override
                public void visit(Light light) {
                    addName(light);
                    addWithHeight(light);

                }

                @Override
                public void visit(Switch s) {
                    addName(s);
                    addWithHeight(s);
                    doAddSet.add(switchOnOffLabel);
                    doAddSet.add(switchOnOffCheckBox);
                    switchOnOffCheckBox.setSelected(s.getValue());

                }

                @Override
                public void visit(Component component) {
                    addName(component);
                    addWithHeight(component);
                    addDelaySpinner(component);
                }

                @Override
                public void visit(Wire wire) {
                    addName(wire);

                }

                @Override
                public void visit(Element element) {

                }
            };

            condAddBrickNameRow();
            condAddDelayRow();
            condAddWidthHeightRow();
            condAddSwitchOnOffRow();

            add(saveButton);
            add(cancelButton, "gapbefore push");
            saveButtonAl = new PropertyDialogSaveActionListener(view,
                    view.getController(), element, circuit, this);
            saveButton.addActionListener(saveButtonAl);

            pack();

        }
    }

    @Override
    public void dispose() {
        if (circuit != null) {
            circuit.deleteObserver(this);
        }
        super.dispose();
    }

    @Override
    public void graphModelChanged(Circuit source, GraphModelInfo i) {
    }

    /**
     * Returns true if the user seleted to activate the switch.
     * 
     * @return true if the user seleted to activate the switch
     */
    public boolean getSwitchOn() {
        return switchOnOffCheckBox.isSelected();
    }

    /**
     * Returns the delay the user has set.
     * 
     * @return returns the delay the user has set.
     */
    public int getDelay() {
        return ((SpinnerNumberModel) delaySpinner.getModel()).getNumber()
                .intValue();
    }

    /**
     * Returns the Dimension of the element the user has set.
     * 
     * @return the Dimension of the element the user has set
     */
    public Dimension getDimension() {
        int width =
                ((SpinnerNumberModel) widthSpinner.getModel()).getNumber()
                        .intValue();
        int height =
                ((SpinnerNumberModel) heightSpinner.getModel()).getNumber()
                        .intValue();

        return new Dimension(width, height);

    }

    @Override
    public String getName() {
        return nameTextField.getText();
    }

    private class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();

        }
    }
}
