package eu.hydrologis.geodroid.osm;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import eu.geodroid.library.util.ResourcesManager;

/**
 * Singleton that takes care of tags.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class OsmTagsManager {

    private static final String MAINICON_POSTFIX = ".n.48.png";
    public static final String ICON_POSTFIX_LOWDENSITY = ".n.32.png";
    public static final String ICON_POSTFIX_MEDIUMDENSITY = ".n.48.png";
    public static final String ICON_POSTFIX_HIGHDENSITY = ".n.64.png";
    public static final String ICON_POSTFIX_XLARGE = ".n.64.png";

    public static final String TAG_LONGNAME = "longname";
    public static final String TAG_SHORTNAME = "shortname";
    public static final String TAG_FORM = "form";
    public static final String TAG_FORMITEMS = "formitems";
    public static final String TAG_KEY = "key";
    public static final String TAG_VALUE = "value";
    public static final String TAG_VALUES = "values";
    public static final String TAG_ITEMS = "items";
    public static final String TAG_ITEM = "item";
    public static final String TAG_TYPE = "type";

    public static final String TYPE_STRING = "string";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_DOUBLECOMBO = "doublecombo";
    public static final String TYPE_STRINGCOMBO = "stringcombo";

    public static String TAGSFOLDERNAME = "osmtags";

    private static OsmTagsManager osmTagsManager;

    private File tagsFolderFile;
    private String[] categoriesNames;

    /**
     * Gets the manager singleton. 
     * 
     * @return the {@link OsmTagsManager} singleton.
     * @throws IOException 
     */
    public static OsmTagsManager getInstance() {
        if (osmTagsManager == null) {
            osmTagsManager = new OsmTagsManager();
        }
        return osmTagsManager;
    }

    /**
     * Get the file of the root folder for OSM tags.
     * 
     * @param context the {@link Context}.
     * @return the root folder for OSM tags.
     * @throws Exception 
     */
    public synchronized File getTagsFolderFile( Context context ) throws Exception {
        if (tagsFolderFile == null) {
            File geoPaparazziDir = ResourcesManager.getInstance(context).getApplicationDir();
            tagsFolderFile = new File(geoPaparazziDir.getParentFile(), TAGSFOLDERNAME);
        }
        return tagsFolderFile;
    }

    /**
     * Get the available OSM categories. 
     * 
     * @param context the {@link Context}.
     * @return the names of the categories.
     * @throws Exception
     */
    public synchronized String[] getTagCategories( Context context ) throws Exception {
        if (categoriesNames == null) {
            File tagsFolderFile = getTagsFolderFile(context);
            if (tagsFolderFile.exists()) {
                File[] foldersList = tagsFolderFile.listFiles(new FileFilter(){
                    public boolean accept( File pathname ) {
                        return pathname.isDirectory();
                    }
                });
                categoriesNames = new String[foldersList.length];
                for( int i = 0; i < foldersList.length; i++ ) {
                    categoriesNames[i] = foldersList[i].getName();
                }
                Arrays.sort(categoriesNames);
                return categoriesNames;
            }
        }
        return categoriesNames;
    }

    /**
     * Get all the item names for a given category.
     * 
     * @param context the {@link Context}.
     * @param category the category from which to fetch the items.
     * @return the names of the items of the category. 
     * @throws Exception 
     */
    public String[] getItemsForCategory( Context context, String category ) throws Exception {
        File tagsFolderFile = getTagsFolderFile(context);
        File categoryFolderFile = new File(tagsFolderFile, category);
        File[] iconFiles = categoryFolderFile.listFiles(new FileFilter(){
            public boolean accept( File pathname ) {
                return pathname.getName().endsWith(MAINICON_POSTFIX);
            }
        });
        int size = 0;
        if (iconFiles != null) {
            size = iconFiles.length;
        }
        String[] iconFileNames = new String[size];
        for( int i = 0; i < iconFiles.length; i++ ) {
            iconFileNames[i] = iconFiles[i].getName().replace(MAINICON_POSTFIX, "");
        }
        Arrays.sort(iconFileNames);
        return iconFileNames;
    }

}
