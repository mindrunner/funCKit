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

/**
 * Store a Pair of values, both values can be set freely (and can be null).
 * 
 * @param <T>
 *            The Type of the left value
 * @param <V>
 *            The type of the right value
 */
public class Pair<T, V> {

    private T left;
    private V right;

    /**
     * Creates a new Pair.
     * 
     * @param left
     *            the left value
     * @param right
     *            the right value
     */
    public Pair(T left, V right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Get the left value.
     * 
     * @return the left value
     */
    public T getLeft() {
        return this.left;
    }

    /**
     * Get the right value.
     * 
     * @return the right value
     */
    public V getRight() {
        return this.right;
    }

    /**
     * Set the left value of the Pair.
     * 
     * @param left
     *            the left value
     */
    public void setLeft(T left) {
        this.left = left;
    }

    /**
     * Set the right value.
     * 
     * @param right
     *            the right value
     */
    public void setRight(V right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object object) {
        boolean leftBool = false;
        boolean rightBool = false;

        if (object instanceof Pair) {
            Pair<?, ?> other = (Pair<?, ?>) object;
            if ((left == null) && (other.left == null)) {
                leftBool = true;
            } else if (left != null) {
                leftBool = left.equals(other.left);
            }

            if ((right == null) && (other.right == null)) {
                rightBool = true;
            } else if (right != null) {
                rightBool = right.equals(other.right);
            }
        }

        return (leftBool && rightBool);
    }

    @Override
    public String toString() {
        return "Pair [left=" + left + ", right=" + right + "]";
    }

    @Override
    public int hashCode() {
        int rightHash = 0;
        int leftHash = 0;

        if (left != null) {
            leftHash = left.hashCode();
        }

        if (right != null) {
            rightHash = right.hashCode();
        }

        return (rightHash + leftHash);
    }

}