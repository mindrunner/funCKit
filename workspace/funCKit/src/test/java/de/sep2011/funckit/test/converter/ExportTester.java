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

package de.sep2011.funckit.test.converter;

import de.sep2011.funckit.converter.GIFExporter;
import de.sep2011.funckit.converter.JPGExporter;
import de.sep2011.funckit.converter.PDFExporter;
import de.sep2011.funckit.converter.PNGExporter;
import de.sep2011.funckit.converter.SVGExporter;
import de.sep2011.funckit.converter.StreamExporter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.DefaultSettings;
import de.sep2011.funckit.model.sessionmodel.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

import static de.sep2011.funckit.util.Log.gl;

public class ExportTester {

    // Change this to your own needs!
    private static final String PATH = "/lu/temp/export/";

    // private static final String PATH = "/Users/myAccount/glump/";
    // private static final String PATH = "c:\\tmp";
    // private static final String PATH = "/tmp/";

    public static void main(String[] args) {

        gl().info("Exporting images to" + PATH);

        String fileName = "3-Mux";

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(
                    "/lu/src/sep/funCKit/data/fck/" + fileName + ".fck"));
        } catch (FileNotFoundException e) {
            gl().error(e.getLocalizedMessage());
        }

        SEPFormatConverter sepFormatConverter = new SEPFormatConverter(
                fileName, SEPFormatConverter.Mode.FUNCKITFORMAT);

        try {
            export(sepFormatConverter.doImport(fileInputStream), fileName);
        } catch (SEPFormatImportException e) {
            gl().error(e.getLocalizedMessage());
        }

        // export(generateSimpleCircuit1(true), "simple");

        // export(generateComponentCircuit1(true), "component");

        // export(generateSimpleFeedbackCircuit1(true), "feedback");
    }

    private static void export(Circuit c, String name) {

        String file = PATH + name;
        String png = file + ".png";
        String gif = file + ".gif";
        String jpg = file + ".jpg";
        String svg = file + ".svg";
        String pdf = file + ".pdf";
        LinkedList<StreamExporter> exporters = new LinkedList<StreamExporter>();

        Settings settings = new Settings();
        settings.apply(DefaultSettings.getDefaultSettings(), true);

        exporters.add(new PNGExporter(settings));
        exporters.add(new GIFExporter(settings));
        exporters.add(new JPGExporter(settings));
        exporters.add(new SVGExporter(settings));
        exporters.add(new PDFExporter(settings));

        try {
            exporters.get(0).doExport(c, new FileOutputStream(new File(png)));
            exporters.get(1).doExport(c, new FileOutputStream(new File(gif)));
            exporters.get(2).doExport(c, new FileOutputStream(new File(jpg)));
            exporters.get(3).doExport(c, new FileOutputStream(new File(svg)));
            exporters.get(4).doExport(c, new FileOutputStream(new File(pdf)));
        } catch (Exception e) {
            gl().error(e);
        }

    }

}