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

package de.sep2011.funckit;

import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.Profiler;
import org.apache.log4j.Level;
import java.io.File;
import java.util.Arrays;

/**
 * Bootstrap class to kick off application and thus controller, view and models.
 */
public class FunCKit {
    private final static String APPLICATION_TITLE = "funCKit";
    private final static String HOME_DIRECTORY = System.getProperty("user.home") + File.separator
            + ".funckit";

    /**
     * Program arguments, passed on command line.
     */
    private static String[] arguments;

    /**
     * Bootstrap method for FunCKit. Passed arguments might influence state of
     * application (e.g. load projects from instance).
     * 
     * @param args
     *            Program arguments
     */
    public static void main(String[] args) {
        /* For possible restart. */
        FunCKit.arguments = args;
        FunCKit.start();
    }

    /**
     * Encapsulates logic to initialize application (here could argument
     * interpreter get injected).
     */
    public static void start() {
        if (Arrays.asList(arguments).contains("nodebug")) { 
            Log.gl().setLevel(Level.INFO);
        }

        if (Arrays.asList(arguments).contains("noprofile")) {
            Profiler.ON = false;
        }
        
        Profiler.ON = false;

        try {
            new StandaloneApplication(APPLICATION_TITLE, HOME_DIRECTORY);
        } catch (final Exception e) {
            e.printStackTrace();
            Log.gl().error(e);
        }
    }

}
