package eu.hydrologis.geodroid.database;

/**
 * The supported types of notes.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public enum NoteType {
    POI(0, "POI"), OSM(1, "OSM");

    private final int num;
    private final String def;

    NoteType( int num, String def ) {
        this.num = num;
        this.def = def;
    }

    public int getTypeNum() {
        return num;
    }

    public String getDef() {
        return def;
    }
}