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

package de.sep2011.funckit.view.filechooser;

import de.sep2011.funckit.Application;
import de.sep2011.funckit.Application.OperatingSystem;
import de.sep2011.funckit.util.Log;
import de.sep2011.funckit.util.internationalization.Language;
import de.sep2011.funckit.view.View;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.awt.FileDialog;
import java.io.File;

/**
 * The filechooser used for this project. It wraps the JFileChooser and the FileDialog
 * using the one that better fits the environment the program is started.
 */
public class FunckitFileChooser {

	/**
	 * The Swing JFileChooser is used as default because it has more features.
	 */
    private JFileChooser fileChooser;
    
    /**
     * The old AWT FileDialog is used to get native file choosers on OSX and WIN.
     */
    private FileDialog fileDialog;
    
    /**
     * Filter for the funCKit format.
     */
    private final AbstractFileFilter fckFormat = new FunckitFileFilter();
    
    /**
     * Filter for the SEP format.
     */
    private final AbstractFileFilter sepFormat = new SepFileFilter();
    
    /**
     * Filter for the GIF format.
     */
    private final AbstractFileFilter gifFormat = new GifFileFilter();
    
    /**
     * Filter for the JPG format.
     */
    private final AbstractFileFilter jpgFormat = new JpgFileFilter();
    
    /**
     * Filter for the PDF format.
     */
    private AbstractFileFilter pdfFormat;
    
    /**
     * Filter for the PNG format.
     */
    private final AbstractFileFilter pngFormat = new PngFileFilter();
    
    /**
     * Filter for the SVG format.
     */
    private AbstractFileFilter svgFormat;
    
    /**
     * Filter for the cmp format (used for ComponentTypes).
     */
    private final AbstractFileFilter cmpFormat = new ComponentFileFilter();
    
    /**
     * The {@link View} used by this filechooser.
     */
    private View view;
    
    /**
     * Use the AWT FileDialog (which is native on some platforms)?
     */
    private final boolean nativeDialog;

    private enum Mode {
        LOAD, SAVE, SAVE_AS, SAVE_COMPONENT
    }

    /**
     * Enum for the various file formats.
     */
    public enum FileFormat {
    	/**
    	 * FCK: funCKit format, SEP: SEP exchange format, CMP: ComponentType format.
    	 */
        FCK, SEP, GIF, JPG, PDF, PNG, SVG, CMP, NONE
    }

    /**
     * Creates a new file chooser for the given {@link View}.
     * @param view the {@link View} to create the file chooser for.
     */
    public FunckitFileChooser(View view) {
        /*
         * only use PDF and SVG if Exporter Classes are available
         */
        try {
            Class.forName("de.sep2011.funckit.converter.PDFExporter");
            pdfFormat = new PdfFileFilter();

        } catch (ClassNotFoundException e) {
            Log.gl().info("PDFExporter not availible");
        }
        try {
            Class.forName("de.sep2011.funckit.converter.SVGExporter");
            svgFormat = new SvgFileFilter();
        } catch (ClassNotFoundException e) {
            Log.gl().info("SVGExporter not availible");
        }
        
        assert view != null;
        this.view = view;
        nativeDialog = Application.OS == OperatingSystem.OSX
                || Application.OS == OperatingSystem.WIN;
        if (nativeDialog) {
            fileDialog = new FileDialog(view.getMainFrame());
        } else {
            fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
    }

    private File[] openDialog(Mode mode) {
        return openDialog(mode, view.getSessionModel().getCurrentProjectPath());
    }

    private File[] openDialog(Mode mode, String path) {

        if (!nativeDialog) {
            fileChooser.resetChoosableFileFilters();
        }

        if (path != null && !path.equals("")) {
            File dir = new File(path);
            if (nativeDialog) {
                fileDialog.setDirectory(path);
            } else {
                fileChooser.setCurrentDirectory(dir);
            }
        }

        String title = "";
        switch (mode) {
        case LOAD: {
            title = "FileDialog.open";
            if (nativeDialog) {
                fileDialog.setFilenameFilter(new LoadFilenameFilter());
            } else {
                fileChooser.addChoosableFileFilter(fckFormat);
                fileChooser.addChoosableFileFilter(cmpFormat);
                fileChooser.addChoosableFileFilter(sepFormat);
                fileChooser.setFileFilter(fckFormat);
                fileChooser.setMultiSelectionEnabled(true);
            }
            break;
        }
        case SAVE: {
            title = "FileDialog.save";
            if (nativeDialog) {
                fileDialog.setFilenameFilter(new SaveFilenameFilter());
            } else {
                fileChooser.addChoosableFileFilter(fckFormat);
                fileChooser.addChoosableFileFilter(sepFormat);
                fileChooser.addChoosableFileFilter(gifFormat);
                fileChooser.addChoosableFileFilter(pdfFormat);
                fileChooser.addChoosableFileFilter(pngFormat);
                fileChooser.addChoosableFileFilter(svgFormat);
                fileChooser.addChoosableFileFilter(jpgFormat);
                fileChooser.setFileFilter(fckFormat);
            }
            break;
        }
        case SAVE_AS: {
            title = "FileDilaog.saveas";
            if (nativeDialog) {
                fileDialog.setFilenameFilter(new SaveFilenameFilter());
            } else {
                fileChooser.addChoosableFileFilter(fckFormat);
                fileChooser.addChoosableFileFilter(sepFormat);
                fileChooser.addChoosableFileFilter(gifFormat);
                fileChooser.addChoosableFileFilter(pdfFormat);
                fileChooser.addChoosableFileFilter(pngFormat);
                fileChooser.addChoosableFileFilter(svgFormat);
                fileChooser.addChoosableFileFilter(jpgFormat);
                fileChooser.setFileFilter(fckFormat);
            }
            break;
        }
        case SAVE_COMPONENT: {
            title = "FileDialog.saveComponent";
            if (nativeDialog) {
                fileDialog.setFilenameFilter(new ComponentFileFilter());
            } else {
                fileChooser.addChoosableFileFilter(cmpFormat);
            }
            break;
        }
        }

        if (nativeDialog) {
            fileDialog.setMode(mode == Mode.LOAD ? FileDialog.LOAD
                    : FileDialog.SAVE);
            fileDialog.setTitle(Language.tr(title));
            fileDialog.setVisible(true);
            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            if (fileName == null) {
                return null;
            }
            File[] files = new File[1];
            files[0] = new File(directory, fileName);
            return files;
        }

        fileChooser.setDialogTitle(Language.tr(title));
        int ret;
        if (mode == Mode.LOAD) {
            ret = fileChooser.showOpenDialog(view.getMainRootPane());
        } else {
            ret = fileChooser.showSaveDialog(view.getMainRootPane());
        }
        if (ret == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFiles().length == 0) {
                File[] files = new File[1];
                files[0] = fileChooser.getSelectedFile();
                return files;
            }
            return fileChooser.getSelectedFiles();
        }
        return null;
    }

