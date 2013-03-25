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

package de.sep2011.funckit.validator;

import de.sep2011.funckit.model.graphmodel.Circuit;
import de.sep2011.funckit.util.internationalization.Language;

/**
 * Check if a {@link Circuit} has loops with a delay of 0.
 */
public class ZeroDelayLoopCheck implements Check {

    private final LoopCheck loopCheck;
    
    public ZeroDelayLoopCheck() {
        loopCheck = new LoopCheck(true);
    }

    /**
     * {@inheritDoc} In this case look if the {@link Circuit} has loops with a
     * delay of 0 by performing a depth-search on the flat {@link Circuit}.
     */
    @Override
    public Result perform(Circuit c) {
        Result loopCheckResult = loopCheck.perform(c);

        // construct Result object
        String message = "check." + this.getClass().getSimpleName();
        if (loopCheckResult.isPassed()) {
            message += ".passedMessage";
        } else {
            message += ".failedMessage";
        }
        return new Result(loopCheckResult.isPassed(), Language.tr(message),
                loopCheckResult.getFlawElements(), this);
    }

    @Override
    public String getName() {
        return Language.tr("Check." + this.getClass().getSimpleName());
    }

}
