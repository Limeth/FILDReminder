package cz.limeth.fildreminder.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Environment;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.File;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Thanks to Paul Burke.
 */
public class FileChooserPreference extends Preference implements Preference.OnPreferenceClickListener {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/cz.limeth.fildreminder";
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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

    public void processActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode != this.requestCode)
            return;

        String path = null;

        if(resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String uriPath = uri.getPath();
            String uriPathSecond = uriPath.split(":")[1];
            path = Environment.getExternalStorageDirectory() + File.separator + uriPathSecond;
        }

        if (shouldPersist()) {
            persistString(path);
            callChangeListener(path);
        }
    }
}
