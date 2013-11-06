package eu.geopaparazzi.library.forms.views;

import static eu.geopaparazzi.library.forms.FormUtilities.COLON;
import static eu.geopaparazzi.library.forms.FormUtilities.UNDERSCORE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.forms.FormDatePickerFragment;
import eu.geodroid.library.util.LibraryConstants;

/**
 * A custom date view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GDateView extends View implements GView {

    private Button button;

    public GDateView( Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);
    }

    public GDateView( Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public GDateView( final Fragment fragment, AttributeSet attrs, LinearLayout parentView, String key, String value,
            String constraintDescription, boolean readonly ) {
        super(fragment.getActivity(), attrs);

        Context context = fragment.getActivity();

        LinearLayout textLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        textLayout.setLayoutParams(layoutParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        parentView.addView(textLayout);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setPadding(2, 2, 2, 2);
        textView.setText(key.replace(UNDERSCORE, " ").replace(COLON, " ") + " " + constraintDescription);
        textView.setTextColor(context.getResources().getColor(R.color.formcolor));
        textLayout.addView(textView);

        button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        button.setPadding(15, 5, 15, 5);

        final SimpleDateFormat dateFormatter = LibraryConstants.DATEONLY_FORMATTER;
        if (value == null || value.length() == 0) {
            String dateStr = dateFormatter.format(new Date());
            button.setText(dateStr);
        } else {
            button.setText(value);
        }
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v ) {
                String dateStr = button.getText().toString();
                Date date = null;
                try {
                    date = dateFormatter.parse(dateStr);
                } catch (ParseException e) {
                    // fallback on current date
                    date = new Date();
                }
                final Calendar c = Calendar.getInstance();
                c.setTime(date);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                FormDatePickerFragment newFragment = new FormDatePickerFragment(year, month, day, button);
                newFragment.show(fragment.getFragmentManager(), "datePicker");
            }
        });
        button.setEnabled(!readonly);

        textLayout.addView(button);
    }

    public String getValue() {
        String text = button.getText().toString();
        if (text.trim().equals("...")) {
            text = "";
        }
        return text;
    }

    @Override
    public void setOnActivityResult( Intent data ) {
    }

    @Override
    public void refresh( Context context ) {
        // TODO Auto-generated method stub
        
    }

}