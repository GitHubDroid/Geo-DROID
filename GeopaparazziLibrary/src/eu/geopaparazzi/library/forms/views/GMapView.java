package eu.geopaparazzi.library.forms.views;

import static eu.geopaparazzi.library.forms.FormUtilities.COLON;
import static eu.geopaparazzi.library.forms.FormUtilities.UNDERSCORE;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.geopaparazzi.library.R;
import eu.geodroid.library.markers.MarkersUtilities;
import eu.geodroid.library.util.FileUtilities;
import eu.geodroid.library.util.ResourcesManager;

/**
 * A custom map view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GMapView extends View implements GView {

    private File image;
    private ImageView imageView;
    private LinearLayout mainLayout;

    public GMapView( Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);
    }

    public GMapView( Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public GMapView( final Context context, AttributeSet attrs, LinearLayout parentView, String key, String value,
            String constraintDescription ) {
        super(context, attrs);

        mainLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        mainLayout.setLayoutParams(layoutParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        parentView.addView(mainLayout);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setPadding(2, 2, 2, 2);
        textView.setText(key.replace(UNDERSCORE, " ").replace(COLON, " ") + " " + constraintDescription);
        textView.setTextColor(context.getResources().getColor(R.color.formcolor));
        mainLayout.addView(textView);

        image = new File(value);
        if (!image.exists()) {
            // look also in media folder for relative path name
            File mediaDir = null;
            try {
                mediaDir = ResourcesManager.getInstance(context).getMediaDir();
            } catch (Exception e) {
                e.printStackTrace();
            }
            File parentFolder = mediaDir.getParentFile();
            image = new File(parentFolder, value);
        }
        if (image.exists()) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            imageView.setPadding(5, 5, 5, 5);
            imageView.setOnClickListener(new View.OnClickListener(){
                public void onClick( View v ) {
                    // the old way
                    // Intent intent = new Intent();
                    // intent.setAction(android.content.Intent.ACTION_VIEW);
                    //                    intent.setDataAndType(Uri.fromFile(image), "image/*"); //$NON-NLS-1$
                    // context.startActivity(intent);

                    /*
                     * open in markers to edit it
                     */
                    MarkersUtilities.launchOnImage(context, image);
                }

            });
            mainLayout.addView(imageView);
        }
        if (image.exists() && imageView != null) {
            Bitmap thumbnail = FileUtilities.readScaledBitmap(image, 200);
            imageView.setImageBitmap(thumbnail);
        }

    }

    public void refresh( final Context context ) {
        try {
            // THIS IS PLAIN UGLY
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (image.exists() && imageView != null) {
            Bitmap thumbnail = FileUtilities.readScaledBitmap(image, 200);
            imageView.setImageBitmap(thumbnail);
        }
    }

    public String getValue() {
        return image.getAbsolutePath();
    }

    @Override
    public void setOnActivityResult( Intent data ) {
    }

}