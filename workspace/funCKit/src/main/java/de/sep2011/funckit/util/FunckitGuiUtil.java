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

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.META_DOWN_MASK;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;

import java.awt.Cursor;
import java.awt.Desktop.Action;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.net.URL;

/**
 * A collection of utility methods for the GUI.
 */
public class FunckitGuiUtil {

    /**
     * Adds a {@link KeyStroke} to a {@link AbstractButton}. Pressing this
     * keystroke ends up in a call to {@link AbstractButton#doClick()}. Uses the
     * buttons {@link InputMap} for {@link JComponent#WHEN_IN_FOCUSED_WINDOW}
     * 
     * @param b
     *            button to add the Keystroke to
     * @param ks
     *            the {@link KeyStroke} to add
     * @param key
     *            identifies the the associated {@link Action} of the
     *            {@link KeyStroke} inside the buttons {@link ActionMap} and
     *            {@link InputMap}
     */
    private static void addButtonClickKeystroke(final AbstractButton b,
                                                KeyStroke ks, Object key) {
        b.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, key);
        b.getActionMap().put(key, new AbstractAction() {

            private static final long serialVersionUID = 2702589216282710389L;

            @Override
            public void actionPerformed(ActionEvent e) {
                b.doClick();
            }
        });
    }

    /**
     * Same as
     * {@link #addButtonClickKeystroke(AbstractButton, KeyStroke, Object)} but
     * generates the key.
     * 
     * @param b
     * @param ks
     * @see #addButtonClickKeystroke(AbstractButton, KeyStroke, Object)
     */
    public static void addButtonClickKeystroke(final AbstractButton b,
            KeyStroke ks) {
        Object key = new Object();
        addButtonClickKeystroke(b, ks, key);
    }

    /**
     * The reverse Operation of
     * {@link #addButtonClickKeystroke(AbstractButton, KeyStroke, Object)}.
     * 
     * @param key
     *            The {@link KeyStroke} to remove
     * @param b
     *            the button to work on
     */
    public static void removeButtonClickKeystroke(AbstractButton b,
            KeyStroke key) {
        InputMap inMap = b.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap acMap = b.getActionMap();

        acMap.remove(inMap.get(key));
        inMap.remove(key);

    }

    public static ImageIcon iconFromResource(String path) {
        assert path != null;
        assert !path.equals("");
        URL r = FunckitGuiUtil.class.getResource(path);
        assert r != null;
        return new ImageIcon(r);
    }

    /**
     * Constructs a {@link Cursor} from an resource image.
     * 
     * @param path
     *            path to the resource
     * @param cursorHotSpot
     *            where inside the Cursor the click Point is
     * @param name
     *            name a localized description of the cursor, for Java
     *            Accessibility use
     * @return the Cursor
     */
    public static Cursor getCursorFromResource(String path,
            Point cursorHotSpot, String name) {
        assert path != null;
        assert !path.equals("");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        assert toolkit != null;
        URL cursorUrl = FunckitGuiUtil.class.getResource(path);
        assert cursorUrl != null;
        Image image = toolkit.getImage(cursorUrl);
        assert image != null;
        Cursor customCursor = toolkit.createCustomCursor(image, cursorHotSpot,
                name);
        assert customCursor != null;
        return customCursor;
    }

    public static void centerDialogInWindow(JDialog dialog, Window win) {
        int posX = win.getLocation().x;
        int posY = win.getLocation().y;
        int dX = (win.getWidth() - dialog.getWidth()) / 2;
        int dY = (win.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(posX + dX, posY + dY);
    }
    
    /**
     * For Mac Os compatibility, use this instead of {@link InputEvent#CTRL_DOWN_MASK}.
     * 
     * @return the platform specific input mask
     */
    public static int ctrlOrMeta() {
        return  (Application.OS == OperatingSystem.OSX ? META_DOWN_MASK : CTRL_DOWN_MASK);
    }
}
