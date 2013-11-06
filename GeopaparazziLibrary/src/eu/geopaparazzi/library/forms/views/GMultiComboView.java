package eu.geopaparazzi.library.forms.views;

import static eu.geopaparazzi.library.forms.FormUtilities.COLON;
import static eu.geopaparazzi.library.forms.FormUtilities.UNDERSCORE;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import eu.geopaparazzi.library.R;
import eu.geodroid.library.util.MultipleChoiceDialog;

/**
 * A custom {@link Spinner} view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GMultiComboView extends View implements GView {

    private Button button;

    public GMultiComboView( Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);
    }

    public GMultiComboView( Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public GMultiComboView( final Context context, AttributeSet attrs, LinearLayout parentView, String key, String value,
            final String[] itemsArray, String constraintDescription ) {
        super(context, attrs);

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

        if (value == null || value.length() == 0) {
            button.setText("...");
        } else {
            button.setText(value);
        }
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v ) {
                MultipleChoiceDialog dialog = new MultipleChoiceDialog();
                dialog.open(context, button, itemsArray);
            }
        });

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