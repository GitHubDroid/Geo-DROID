package eu.geopaparazzi.library.webproject;

import static eu.geodroid.library.util.LibraryConstants.PREFS_KEY_PWD;
import static eu.geodroid.library.util.LibraryConstants.PREFS_KEY_URL;
import static eu.geodroid.library.util.LibraryConstants.PREFS_KEY_USER;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.database.GPLog;
import eu.geodroid.library.util.Utilities;

/**
 * Web projects listing activity.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class WebProjectsListActivity extends ListActivity {
    private static final String ERROR = "error"; //$NON-NLS-1$

    private ArrayAdapter<Webproject> arrayAdapter;
    private EditText filterText;

    private List<Webproject> projectList = new ArrayList<Webproject>();
    private List<Webproject> projectListToLoad = new ArrayList<Webproject>();

    private String user;
    private String pwd;
    private String url;

    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);
        setContentView(R.layout.webprojectlist);

        Bundle extras = getIntent().getExtras();
        user = extras.getString(PREFS_KEY_USER);
        pwd = extras.getString(PREFS_KEY_PWD);
        url = extras.getString(PREFS_KEY_URL);

        filterText = (EditText) findViewById(R.id.search_box);
        filterText.addTextChangedListener(filterTextWatcher);

        final ProgressDialog downloadProjectListDialog = ProgressDialog.show(this, getString(R.string.downloading),
                getString(R.string.downloading_projects_list_from_server));
        new AsyncTask<String, Void, String>(){

            protected String doInBackground( String... params ) {
                WebProjectsListActivity context = WebProjectsListActivity.this;
                try {
                    projectList = WebProjectManager.INSTANCE.downloadProjectList(context, url, user, pwd);
                    for( Webproject wp : projectList ) {
                        projectListToLoad.add(wp);
                    }
                    return ""; //$NON-NLS-1$
                } catch (Exception e) {
                    e.printStackTrace();
                    return ERROR;
                }
            }

            protected void onPostExecute( String response ) { // on UI thread!
                downloadProjectListDialog.dismiss();
                WebProjectsListActivity context = WebProjectsListActivity.this;
                if (response.equals(ERROR)) {
                    Utilities.messageDialog(context, R.string.error_projects_list, null);
                } else {
                    refreshList();
                }
            }

        }.execute((String) null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    protected void onDestroy() {
        super.onDestroy();
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    private void filterList( String filterText ) {
        if (GPLog.LOG)
            GPLog.addLogEntry(this, "filter projects list"); //$NON-NLS-1$

        projectListToLoad.clear();
        for( Webproject project : projectList ) {
            if (project.matches(filterText)) {
                projectListToLoad.add(project);
            }
        }

        refreshList();
    }

    private void refreshList() {
        if (GPLog.LOG)
            GPLog.addLogEntry(this, "refreshing projects list"); //$NON-NLS-1$
        arrayAdapter = new ArrayAdapter<Webproject>(this, R.layout.webprojectsrow, projectListToLoad){
            @Override
            public View getView( int position, View cView, ViewGroup parent ) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.webprojectsrow, null);

                TextView titleText = (TextView) rowView.findViewById(R.id.titletext);
                TextView descriptionText = (TextView) rowView.findViewById(R.id.descriptiontext);
                TextView authorText = (TextView) rowView.findViewById(R.id.authortext);
                TextView dateText = (TextView) rowView.findViewById(R.id.datetext);
                // TextView sizeText = (TextView) rowView.findViewById(R.id.sizetext);

                final Webproject webproject = projectListToLoad.get(position);
                titleText.setText(webproject.name);
                descriptionText.setText(webproject.title);
                authorText.setText(webproject.author);
                dateText.setText(webproject.date);
                // int kbSize = (int) (webproject.size / 1024.0);
                // sizeText.setText(kbSize + "Kb");

                ImageView imageText = (ImageView) rowView.findViewById(R.id.downloadproject_image);
                imageText.setOnClickListener(new View.OnClickListener(){
                    public void onClick( View v ) {
                        downloadProject(webproject);
                    }
                });
                return rowView;
            }

        };

        setListAdapter(arrayAdapter);
    }

    private TextWatcher filterTextWatcher = new TextWatcher(){

        public void afterTextChanged( Editable s ) {
        }

        public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
        }

        public void onTextChanged( CharSequence s, int start, int before, int count ) {
            // arrayAdapter.getFilter().filter(s);
            filterList(s.toString());
        }
    };

    private void downloadProject( final Webproject webproject ) {
        final ProgressDialog cloudProgressDialog = ProgressDialog.show(this, getString(R.string.downloading_project),
                getString(R.string.downloading_project_to_the_device), true, true);
        new AsyncTask<String, Void, String>(){
            protected String doInBackground( String... params ) {
                try {
                    String returnCode = WebProjectManager.INSTANCE.downloadProject(WebProjectsListActivity.this, url, user, pwd,
                            webproject);
                    return returnCode;
                } catch (Exception e) {
                    GPLog.error(this, e.getLocalizedMessage(), e);
                    e.printStackTrace();
                    return e.getMessage();
                }
            }

            protected void onPostExecute( String response ) { // on UI thread!
                cloudProgressDialog.dismiss();
                String okMsg = getString(R.string.project_successfully_downloaded);
                if (response.equals(okMsg)) {
                    Utilities.messageDialog(WebProjectsListActivity.this, okMsg, null);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WebProjectsListActivity.this);
                    builder.setMessage(response).setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                                public void onClick( DialogInterface dialog, int id ) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        }.execute((String) null);
    }
}
