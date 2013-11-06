package eu.geopaparazzi.library.forms.views;

import static eu.geopaparazzi.library.forms.FormUtilities.COLON;
import static eu.geopaparazzi.library.forms.FormUtilities.UNDERSCORE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.forms.FragmentDetail;
import eu.geopaparazzi.library.nfc.NfcIdReaderActivity;
import eu.geodroid.library.util.LibraryConstants;

/**
 * A custom NFC UID reader view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GNfcUidView extends View implements GView {

    private String _value;
    private EditText uidText;
    private FragmentDetail fragmentDetail;

    public GNfcUidView( Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);
    }

    public GNfcUidView( Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public GNfcUidView( final Activity activity, AttributeSet attrs, final int requestCode, LinearLayout parentView, String key,
            String value, String constraintDescription ) {
        super(activity, attrs);

        handleView(activity, requestCode, parentView, key, value, constraintDescription);
    }

    public GNfcUidView( FragmentDetail fragmentDetail, AttributeSet attrs, final int requestCode, LinearLayout parentView,
            String key, String value, String constraintDescription ) {
        super(fragmentDetail.getActivity(), attrs);
        this.fragmentDetail = fragmentDetail;
        handleView(fragmentDetail.getActivity(), requestCode, parentView, key, value, constraintDescription);
    }

    private void handleView( final Activity activity, final int requestCode, LinearLayout parentView, String key, String value,
            String constraintDescription ) {
        _value = value;

        LinearLayout textLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        textLayout.setLayoutParams(layoutParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        parentView.addView(textLayout);

        TextView textView = new TextView(activity);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setPadding(2, 2, 2, 2);
        textView.setText(key.replace(UNDERSCORE, " ").replace(COLON, " ") + " " + constraintDescription);
        textView.setTextColor(activity.getResources().getColor(R.color.formcolor));
        textLayout.addView(textView);

        final Button button = new Button(activity);
        button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        button.setPadding(15, 5, 15, 5);
        button.setText(R.string.read_nfc_uid);
        textLayout.addView(button);

        uidText = new EditText(activity);
        uidText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        uidText.setPadding(2, 2, 2, 2);
        uidText.setEnabled(false);
        uidText.setText(value);
        uidText.setTextColor(activity.getResources().getColor(R.color.main_text_color_neutral));
        uidText.setKeyListener(null);
        textLayout.addView(uidText);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v ) {
                Intent nfcIntent = new Intent(activity, NfcIdReaderActivity.class);
                if (fragmentDetail != null) {
                    fragmentDetail.startActivityForResult(nfcIntent, requestCode);
                } else {
                    activity.startActivityForResult(nfcIntent, requestCode);
                }
            }
        });
    }

    public String getValue() {
        return _value;
    }

    @Override
    public void setOnActivityResult( Intent data ) {
        String rfidUID = data.getStringExtra(LibraryConstants.PREFS_KEY_TEXT);
        if (rfidUID == null) {
            rfidUID = ""; //$NON-NLS-1$
        }
        _value = rfidUID;
        uidText.setText(rfidUID);
    }

    @Override
    public void refresh( Context context ) {
        // TODO Auto-generated method stub
        
    }

}