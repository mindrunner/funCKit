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
import de.sep2011.funckit.converter.PNGExporter;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.DefaultSettings;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.test.factory.circuit.ComplexComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.ComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.ExtremeSimpleComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.SimpleFeedbackCircuit1Factory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static de.sep2011.funckit.util.Log.gl;

/**
 * This class tests the {@link JPGExporter}, {@link GIFExporter} and
 * {@link PNGExporter} by exporting various Circuits. This only tests if the
 * exporter runs without errors and the created file is not empty.
 */
public class PixelExportTest {

    private File jpgFile;
    private File pngFile;
    private File gifFile;

    /**
     * Tests the export of the {@link Circuit} from
     * {@link SimpleCircuit1Factory}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testSimpleCircuit() throws FileNotFoundException {
        testCircuit((new SimpleCircuit1Factory(true)).getCircuit(),
                "SimpleCircuit1");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link ComponentCircuit1Factory}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testComponentCircuit() throws FileNotFoundException {
        testCircuit((new ComponentCircuit1Factory(true)).getCircuit(),
                "ComponentCircuit1");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link SimpleFeedbackCircuit1Factory}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testFeedbackCircuit() throws FileNotFoundException {
        testCircuit(new SimpleFeedbackCircuit1Factory(true).getCircuit(),
                "SimpleFeedbackCircuit1");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link ComplexComponentCircuit1Factory}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testComplexComponentCircuit() throws FileNotFoundException {
        testCircuit(new ComplexComponentCircuit1Factory().getCircuit(),
                "ComplexComponentCircuit");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link ExtremeSimpleComponentCircuit1Factory}
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testExtremeSimpleComponentCircuit()
            throws FileNotFoundException {
        testCircuit(new ExtremeSimpleComponentCircuit1Factory().getCircuit(),
                "ExtremeSimpleCircuit");
    }

    private void testCircuit(Circuit circuit, String name)
            throws FileNotFoundException {
        gl().debug("Testing SVG Converter with " + name);
        String path = System.getProperty("java.io.tmpdir") + File.separator
                + "test" + name;

        Settings settings = new Settings();
        settings.apply(DefaultSettings.getDefaultSettings(), true);

        PNGExporter pngExporter = new PNGExporter(settings);
        pngFile = new File(path + ".png");
        pngExporter.doExport(circuit, new FileOutputStream(pngFile));
        Assert.assertTrue(pngFile.length() > 0L);

        JPGExporter jpgExporter = new JPGExporter(settings);
        jpgFile = new File(path + ".jpg");
        jpgExporter.doExport(circuit, new FileOutputStream(jpgFile));
        Assert.assertTrue(jpgFile.length() > 0L);

        GIFExporter gifExporter = new GIFExporter(settings);
        gifFile = new File(path + ".gif");
        gifExporter.doExport(circuit, new FileOutputStream(gifFile));
        Assert.assertTrue(jpgFile.length() > 0L);

    }

    @After
    public void tearDown() {
        if (jpgFile != null) {
            jpgFile.delete();
        }

        if (pngFile != null) {
            jpgFile.delete();
        }

        if (gifFile != null) {
            gifFile.delete();
        }
    }

}
