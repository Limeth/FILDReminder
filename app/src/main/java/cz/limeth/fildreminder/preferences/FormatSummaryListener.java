package cz.limeth.fildreminder.preferences;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.Preference;

import cz.limeth.fildreminder.util.Function;

public class FormatSummaryListener extends SummaryListener {
    private Function<? extends Object, Object> valueModifier;

    public FormatSummaryListener(Context context, Function<? extends Object, Object> valueModifier) {
        super(context);

        this.valueModifier = valueModifier;
    }

    public FormatSummaryListener(Context context) {
        this(context, null);
    }

    @Override
    public void onSummaryChange(Preference preference, Object value, String defaultSummary) {
        if(valueModifier != null)
            value = valueModifier.apply(value);

        String summary;

        if(defaultSummary == null) {
            summary = String.valueOf(value);
        } else {
            Context context = getContext();
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            summary = String.format(configuration.locale, defaultSummary, value);
        }

        preference.setSummary(summary);
    }

    public Function getValueModifier() {
        return valueModifier;
    }

    public void setValueModifier(Function valueModifier) {
        this.valueModifier = valueModifier;
    }
}
