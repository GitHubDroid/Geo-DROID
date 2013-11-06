package eu.hydrologis.geodroid.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import eu.hydrologis.geodroid.R;

public class PreferencesActivity extends PreferenceActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.my_preferences);

        // TODO make them all white
        // getWindow().setBackgroundDrawableResource(R.drawable.background);
        //
        // PreferenceScreen b = (PreferenceScreen)
        // findPreference("pref_second_preferencescreen_key");
        // b.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        //
        // @Override
        // public boolean onPreferenceClick(Preference preference) {
        // PreferenceScreen a = (PreferenceScreen) preference;
        // a.getDialog().getWindow().setBackgroundDrawableResource(R.drawable.background);
        // return false;
        // }
        // });

    }

}
