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

import de.sep2011.funckit.drawer.DecisionTable;
import de.sep2011.funckit.drawer.Drawer;
import de.sep2011.funckit.drawer.ElementState;
import de.sep2011.funckit.drawer.FancyDrawer;
import de.sep2011.funckit.drawer.Layout;
import de.sep2011.funckit.drawer.LayoutResolver;
import de.sep2011.funckit.drawer.LayoutShape;
import de.sep2011.funckit.drawer.action.DrawAction;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Wire;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import de.sep2011.funckit.model.simulationmodel.Simulation;
import de.sep2011.funckit.validator.Result;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class DrawUtil {
    public static final Color TRANSPARENT = new Color(255, 255, 255, 0);

    private static Drawer drawer;
    private static final LayoutResolver LAYOUT_RESOLVER = new LayoutResolver();
    private static final Set<Character> BREAK_CHARACTERS = new LinkedHashSet<Character>();

    static {
        BREAK_CHARACTERS.add(new Character(' '));
        BREAK_CHARACTERS.add(new Character(','));
        BREAK_CHARACTERS.add(new Character('.'));
        BREAK_CHARACTERS.add(new Character('-'));
    }

    private static final Character NEW_LINE = Character.valueOf('\n');

    /**
     * Calculates simple line of a given {@link Wire} as {@link Path2D} for
     * drawing it on a {@link Graphics2D} object.
     *
     * @param wire Wire with its {@link AccessPoint} information.
     * @return Shape descriptive object of wire.
     */
    public static Path2D calculateWireShape(Wire wire) {
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

        return new Path2D.Double(new Line2D.Double(x1, y1, x2, y2));
    }

    /**
     * Calculates shape for a given light.
     *
     * @param light Light with size information.
     * @return Shape descriptive object to draw it on a {@link Graphics2D}
     *         object.
     */
    public static Path2D calculateLightShape(Light light) {
        Rectangle r = light.getBoundingRect();
        int width = r.width;
        int x = r.x;
        int y = r.y;

        Path2D shape = new Path2D.Double(new Ellipse2D.Double(x, y, width,
                width));

        /*
         * Following is simply hard coded and can not change angle .. reconsider
         * & recalculate
         */
        double radius = 0.5 * width;
        double line1CircleDifferenceX = radius * Math.cos(Math.PI / 4);
        double line1CircleDifferenceY = radius * Math.sin(Math.PI / 4);
        double line1x1 = x + radius - line1CircleDifferenceX + 1;
        double line1y1 = y + radius - line1CircleDifferenceY + 1;
        double line1x2 = x + radius + line1CircleDifferenceX;
        double line1y2 = y + radius + line1CircleDifferenceY;
        Line2D line1 = new Line2D.Double(line1x1, line1y1, line1x2, line1y2);
        shape.append(line1, false);

        Line2D line2 = new Line2D.Double(line1x2, line1y1, line1x1, line1y2);
        shape.append(line2, false);

        return shape;
    }

    /**
     * Utility method to simply inject a given shape as {@link
     * Layout.BASE_SHAPE} with default colors (debugging colors for user
     * interface notice).
     *
     * @param layout Layout to inject the shape.
     * @param shape  Shape object to inject in layout.
     */
    public static void injectShape(Layout layout, Path2D shape) {
        layout.putShape(
                Layout.BASE_SHAPE,
                new LayoutShape(
                        shape,
                        Layout.DEBUG_BORDER_COLOR,
                        Layout.DEBUG_FILL_COLOR
                )
        );
    }

    /**
     * Draws a grid on given graphics object with specified cell size, width,
     * height, affine transformation and grid color.
     *
     * @param graphics
     * @param gridSize
     * @param width
     * @param height
     * @param at
     * @param gridColor
     */
    public static void drawGrid(Graphics2D graphics, int gridSize, int width,
                                int height, AffineTransform at, Color gridColor) {
        graphics.shear(at.getShearX(), at.getShearY());

        double scGridSizeX = at.getScaleX() * gridSize;
        double scGridSizeY = at.getScaleY() * gridSize;

        /*
         * calculate where the first line starts. This creates the illusion that
         * the grid is everywhere and moves with the other elements on the panel
         */
        double startX = at.getTranslateX() % scGridSizeX;
        double startY = at.getTranslateY() % scGridSizeY;

        graphics.setColor(gridColor);

        for (double x = startX; x < width + abs(startX); x += scGridSizeX) {
            graphics.drawLine((int) round(x), 0, (int) round(x), height);
        }

        for (double y = startY; y < height + abs(startY); y += scGridSizeY) {
            graphics.drawLine(0, (int) round(y), width, (int) round(y));
        }
    }

    public static void drawElements(Graphics2D graphics, Set<Element> elements,
                                    EditPanelModel editPanelModel, SessionModel sessionModel) {
        if (drawer == null) {
            drawer = new FancyDrawer(sessionModel.getSettings());
        }
        Deque<Component> stack = editPanelModel.getComponentStack();
        Simulation simulation = sessionModel.getCurrentSimulation();
        drawer.setGraphics(graphics);
        for (Element element : elements) {
            ElementState state = resolveElementState(element, editPanelModel,
                    sessionModel);
            Layout layout = resolveElementLayout(element, stack, simulation);
            DrawAction action = DecisionTable.resolve(state);
            drawer.setLayout(layout);
            drawer.setAction(action);
            element.dispatch(drawer);
        }
    }

    /**
     * Returns center position for a text with specified element and text size
     * information. Deprecated, as it does not regard text height. Should use
     * {@link DrawUtil#calculateCenteredStart()}.
     *
     * @param width      Width of element.
     * @param height     Height of element.
     * @param textWidth  Text width of text to center.
     * @param textHeight Text height of text, that should be centered.
     * @return
     */
    @Deprecated
    public static Point getCenterPosition(int width, int height, int textWidth,
                                          int textHeight) {
        return new Point((int) Math.round((width - textWidth) / 2.0),
                (int) Math.round(height / 2.0));
    }

    /**
     * Calculates a centered start position for a text with specified text width
     * in given rectangle.
     *
     * @param r         Bounding rectangle.
     * @param textWidth Width of text, that is to center.
     * @return Position to center text.
     */
    public static int calculateCenteredStart(Rectangle r, int textWidth) {
        return (int) Math.round((r.width - textWidth) / 2.0);
    }

    public static Font resizeFontToFit(
            int width,
            int height,
            String text,
            Font font,
            Graphics2D graphics) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        int size = font.getSize();

        // Probably increase font if it is too small :)
        while (metrics.stringWidth(text) < width
                && metrics.getHeight() < height) {
            size++;
            font = font.deriveFont(Float.valueOf(size));
            metrics = graphics.getFontMetrics(font);
        }
        // And if it was too large, decrease it now.
        while ((metrics.stringWidth(text) > width || metrics.getHeight() > height) && size > 1) {
            size--;
            font = font.deriveFont(Float.valueOf(size));
            metrics = graphics.getFontMetrics(font);
        }

        return font;
    }

    /**
     * Returns a shortened string, that fits into the given space with the
     * specified font on the given graphics object considering a possible
     * suffix.
     *
     * @param text
     * @param spaceForText
     * @param font
     * @param graphics
     * @param suffix
     * @return
     */
    public static String cutTextToWidth(String text, int spaceForText, Font font,
                                        Graphics2D graphics, String suffix) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        StringBuilder actualText = new StringBuilder(text);
        int actualTextWidth = metrics.stringWidth(text);
        if (textWidth > spaceForText) {
            int suffixWidth = 0;
            if (!suffix.isEmpty()) {
                suffixWidth = metrics.stringWidth(suffix);
            }
            actualTextWidth += suffixWidth;
            int length = actualText.length();
            while (actualTextWidth > spaceForText && length > 0) {
                actualText.deleteCharAt(length - 1);
                actualTextWidth = metrics.stringWidth(actualText.toString())
                        + suffixWidth;
                length = actualText.length();
            }
            actualText.append(suffix);
        }
        return actualText.toString();
    }

    private static Layout resolveElementLayout(Element element,
                                               Deque<Component> stack, Simulation simulation) {
        Layout layout = new Layout();
        LAYOUT_RESOLVER.setComponentStack(stack);
        LAYOUT_RESOLVER.setLayout(layout);
        LAYOUT_RESOLVER.setSimulation(simulation);
        element.dispatch(LAYOUT_RESOLVER);
        return layout;
    }

    private static ElementState resolveElementState(Element element,
                                                    EditPanelModel panelModel, SessionModel sessionModel) {
        ElementState elementState = new ElementState();

        elementState.setMode(ElementState.Mode.NORMAL);

        if (panelModel.getSelectedElements().contains(element)) {
            elementState.setMode(ElementState.Mode.SELECTED);
        } else if (panelModel.getGhosts().contains(element)) {
            elementState.setMode(ElementState.Mode.GHOST);
        }
        elementState.setSimulated(sessionModel.getCurrentSimulation() != null);

        List<Result> results = sessionModel.getCurrentCheckResults();
        if (results != null) {
            for (Result r : results) {
                if (r.getFlawElements().contains(element)) {
                    elementState.setHasError(true);
                }
            }
        }

        return elementState;
    }

    public static void drawToolTip(
            Graphics2D graphics,
            Point p,
            int toolTipWidth,
            String text,
            Color tooltipBorderColor,
            Color tooltipFillColor,
            Color tooltipTextColor,
            Font font,
            int shiftingX,
            int shiftingY,
            int marginTop,
            int marginLeft,
            int marginRight,
            int marginBottom) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int startX = p.x + shiftingX;
        int startY = p.y - shiftingY;
        int roundRectangleDiameter = 5;

        /* Analyze text. */
        int fontHeight = graphics.getFontMetrics(font).getHeight();
        Vector<?> lines = DrawUtil.parseText(text, toolTipWidth,
                graphics.getFontMetrics(font));

        int toolTipHeight = (lines.size() + 1) * fontHeight + marginBottom;
        toolTipWidth += marginRight;
        int toolTipX = startX;
        int toolTipY = startY;

        /* Draw optical lines from point to tooltip rectangle. */
        Polygon polygon = new Polygon();
        polygon.addPoint(p.x, p.y);
        polygon.addPoint(toolTipX, toolTipY);
        polygon.addPoint(toolTipX, toolTipY + toolTipHeight
                - roundRectangleDiameter);
        Path2D shape = new Path2D.Double(polygon);
        graphics.setColor(new Color(180, 180, 180, 200));
        graphics.draw(shape);
        graphics.setColor(new Color(180, 180, 180, 50));
        graphics.fill(shape);

        /* Draw rounded rectangle. */
        graphics.setColor(tooltipBorderColor);
        graphics.drawRoundRect(toolTipX, toolTipY, toolTipWidth, toolTipHeight,
                roundRectangleDiameter, roundRectangleDiameter);

        /* Fill rounded rectangle. */
        graphics.setColor(tooltipFillColor);
        graphics.fillRoundRect(toolTipX, toolTipY, toolTipWidth, toolTipHeight,
                roundRectangleDiameter, roundRectangleDiameter);

        /* Draw text. */
        int textPositionX = startX + marginLeft;
        int textPositionY = startY + fontHeight + marginTop;
        graphics.setColor(tooltipTextColor);
        graphics.setFont(font);
        for (int i = 0; i < lines.size(); i++) {
            int lineY = textPositionY + (i * fontHeight);
            graphics.drawString((String) lines.elementAt(i), textPositionX,
                    lineY);
        }
    }

    private static Vector<String> parseText(String text, int width,
                                            FontMetrics metrics) {
        Vector<String> parsedStrings = new Vector<String>();

        Set<Character> breakCharacters = BREAK_CHARACTERS;
        LinkedList<Integer> breakPositions = new LinkedList<Integer>();

        int numChars = text.length();
        int lineWidth = width;

        int currentStartPosition = 0;
        while (currentStartPosition < numChars) {
            int currentPosition = currentStartPosition;
            boolean fitsInLine = true;
            while (fitsInLine && currentPosition < numChars) {
                /* Hard break if there is a hard coded new line. */
                if (text.charAt(currentPosition) == NEW_LINE) {
                    break;
                }

                int currentWidth = metrics.stringWidth(
                        text.substring(currentStartPosition,
                                currentPosition));

                /*
                 * If current line width exceeds line width, jump back to last
                 * breakable position.
                 */
                if (currentWidth >= lineWidth) {
                    fitsInLine = false;
                    if (breakPositions.getLast() != null) {
                        currentPosition = breakPositions.getLast();
                    }
                } else {
                    /* Save possible break position to list. */
                    if (breakCharacters.contains(text.charAt(currentPosition))) {
                        breakPositions.add(currentPosition);
                    }

                    /* Otherwise continue with next character. */
                    currentPosition++;
                }
            }
            parsedStrings.add(text.substring(currentStartPosition, currentPosition));
            currentStartPosition = currentPosition + 1;
        }

        return parsedStrings;
    }

    @Deprecated
    public static Vector<String> parseMessage(String message, int width,
                                              FontMetrics metrics) {
        Vector<String> parsedStrings = new Vector<String>();
        int start = 0, stop = 0;
        final int toleranceFactor = 3;
        int lineWidth = width - toleranceFactor;
        int numChars = message.length();

        boolean ok = false;
        start = 0;
        stop = 0;

        Character space = Character.valueOf(' ');
        Character nLine = Character.valueOf('\n');
        Character comma = Character.valueOf(',');
        Character period = Character.valueOf('.');
        Character dash = Character.valueOf('-');
        Character fslash = Character.valueOf('/');
        Character bslash = Character.valueOf('\\');

        while ((start < numChars)) {
            int check = start;
            int subLength = 0;
            while (!ok) {
                if (space.charValue() == message.charAt(check)
                        || comma.charValue() == message.charAt(check)
                        || period.charValue() == message.charAt(check)
                        || dash.charValue() == message.charAt(check)
                        || fslash.charValue() == message.charAt(check)
                        || bslash.charValue() == message.charAt(check)) {
                    subLength = metrics.stringWidth(message.substring(start,
                            check));
                    if (subLength < lineWidth)
                        stop = check;
                    else
                        ok = true;
                } else if (nLine.charValue() == message.charAt(check)) {
                    stop = check;
                    ok = true;
                } else if (metrics.stringWidth(message.substring(start, check)) > lineWidth) {
                    stop = check;
                    ok = true;
                }

                if (!ok) {
                    if (check == (numChars - 1)) {
                        stop = check + 1;
                        ok = true;
                    } else {
                        check = check + 1;
                    }
                }
            }

            if (stop == numChars) {
                parsedStrings.addElement(message.substring(start));
            } else {
                parsedStrings.addElement(message.substring(start, stop));
            }

            start = stop + 1;
            stop = start;
            ok = false;
        }
        return parsedStrings;
    }
}
