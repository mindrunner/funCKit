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

package de.sep2011.funckit.controller;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Set;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Element;
import de.sep2011.funckit.model.graphmodel.Input;
import de.sep2011.funckit.model.graphmodel.Output;
import de.sep2011.funckit.model.sessionmodel.EditPanelModel;
import de.sep2011.funckit.util.GraphmodelUtil;
import de.sep2011.funckit.util.Pair;

/**
 * This {@link Tool} connects two {@link Brick}s by connecting multiple {@link Input}s
 * with multiple {@link Output}s.
 */
public class MultiConnectTool extends AbstractTool {
    
    /**
     * Offset to use on the Brick with less AccessPoints from the top.
     */
    private int offset = 0;

    public MultiConnectTool(Controller controller) {
        this.controller = controller;
    }

    @Override
    public MultiConnectTool getNewInstance(Controller c) {
        return new MultiConnectTool(c);
    }

    @Override
    public void mouseClicked(MouseEvent e, EditPanelModel editPanelModel) {
        super.mouseClicked(e, editPanelModel);
        Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());

        switch (editPanelModel.getToolMode()) {
        case DEFAULT_MODE:
        	validateEditPanelModel(editPanelModel);
        	Set<Element> selected = editPanelModel.getSelectedElements();
        	selected.clear();
        	Brick firstBrick = editPanelModel.getMultiConnectBrick1();
        	
        	if (firstBrick == null) {
        		firstBrick = editPanelModel.getCircuit().getBrickAtPosition(click);
        		editPanelModel.setMultiConnectBrick1(firstBrick);
        		if (firstBrick != null) {
        			selected.add(firstBrick);
        		}
        	} else {
        		Brick secondBrick = editPanelModel.getCircuit().getBrickAtPosition(click);
				GraphmodelUtil.connectBricks(editPanelModel.getCircuit(), controller.getSessionModel()
								.getCurrentGraphCommandDispatcher(), firstBrick, secondBrick,
								isPlatformCtrlOrBlumenkohlDown(e), offset);
				editPanelModel.setMultiConnectBrick1(null);
				editPanelModel.setMultiConnectBrick2(null);
        	}
        	editPanelModel.setSelectedElements(selected);
            break;

        default:
            break;
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e, EditPanelModel editPanelModel) {
    	super.mouseMoved(e, editPanelModel);
    	validateEditPanelModel(editPanelModel);
    	Point click = calculateInversePoint(e.getPoint(), editPanelModel.getTransformation());
    	Brick brickUnderMouse = editPanelModel.getCircuit().getBrickAtPosition(click);
    	if (brickUnderMouse != editPanelModel.getMultiConnectBrick2()) {
    		editPanelModel.setMultiConnectBrick2(brickUnderMouse);
    		offset = 0;
    		updateGhosts(e, editPanelModel);
    	}
    }
    
    @Override
    public void cancelCurrentAction(EditPanelModel editPanelModel) {
    	editPanelModel.setMultiConnectBrick1(null);
    	editPanelModel.setMultiConnectBrick2(null);
    	clearGhosts(editPanelModel);
    	Set<Element> selected = editPanelModel.getSelectedElements();
    	selected.clear();
    	editPanelModel.setSelectedElements(selected);
    }

	@Override
	public void keyPressed(KeyEvent e, EditPanelModel editPanelModel) {
		super.keyPressed(e, editPanelModel);
    	validateEditPanelModel(editPanelModel);
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			offset++;
			e.consume();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (offset > 0) {
				offset--;
			}
			e.consume();
		}
		updateGhosts(e, editPanelModel);
	}

	@Override
	public void keyReleased(KeyEvent e, EditPanelModel editPanelModel) {
		super.keyReleased(e, editPanelModel);
    	validateEditPanelModel(editPanelModel);
		updateGhosts(e, editPanelModel);
	}
	
	private void updateGhosts(InputEvent e, EditPanelModel editPanelModel) {
		Brick firstBrick = editPanelModel.getMultiConnectBrick1();
		Brick secondBrick = editPanelModel.getMultiConnectBrick2();
		if (firstBrick != null && secondBrick != null) {
			Pair<Integer, Set<Element>> offsetGhostsPair = GraphmodelUtil.createWiresforBricks(firstBrick, secondBrick,
					isPlatformCtrlOrBlumenkohlDown(e), offset);
			offset = offsetGhostsPair.getLeft();
			editPanelModel.setGhosts(offsetGhostsPair.getRight());
		} else {
			clearGhosts(editPanelModel);
		}
	}
	
	private void validateEditPanelModel(EditPanelModel editPanelModel) {
		if (!editPanelModel.getCircuit().getElements().contains(editPanelModel.getMultiConnectBrick1())) {
    		editPanelModel.setMultiConnectBrick1(null);
    	}
		if (!editPanelModel.getCircuit().getElements().contains(editPanelModel.getMultiConnectBrick2())) {
    		editPanelModel.setMultiConnectBrick2(null);
    	}
	}
}
