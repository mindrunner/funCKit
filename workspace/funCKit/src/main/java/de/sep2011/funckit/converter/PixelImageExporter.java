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

import de.sep2011.funckit.drawer.DecisionTable;
import de.sep2011.funckit.drawer.Drawer;
import de.sep2011.funckit.drawer.ElementState;
import de.sep2011.funckit.drawer.FancyDrawer;
import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.sessionmodel.Settings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * Abstract Class for {@link StreamExporter}s which export into pixel images.
 */
public abstract class PixelImageExporter extends ImageExporter {

    /**
     * Creates a new {@link PixelImageExporter}.
     * 
     * @param settings
     *            the settings to use
     */
    protected PixelImageExporter(Settings settings) {
        super(settings);
    }

    @Override
    protected abstract void saveToStream(OutputStream outputStream);

    @Override
    public void doExport(Circuit circuit, OutputStream outputStream) {
        Rectangle rectangle = this.getRect(circuit);

        int width = Math.max(rectangle.width, 1);
        int height = Math.max(rectangle.height, 1);

        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            // Create an image that supports transparent pixels

            this.image = gc.createCompatibleImage(width, height, Transparency.OPAQUE);
        } catch (HeadlessException e) {
            this.image = new BufferedImage(width, height, Transparency.OPAQUE);
        }

        Graphics graphics = image.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        this.repositionRectangle(rectangle, graphics);

        Drawer drawer = new FancyDrawer(settings);
        drawer.setGraphics(graphics);
        for (Element element : circuit.getElements()) {
            ElementState state = new ElementState();
            Layout layout = new Layout();
            DrawAction action = DecisionTable.resolve(state);
            drawer.setLayout(layout);
            drawer.setAction(action);
            element.dispatch(drawer);

        }

        this.saveToStream(outputStream);
    }

}
