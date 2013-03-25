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

package de.sep2011.funckit.util.internationalization;

import de.sep2011.funckit.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Translate Strings from property files corresponding to the set locale see
 * {@link ResourceBundle}. Default: uses {@link Locale#getDefault()}.
 */
public class Language {
    private static final String BUNDLENAME = "i18n/LanguageBundle";

    /*
     * Use only Language Locales for ease of use. Country and variant are not
     * necessary for us i think ~PETER
     */
    private static Locale currentLocale = new Locale(Locale.getDefault()
            .getLanguage());
    private static final Set<Locale> availableLocales = buildLocaleSet();

    /**
     * Set the locale to translate to.
     * 
     * @param locale
     *            new locale
     */
    public static void setLocale(Locale locale) {
        Language.currentLocale = locale;
        try {
            Locale.setDefault(locale);
        } catch (SecurityException e) {
            Log.gl().debug(e);
        }
        
    }

    private static Set<Locale> buildLocaleSet() {
        LinkedHashSet<Locale> ls = new LinkedHashSet<Locale>();
        ls.add(Locale.GERMAN);
        ls.add(Locale.ENGLISH);
        // ls.add(new Locale("es"));
        return ls;
    }

    /**
     * Get the Translation of a {@link String}.
     * 
     * @param key
     *            {@link String} to translate
     * @param args
     *            Optional arguments to inject in translation string.
     * @return translated {@link String}, key if not existing * @see
     *         String#format(String, Object...)
     */
    public static String tr(String key, Object... args) {
        return getTranslation(key, args);
    }

    /**
     * Returns translation of specific key from a localized property file.
     * 
     * @param key
     *            Keyword for translation.
     * @param args
     *            Optional arguments to inject in translation string.
     * @return translated {@link String}, key if not existing
     * @see String#format(String, Object...)
     */
    private static String getTranslation(String key, Object... args) {
        String translation;
        try {
            translation =
                    ResourceBundle.getBundle(BUNDLENAME, currentLocale)
                            .getString(key);

            /*
             * Allow usage of UTF-8 resource files as they are ISO 8859-1 by
             * default in java.
             */
            try {
                translation = new String(translation.getBytes("ISO-8859-1"),
                        "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.gl().debug(e.toString());
            }
        } catch (MissingResourceException e) {
            Log.gl().warn("Missing translation for key '" + key + "'");
            return key;
        }
        return String.format(translation, args);
    }

    /**
     * Returns all {@link Locale}s we have a translation for.
     * 
     * @return all {@link Locale}s we have a translation for
     */
    public static Set<Locale> getAvailableLocales() {
        return availableLocales;
    }

    /**
     * Returns the current set Locale.
     * 
     * @return the current set {@link Locale}
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Clears the {@link ResourceBundle}.
     */
    public static void reset() {
        ResourceBundle.clearCache();
    }
}
