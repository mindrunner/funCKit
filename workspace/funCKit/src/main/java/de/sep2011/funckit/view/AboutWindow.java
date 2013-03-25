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

import de.sep2011.funckit.FunCKit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.BorderLayout;
import java.awt.Font;

import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * The about window displays various information about the Application.
 */
public class AboutWindow extends JDialog {

    private static final long serialVersionUID = 4621622571044809282L;

    /**
     * Creates a new {@link AboutWindow}.
     * @param frame see {@link JDialog#JDialog(java.awt.Frame)}
     */
    public AboutWindow(JFrame frame) {
        super(frame);

        this.setTitle(tr("AboutWindow.about"));
        this.setLayout(new BorderLayout(500, 80));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(400, 400);
        Font font = new Font("Helvetica", Font.PLAIN, 14);

        JTextPane textPane = new JTextPane();
        textPane.setFont(font);
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        textPane.setCharacterAttributes(attributeSet, true);
        textPane.setEditable(false);

        StringBuilder about = new StringBuilder();
        about.append("FunCKit ").append(getVersion()).append("\n\n");
        about.append("Peter Dahlberg\n");
        about.append("Lukas Elsner\n");
        about.append("Thomas Poxrucker\n");
        about.append("Julian Stier\n");
        about.append("Alexander Treml\n");
        about.append("Sebastian Vetter");

        textPane.setText(about.toString());
        this.add(textPane);
    }

    private static String getVersion() {
        String ver = FunCKit.class.getPackage().getImplementationVersion();
        return ver == null ? "development version" : ver;
    }

}
