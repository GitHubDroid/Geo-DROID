package eu.geodroid.library.share;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Utilities to help sharing of data through the android intents. 
 * 
 * <p>Adapted from http://writecodeeasy.blogspot.it/2012/09/androidtutorial-shareintents.html</p>
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ShareUtilities {
    // private static final boolean LOG_HOW = GPLog.LOG_ABSURD;

    public static void shareText( Context context, String titleMessage, String textToShare ) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        context.startActivity(Intent.createChooser(intent, titleMessage));
    }

    public static void shareTextByEmail( Context context, String titleMessage, String emailSubject, String textToShare,
            String email ) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc882");
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        context.startActivity(Intent.createChooser(intent, titleMessage));
    }

    public static void shareImage( Context context, String titleMessage, File imageFile ) {
        String mimeType = "image/png";
        if (imageFile.getName().toLowerCase().endsWith("jpg")) {
            mimeType = "image/jpg";
        }
        shareFile(context, titleMessage, imageFile, mimeType);
    }

    public static void shareTextAndImage( Context context, String titleMessage, String textToShare, File imageFile ) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        Uri uri = Uri.fromFile(imageFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, titleMessage));
    }

    public static void shareAudio( Context context, String titleMessage, File audioFile ) {
        String mimeType = "audio/amr";
        shareFile(context, titleMessage, audioFile, mimeType);
    }

    public static void shareVideo( Context context, String titleMessage, File videoFile ) {
        String mimeType = "video/mp4";
        shareFile(context, titleMessage, videoFile, mimeType);
    }

    private static void shareFile( Context context, String titleMessage, File file, String mimeType ) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, titleMessage));
    }

}
