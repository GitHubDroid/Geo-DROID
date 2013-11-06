package eu.geodroid.library.util.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.database.GPLog;
import eu.geodroid.library.util.LibraryConstants;
import eu.geodroid.library.util.Utilities;

/**
 * Activity to get to a location by coordinate.
 * 
 * <p>Returns lat and long in the result bundle through the 
 * key: {@link LibraryConstants#LATITUDE} and {@link LibraryConstants#LONGITUDE}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class InsertCoordActivity extends Activity {
    private EditText latText;
    private EditText lonText;

    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);
        setContentView(R.layout.insertcoord);

        lonText = (EditText) findViewById(R.id.longitudetext);
        latText = (EditText) findViewById(R.id.latitudetext);

        Button fromCoordsButton = (Button) findViewById(R.id.coordOkButton);
        fromCoordsButton.setOnClickListener(new Button.OnClickListener(){
            private double lat;
            private double lon;

            public void onClick( View v ) {
                String lonString = String.valueOf(lonText.getText());
                try {
                    lon = Double.parseDouble(lonString);
                    if (lon < -180 || lon > 180) {
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    GPLog.error(this, e1.getLocalizedMessage(), e1);
                    Utilities.toast(InsertCoordActivity.this, R.string.wrongLongitude, Toast.LENGTH_LONG);
                    return;
                }
                String latString = String.valueOf(latText.getText());
                try {
                    lat = Double.parseDouble(latString);
                    if (lat < -90 || lat > 90) {
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    GPLog.error(this, e1.getLocalizedMessage(), e1);
                    Utilities.toast(InsertCoordActivity.this, R.string.wrongLatitude, Toast.LENGTH_LONG);
                    return;
                }

                Intent intent = getIntent();
                intent.putExtra(LibraryConstants.LATITUDE, lat);
                intent.putExtra(LibraryConstants.LONGITUDE, lon);
                // if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
                // } else {
                // getParent().setResult(Activity.RESULT_OK, intent);
                // }

                finish();
            }
        });
    }

}
