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

/**
 * Represents state of a certain element. This state is build by {@link
 * ElementStateResolver} upon information collected from all over the
 * application.
 */
public class ElementState implements Cloneable {
    /* Specifies if element should be drawn very simple. */
    private boolean simple;

    public enum Mode {
        NORMAL, SELECTED, GHOST
    }

    private Mode mode;
    private boolean hasInfo;
    private boolean hasError;
    private boolean isSimulated;
    private boolean active;

    public ElementState() {
        mode = Mode.NORMAL;
    }

    /**
     * Returns a copy of this element state.
     *
     * @return Copy of this element state.
     */
    @Override
    public ElementState clone() {
        // TODO make via reflection? .. :P
        ElementState clone = new ElementState();
        clone.mode = mode;
        clone.hasInfo = hasInfo;
        clone.hasError = hasError;
        clone.isSimulated = isSimulated;
        clone.active = active;
        clone.simple = simple;

        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        // TODO do via reflection :P
        if (other instanceof ElementState) {
            ElementState state = (ElementState) other;
            return state.mode.equals(this.mode)
                    && state.hasInfo == this.hasInfo
                    && state.hasError == this.hasError
                    && state.isSimulated == this.isSimulated
                    && state.active == this.active
                    && state.simple == this.simple;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // TODO do via reflection :P
        int code = 1;
        code *= 13 * mode.hashCode();
        code *= hasInfo ? 29 : 1;
        code *= hasError ? 53 : 1;
        code *= isSimulated ? 67 : 1;
        code *= active ? 9 : 1;
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementState: ");
        builder.append("mode: ").append(mode);
        builder.append(", hasInfo: ").append(hasInfo);
        builder.append(", hasError: ").append(hasError);
        builder.append(", isSimulated: ");
        builder.append(isSimulated);
        builder.append(", active: ");
        builder.append(active);
        builder.append(", simple: ");
        builder.append(simple);
        return builder.toString();
    }

    /**
     * Getter method for mode of element.
     *
     * @return {@link ElementState.Mode}
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Specifies mode of this {@link ElementState}.
     *
     * @param mode New element {@link ElementState.Mode}
     * @return This object to perform method invocation chain.
     */
    public ElementState setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Flag, that specifies if element has informational data.
     *
     * @return True, iff element has info.
     */
    public boolean hasInfo() {
        return hasInfo;
    }

    /**
     * Specifies if element has informational data.
     *
     * @return True, iff element has info.
     */
    public ElementState setHasInfo() {
        this.hasInfo = true;
        return this;
    }

    /**
     * Flag, that specifies if element has an error.
     *
     * @return Error, iff element has error.
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * Specifies flag, if associated {@link Element} of this {@link
     * ElementState} has error.
     *
     * @param hasError Specifying flag.
     * @return This object to perform method invocation chain.
     */
    public ElementState setHasError(boolean hasError) {
        this.hasError = hasError;
        return this;
    }

    /**
     * Flag, that specifies if associated {@link Element} of this {@link
     * ElementState} is simulated (e.g. in case of an {@link Wire} meaning
     * logic-1).
     *
     * @return True, iff simulated-1.
     */
    public boolean isSimulated() {
        return isSimulated;
    }

    /**
     * Specifies, if associated {@link Element} of this {@link ElementState}
     * could be simulated.
     *
     * @param simulated True, if currently simulating..
     * @return This object to perform method invocation chain.
     */
    public ElementState setSimulated(boolean simulated) {
        isSimulated = simulated;
        return this;
    }

    /**
     * Flag, that specifies if associated {@link Element} of this {@link
     * ElementState} is active (e.g. user moves mouse over element).
     *
     * @return True, iff element is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Specifies, that element is active.
     *
     * @return This object to perform method invocation chain.
     */
    public ElementState setActive() {
        this.active = true;
        return this;
    }

    /**
     * Specifies, if associated {@link Element} of this {@link ElementState} is
     * in a simple state (e.g. zoomed and thus very small).
     *
     * @return This object to perform method invocation chain.
     */
    public ElementState setSimple() {
        this.simple = true;
        return this;
    }

    /**
     * Flag, that specifies if associated {@link Element} of this {@link
     * ElementState} is simple ({@link ElementState#setSimple()}.
     *
     * @return Flag, if it is simple indeed.
     */
    public boolean isSimple() {
        return simple;
    }
}
