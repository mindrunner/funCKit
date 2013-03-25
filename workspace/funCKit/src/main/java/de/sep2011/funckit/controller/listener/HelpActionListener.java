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

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;
import de.sep2011.funckit.controller.Controller;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.view.View;
import javax.swing.AbstractAction;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Action listener to open helping manual or documentation.
 */
public class HelpActionListener extends AbstractAction {

    private static final long serialVersionUID = -6821278684366778247L;

    /**
     * Constructor that expects the current {@link Controller} and {@link View}
     * reference.
     * 
     * @param controller
     *            Application controller object, should not be null
     * @param view
     *            associated View object, should not be null
     */
    public HelpActionListener(View view, Controller controller) {
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        /* run in new thread to not interrupt the GUI */
        Thread helpThread = new Thread() {
            @Override
            public void run() {
                String pdfpath;
                try {
                    pdfpath = getPdfPath("/handbuch.pdf");
                } catch (IOException e) {
                    Log.gl().warn("could not open manual!");
                    return;
                }

                boolean success = false;

                /*
                 * try with xdg-open on other first as it should give better
                 * results
                 */
                if (!success && Application.OS == OperatingSystem.OTHER) {
                    success = tryWithXdgOpen(pdfpath);
                }

                /* try with the Desktop class */
                if (!success) {
                    success = tryWithDesktop(pdfpath);
                }

                if (!success) {
                    Log.gl().warn("could not open manual!");
                }
            }
        };

        helpThread.setDaemon(true); // ends if main app ends
        helpThread.start();
    }

    private static String getPdfPath(String resourcePath) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File temporaryFile = new File(tempDir, "funckit-help.pdf");
        temporaryFile.deleteOnExit();
        final InputStream templateStream =
                resourcePath.getClass().getResourceAsStream(resourcePath);
        Files.copy(new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                return templateStream;
            }
        }, temporaryFile);

        String absolutePath = temporaryFile.getAbsolutePath();

        return absolutePath;

    }

    private static boolean tryWithXdgOpen(String pdfPath) {
        try {
            Process process = Runtime.getRuntime().exec("xdg-open " + pdfPath);
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Log.gl().warn("Warning: Help Thread was interrupted");
            }

            if (process.exitValue() != 0) {
                return false;
            }

        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static boolean tryWithDesktop(String pdfPath) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(pdfPath));
            }
        } catch (IOException ex) {
            return false;
        } catch (IllegalArgumentException ex) {
            return false;
        } catch (UnsupportedOperationException ex) {
            return false;
        }

        return true;
    }
}
