package cz.limeth.fildreminder.preferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.Preference;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import java.io.File;

import cz.limeth.fildreminder.R;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Thanks to Paul Burke.
 */
public class FileChooserPreference extends Preference implements Preference.OnPreferenceClickListener {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/cz.limeth.fildreminder";
    private static final int VERSION_SINCE = Build.VERSION_CODES.KITKAT;
    private int requestCode;
    private String chooserTitle;
    private String chooserNotFound;

    public FileChooserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnPreferenceClickListener(this);
        requestCode = attrs.getAttributeIntValue(NAMESPACE, "requestCode", 0);
        String chooserTitleNullable = attrs.getAttributeValue(NAMESPACE, "chooserTitle");
        chooserTitle = chooserTitleNullable != null ? chooserTitleNullable : "Select a file.";
        String chooserNotFoundNullable = attrs.getAttributeValue(NAMESPACE, "chooserNotFound");
        chooserNotFound = chooserTitleNullable != null ? chooserNotFoundNullable : "Please install a File Manager.";
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        if(Build.VERSION.SDK_INT < VERSION_SINCE) {
            view.setOnClickListener(null);
            setSummary(R.string.pref_filechooser_unsupported);
        }

        return view;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if(!restorePersistedValue && shouldPersist())
            persistString(defaultValue == null ? null : defaultValue.toString());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent chooserIntent = Intent.createChooser(intent, chooserTitle);

        try {
            startActivityForResult((Activity) getContext(), chooserIntent, requestCode, null);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), chooserNotFound,
                    Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @TargetApi(VERSION_SINCE)
    public void processActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode != this.requestCode)
            return;

        String path = null;

        if(resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Context context = getContext();
            ContentResolver contentResolver = context.getContentResolver();
            path = uri.toString();

            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        if (shouldPersist()) {
            persistString(path);
            callChangeListener(path);
        }
    }
}
