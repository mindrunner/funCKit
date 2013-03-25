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
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.google.common.base.Preconditions;

import net.miginfocom.swing.MigLayout;
import de.sep2011.funckit.controller.listener.project.ChangeProjectNameActionListener;
import de.sep2011.funckit.model.sessionmodel.Project;
import de.sep2011.funckit.util.FunckitGuiUtil;
import de.sep2011.funckit.util.internationalization.Language;

/**
 * Will be opened if the user wants to change the project name.
 */
public class ChangeProjectNameDialog extends JDialog {

    private static final long serialVersionUID = 7846820724235023071L;
    private JTextField projectNameField;
    private final View view;
    private final Project project;

    /**
     * Create a new {@link ChangeProjectNameDialog}.
     * 
     * @param view
     *            the associated view object, not null
     * @param project
     *            the Project, not null
     */
    public ChangeProjectNameDialog(View view, Project project) {
        super(view.getMainFrame(), Language.tr("changeProjectNameProject.title"), true);

        Preconditions.checkArgument(project != null);

        this.view = view;
        this.project = project;
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout());

        projectNameField = new JTextField(25);
        projectNameField.setText(project.getName());
        JLabel projectNameLabel = new JLabel(tr("newProject.projectName"));

        JButton okButton = new JButton(tr("changeProjectNameProject.okButton"));
        okButton.addActionListener(new OkActionListener());
        okButton.setMnemonic(tr("newProject.Mnemonic.okButton").charAt(0));

        JButton cancelButton = new JButton(Language.tr("newProject.cancelButton"));
        cancelButton.addActionListener(new CancelActionListener());
        cancelButton.setMnemonic(tr("newProject.Mnemonic.cancelButton").charAt(0));

        FunckitGuiUtil.addButtonClickKeystroke(okButton,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        FunckitGuiUtil.addButtonClickKeystroke(cancelButton,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

        Container c = getContentPane();
        c.setLayout(new MigLayout());
        c.add(projectNameLabel);
        c.add(projectNameField, "growx, wrap");
        c.add(okButton);
        c.add(cancelButton, "gapbefore push");

        pack();
    }

    private class OkActionListener extends AbstractAction {

        private static final long serialVersionUID = -7118584146140406480L;

        @Override
        public void actionPerformed(ActionEvent e) {
            new ChangeProjectNameActionListener(view, view.getController(),
                    projectNameField.getText(), project).actionPerformed(null);
            ChangeProjectNameDialog.this.setVisible(false);
            ChangeProjectNameDialog.this.dispose();
        }

    }

    private class CancelActionListener extends AbstractAction {

        private static final long serialVersionUID = -9071753147037912177L;

        @Override
        public void actionPerformed(ActionEvent e) {
            ChangeProjectNameDialog.this.setVisible(false);
            ChangeProjectNameDialog.this.dispose();
        }

    }
}
