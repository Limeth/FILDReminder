package cz.limeth.fildreminder.preferences;

import android.content.Context;
import android.preference.Preference;

public abstract class SummaryListener implements Preference.OnPreferenceChangeListener {
    private Context context;
    private String defaultSummary;

    public SummaryListener(Context context)
    {
        if(context == null)
            throw new NullPointerException("The Context must not be null!");

        this.context = context;
    }

    public abstract void onSummaryChange(Preference preference, Object value, String defaultSummary);

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if(defaultSummary == null) {
            CharSequence firstSummary = preference.getSummary();

            if(firstSummary != null) {
                defaultSummary = firstSummary.toString();
            }
        }

        onSummaryChange(preference, value, defaultSummary);
        return true;
    }

    public Context getContext() {
        return context;
    }
}
