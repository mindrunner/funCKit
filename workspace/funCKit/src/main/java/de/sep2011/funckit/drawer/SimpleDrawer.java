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

package de.sep2011.funckit.drawer;

import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Switch;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.IdPoint;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.DrawUtil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Extreme fast and simple drawer that simply draws shapes without further
 * styling or information.
 */
public class SimpleDrawer implements Drawer {
    private Graphics2D graphics;
    private Settings settings;

    /**
     * Constructs a {@link SimpleDrawer} with injection of current applications
     * setting object.
     *
     * @param settings Current settings object.
     */
    public SimpleDrawer(Settings settings) {
        assert settings != null;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGraphics(Graphics graphics) {
        assert graphics != null;
        this.graphics = (Graphics2D) graphics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAction(DrawAction action) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLayout(Layout layout) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Element element) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Wire wire) {
        assert graphics != null;
        assert settings != null;

        int relativeX1 = wire.getFirstAccessPoint().getPosition().x;
        int relativeX2 = wire.getSecondAccessPoint().getPosition().x;
        int relativeY1 = wire.getFirstAccessPoint().getPosition().y;
        int relativeY2 = wire.getSecondAccessPoint().getPosition().y;
        int x1 = wire.getFirstAccessPoint().getBrick().getBoundingRect().x
                + relativeX1;
        int x2 = wire.getSecondAccessPoint().getBrick().getBoundingRect().x
                + relativeX2;
        int y1 = wire.getFirstAccessPoint().getBrick().getBoundingRect().y
                + relativeY1;
        int y2 = wire.getSecondAccessPoint().getBrick().getBoundingRect().y
                + relativeY2;

        /*
         * Wire is from one access point to another one, so accept a direct
         * line.
         */
        graphics.setColor(settings.get(Settings.ELEMENT_BORDER_COLOR,
                Color.class));
        graphics.drawLine(x1, y1, x2, y2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Component component) {
        assert graphics != null;
        assert settings != null;

        drawRectangle(component.getBoundingRect().x,
                component.getBoundingRect().y,
                component.getBoundingRect().width,
                component.getBoundingRect().height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Switch s) {
        assert graphics != null;
        assert settings != null;

        drawRectangle(s.getBoundingRect().x, s.getBoundingRect().y,
                s.getBoundingRect().width, s.getBoundingRect().height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Light light) {
        assert graphics != null;
        assert settings != null;

        graphics.setColor(settings.get(Settings.ELEMENT_BORDER_COLOR, Color.class));
        graphics.draw(DrawUtil.calculateLightShape(light));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(And and) {
        assert graphics != null;
        assert settings != null;

        drawRectangle(and.getBoundingRect().x, and.getBoundingRect().y,
                and.getBoundingRect().width, and.getBoundingRect().height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Or or) {
        assert graphics != null;
        assert settings != null;

        drawRectangle(or.getBoundingRect().x, or.getBoundingRect().y,
                or.getBoundingRect().width, or.getBoundingRect().height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Not not) {
        assert graphics != null;
        assert settings != null;

        drawRectangle(not.getBoundingRect().x, not.getBoundingRect().y,
                not.getBoundingRect().width, not.getBoundingRect().height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdPoint idPoint) {
        /* Do not draw id points. */
    }

    private void drawRectangle(int x, int y, int width, int height) {
        assert graphics != null;
        assert settings != null;
        Color color = settings.get(Settings.ELEMENT_BORDER_COLOR, Color.class);
        graphics.setColor(color);
        graphics.drawRect(x, y, width, height);
    }
}
