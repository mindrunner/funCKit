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

package de.sep2011.funckit.converter;

import de.sep2011.funckit.model.sessionmodel.Settings;
import javax.imageio.ImageIO;

import java.io.OutputStream;

import static de.sep2011.funckit.util.Log.gl;

/**
 * This class contains the methods to export PNG-Pictures.
 */
public class PNGExporter extends PixelImageExporter {

    /**
     * Creates a new PNGExporter.
     * 
     * @param settings
     *            The current settings object
     */
    public PNGExporter(Settings settings) {
        super(settings);
    }

    @Override
    protected void saveToStream(OutputStream outputStream) {
        try {
            ImageIO.write(this.image, "PNG", outputStream);
        } catch (Exception e) {
            gl().error(e);
        }
    }
}