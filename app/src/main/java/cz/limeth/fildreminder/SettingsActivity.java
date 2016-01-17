package cz.limeth.fildreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import cz.limeth.fildreminder.preferences.FileChooserPreference;

/**
 * Created by limeth on 15.1.16.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = new SettingsFragment();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FileChooserPreference audioPreference = fragment.getReminderAudioPreference();
        audioPreference.processActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    The Back Arrow is the only icon we have on the AppCompat menu bar,
    so we can assume the user didn't press a different icon.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
