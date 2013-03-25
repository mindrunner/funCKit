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

import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.controller.listener.edit.GenerateActionListener;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.FunckitGuiUtil;
import de.sep2011.funckit.util.internationalization.Language;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import static de.sep2011.funckit.util.internationalization.Language.tr;

public class GenerateDialog extends JDialog {
    private static final long serialVersionUID = 3760841110690662178L;
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_LOCATION_X = 300;
    private static final int DEFAULT_LOCATION_Y = 300;
    private JTextField brickNumberField;
    private JComboBox brickTypeField;
    private JTextField brickSpaceField;

    public GenerateDialog(View view, Controller controller) {
        super(view.getMainFrame(), Language.tr("generate.title"), true);
        initialize(view, controller, true);
    }

    public GenerateDialog(View view, Controller controller, boolean useBrickType) {
        super(view.getMainFrame(), Language.tr("generate.title"), true);
        initialize(view, controller, useBrickType);
    }

    private void initialize(View view, Controller controller,
            boolean useBrickType) {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocation(DEFAULT_LOCATION_X, DEFAULT_LOCATION_Y);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout());

        /* Initialize fields for brick number. */
        brickNumberField = new JTextField(15);
        brickNumberField.setText("3");
        JLabel brickNumberLabel = new JLabel(tr("generate.numberOfBricks"));

        JButton okButton = new JButton(tr("generate.okButton"));
        okButton.addActionListener(new GenerateActionListener(view, this,
                controller));
        okButton.setMnemonic(tr("generate.Mnemonic.okButton").charAt(0));
        FunckitGuiUtil.addButtonClickKeystroke(okButton,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        JButton cancelButton = new JButton(Language.tr("generate.cancelButton"));
        cancelButton.addActionListener(new CancelActionListener());
        cancelButton
                .setMnemonic(tr("generate.Mnemonic.cancelButton").charAt(0));
        FunckitGuiUtil.addButtonClickKeystroke(cancelButton,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

        /* Initialize field for space between bricks for generating. */
        brickSpaceField = new JTextField(15);
        int gridSize = view.getSessionModel().getSettings()
                .getInt(Settings.GRID_SIZE);
        brickSpaceField.setText(String.valueOf(gridSize));
        JLabel brickSpaceLabel = new JLabel(tr("generate.spaceBetweenBricks"));

        /* Assign fields to content pane. */
        Container c = getContentPane();
        c.setLayout(new MigLayout());

        /**
         * Only display brick type combo box, if we want to select one brick
         * type to generate our circuit. If no combo box is displayed and passed
         * to our action listener, the action listener takes selected elements
         * and duplicates them to add them to circuit.
         */
        if (useBrickType) {
            /* Initialize fields for brick type. */
            JLabel brickTypeLabel = new JLabel(tr("generate.brickType"));
            final NewBrickListManager newBrickListManager = view
                    .getSessionModel().getNewBrickListManager();
            final List<Brick> brickList = newBrickListManager.getNewBrickList();
            String[] brickNames = new String[brickList.size()];
            int i = 0;
            for (Brick brick : brickList) {
                brickNames[i] = brick.getName();
                i++;
            }
            brickTypeField = new JComboBox(brickNames);
            c.add(brickTypeField);
            c.add(brickTypeLabel, "growx, wrap");
        }

        c.add(brickNumberField);
        c.add(brickNumberLabel, "growx, wrap");
        c.add(brickSpaceField);
        c.add(brickSpaceLabel, "growx, wrap");

        c.add(cancelButton);
        c.add(okButton, "gapbefore push");

        pack();
    }

    /**
     * Must return a non-null JTextField, that contains a (probably
     * user-defined) string defining space between duplicates that should be
     * generated.
     * 
     * @return a JTextField
     */
    public JTextField getBrickSpaceField() {
        return brickSpaceField;
    }

    /**
     * Field containing text defining how much duplicates should be generated.
     * May not be null.
     * 
     * @return Field containing text defining how much duplicates should be
     *         generated
     */
    public JTextField getBrickNumberField() {
        return brickNumberField;
    }

    /**
     * JComboBox that contains a selected name of a brick from
     * {@link NewBrickListManager}. Returned object may be null, so circuit
     * should be generated via selected elements from edit panel model instead
     * of using one brick type.
     * 
     * @return JComboBox that contains a selected name of a brick from
     *         {@link NewBrickListManager}
     */
    public JComboBox getBrickTypeField() {
        return brickTypeField;
    }

    private class CancelActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            GenerateDialog.this.setVisible(false);
            GenerateDialog.this.dispose();
        }
    }
}
