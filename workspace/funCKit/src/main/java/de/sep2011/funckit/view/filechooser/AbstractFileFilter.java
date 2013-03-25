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

package de.sep2011.funckit.view.filechooser;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This filter combines FileFilter and FilenameFilter to be usable by JFileChooser and FileDialog.
 * It only filters by file names.
 */
public abstract class AbstractFileFilter extends FileFilter implements
        FilenameFilter {

	/**
	 * The filter to use for filtering the file extension.
	 */
    protected FileNameExtensionFilter filter;

    @Override
    public boolean accept(File f) {
        return filter.accept(f);
    }

    @Override
    public boolean accept(File dir, String filename) {
        return filter.accept(new File(dir.getAbsolutePath(), filename));
    }

    @Override
    public String getDescription() {
        return filter.getDescription();
    }

    /**
     * Returns the main file extension for this filter. Can be used to add missing file extensions.
     * @return the main file extension for this filter.
     */
    abstract public String getMainExtension();

}
