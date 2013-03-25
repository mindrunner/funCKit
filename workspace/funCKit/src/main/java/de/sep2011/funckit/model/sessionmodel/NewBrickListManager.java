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

package de.sep2011.funckit.model.sessionmodel;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.google.common.base.Preconditions;

import de.sep2011.funckit.circuitfactory.ClockFactory;
import de.sep2011.funckit.circuitfactory.DFlipFlopFactory;
import de.sep2011.funckit.circuitfactory.DLatchFactory;
import de.sep2011.funckit.circuitfactory.FullAdderFactory;
import de.sep2011.funckit.circuitfactory.HalfAdderFactory;
import de.sep2011.funckit.circuitfactory.IdentityFactory;
import de.sep2011.funckit.circuitfactory.MultiplexerFactory;
import de.sep2011.funckit.circuitfactory.NandFactory;
import de.sep2011.funckit.circuitfactory.NorFactory;
import de.sep2011.funckit.circuitfactory.RSFlipFlopFactory;
import de.sep2011.funckit.circuitfactory.ShiftRegisterFactory;
import de.sep2011.funckit.circuitfactory.XnorFactory;
import de.sep2011.funckit.circuitfactory.XorFactory;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.Component;
import de.sep2011.funckit.model.graphmodel.ComponentType;
import de.sep2011.funckit.model.graphmodel.implementations.And;
import de.sep2011.funckit.model.graphmodel.implementations.ComponentImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;
import de.sep2011.funckit.model.graphmodel.implementations.Or;
import de.sep2011.funckit.model.graphmodel.implementations.SwitchImpl;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.validator.SimulationValidatorFactory;
import de.sep2011.funckit.validator.Validator;

/**
 * Manages the contents of the List which contains Prototypes of Bricks which
 * can be copied and added to the {@link Circuit}. It also loads Bricks from the
 * components folder.
 */
public class NewBrickListManager {

    private final List<Brick> newBrickList;
    private final Map<Brick, BrickListType> listTypeMap;
    private final String externalTypePath;
    private final Map<Brick, File> brickToFileMap;
    private final SessionModel sessionModel;
    private final Map<String, ComponentType> internalNameTypeMap;

    /**
     * Defines the type of the Brick.
     */
    public enum BrickListType {
        /**
         * A normal {@link Brick}.
         */
        TYPE_BRICK,
        /**
         * A {@link Component} not loaded from a file.
         */
        TYPE_INTERNAL_COMPONENT,
        /**
         * A {@link Component} loaded from the file system.
         */
        TYPE_EXTERNAL_COMPONENT
    }

    /**
     * Creates a new {@link NewBrickListManager}.
     * 
     * @param sessionModel
     *            The {@link SessionModel} the list belongs to, not null
     * @param externalTypePath
     *            the path the TYPE_EXTERNAL_COMPONENT components are loaded
     *            from, can be null if nothing should be loaded
     * @param loadInternal
     *            Load internal Component types
     */
    public NewBrickListManager(SessionModel sessionModel, String externalTypePath,
            boolean loadInternal) {
        Preconditions.checkArgument(sessionModel != null);
        this.sessionModel = sessionModel;
        newBrickList = new ArrayList<Brick>();
        listTypeMap = new HashMap<Brick, BrickListType>();
        this.externalTypePath = externalTypePath;
        brickToFileMap = new HashMap<Brick, File>();
        internalNameTypeMap = new HashMap<String, ComponentType>();

        initPrimitiveBricks();
        initInternalNameTypeMap();
        if (loadInternal) {
            loadAllInternalTypes();
        }
        initExternalTypes();
    }

    /**
     * Returns the path the TYPE_EXTERNAL_COMPONENT components are loaded from.
     * 
     * @return the path the TYPE_EXTERNAL_COMPONENT components are loaded from.
     */
    public String getExternalTypePath() {
        return externalTypePath;
    }
    
