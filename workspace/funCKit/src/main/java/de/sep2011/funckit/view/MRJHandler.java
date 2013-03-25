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

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJQuitHandler;

import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;
import de.sep2011.funckit.controller.listener.AboutFunckitActionListener;

public class MRJHandler implements MRJAboutHandler, MRJPrefsHandler, MRJQuitHandler {
	
	private View view;
	
	public MRJHandler(View view)  {
		this.view = view;
		
		// add OSX listeners
		MRJApplicationUtils.registerAboutHandler(this);
		MRJApplicationUtils.registerQuitHandler(this);
		MRJApplicationUtils.registerPrefsHandler(this);
	}

    @Override
    public void handlePrefs() {
    }

    @Override
    public void handleQuit() {
    	view.exit();
    }

    @Override
    public void handleAbout() {
        new AboutFunckitActionListener(view, view.getController()).actionPerformed(null);
    }

}
