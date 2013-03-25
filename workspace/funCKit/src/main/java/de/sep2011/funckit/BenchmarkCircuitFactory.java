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

package de.sep2011.funckit;

import de.sep2011.funckit.model.graphmodel.Brick;
import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.model.graphmodel.implementations.CircuitImpl;
import de.sep2011.funckit.model.graphmodel.implementations.Light;
import de.sep2011.funckit.model.graphmodel.implementations.Not;

import java.awt.Point;

/**
 * Generates a Circuit which can be used for benchmarking as it is dynamic in
 * size but consists of always the same Circuit.
 */
class BenchmarkCircuitFactory {
    private static final int CLOCK_DELAY = 5;
    private static final int START_X = 0;
    private static final int START_Y = 0;
    private static final int DELIMITER_ROW = 200;
    private static final int DELIMITER_COLUMN = 200;
    private Circuit circuit;

    /**
     * Generate a Circuit for Benchmark.
     * 
     * @param rows
     *            how many rows of the part circuit
     * @param columns
     *            how many colnums of the part circuit
     * @return the generated circuit
     */
    Circuit generateCircuit(int rows, int columns) {
        circuit = new CircuitImpl();

        for (int i = 0; i < rows; i++) {
            addRow(new Point(START_X, START_Y + i * DELIMITER_ROW), columns);
        }
        return circuit;
    }

    private void addRow(Point p, int columns) {
        Not current = addClock(p, CLOCK_DELAY);
        for (int i = 0; i < columns; i++) {
            Not newNot = new Not(new Point(p.x + (i + 1) * DELIMITER_COLUMN, p.y
                    + Brick.DEFAULT_HEIGHT * 2));
            circuit.addBrick(newNot);
            circuit.connect(current.getOutputO(), newNot.getInputA());
            Light light = new Light(new Point(p.x + (i + 1) * DELIMITER_COLUMN, p.y));
            circuit.addBrick(light);
            circuit.connect(current.getOutputO(), light.getInputA());
            current = newNot;
        }
        Light light = new Light(new Point(p.x + (columns + 1) * DELIMITER_COLUMN, p.y
                + Brick.DEFAULT_HEIGHT * 2));
        circuit.addBrick(light);
        circuit.connect(current.getOutputO(), light.getInputA());
    }

    private Not addClock(Point p, int delay) {
        Not not = new Not(p);
        not.setDelay(delay);
        circuit.addBrick(not);
        circuit.connect(not.getOutputO(), not.getInputA());
        return not;
    }
}
