package eu.geodroid.library.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import eu.geopaparazzi.library.R;

/**
 * A dialog that permits multiple selection.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class MultipleChoiceDialog {

    private List<String> allSelected = new ArrayList<String>();

    /**
     * Open the dialog.
     * 
     * @param context the {@link Context} to use.
     * @param parentButton the parent {@link Button} that will be updated and contain
     *              the text of semicolon separated list of the selected items.
     * @param items the items to be presented in the dialog.
     */
    public void open( Context context, final Button parentButton, final String[] items ) {

        String buttonString = parentButton.getText().toString();
        if (!buttonString.equals("...")) { //$NON-NLS-1$
            // we have values already
            String[] split = buttonString.split(";"); //$NON-NLS-1$
            for( String string : split ) {
                allSelected.add(string);
            }
        }

        boolean[] checkedValues = new boolean[items.length];
        int count = items.length;

        for( int i = 0; i < count; i++ )
            checkedValues[i] = allSelected.contains(items[i]);

        DialogInterface.OnMultiChoiceClickListener dialogListener = new DialogInterface.OnMultiChoiceClickListener(){
            @Override
            public void onClick( DialogInterface dialog, int which, boolean isChecked ) {
                if (items[which].length() == 0) {
                    return;
                }
                if (isChecked) {
                    allSelected.add(items[which]);
                } else {
                    allSelected.remove(items[which]);
                }
                StringBuilder sB = new StringBuilder();

                for( CharSequence selected : allSelected )
                    sB.append(";" + selected); //$NON-NLS-1$

                String buttonString = ""; //$NON-NLS-1$
                if (sB.length() > 0) {
                    buttonString = sB.substring(1);
                } else {
                    buttonString = "..."; //$NON-NLS-1$
                }
                parentButton.setText(buttonString);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.select_dotdot);
        builder.setMultiChoiceItems(items, checkedValues, dialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
