package org.esa.beam.processor.binning.store;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.esa.beam.processor.binning.database.Bin;
import org.esa.beam.util.Guardian;

/**
 * Created by IntelliJ IDEA.
 * User: marcoz
 * Date: 15.07.2005
 * Time: 15:02:59
 * To change this template use File | Settings | File Templates.
 */
public class QuadTreeBinStore implements BinStore {
    // the maximum tile size (width and height) in bins
    private static final int TILE_SIZE = 64;

    private QuadTreeFile qtFile;

    private float[] tempBinContent;

    /**
     * Creates a new QuadTreeBinStore.
     *
     * @param dbDir
     * @param dbName
     * @param width
     * @param height
     * @param numVarsPerBin
     * @throws IOException
     */
    public QuadTreeBinStore(File dbDir, String dbName, int width, int height, int numVarsPerBin) throws IOException {
        final int numBuffer = width / TILE_SIZE + 1;

        qtFile = new QuadTreeFile();
        File quadTreeDbDir = new File(dbDir, dbName);
        qtFile.create(quadTreeDbDir, width, height, TILE_SIZE, numBuffer, numVarsPerBin);
        qtFile.open(quadTreeDbDir);

        tempBinContent = new float[numVarsPerBin];
    }

    /**
     * Opens an existing QuadTreeBinStore.
     *
     * @param dbDir
     * @param dbName
     * @throws IOException
     */
    public QuadTreeBinStore(File dbDir, String dbName) throws  IOException {
        File quadTreeDbDir = new File(dbDir, dbName);
        qtFile = new QuadTreeFile();
        checkLocation(quadTreeDbDir);

        qtFile.open(quadTreeDbDir);

        tempBinContent = new float[qtFile.getNumberOfVariables()];
    }

    public void write(Point rowCol, Bin bin) throws IOException {
        bin.save(tempBinContent);
        qtFile.write(rowCol, tempBinContent);
    }

    public void read(Point rowCol, Bin bin) throws IOException {
        qtFile.read(rowCol, tempBinContent);
        bin.load(tempBinContent);
    }

    /**
     * Flushes buffered content to disk.
     */
    public void flush() throws IOException {
        qtFile.flush();
    }

    /**
     * Closes the database
     */
    public void close() throws IOException {
        qtFile.close();
    }

    /**
     * Deletes the database.
     */
    public void delete() throws IOException {
        qtFile.close();
        qtFile.delete();
    }

    /**
     * Checks whether the directory passed in is a valid directory for a quad tree file.
     *
     * @param location the desired location of the quad tree file
     */
    private void checkLocation(File location) throws IOException {
        Guardian.assertNotNull("location", location);

        if (!location.exists()) {
            throw new IOException("database location does not exist: '" + location.toString() + "'");
        }

        if (!location.isDirectory()) {
            throw new IOException("database location must be a directory: '" + location.toString() + "'");
        }
    }
}
