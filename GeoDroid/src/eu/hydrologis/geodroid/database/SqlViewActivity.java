package eu.hydrologis.geodroid.database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import eu.geopaparazzi.library.database.DatabaseListActivity;
import eu.geodroid.library.util.LibraryConstants;
import eu.hydrologis.geodroid.R;

/**
 * A view for db queries.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SqlViewActivity extends Activity {

    private HashMap<String, Query> queriesMap;
    private Spinner sqlSpinner;
    private EditText customQueryText;

    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sql_view);

        sqlSpinner = (Spinner) findViewById(R.id.sqlQuerySpinner);
        try {
            List<String> queriesNames = createQueries();

            ArrayAdapter<String> queryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, queriesNames);
            queryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sqlSpinner.setAdapter(queryAdapter);
            sqlSpinner.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        customQueryText = (EditText) findViewById(R.id.ownQueryEditText);
    }

    public void launchQuery( View view ) throws Exception {
        String sqlName = sqlSpinner.getSelectedItem().toString();
        Query query = queriesMap.get(sqlName);

        Intent dbViewIntent = new Intent(this, DatabaseListActivity.class);
        dbViewIntent.putExtra(LibraryConstants.PREFS_KEY_QUERY, query.query);
        startActivity(dbViewIntent);
    }

    public void launchOwnQuery( View view ) throws Exception {
        String customQuery = customQueryText.getText().toString();

        Intent dbViewIntent = new Intent(this, DatabaseListActivity.class);
        dbViewIntent.putExtra(LibraryConstants.PREFS_KEY_QUERY, customQuery);
        startActivity(dbViewIntent);
    }

    private List<String> createQueries() throws Exception {
        queriesMap = new HashMap<String, Query>();

        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("queries_select.txt");
        String viewQueriesString = new Scanner(inputStream).useDelimiter("\\A").next();
        String[] queriesSplit = viewQueriesString.split("\n");
        for( String queryLine : queriesSplit ) {
            String[] lineSplit = queryLine.split(";");
            Query q = new Query();
            q.name = lineSplit[0].trim();
            String[] split = lineSplit[1].trim().split(",");
            String[] splitTrim = new String[split.length];
            for( int i = 0; i < splitTrim.length; i++ ) {
                splitTrim[i] = split[i].trim();
            }
            q.query = lineSplit[1].trim();
            queriesMap.put(q.name, q);
        }

        Set<String> keySet = queriesMap.keySet();
        List<String> queries = new ArrayList<String>();
        for( String query : keySet ) {
            queries.add(query);
        }
        Collections.sort(queries);
        return queries;
    }

    private static class Query {
        String name;
        String query;
    }
}
