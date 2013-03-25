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

import de.sep2011.funckit.controller.listener.OpenPropertyDialogActionListener;
import de.sep2011.funckit.controller.listener.edit.CopyActionListener;
import de.sep2011.funckit.controller.listener.edit.CutActionListener;
import de.sep2011.funckit.controller.listener.edit.PasteActionListener;
import de.sep2011.funckit.controller.listener.edit.RedoActionListener;
import de.sep2011.funckit.controller.listener.edit.SelectAllActionListener;
import de.sep2011.funckit.controller.listener.edit.UndoActionListener;
import de.sep2011.funckit.controller.listener.editpanel.EditPanelContextMenuBrickListener;
import de.sep2011.funckit.controller.listener.project.OpenAsNewPojectActionListener;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.model.sessionmodel.NewBrickListManager;
import de.sep2011.funckit.model.sessionmodel.SessionModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.awt.Toolkit;
import java.util.List;
import java.util.Set;

import static de.sep2011.funckit.util.internationalization.Language.tr;
import static de.sep2011.funckit.model.sessionmodel.SessionModel.ViewType.VIEW_TYPE_ELEANING_SOLVE;
import static de.sep2011.funckit.model.sessionmodel.SessionModel.ViewType.VIEW_TYPE_STANDALONE;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;
import static javax.swing.KeyStroke.getKeyStroke;

public class ContextMenu extends JPopupMenu {
    private static final long serialVersionUID = 5294294952347128848L;

    private JMenuItem undoItem;
    private JMenuItem redoItem;

    private final EditPanelModel editPanelModel;
    private final View view;
    private Multimap<Object, SessionModel.ViewType> viewTypeObjectMap;

    ContextMenu(View view, EditPanelModel editPanelModel) {
        viewTypeObjectMap = HashMultimap.create();
        this.editPanelModel = editPanelModel;
        this.view = view;
        this.addPopupMenuListener(new FunckitPopupMenuListener());
    }

