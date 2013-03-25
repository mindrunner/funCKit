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

import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatExportException;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.DefaultSettings;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.test.factory.circuit.ComplexComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.ComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.ExtremeSimpleComponentCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.SimpleCircuit1Factory;
import de.sep2011.funckit.test.factory.circuit.SimpleFeedbackCircuit1Factory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static de.sep2011.funckit.util.Log.gl;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the {@link SEPFormatConverter} by exporting various
 * Circuits, reimporting them and check them with
 * {@link Circuit#equalGraph(Circuit)}
 */
public class SepConverterTest {

    private File fckFile;

    /**
     * Tests the export of the {@link Circuit} from
     * {@link SimpleCircuit1Factory}
     * 
     * @throws SEPFormatImportException
     * @throws SEPFormatExportException
     * @throws FileNotFoundException
     */
    @Test
    public void testSimpleCircuit() throws SEPFormatImportException,
            SEPFormatExportException, FileNotFoundException {
        testCircuit((new SimpleCircuit1Factory(true)).getCircuit(),
                "SimpleCircuit1");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link ComponentCircuit1Factory}
     * 
     * @throws SEPFormatImportException
     * @throws SEPFormatExportException
     * @throws FileNotFoundException
     */
    @Test
    public void testComponentCircuit() throws SEPFormatImportException,
            SEPFormatExportException, FileNotFoundException {
        testCircuit((new ComponentCircuit1Factory(true)).getCircuit(),
                "ComponentCircuit1");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link SimpleFeedbackCircuit1Factory}
     * 
     * @throws SEPFormatImportException
     * @throws SEPFormatExportException
     * @throws FileNotFoundException
     */
    @Test
    public void testFeedbackCircuit() throws SEPFormatImportException,
            SEPFormatExportException, FileNotFoundException {
        testCircuit(new SimpleFeedbackCircuit1Factory(true).getCircuit(),
                "SimpleFeedbackCircuit1");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link ComplexComponentCircuit1Factory}
     * 
     * @throws SEPFormatImportException
     * @throws SEPFormatExportException
     * @throws FileNotFoundException
     */
    @Test
    public void testComplexComponentCircuit() throws SEPFormatImportException,
            SEPFormatExportException, FileNotFoundException {
        testCircuit(new ComplexComponentCircuit1Factory().getCircuit(),
                "ComplexComponentCircuit");
    }

    /**
     * Tests the export of the {@link Circuit} from
     * {@link ExtremeSimpleComponentCircuit1Factory}
     * 
     * @throws SEPFormatImportException
     * @throws SEPFormatExportException
     * @throws FileNotFoundException
     */
    @Test
    public void testExtremeSimpleComponentCircuit()
            throws SEPFormatImportException, SEPFormatExportException,
            FileNotFoundException {
        testCircuit(new ExtremeSimpleComponentCircuit1Factory().getCircuit(),
                "ExtremeSimpleCircuit");
    }

    private void testCircuit(Circuit circuit, String name)
            throws SEPFormatImportException, SEPFormatExportException,
            FileNotFoundException {
        gl().debug("Testing SEP Converter with " + name);
        String path = System.getProperty("java.io.tmpdir") + File.separator
                + "test" + name;
        SEPFormatConverter converter = new SEPFormatConverter("Test projekt",
                Mode.FUNCKITFORMAT);

        fckFile = new File(path + ".fck");

        Settings settings = new Settings();
        settings.apply(DefaultSettings.getDefaultSettings(), true);
        converter.doExport(circuit, new FileOutputStream(fckFile));

        // reset converter (clear uuids!)
        converter = new SEPFormatConverter("Test projekt", Mode.FUNCKITFORMAT);
        Circuit importedCircuit = converter.doImport(new FileInputStream(
                fckFile));
        assertTrue(circuit.equalGraph(importedCircuit));

    }

    @After
    public void tearDown() {
        if (fckFile != null) {
            fckFile.delete();
        }

    }
}
