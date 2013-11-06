package eu.geodroid.library.util;

/**
 * A runnable that runs on text.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class TextRunnable implements Runnable {
    protected String theTextToRunOn = ""; //$NON-NLS-1$

    public void setText( String text ) {
        theTextToRunOn = text;
    }
}
