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

package de.sep2011.funckit.test.util;

import de.sep2011.funckit.util.internationalization.Language;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

/**
 * This class contains tests for the {@link Language} class.
 */
public class LanguageTest {
    private Locale tmp;

    @Before
    public void setUp() throws Exception {
        tmp = Language.getCurrentLocale();
        Language.setLocale(new Locale("de"));
    }

    @After
    public void tearDown() throws Exception {
        Language.setLocale(tmp);
    }

    /**
     * Tests if a existing keyword is correctly translated.
     */
    @Test
    public void testExistingKeyword() {
        String translation = Language.tr("test.existing");
        Assert.assertEquals("exists", translation);
    }

    /**
     * Tests if a not existing keyword is correctly not translated and the key
     * is returned.
     */
    @Test
    public void testNotExistingKeyword() {
        String translation = Language.tr("test.notExisting");
        Assert.assertEquals("test.notExisting", translation);
    }

    /**
     * Tests if a key with Arguments is correctly translated.
     */
    @Test
    public void testKeyWithArguments() {
        String arg = "foo";
        String translation = Language.tr("test.keyWithArguments", arg);
        Assert.assertEquals("key with '" + arg + "' as argument", translation);

        translation = Language.tr("test.keyWithArguments", arg, "bar");
        Assert.assertEquals("key with '" + arg + "' as argument", translation);
    }

    /**
     * Tests if a key with multiple Arguments is correctly translated.
     */
    @Test
    public void testKeyWithMultipleArguments() {
        String arg1 = "foo";
        int arg2 = 5;
        String translation = Language.tr("test.keyWithMultipleArguments", arg1,
                arg2);
        Assert.assertEquals("key with '" + arg1 + "' and '" + arg2
                + "' as argument", translation);
    }
}