    /**
     * method to add the Cut functionality to the Menu.
     */
    private void addCut(View view) {
        JMenuItem cutItem = new JMenuItem(tr("menuBar.edit.cut"));
        viewTypeObjectMap.put(cutItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(cutItem, VIEW_TYPE_ELEANING_SOLVE);
        cutItem.addActionListener(new CutActionListener(view, view.getController()));
        cutItem.setAccelerator(getKeyStroke(VK_X, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        addViewTypeDependant(cutItem);
    }

    /**
     * method to add the Copy functionality to the Menu.
     */
    private void addCopy(View view) {
        JMenuItem copyItem = new JMenuItem(tr("menuBar.edit.copy"));
        copyItem.addActionListener(new CopyActionListener(view, view.getController()));
        copyItem.setAccelerator(getKeyStroke(VK_C, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        viewTypeObjectMap.put(copyItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(copyItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(copyItem);

    }

    /**
     * method to add the Paste functionality to the Menu.
     */
    private void addPaste(View view) {
        JMenuItem pasteItem = new JMenuItem(tr("menuBar.edit.paste"));
        pasteItem.addActionListener(new PasteActionListener(view, view.getController()));
        pasteItem.setAccelerator(getKeyStroke(VK_V, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        viewTypeObjectMap.put(pasteItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(pasteItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(pasteItem);

    }

    private void addCreate(final View view) {

        JMenuItem createBrickItem = new JMenu(tr("menuBar.edit.CreateBrick"));
        viewTypeObjectMap.put(createBrickItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(createBrickItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(createBrickItem);


        JMenuItem createInternalComponentItem = new JMenu(
                tr("menuBar.edit.CreateInternalComponent"));
        viewTypeObjectMap.put(createInternalComponentItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(createInternalComponentItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(createInternalComponentItem);

        JMenuItem createExternalComponentItem = new JMenu(
                tr("menuBar.edit.CreateExternalComponent"));
        viewTypeObjectMap.put(createExternalComponentItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(createExternalComponentItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(createExternalComponentItem);

        NewBrickListManager newBrickListManager = view.getSessionModel().getNewBrickListManager();

        List<Brick> brickList = newBrickListManager.getNewBrickList();

        for (final Brick b : brickList) {
            switch (newBrickListManager.getType(b)) {
            case TYPE_BRICK: {
                JMenuItem createSelectBrickItem = new JMenuItem(b.getName());

                createSelectBrickItem.addMouseListener(new EditPanelContextMenuBrickListener(view
                        .getSessionModel(), view.getController(), b));

                createBrickItem.add(createSelectBrickItem);
                break;
            }
            case TYPE_INTERNAL_COMPONENT: {
                JMenuItem createSelectInternalComponentItem = new JMenuItem(b.getName());
                createSelectInternalComponentItem
                        .addMouseListener(new EditPanelContextMenuBrickListener(view.getSessionModel(),
                                view.getController(), b));
                createInternalComponentItem.add(createSelectInternalComponentItem);
                break;
            }
            case TYPE_EXTERNAL_COMPONENT: {
                JMenuItem createSelectExternalComponentItem = new JMenuItem(b.getName());
                createSelectExternalComponentItem
                        .addMouseListener(new EditPanelContextMenuBrickListener(view.getSessionModel(),
                                view.getController(), b));
                createExternalComponentItem.add(createSelectExternalComponentItem);
                break;
            }

            }

        }

    }

    private void addUndo() {
        undoItem = new JMenuItem(tr("menuBar.edit.undo"));
        undoItem.addActionListener(new UndoActionListener(view, view.getController()));
        undoItem.setAccelerator(getKeyStroke(VK_Z, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        viewTypeObjectMap.put(undoItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(undoItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(undoItem);
    }

    private void addRedo() {
        redoItem = new JMenuItem(tr("menuBar.edit.redo"));
        redoItem.addActionListener(new RedoActionListener(view, view.getController()));
        redoItem.setAccelerator(getKeyStroke(VK_Y, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        viewTypeObjectMap.put(redoItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(redoItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(redoItem);
    }

    private void addSelectAll() {
        JMenuItem selectAllItem = new JMenuItem(tr("menuBar.edit.selectAll"));
        selectAllItem.addActionListener(new SelectAllActionListener(view, view.getController()));
        selectAllItem.setAccelerator(getKeyStroke(VK_A, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        viewTypeObjectMap.put(selectAllItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(selectAllItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(selectAllItem);
    }

    private void addProperties() {
        JMenuItem propertiesItem = new JMenuItem(tr("contextMenu.properties"));
        propertiesItem.addActionListener(new OpenPropertyDialogActionListener(view, view
                .getController()));
        viewTypeObjectMap.put(propertiesItem, VIEW_TYPE_STANDALONE);
        viewTypeObjectMap.put(propertiesItem, VIEW_TYPE_ELEANING_SOLVE);
        addViewTypeDependant(propertiesItem);
    }

    private void addOpenAsNewComponent(Component component) {
        JMenuItem openAsNewComponentItem = new JMenuItem(tr("contextMenu.openAsNewComponent"));
        openAsNewComponentItem.addActionListener(new OpenAsNewPojectActionListener(view, view
                .getController(), component.getType()));
        viewTypeObjectMap.put(openAsNewComponentItem, VIEW_TYPE_STANDALONE);
        addViewTypeDependant(openAsNewComponentItem);
    }

    private void removeAllFromContextMenu() {
        this.removeAll();
    }
    
    public void addViewTypeDependant(JMenuItem item) {
        if(viewTypeObjectMap.containsEntry(item, view.getSessionModel().getViewType())) {
            add(item);
        }
    }

    private class FunckitPopupMenuListener implements PopupMenuListener {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            removeAllFromContextMenu();

            // Is not in the simulation and has main circuit
            if (!view.getSessionModel().getCurrentProject().hasSimulation()
                    && editPanelModel.hasMainCircuit()) {

                // Nothing is selected
                if (editPanelModel.getSelectedElements().isEmpty()) {

                }

                addCreate(view);
                addSeparator();
                addUndo();
                addRedo();
                addSeparator();
                addSelectAll();

                undoItem.setEnabled(view.getSessionModel().getCurrentGraphCommandDispatcher()
                        .canStepBack());
                redoItem.setEnabled(view.getSessionModel().getCurrentGraphCommandDispatcher()
                        .canStepForward());

                boolean selElemeEmpty = editPanelModel.getSelectedElements().isEmpty();
                boolean copybuffEmpty = view.getSessionModel().getCopyBuffer().getElements()
                        .isEmpty();

                if (!selElemeEmpty || !copybuffEmpty) {
                    addSeparator();
                }

                if (!selElemeEmpty) {

                    addCut(view);
                    addCopy(view);
                }

                if (!copybuffEmpty) {
                    addPaste(view);
                }

                if (!editPanelModel.getSelectedElements().isEmpty()) {

                    // JUST ONE BRICK SELECTED
                    Set<Element> elementList = editPanelModel.getSelectedElements();
                    if (elementList.size() == 1) {
                        addSeparator();
                        Element selectedElem = elementList.iterator().next();
                        addProperties();

                        if (selectedElem instanceof Component) {
                            addOpenAsNewComponent((Component) selectedElem);
                        }
                    }
                }

                // is in the simulation
            } else {

            }

        }
        
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            removeAllFromContextMenu();
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            removeAllFromContextMenu();
        }
    }

}
