package eu.geodroid.library.util.debug;

import java.io.File;
import java.io.IOException;

/**
 * Small interface to get hold of all debug possibilities in one place. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class Debug {
    /**
     * Flag to define if we are in debug mode.
     * 
     * <p>For release = <code>false</code>.
     */
    public final static boolean D = false;

    /**
     * Flag to define the use of mock locations instead of the gps.
     * 
     * <p>For release = <code>false</code>.
     */
    public final static boolean doMock = false;

    /**
     * Flag to define normal drawing of renderers, as opposed to drawing some debug info.
     * 
     * <p>For release = <code>true</code>.
     */
    public final static boolean doDrawNormal = true;

    /**
     * Flag to define if the tags file should be overwritten. 
     * 
     * <p>For release = <code>false</code>.
     */
    public final static boolean doOverwriteTags = false;

    /**
     * Dump heap data to a folder.
     * 
     * <p>This will need to be converted to be analized in MAT with:
     * <pre>
     * hprof-conv heap_dump_id_.dalvik-hprof heap_dump_id.hprof
     * </pre>
     * 
     * @param folder the folder to which to dump to.
     * @param id the id to add to the file name.
     * @throws IOException 
     */
    public static void dumpHProfData( File folder, int id ) throws IOException {
        String absPath = new File(folder, "heap_dump_" + id + ".dalvik-hprof").getAbsolutePath();
        android.os.Debug.dumpHprofData(absPath);
    }

}