    /**
     * Opens the open file dialog and returns the selected files.
     * @return Array of all selected files.
     */
    public File[] openFile() {
        return openDialog(Mode.LOAD);
    }

    private File saveDialog(Mode dialogMode) {
        return saveDialog(dialogMode, null);
    }

    private File saveDialog(Mode dialogMode, String path) {
        File[] files = (path != null) ? openDialog(dialogMode, path)
                : openDialog(dialogMode);
        if (files == null || files.length == 0) {
            return null;
        }

        // add right extension if missing
        FileFormat format = getSelectedFileFormat(files[0]);
        if (!nativeDialog || format == FileFormat.NONE
                || format == FileFormat.FCK) {
            AbstractFileFilter filter = getSelectedFilter();
            if (!filter.accept(files[0])) {
                files[0] = new File(files[0].getAbsolutePath() + "."
                        + filter.getMainExtension());
            }
        }

        // prompt for overwrite
        if (files[0].exists()) {
            int ret = JOptionPane.showOptionDialog(view.getMainRootPane(),
                    "FileDialog.OverwriteDialog.text",
                    "FileDialog.OverwriteDialog.title",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
            if (ret == JOptionPane.NO_OPTION) {
                return null;
            }
        }

        return files[0];
    }

    /**
     * Opens the save file dialog and returns the selected file.
     * @return the selected file.
     */
    public File saveFile() {
        return saveDialog(Mode.SAVE);
    }

    /**
     * Opens the save file as dialog and returns the selected file.
     * @return the selected file.
     */
    public File saveFileAs() {
        return saveDialog(Mode.SAVE_AS);
    }

    /**
     * Opens the save component dialog and returns the selected file.
     * The start directory is the one used to store the components in
     * the NewBrickList. 
     * @return the selected file.
     */
    public File saveComponent() {
        return saveDialog(Mode.SAVE_COMPONENT, view.getSessionModel()
                .getNewBrickListManager().getExternalTypePath());
    }

    private AbstractFileFilter getSelectedFilter() {
        if (nativeDialog) {
            return fckFormat; // default to funckit, because you cannot select one
        }
        return (AbstractFileFilter) fileChooser.getFileFilter();
    }

    /**
     * Checks what {@link FileFormat} was selected. In native mode
     * this is done by checking what file extension matches the given file.
     * @param file the file of what the extension is checked in native mode.
     * @return the selected {@link FileFormat}
     */
    public FileFormat getSelectedFileFormat(File file) {
        if (nativeDialog) {
            if (fckFormat.accept(file))
                return FileFormat.FCK;
            if (sepFormat.accept(file))
                return FileFormat.SEP;
            if (gifFormat.accept(file))
                return FileFormat.GIF;
            if (jpgFormat.accept(file))
                return FileFormat.JPG;
            if (pdfFormat.accept(file))
                return FileFormat.PDF;
            if (svgFormat.accept(file))
                return FileFormat.SVG;
            if (pngFormat.accept(file))
                return FileFormat.PNG;
            if (cmpFormat.accept(file))
                return FileFormat.CMP;
        } else {
            if (fileChooser.getFileFilter() == fckFormat)
                return FileFormat.FCK;
            if (fileChooser.getFileFilter() == sepFormat)
                return FileFormat.SEP;
            if (fileChooser.getFileFilter() == gifFormat)
                return FileFormat.GIF;
            if (fileChooser.getFileFilter() == jpgFormat)
                return FileFormat.JPG;
            if (fileChooser.getFileFilter() == pdfFormat)
                return FileFormat.PDF;
            if (fileChooser.getFileFilter() == svgFormat)
                return FileFormat.SVG;
            if (fileChooser.getFileFilter() == pngFormat)
                return FileFormat.PNG;
            if (fileChooser.getFileFilter() == cmpFormat)
                return FileFormat.CMP;
        }
        return FileFormat.NONE;
    }
}
