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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager;
import de.sep2011.funckit.util.internationalization.Language;

public class WizardDialog extends JDialog {
	private static final long serialVersionUID = -7040251289941484631L;
	private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_LOCATION_X = 300;
    private static final int DEFAULT_LOCATION_Y = 300;
    private JComboBox chooseActionField;

    public WizardDialog(View view, Controller controller) {
        super(view.getMainFrame(), Language.tr("generate.title"), true);
        initialize(view, controller, true);
    }

    public WizardDialog(View view, Controller controller, boolean useBrickType) {
        super(view.getMainFrame(), Language.tr("generate.title"), true);
        initialize(view, controller, useBrickType);
    }

    private void initialize(View view, Controller controller, boolean useBrickType) {

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocation(DEFAULT_LOCATION_X, DEFAULT_LOCATION_Y);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout());

        JLabel chooseActionLabel = new JLabel(tr("generate.chooseAction"));
        String[] actions = new String[]{"a", "b"};

        /* Assign fields to content pane. */
        Container c = getContentPane();
        c.setLayout(new MigLayout());
        chooseActionField = new JComboBox(actions);
        c.add(chooseActionField);
        c.add(chooseActionLabel, "growx, wrap");

        //this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put();
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    WizardDialog.this.close();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        pack();
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
    public JComboBox getChooseActionField() {
        return chooseActionField;
    }

    void close() {
        this.setVisible(false);
        this.dispose();
    }

    private class CancelActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            close();
        }
    }
}
