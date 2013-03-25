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

package de.sep2011.funckit.util;

import static de.sep2011.funckit.util.Log.gl;

/**
 * Helper class to print out system information from {@link System#getProperty(String)}.
 */
public class SystemInformation {

    private final String[] dataKeys = { "java.version", // //The version of Java
                                                  // Runtime Environment.
            "java.vendor", // The name of Java Runtime Environment vendor
            "java.vendor.url", // The URL of Java vendor
            "java.home", // The directory of Java installation
            "java.vm.specification.version", // The specification version of
                                             // Java Virtual Machine
            "java.vm.specification.vendor", // The name of specification vendor
                                            // of Java Virtual Machine
            "java.vm.specification.name", // Java Virtual Machine specification
                                          // name
            "java.vm.version", // JVM implementation version
            "java.vm.vendor", // JVM implementation vendor
            "java.vm.name", // JVM implementation name
            "java.specification.version", // The name of specification version
                                          // Java Runtime Environment
            "java.specification.vendor", // JRE specification vendor
            "java.specification.name", // JREspecification name
            "java.class.version", // Java class format version number
            "java.class.path", // Path of java class
            "java.library.path", // List of paths to search when loading
                                 // libraries
            "java.io.tmpdir", // The path of temp file
            "java.compiler", // The Name of JIT compiler to use
            "java.ext.dirs", // The path of extension directory or directories
            "os.name", // The name of OS name
            "os.arch", // The OS architecture
            "os.version", // The version of OS
            "file.separator", // The File separator
            "path.separator", // The path separator
            "line.separator", // The line separator
            "user.name", // The name of account name user
            "user.home", // The home directory of user
            "user.dir", // The current working directory of the user
    };

    /**
     * Prints out system information to the log.
     */
    public void printAll() {
        for (String s : dataKeys)
            gl().info(s + ": " + System.getProperty(s));
    }

}
