package eu.geopaparazzi.library.forms;

import static eu.geopaparazzi.library.forms.FormUtilities.ATTR_FORMNAME;
import static eu.geopaparazzi.library.forms.FormUtilities.ATTR_FORMS;
import static eu.geopaparazzi.library.forms.FormUtilities.ATTR_SECTIONNAME;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_FORMITEMS;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_FORMS;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_ITEM;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_ITEMS;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_LONGNAME;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_SHORTNAME;
import static eu.geopaparazzi.library.forms.FormUtilities.TAG_VALUES;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import eu.geodroid.library.util.FileUtilities;
import eu.geodroid.library.util.ResourcesManager;
import eu.geodroid.library.util.debug.Debug;

/**
 * Singleton that takes care of tags.
 * 
 * <p>The tags are looked for in the following places:</p>
 * <ul>
 *  <li>a file named <b>tags.json</b> inside the application folder (Which 
 *      is retrieved via {@link ResourcesManager#getApplicationDir()}</li>
 *  <li>or, if the above is missing, a file named <b>tags/tags.json</b> in
 *      the asset folder of the project. In that case the file is copied over 
 *      to the file in the first point.</li>
 * </ul>
 * 
 * <p>
 * The tags file is subdivided as follows:
 * 
 * [{
 *      "sectionname": "scheda_sisma",
 *      "sectiondescription": "this produces a button names scheda_sisma",
 *      "forms": [
 *          {
 *              "formname": "Name of the section, used in the fragments list",
 *              "formitems": [
 *                  ....
 *                  ....
 *              ]
 *          },{
 *              "formname": "This name produces a second fragment",
 *              "formitems": [
 *                  ....
 *                  ....
 *              ]
 *          }
 *      ]
 *  },{
 *      "sectionname": "section 2",
 *      "sectiondescription": "this produces a second button",
 *      "forms": [
 *          {
 *              "formname": "this produces one fragment in the list",
 *              "formitems": [
 *                  ....
 *                  ....
 *              ]
 *          },{
 *      
 *          }
 *      ]
 * }]
 * 
 * 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class TagsManager {

    public static String TAGSFILENAME = "tags.json";

    private LinkedHashMap<String, JSONObject> sectionsMap = null;

    private static TagsManager tagsManager;

    /**
     * Gets the manager singleton. 
     * 
     * @return the {@link TagsManager} singleton.
     * @throws IOException 
     */
    public static synchronized TagsManager getInstance( Context context ) throws Exception {
        if (tagsManager == null) {
            tagsManager = new TagsManager();
            tagsManager.getFileTags(context);
        }
        return tagsManager;
    }

    /**
     * Performs the first data reading. Necessary for everything else.
     * 
     * @param context
     * @throws Exception
     */
    private void getFileTags( Context context ) throws Exception {
        if (sectionsMap == null) {
            sectionsMap = new LinkedHashMap<String, JSONObject>();
        }
        File applicationDir = ResourcesManager.getInstance(context).getApplicationDir();
        File tagsFile = new File(applicationDir, TAGSFILENAME);
        if (!tagsFile.exists() || Debug.doOverwriteTags) {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("tags/tags.json");

            FileUtilities.copyFile(inputStream, new FileOutputStream(tagsFile));
        }

        if (tagsFile.exists()) {
            sectionsMap.clear();
            String tagsFileString = FileUtilities.readfile(tagsFile);
            JSONArray sectionsArrayObj = new JSONArray(tagsFileString);
            int tagsNum = sectionsArrayObj.length();
            for( int i = 0; i < tagsNum; i++ ) {
                JSONObject jsonObject = sectionsArrayObj.getJSONObject(i);
                if (jsonObject.has(ATTR_SECTIONNAME)) {
                    String sectionName = jsonObject.get(ATTR_SECTIONNAME).toString();
                    sectionsMap.put(sectionName, jsonObject);
                }
            }
        }
    }

    public Set<String> getSectionNames() {
        return sectionsMap.keySet();
    }

    public JSONObject getSectionByName( String name ) {
        return sectionsMap.get(name);
    }

    public static List<String> getFormNames4Section( JSONObject section ) throws JSONException {
        List<String> names = new ArrayList<String>();
        JSONArray jsonArray = section.getJSONArray(ATTR_FORMS);
        if (jsonArray != null && jsonArray.length() > 0) {
            for( int i = 0; i < jsonArray.length(); i++ ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has(ATTR_FORMNAME)) {
                    String formName = jsonObject.getString(ATTR_FORMNAME);
                    names.add(formName);
                }
            }
        }
        return names;
    }

    public static JSONObject getForm4Name( String formName, JSONObject section ) throws JSONException {
        JSONArray jsonArray = section.getJSONArray(ATTR_FORMS);
        if (jsonArray != null && jsonArray.length() > 0) {
            for( int i = 0; i < jsonArray.length(); i++ ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has(ATTR_FORMNAME)) {
                    String tmpFormName = jsonObject.getString(ATTR_FORMNAME);
                    if (tmpFormName.equals(formName)) {
                        return jsonObject;
                    }
                }
            }
        }
        return null;
    }

    public static TagObject stringToTagObject( String jsonString ) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String shortname = jsonObject.getString(TAG_SHORTNAME);
        String longname = jsonObject.getString(TAG_LONGNAME);

        TagObject tag = new TagObject();
        tag.shortName = shortname;
        tag.longName = longname;
        if (jsonObject.has(TAG_FORMS)) {
            tag.hasForm = true;
        }
        tag.jsonString = jsonString;
        return tag;
    }

    /**
     * Utility method to get the formitems of a form object.
     * 
     * <p>Note that the entering json object has to be one 
     * object of the main array, not THE main array itself, 
     * i.e. a choice was already done.
     * 
     * @param jsonObj the single object.
     * @return the array of items of the contained form or <code>null</code> if 
     *          no form is contained.
     * @throws JSONException
     */
    public static JSONArray getFormItems( JSONObject formObj ) throws JSONException {
        if (formObj.has(TAG_FORMITEMS)) {
            JSONArray formItemsArray = formObj.getJSONArray(TAG_FORMITEMS);
            return formItemsArray;
        }
        return null;
    }

    /**
     * Utility method to get the combo items of a formitem object.
     * 
     * @param formItem the json form <b>item</b>.
     * @return the array of items.
     * @throws JSONException
     */
    public static JSONArray getComboItems( JSONObject formItem ) throws JSONException {
        if (formItem.has(TAG_VALUES)) {
            JSONObject valuesObj = formItem.getJSONObject(TAG_VALUES);
            if (valuesObj.has(TAG_ITEMS)) {
                JSONArray itemsArray = valuesObj.getJSONArray(TAG_ITEMS);
                return itemsArray;
            }
        }
        return null;
    }

    public static String[] comboItems2StringArray( JSONArray comboItems ) throws JSONException {
        int length = comboItems.length();
        String[] itemsArray = new String[length];
        for( int i = 0; i < length; i++ ) {
            JSONObject itemObj = comboItems.getJSONObject(i);
            if (itemObj.has(TAG_ITEM)) {
                itemsArray[i] = itemObj.getString(TAG_ITEM).trim();
            } else {
                itemsArray[i] = " - ";
            }
        }
        return itemsArray;
    }

    public static class TagObject {
        public String shortName;
        public String longName;
        public boolean hasForm;
        public String jsonString;
    }
}
