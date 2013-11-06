package eu.hydrologis.geodroid.util;

import static eu.hydrologis.geodroid.util.Constants.PREF_KEY_PWD;
import static eu.hydrologis.geodroid.util.Constants.PREF_KEY_SERVER;
import static eu.hydrologis.geodroid.util.Constants.PREF_KEY_USER;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import eu.geopaparazzi.library.network.NetworkUtilities;
import eu.geodroid.library.util.FileUtilities;
import eu.geodroid.library.util.LibraryConstants;
import eu.geodroid.library.util.ResourcesManager;
import eu.geodroid.library.util.Utilities;
import eu.geodroid.library.util.activities.DirectoryBrowserActivity;
import eu.geopaparazzi.library.webproject.WebProjectsListActivity;
import eu.hydrologis.geodroid.R;
import eu.hydrologis.geodroid.database.DaoBookmarks;

/**
 * Activity for export tasks.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ImportActivity extends Activity {

    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);
        setContentView(R.layout.imports);

        ImageButton gpxExportButton = (ImageButton) findViewById(R.id.gpxImportButton);
        gpxExportButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick( View v ) {
                try {
                    importGpx();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ImageButton cloudImportButton = (ImageButton) findViewById(R.id.cloudImportButton);
        cloudImportButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick( View v ) {
                final ImportActivity context = ImportActivity.this;

                if (!NetworkUtilities.isNetworkAvailable(context)) {
                    Utilities.messageDialog(context, context.getString(R.string.available_only_with_network), null);
                    return;
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ImportActivity.this);
                final String user = preferences.getString(PREF_KEY_USER, "geopaparazziuser"); //$NON-NLS-1$
                final String passwd = preferences.getString(PREF_KEY_PWD, "geopaparazzipwd"); //$NON-NLS-1$
                final String server = preferences.getString(PREF_KEY_SERVER, ""); //$NON-NLS-1$

                if (server.length() == 0) {
                    Utilities.messageDialog(context, R.string.error_set_cloud_settings, null);
                    return;
                }

                Intent webImportIntent = new Intent(ImportActivity.this, WebProjectsListActivity.class);
                webImportIntent.putExtra(LibraryConstants.PREFS_KEY_URL, server);
                webImportIntent.putExtra(LibraryConstants.PREFS_KEY_USER, user);
                webImportIntent.putExtra(LibraryConstants.PREFS_KEY_PWD, passwd);
                startActivity(webImportIntent);
            }
        });

        ImageButton bookmarksImportButton = (ImageButton) findViewById(R.id.bookmarksImportButton);
        bookmarksImportButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick( View v ) {
                final ImportActivity context = ImportActivity.this;

                ResourcesManager resourcesManager = null;
                try {
                    resourcesManager = ResourcesManager.getInstance(context);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                File sdcardDir = resourcesManager.getSdcardDir();
                File bookmarksfile = new File(sdcardDir, "bookmarks.csv"); //$NON-NLS-1$
                if (bookmarksfile.exists()) {
                    try {
                        // try to load it
                        List<Bookmark> allBookmarks = DaoBookmarks.getAllBookmarks();
                        TreeSet<String> bookmarksNames = new TreeSet<String>();
                        for( Bookmark bookmark : allBookmarks ) {
                            String tmpName = bookmark.getName();
                            bookmarksNames.add(tmpName.trim());
                        }

                        List<String> bookmarksList = FileUtilities.readfileToList(bookmarksfile);
                        int imported = 0;
                        for( String bookmarkLine : bookmarksList ) {
                            String[] split = bookmarkLine.split(","); //$NON-NLS-1$
                            // bookmarks are of type: Agritur BeB In Valle, 45.46564, 11.58969, 12
                            if (split.length < 3) {
                                continue;
                            }
                            String name = split[0].trim();
                            if (bookmarksNames.contains(name)) {
                                continue;
                            }
                            try {
                                double zoom = 16.0;
                                if (split.length == 4) {
                                    zoom = Double.parseDouble(split[3]);
                                }
                                double lat = Double.parseDouble(split[1]);
                                double lon = Double.parseDouble(split[2]);
                                DaoBookmarks.addBookmark(lon, lat, name, zoom, -1, -1, -1, -1);
                                imported++;
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }

                        Utilities.messageDialog(context, getString(R.string.successfully_imported_bookmarks) + imported, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utilities.messageDialog(context, R.string.error_bookmarks_import, null);
                    }
                } else {
                    Utilities.messageDialog(context, R.string.no_bookmarks_csv, null);
                }
            }
        });
    }

    private void importGpx() throws Exception {
        Intent browseIntent = new Intent(ImportActivity.this, DirectoryBrowserActivity.class);
        browseIntent.putExtra(DirectoryBrowserActivity.STARTFOLDERPATH, ResourcesManager.getInstance(ImportActivity.this)
                .getApplicationDir().getAbsolutePath());
        browseIntent.putExtra(DirectoryBrowserActivity.INTENT_ID, Constants.GPXIMPORT);
        browseIntent.putExtra(DirectoryBrowserActivity.EXTENTION, ".gpx"); //$NON-NLS-1$
        startActivity(browseIntent);
        finish();
    }

}
