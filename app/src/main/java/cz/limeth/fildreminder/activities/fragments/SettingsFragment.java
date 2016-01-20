package cz.limeth.fildreminder.activities.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import cz.limeth.fildreminder.R;
import cz.limeth.fildreminder.preferences.FileChooserPreference;
import cz.limeth.fildreminder.preferences.SeekBarPreference;

public class SettingsFragment extends PreferenceFragment {
    private SeekBarPreference reminderDelayPreference;
    private SeekBarPreference reminderVibratorPreference;
    private FileChooserPreference reminderAudioPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(reminderDelayPreference = (SeekBarPreference) findPreference("pref_key_category_reminder_delay"));
        bindPreferenceSummaryToValue(reminderVibratorPreference = (SeekBarPreference) findPreference("pref_key_category_reminder_vibrator"));
        bindPreferenceSummaryToValue(reminderAudioPreference = (FileChooserPreference) findPreference("pref_key_category_reminder_audio"));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        SummaryListener summaryListener = new SummaryListener();
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(summaryListener);

        // Trigger the listener immediately with the preference's
        // current value.
        summaryListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext()).getAll().get(preference.getKey()));
    }

    public SeekBarPreference getReminderDelayPreference() {
        return reminderDelayPreference;
    }

    public SeekBarPreference getReminderVibratorPreference() {
        return reminderVibratorPreference;
    }

    public FileChooserPreference getReminderAudioPreference() {
        return reminderAudioPreference;
    }

    private class SummaryListener implements Preference.OnPreferenceChangeListener {
        private String defaultSummary;

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if(defaultSummary == null) {
                CharSequence firstSummary = preference.getSummary();

                if(firstSummary != null) {
                    defaultSummary = firstSummary.toString();
                }
            }

            String summary;

            if(defaultSummary == null) {
                summary = String.valueOf(value);
            } else {
                Configuration configuration = getResources().getConfiguration();
                summary = String.format(configuration.locale, defaultSummary, value);
            }

            preference.setSummary(summary);

            return true;
        }
    };
}
