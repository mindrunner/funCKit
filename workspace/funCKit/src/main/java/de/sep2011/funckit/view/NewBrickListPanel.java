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

package de.sep2011.funckit.view;

import de.sep2011.funckit.controller.listener.NewBrickListListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;

import static de.sep2011.funckit.util.internationalization.Language.tr;

/**
 * Panel that contains the {@link NewBrickList}.
 */
public class NewBrickListPanel extends JPanel {
    private static final long serialVersionUID = 8395833035678458119L;
    private View view;
    private JLabel label;
    private JScrollPane newBrickScrollList;

    /**
     * Creates a new {@link NewBrickListPanel}.
     * 
     * @param view
     *            the associated View
     */
    public NewBrickListPanel(View view) {
        super(new MigLayout("insets 0, wrap", "[grow]", "[][grow]"));
        assert view != null;
        this.view = view;
        NewBrickList newBrickList = new NewBrickList(view);
        NewBrickListListener newBrickListListener = new NewBrickListListener(view,
                view.getController());
        newBrickList.addMouseMotionListener(newBrickListListener);
        newBrickList.addMouseListener(newBrickListListener);
        newBrickList.addMouseWheelListener(newBrickListListener);
        newBrickList.addListSelectionListener(newBrickListListener);
        newBrickScrollList = new JScrollPane(newBrickList);
        label = new JLabel(tr("view.newBricks"));
    }

    /**
     * Shows the {@link NewBrickList}.
     */
    public void showPanel() {
        add(label);
        add(newBrickScrollList, "grow 100 100");
        actualizeView();
    }

    /**
     * Hides the {@link NewBrickList}.
     */
    public void hidePanel() {
        remove(label);
        remove(newBrickScrollList);
        actualizeView();
    }

    void actualizeView() {
        if (view.getMainRootPane() != null) {
            view.getMainRootPane().validate();
        }
        revalidate();
        repaint();
    }
}
