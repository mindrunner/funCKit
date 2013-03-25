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

package de.sep2011.funckit.converter.sepformat;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class InputStreamProgress extends FilterInputStream {

    private int nread = 0;
    private int size = 0;
    private final SEPFormatConverterProgressHandler progressHandler;

    /**
     * Constructs an object to monitor the progress of an input stream.
     * 
     * @param in
     *            The input stream to be monitored.
     * @param progressHandler
     *            the {@link SEPFormatConverterProgressHandler} that gets
     *            notified about the progress.
     */
    public InputStreamProgress(InputStream in,
            SEPFormatConverterProgressHandler progressHandler) {
        super(in);
        this.progressHandler = progressHandler;
        try {
            size = in.available();
        } catch (IOException ioe) {
            size = 0;
        }
        if (progressHandler != null) {
            progressHandler.handleDataSize(size);
        }
    }

    /**
     * Overrides {@code FilterInputStream.read} to update the progress
     * after the read.
     */
    @Override
    public int read() throws IOException {
        int c = in.read();
        if (c >= 0) {
            handleDataRead(++nread);
        }
        return c;
    }

    /**
     * Overrides {@code FilterInputStream.read} to update the progress
     * monitor after the read.
     */
    @Override
    public int read(byte b[], int off, int len) throws IOException {
        int nr = in.read(b, off, len);
        if (nr >= 0) {
            handleDataRead(nread += nr);
        }
        return nr;
    }

    /**
     * Overrides {@code FilterInputStream.skip} to update the progress
     * monitor after the skip.
     */
    @Override
    public long skip(long n) throws IOException {
        long nr = in.skip(n);
        if (nr > 0) {
            handleDataRead(nread += nr);
        }
        return nr;
    }

    /**
     * Overrides {@code FilterInputStream.close} to close the progress
     * monitor as well as the stream.
     */
    @Override
    public void close() throws IOException {
        in.close();
        if (progressHandler != null) {
            progressHandler.handleDataClose();
        }
    }

    /**
     * Overrides {@code FilterInputStream.reset} to reset the progress
     * monitor as well as the stream.
     */
    @Override
    public synchronized void reset() throws IOException {
        in.reset();
        nread = size - in.available();
        handleDataRead(nread);
    }

    private void handleDataRead(int amount) throws IOException {
        if (progressHandler != null) {
            progressHandler.handleDataProgress(amount);
        }
    }
}
