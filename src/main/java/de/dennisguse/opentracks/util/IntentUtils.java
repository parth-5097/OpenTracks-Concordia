
package de.dennisguse.opentracks.util;

import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import de.dennisguse.opentracks.data.models.Marker;

/**
 * Utilities for creating intents.
 *
 * @author Jimmy Shih
 */
public class IntentUtils {

    private static final String TAG = IntentUtils.class.getSimpleName();


    private IntentUtils() {

    }

    /**
     * Creates an intent with {@link Intent#FLAG_ACTIVITY_CLEAR_TOP} and {@link Intent#FLAG_ACTIVITY_NEW_TASK}.
     *
     * @param context the context
     * @param cls     the class
     */
    public static Intent newIntent(Context context, Class<?> cls) {
        return new Intent(context, cls).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void showCoordinateOnMap(Context context, Marker marker) {
        showCoordinateOnMap(context, marker.getLatitude(), marker.getLongitude(), marker.getName());
    }

    /**
     * Send intent to show coordinates on a map (needs an another app).
     *
     * @param context   the context
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param label     the label
     */
    private static void showCoordinateOnMap(Context context, double latitude, double longitude, String label) {
        //SEE https://developer.android.com/guide/components/intents-common.html#Maps
        String uri = "geo:0,0?q=" + latitude + "," + longitude;
        if (label != null && label.length() > 0) {
            uri += "(" + label + ")";
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));


        context.startActivity(Intent.createChooser(intent, null));
    }

    public static void persistDirectoryAccessPermission(Context context, Uri directoryUri, int existingFlags) {
        int newFlags = existingFlags & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.getApplicationContext().getContentResolver().takePersistableUriPermission(directoryUri, newFlags);
    }

    public static void releaseDirectoryAccessPermission(Context context, final Uri documentUri) {
        if (documentUri == null) {
            return;
        }

        context.getApplicationContext().getContentResolver().getPersistedUriPermissions().stream()
                .map(UriPermission::getUri)
                .filter(documentUri::equals)
                .forEach(u -> context.getContentResolver().releasePersistableUriPermission(u, 0));
    }

    public static DocumentFile toDocumentFile(Context context, Uri directoryUri) {
        if (directoryUri == null) {
            return null;
        }
        try {
            return DocumentFile.fromTreeUri(context.getApplicationContext(), directoryUri);
        } catch (Exception e) {
            Log.w(TAG, "Could not decode directory: " + e.getMessage());
        }
        return null;
    }

}
