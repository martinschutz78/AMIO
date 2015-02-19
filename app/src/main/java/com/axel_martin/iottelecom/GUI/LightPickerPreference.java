package com.axel_martin.iottelecom.GUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class LightPickerPreference extends DialogPreference {

    public static final int MAX_VALUE = 200;
    public static final int MIN_VALUE = 10;

    private NumberPicker picker;
    private int value;

    public LightPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(linearParams);

        TextView text = new TextView(getContext());
        text.setLayoutParams(linearParams);
        text.setText("lx");
        text.setTextSize(30);

        FrameLayout dialogView = new FrameLayout(getContext());

        LinearLayout linearLayout = new LinearLayout(getContext());

        linearLayout.setLayoutParams(layoutParams);

        linearLayout.addView(picker);
        linearLayout.addView(text);

        dialogView.addView(linearLayout);
        //dialogView.addView(text);


        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setValue(picker.getValue());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }

}