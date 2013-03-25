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

import de.sep2011.funckit.converter.sepformat.SEPFormatConverter;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverter.Mode;
import de.sep2011.funckit.converter.sepformat.SEPFormatConverterProgressHandler;
import de.sep2011.funckit.converter.sepformat.SEPFormatExportException;
import de.sep2011.funckit.converter.sepformat.SEPFormatImportException;
import de.sep2011.funckit.model.graphmodel.Brick.Orientation;
import de.sep2011.funckit.model.sessionmodel.Settings;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.view.View;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

public class ImportWorker extends SwingWorker<Object, Integer> implements SEPFormatConverterProgressHandler {

    private static final int DATA_PROGRESS_PRECENTAGE = 80;

    private View view;
    private InputStream inputStream;
    private SEPFormatConverter converter;
    private int elementsCount;
    private int elementsImported;
    private int dataSize;
    private int ret;
    private String warningMessage;
    private Object[] warningArgs;
    private WorkerMode mode;

    public enum WorkerMode {CIRCUIT, COMPONENTTYPE}

    public ImportWorker(View view, InputStream inputStream, WorkerMode mode) {
        assert inputStream != null;
        assert view != null;
        assert mode != null;
        this.inputStream = inputStream;
        this.view = view;
        this.mode = mode;
        converter = new SEPFormatConverter("", Mode.FUNCKITFORMAT);
        converter.setProgressHandler(this);
        elementsImported = 0;
        elementsCount = 0;
        
        Settings settings = view.getSessionModel().getSettings();
		if (settings != null) {
			converter.setSettings(settings
					.getInt(Settings.DEFAULT_BRICK_HEIGHT), settings
					.getInt(Settings.DEFAULT_BRICK_WIDTH), settings.get(
					Settings.DEFAULT_BRICK_ORIENTATION, Orientation.class));
		}
    }

    @Override
    protected Object doInBackground() throws Exception {
        return mode == WorkerMode.CIRCUIT ? converter.doImport(inputStream) : converter.importComponentType(inputStream);
    }

    @Override
    protected void done() {
        super.done();
        if (isCancelled()) {
            view.hideProgress();
            view.setStatusText(Language.tr("status.canceled"));
            firePropertyChange("canceled", false, true);
            return;
        }
        Object result;
        try {
            result = get();
            view.hideProgress();
            view.setStatusText(Language.tr("status.finished"));
        } catch (InterruptedException e) {
            e.printStackTrace();
            view.hideProgress();
            view.setStatusText(Language.tr("status.finished"));
            firePropertyChange("interrupted", false, true);
            return;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof SEPFormatImportException)) {
                e.printStackTrace();
            }
            view.hideProgress();
            view.setStatusText(Language.tr("status.finished"));
            if (cause != SEPFormatImportException.CANCELED) {
                view.showErrorMessage(Language.tr("loadError.title.SEPFormatImportException"), cause.getLocalizedMessage());
                firePropertyChange("error", false, true);
            } else {
                firePropertyChange("canceled", false, true);
            }
            return;
        }

        firePropertyChange("result", null, result);
        firePropertyChange("converted", null, converter);
    }

    @Override
    public void handleImporterWarning(String message, Object... args)
            throws SEPFormatImportException {
        warningMessage = message;
        warningArgs = args;
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    ret = view.showWarningOptionDialog(Language.tr("loadWarning.title"), Language.tr(warningMessage, warningArgs));
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.gl().error(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.gl().error(e);
        }
        if (ret == JOptionPane.CANCEL_OPTION) {
            throw SEPFormatImportException.CANCELED;
        }
    }

    @Override
    public void handleExporterWarning(String message, Object... args)
            throws SEPFormatExportException {
    }

    @Override
    public void handleCircuitImported() throws SEPFormatImportException {
        checkForCancel();
    }

    @Override
    public void handleCircuitsToImport(int circuitsCount) {
    }

    @Override
    public void handleComponentImported() throws SEPFormatImportException {
        elementsImported++;
        setProgress(DATA_PROGRESS_PRECENTAGE + elementsImported
                * (100 - DATA_PROGRESS_PRECENTAGE) / elementsCount);
        checkForCancel();
    }

    @Override
    public void handleComponentsToImport(int componentsCount) {
    	Log.gl().debug("Starting to load file with " + componentsCount + "components");
        elementsCount += componentsCount;
    }

    @Override
    public void handleConnectionImported() throws SEPFormatImportException {
        elementsImported++;
        setProgress(DATA_PROGRESS_PRECENTAGE + elementsImported
                * (100 - DATA_PROGRESS_PRECENTAGE) / elementsCount);
        checkForCancel();
    }

    @Override
    public void handleConnectionsToImport(int connectionsCount) {
    	Log.gl().debug("Starting to load file with " + connectionsCount + "connections");
        elementsCount += connectionsCount;
    }

    @Override
    public void handleDataProgress(int amount) throws IOException {
        if (dataSize != 0) {
            int value = amount * DATA_PROGRESS_PRECENTAGE / dataSize;
            if (value < 0 || value > DATA_PROGRESS_PRECENTAGE) {
                value = DATA_PROGRESS_PRECENTAGE;
            }
            setProgress(value);
        }
        if (view.isProgressCanceled()) {
            if (!cancel(true)) {
                throw new IOException(Language.tr("SEPFormatImportException.canceledByUser"));
            }
        }
    }

    @Override
    public void handleDataClose() {
        firePropertyChange("dataClose", false, true);
    }

    @Override
    public void handleDataSize(int size) {
        dataSize = size;
    }

    private void checkForCancel() throws SEPFormatImportException {
        if (view.isProgressCanceled()) {
            if (!cancel(true)) {
                throw SEPFormatImportException.CANCELED;
            }
        }
    }
}
