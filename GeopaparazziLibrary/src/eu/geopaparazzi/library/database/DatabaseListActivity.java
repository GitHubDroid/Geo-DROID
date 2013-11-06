package eu.geopaparazzi.library.database;

import java.io.IOException;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import eu.geopaparazzi.library.R;
import eu.geodroid.library.util.LibraryConstants;
import eu.geodroid.library.util.Utilities;

/**
 * A database list activity.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class DatabaseListActivity extends ListActivity {
    private Cursor cursor;

    @SuppressWarnings("nls")
    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);
        setContentView(R.layout.database_list);

        Bundle extras = getIntent().getExtras();
        String query = extras.getString(LibraryConstants.PREFS_KEY_QUERY);

        SQLiteDatabase database = null;
        try {
            database = ADbHelper.getInstance().getDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (database != null && query != null) {
                cursor = database.rawQuery(query, null);
                startManagingCursor(cursor);

                DbCursorAdapter data = new DbCursorAdapter(this, cursor);
                setListAdapter(data);
            }
        } catch (SQLException e) {
            Utilities.messageDialog(this, "An error occurred while launching the query: " + e.getLocalizedMessage(),
                    new Runnable(){
                        public void run() {
                            finish();
                        }
                    });
        }

    }

    @Override
    protected void onDestroy() {
        if (cursor != null) {
            stopManagingCursor(cursor);
            cursor.close();
        }
        super.onDestroy();
    }
}
