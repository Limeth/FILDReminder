package cz.limeth.fildreminder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import cz.limeth.fildreminder.activities.fragments.SettingsFragment;
import cz.limeth.fildreminder.preferences.FileChooserPreference;

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
        FileChooserPreference audioPreference = fragment.getAudioFilePreference();
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
