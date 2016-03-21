package net.gumcode.binarymobile.views;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import net.gumcode.binarymobile.R;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class SeekbarDialogPreference extends DialogPreference {

    public SeekbarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_seekbar);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        final TextView percent = (TextView) view.findViewById(R.id.percent);
        percent.setText("0%");

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {

        }
    }
}
