package cz.limeth.fildreminder.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cz.limeth.fildreminder.util.AttributeHelper;
import cz.limeth.fildreminder.util.Function;


/**
 * Represents an integer value via a (possibly non-linear) seek bar.
 * Expression: y = (coefficient) * x^(exponent) + (absolute)
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, OnClickListener
{
    private static final String NAMESPACE_ANDROID ="http://schemas.android.com/apk/res/android";
    private static final String NAMESPACE_FILDREMINDER = "http://schemas.android.com/apk/res/cz.limeth.fildreminder";

    private LinearLayout view;
    private SeekBar mSeekBar;
    private TextView mSplashText, mValueText;
    private Context mContext;

    private String mDialogMessage, mSuffix;
    private int mDefault, mMax, mValue = 0;
    private float mExponent, mCoefficient, mAbsolute;

    private OnProgressChangedListener onProgressChangedListener;

    public SeekBarPreference(Context context, AttributeSet attrsRaw) {

        super(context, attrsRaw);
        mContext = context;
        Resources resources = context.getResources();
        AttributeHelper attrs = new AttributeHelper(attrsRaw, resources);

        // Get string value for dialogMessage :
        int mDialogMessageId = attrs.getAttributeResourceValue(NAMESPACE_ANDROID, "dialogMessage", 0);
        if(mDialogMessageId == 0) mDialogMessage = attrs.getAttributeValue(NAMESPACE_ANDROID, "dialogMessage");
        else mDialogMessage = mContext.getString(mDialogMessageId);

        // Get string value for suffix (text attribute in xml file) :
        int mSuffixId = attrs.getAttributeResourceValue(NAMESPACE_ANDROID, "text", 0);
        if(mSuffixId == 0) mSuffix = attrs.getAttributeValue(NAMESPACE_ANDROID, "text");
        else mSuffix = mContext.getString(mSuffixId);

        // Get default and max seekbar values :
        mDefault = attrs.getAttributeIntValue(NAMESPACE_ANDROID, "defaultValue", 0);
        mMax = attrs.resolveInteger(NAMESPACE_ANDROID, "max", 100);

        mExponent = attrs.getAttributeFloatValue(NAMESPACE_FILDREMINDER, "exponent", 1);
        mCoefficient = attrs.getAttributeFloatValue(NAMESPACE_FILDREMINDER, "coefficient", 1);
        mAbsolute = attrs.getAttributeFloatValue(NAMESPACE_FILDREMINDER, "absolute", 0);

        onProgressChangedListener = new DefaultOnProgressChangedListener();
    }

    @Override
    protected View onCreateDialogView() {

        // TODO: Use an xml layout

        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mSplashText = new TextView(mContext);
        mSplashText.setPadding(30, 10, 30, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);
        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        updateSeekBar();

        return view = layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        updateSeekBar();
    }

    private void updateSeekBar()
    {
        mSeekBar.setMax(toSliderValue(mMax));
        mSeekBar.setProgress(toSliderValue(mValue));
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValueWhenNotRestoring)
    {
        int def = defaultValueWhenNotRestoring == null ? 0 : Integer.valueOf(defaultValueWhenNotRestoring.toString());

        if (restore) {
            mValue = getPersistedInt(mDefault);
        } else {
            mValue = def;
        }

        if (shouldPersist()) {
            persistInt(mValue);
        }
    }

    private int fromSliderValue(int value)
    {
        return (int) Math.round(mCoefficient * Math.pow(value, mExponent) + mAbsolute);
    }

    private int toSliderValue(int value)
    {
        return (int) Math.round(Math.pow((value - mAbsolute) / mCoefficient, 1 / mExponent));
    }

    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        if (onProgressChangedListener != null)
            onProgressChangedListener.onProgressChanged(this, seek, value, fromTouch);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {}
    @Override
    public void onStopTrackingTouch(SeekBar seek) {}

    public void setMax(int max) { mMax = max; }
    public int getMax() { return mMax; }

    public void setProgress(int progress) {
        mValue = progress;
        if (mSeekBar != null)
            mSeekBar.setProgress(toSliderValue(progress));
    }
    public int getProgress() { return mValue; }

    @Override
    public void showDialog(Bundle state) {

        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (shouldPersist()) {
            mValue = fromSliderValue(mSeekBar.getProgress());
            persistInt(mValue);
            callChangeListener(mValue);
        }

        getDialog().dismiss();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mDefault);
    }

    public LinearLayout getView() {
        return view;
    }

    public OnProgressChangedListener getOnProgressChangedListener() {
        return onProgressChangedListener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.onProgressChangedListener = onProgressChangedListener;
    }

    public TextView getTextView() {
        return mValueText;
    }

    public String getSuffix() {
        return mSuffix;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(SeekBarPreference preference, SeekBar seekBar, int value, boolean fromTouch);
    }

    public static class DefaultOnProgressChangedListener implements OnProgressChangedListener {
        private Function<? extends Object, Integer> valueModifier;

        public DefaultOnProgressChangedListener(Function<? extends Object, Integer> valueModifier) {
            this.valueModifier = valueModifier;
        }

        public DefaultOnProgressChangedListener() {
            this(null);
        }

        @Override
        public void onProgressChanged(SeekBarPreference preference, SeekBar seekBar, int value, boolean fromTouch) {
            value = preference.fromSliderValue(value);
            Object displayedValue;

            if(valueModifier == null)
                displayedValue = value;
            else
                displayedValue = valueModifier.apply(value);

            String displayedValueString = String.valueOf(displayedValue);
            TextView mValueText = preference.getTextView();
            String mSuffix = preference.getSuffix();
            mValueText.setText(mSuffix == null ? displayedValueString : displayedValueString.concat(mSuffix));

            if(!fromTouch)
                return;

            int sliderValue = preference.toSliderValue(value);
            seekBar.setProgress(sliderValue);
        }
    }
}
