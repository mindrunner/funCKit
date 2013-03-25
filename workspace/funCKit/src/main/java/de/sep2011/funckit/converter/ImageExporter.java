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

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.sessionmodel.Settings;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * Abstract Class for {@link StreamExporter}s which export into images.
 */
public abstract class ImageExporter implements StreamExporter {

    private final static int MARGIN = 20;
    BufferedImage image;
    final Settings settings;

    /** Constructs a new ImageExporter. */
    ImageExporter(Settings settings) {
        this.settings = settings;
    }

    /**
     * Get the image rectangle.
     * 
     * @param circuit
     *            The circuit
     * @return the image rectangle
     */
    Rectangle getRect(Circuit circuit) {
        Rectangle r = circuit.getBoundingRectangle();
        r.setBounds(r.x, r.y, r.width + MARGIN, r.height + MARGIN);
        return r;
    }

    /**
     * Repositions the rectangle.
     * 
     * @param rectangle
     *            The ractangle.
     * @param graphics
     *            The current graphics context.
     */
    void repositionRectangle(Rectangle rectangle, Graphics graphics) {
        int cx = rectangle.x;
        int cy = rectangle.y;
        graphics.translate(-cx + MARGIN / 2, -cy + MARGIN / 2);
    }

    /**
     * Saves image to a stream.
     * 
     * @param outputStream
     *            The corresponding OutputStream
     */
    protected abstract void saveToStream(OutputStream outputStream);

    /**
     * Initializes the export.
     * 
     * @param circuit
     *            to export
     * @param outputStream
     *            stream to export into
     */
    @Override
    public abstract void doExport(Circuit circuit, OutputStream outputStream);
}