    private void initInternalNameTypeMap() {
        internalNameTypeMap.put("identity", new IdentityFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("clock", new ClockFactory(1).getComponentTypeForCircuit());
        internalNameTypeMap.put("shiftregister",new ShiftRegisterFactory(5).getComponentTypeForCircuit() );
        internalNameTypeMap.put("halfadder", new HalfAdderFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("fulladder", new FullAdderFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("dflipflop", new DFlipFlopFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("dlatch", new DLatchFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("rsflipflop", new RSFlipFlopFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("nand", new NandFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("nor", new NorFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("xor", new XorFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("xnor", new XnorFactory().getComponentTypeForCircuit());
        internalNameTypeMap.put("mux", new MultiplexerFactory().getComponentTypeForCircuit());
    }

    /**
     * Adds a Brick to the List.
     * 
     * @param b
     *            the Brick to add.
     * @param t
     *            the {@link BrickListType} the brick has.
     */
    public void add(Brick b, BrickListType t) {
        newBrickList.add(b);
        listTypeMap.put(b, t);
    }

    private void initPrimitiveBricks() {
        Brick and = new And(new Point());
        and.setName("And");
        Brick or = new Or(new Point());
        or.setName("Or");
        Brick not = new Not(new Point());
        not.setName("Not");
        Brick switchImpl = new SwitchImpl(new Point());
        switchImpl.setName("Switch");
        Brick light = new Light(new Point());
        light.setName("Light");

        add(and, BrickListType.TYPE_BRICK);
        add(or, BrickListType.TYPE_BRICK);
        add(not, BrickListType.TYPE_BRICK);
        add(switchImpl, BrickListType.TYPE_BRICK);
        add(light, BrickListType.TYPE_BRICK);
    }

    private void initExternalTypes() {        
        loadExternalTypes();
    }

    private void removeAllExternalBrickTypes() {
        List<Integer> removed = new LinkedList<Integer>();

        for (ListIterator<Brick> it = newBrickList.listIterator(); it.hasNext();) {
            int index = it.nextIndex();
            Brick brick = it.next();
            if (listTypeMap.get(brick) == BrickListType.TYPE_EXTERNAL_COMPONENT) {
                removed.add(index);
                it.remove();
                listTypeMap.remove(brick);
            }
        }

        brickToFileMap.clear();

        int[] idxes = new int[removed.size()];
        for (ListIterator<Integer> it = removed.listIterator(); it.hasNext();) {
            idxes[it.nextIndex()] = it.next();
        }

        sessionModel.getInfo().setNewBrickListChanged(true);
        sessionModel.getInfo().setNewBrickListRemovedIndices(idxes);
        sessionModel.notifyObservers();

    }

    /**
     * Loads the external types from {@link #getExternalTypePath()}. It removes
     * all Bricks with TYPE_EXTERNAL_COMPONENT first.
     */
    public void loadExternalTypes() {
        if(externalTypePath == null) {
            return;
        }
        
        String[] files = new File(externalTypePath).list();

        Log.gl().debug("Files in component type folder: " + Arrays.toString(files));

        removeAllExternalBrickTypes();
        int oldStartIdx = newBrickList.size() - 1;

        if (files == null) {
            return;
        }

        for (String filePath : files) {
            SEPFormatConverter conv = new SEPFormatConverter("", Mode.FUNCKITFORMAT);

            File file = new File(externalTypePath + File.separator + filePath);
            try {
                ComponentType t = conv.importComponentType(new FileInputStream(file));
                Validator simulationValidator = new SimulationValidatorFactory().getValidator();
                simulationValidator.validate(t.getCircuit());
                if (simulationValidator.allPassed()) {
                    Component component = new ComponentImpl(t, new Point(), t.getName());
                    add(component, BrickListType.TYPE_EXTERNAL_COMPONENT);
                    brickToFileMap.put(component, file);
                }
            } catch (SEPFormatImportException e) {
                Log.gl().debug(e.toString());
            } catch (FileNotFoundException e) {
                Log.gl().debug(e.toString());
            }

        }

        int[] idxes = new int[newBrickList.size() - oldStartIdx];
        for (int i = oldStartIdx; i < newBrickList.size(); i++) {
            idxes[i - oldStartIdx] = i;
        }

        sessionModel.getInfo().setNewBrickListChanged(true);
        sessionModel.getInfo().setNewBrickListAddedIndices(idxes);
        sessionModel.notifyObservers();

    }

    private void loadAllInternalTypes() {
        for (ComponentType t : internalNameTypeMap.values()) {
            add(new ComponentImpl(t, new Point(), t.getName()),
                    BrickListType.TYPE_INTERNAL_COMPONENT);
        }
    }
    
    public void addInternalType(ComponentType type) {
        add(new ComponentImpl(type, new Point(), type.getName()),
                BrickListType.TYPE_INTERNAL_COMPONENT);
        
        int[] idxes = new int[1];
        idxes[0] = newBrickList.size() - 1;

        sessionModel.getInfo().setNewBrickListChanged(true);
        sessionModel.getInfo().setNewBrickListAddedIndices(idxes);
        sessionModel.notifyObservers();

    }

    public void loadPredefinedInternalType(String name) {
        ComponentType t = internalNameTypeMap.get(name);
        if (t != null) {
            add(new ComponentImpl(t, new Point(), t.getName()),
                    BrickListType.TYPE_INTERNAL_COMPONENT);
        }
        
        int[] idxes = new int[1];
        idxes[0] = newBrickList.size() - 1;

        sessionModel.getInfo().setNewBrickListChanged(true);
        sessionModel.getInfo().setNewBrickListAddedIndices(idxes);
        sessionModel.notifyObservers();
    }

    /**
     * Returns the List of "new" Bricks.
     * 
     * @return the List of "new" Bricks, do not modify it directly.
     */
    public List<Brick> getNewBrickList() {
        return newBrickList;
    }

    /**
     * For a Brick in the new brick list get its {@link BrickListType}.
     * 
     * @param b
     *            the brick to lookup
     * @return the corresponding type
     */
    public BrickListType getType(Brick b) {
        BrickListType t = listTypeMap.get(b);
        if (t == null) {
            throw new IllegalArgumentException("Not in Map");
        }

        return t;
    }

    /**
     * Returns the {@link BrickListType} for an Brick at the given index.
     * 
     * @param index
     *            index of the Brick to get the type for.
     * @return the corresponding type
     */
    public BrickListType getType(int index) {
        return getType(newBrickList.get(index));
    }

    /**
     * Removes a brick if it is a TYPE_EXTERNAL_COMPONENT and removes the
     * corresponding file from the file system.
     * 
     * @param brick
     *            the brick to remove.
     */
    public void removeExternalBrickWithFile(Brick brick) {
        if (brickToFileMap.containsKey(brick)) {
            brickToFileMap.remove(brick).delete();

            int[] removedIdx = { newBrickList.indexOf(brick) };
            newBrickList.remove(removedIdx[0]);

            sessionModel.getInfo().setNewBrickListChanged(true);
            sessionModel.getInfo().setNewBrickListRemovedIndices(removedIdx);
            sessionModel.notifyObservers();
        }
    }
}
