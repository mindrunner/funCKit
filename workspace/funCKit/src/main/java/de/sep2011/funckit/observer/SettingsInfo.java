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

package de.sep2011.funckit.observer;

import de.sep2011.funckit.model.sessionmodel.Settings;

/**
 * {@link Info} Object for notifications of the {@link Settings} class.
 */
public class SettingsInfo extends Info<SettingsInfo> {
    private String changedSetting = "";

    @Override
    public SettingsInfo getNewInstance() {
        return new SettingsInfo();
    }

    /**
     * Factory Method to get a new instance of this Info where every property is
     * false or unset.
     * 
     * @return the new instance
     */
    public static SettingsInfo getInfo() {
        return new SettingsInfo();
    }

    /**
     * Returns the value set by {@link #setChangedSetting(String)}.
     * 
     * @return the value set by {@link #setChangedSetting(String)}
     */
    public String getChangedSetting() {
        return changedSetting;
    }

    /**
     * Sets the setting key of the setting that changed.
     * 
     * @param changedSetting
     *            the key of the changed setting, not null
     * @return this for convenience
     */
    public SettingsInfo setChangedSetting(String changedSetting) {
        this.changedSetting = changedSetting;
        return this;
    }
}
