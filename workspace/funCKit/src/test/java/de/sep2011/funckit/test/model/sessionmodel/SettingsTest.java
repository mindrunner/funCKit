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

package de.sep2011.funckit.test.model.sessionmodel;

import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SettingsTest {
    private String settingsFilePath;
    private File settingsFile;
    private Settings settings;

    @Before
    public void setUp() {
        Log.gl().info("SettingsTest.setUp()");
        settingsFilePath = "funckit-settings.xml";

        settings = new Settings(settingsFilePath);

        settingsFile = new File(settingsFilePath);
    }

    @After
    public void tearDown() {
        Log.gl().info("SettingsTest.tearDown()");
        if (settingsFile.exists()) {
            settingsFile.delete();
        }
    }

    @Test
    public void testSaving() {
        Log.gl().info("SettingsTest.testLoading()");
        Assert.assertTrue(settings.save());
    }

    @Test
    public void testAutoSave() {
        Log.gl().info("SettingsTest.testAutoSave()");
        String key = "testAutoSave";
        int value = 500;

        settings.setAutosave(true);
        settings.set(key, value);

        Settings other = new Settings(settingsFilePath);
        Assert.assertEquals(value, other.getInt(key));
    }

    @Test
    public void testReceivingValues() {
        Log.gl().info("SettingsTest.testReceivingValues()");

        settings.setAutosave(false);

        settings.set("a", 5f);
        Assert.assertEquals(5f, settings.getFloat("a"));
        Assert.assertEquals(0, settings.getInt("a"));

        settings.set("b", 2.4d);
        Assert.assertEquals(2.4d, settings.getDouble("b"));

        settings.set("c", "value");
        Assert.assertEquals("value", settings.getString("c"));

        settings.set("d", true);
        Assert.assertTrue(settings.getBoolean("d"));

        settings.set("e", 4);
        Assert.assertEquals(4, settings.getInt("e"));

        settings.set("f", 523L);
        Assert.assertEquals(523L, settings.getLong("f"));
        Assert.assertEquals(523, settings.getInt("f"));
        Assert.assertEquals(523f, settings.getFloat("f"));
    }

    @Test
    public void testLoading() {
        Log.gl().info("SettingsTest.testLoading()");
        Assert.assertTrue(settings.save());
        Assert.assertTrue(settings.reload());
    }
}
