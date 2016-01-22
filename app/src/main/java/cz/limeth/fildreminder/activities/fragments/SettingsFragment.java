package cz.limeth.fildreminder.activities.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;

import cz.limeth.fildreminder.R;
import cz.limeth.fildreminder.preferences.FileChooserPreference;
import cz.limeth.fildreminder.preferences.FormatSummaryListener;
import cz.limeth.fildreminder.preferences.SeekBarPreference;
import cz.limeth.fildreminder.preferences.SummaryListener;
import cz.limeth.fildreminder.util.Function;

public class SettingsFragment extends PreferenceFragment {
    private SeekBarPreference delayPreference;
    private SeekBarPreference vibratorDurationPreference;
    private SeekBarPreference vibratorIntensityPreference;
    private FileChooserPreference audioFilePreference;
    private SeekBarPreference audioVolumePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity().getApplicationContext();
        Resources resources = context.getResources();
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        addPreferencesFromResource(R.xml.preferences);

        delayPreference = (SeekBarPreference) findPreference(resources.getString(R.string.preference_delay_key));
        vibratorDurationPreference = (SeekBarPreference) findPreference(resources.getString(R.string.preference_vibrator_duration_key));
        vibratorIntensityPreference = (SeekBarPreference) findPreference(resources.getString(R.string.preference_vibrator_intensity_key));
        audioFilePreference = (FileChooserPreference) findPreference(resources.getString(R.string.preference_audio_file_key));
        audioVolumePreference = (SeekBarPreference) findPreference(resources.getString(R.string.preference_audio_volume_key));

        setSummaryListener(delayPreference, new FormatSummaryListener(context));

        if(vibrator.hasVibrator()) {
            setSummaryListener(vibratorDurationPreference, new FormatSummaryListener(context));
            setSummaryListener(vibratorIntensityPreference, new FormatSummaryListener(context, intensitySummaryValueModifierUnsafe));
            vibratorIntensityPreference.setOnProgressChangedListener(new SeekBarPreference.DefaultOnProgressChangedListener(intensitySummaryValueModifier));
        } else {
            vibratorDurationPreference.getView().setOnClickListener(null);
            vibratorIntensityPreference.getView().setOnClickListener(null);
            vibratorDurationPreference.setSummary(R.string.preference_vibrator_unsupported);
            vibratorIntensityPreference.setSummary(R.string.preference_vibrator_unsupported);
        }

        setSummaryListener(audioFilePreference, new FormatSummaryListener(context));
        setSummaryListener(audioVolumePreference, new FormatSummaryListener(context, volumeSummaryValueModifierUnsafe));
        audioVolumePreference.setOnProgressChangedListener(new SeekBarPreference.DefaultOnProgressChangedListener(volumeSummaryValueModifier));
    }

    private void setSummaryListener(Preference preference, SummaryListener listener) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(listener);

        // Trigger the listener immediately with the preference's
        // current value.
        listener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext()).getAll().get(preference.getKey()));
    }

    public SeekBarPreference getDelayPreference() {
        return delayPreference;
    }

    public SeekBarPreference getVibratorDurationPreference() {
        return vibratorDurationPreference;
    }

    public SeekBarPreference getVibratorIntensityPreference() {
        return vibratorIntensityPreference;
    }

    public FileChooserPreference getAudioFilePreference() {
        return audioFilePreference;
    }

    private final Function<Double, Object> intensitySummaryValueModifierUnsafe = new Function<Double, Object>() {
        @Override
        public Double apply(Object value) {
            return intensitySummaryValueModifier.apply((int) value);
        }
    };

    private final Function<Double, Object> volumeSummaryValueModifierUnsafe = new Function<Double, Object>() {
        @Override
        public Double apply(Object value) {
            return volumeSummaryValueModifier.apply((int) value);
        }
    };

    private final Function<Double, Integer> intensitySummaryValueModifier = new SummaryValueModifier(R.integer.constant_vibrator_period);
    private final Function<Double, Integer> volumeSummaryValueModifier = new SummaryValueModifier(R.integer.preference_audio_volume_max);

    private class SummaryValueModifier implements Function<Double, Integer> {
        @IntegerRes
        private int maxId;

        private SummaryValueModifier(@IntegerRes int maxId) {
            this.maxId = maxId;
        }

        @Override
        public Double apply(Integer value) {
            Context context = getActivity().getApplicationContext();
            Resources resources = context.getResources();
            int max = resources.getInteger(maxId);
            return 100 * (double) (int) value / (double) max;
        }
    }
}
