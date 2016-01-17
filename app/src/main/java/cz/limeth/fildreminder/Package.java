package cz.limeth.fildreminder;

import android.net.Uri;

/**
 * Used to retrieve the package name
 */
public class Package {
    public static String get()
    {
        return Package.class.getPackage().getName();
    }

    public static Uri getStoreUri()
    {
        return Uri.parse("https://play.google.com/store/apps/details?id=" + get());
    }
}
