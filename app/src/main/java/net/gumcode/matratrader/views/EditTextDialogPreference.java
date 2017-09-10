package net.gumcode.matratrader.views;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.utilities.PreferenceManager;
import net.gumcode.matratrader.utilities.Utils;

/**
 * Created by A. Fauzi Harismawan on 5/10/2016.
 */
public class EditTextDialogPreference extends DialogPreference {

    private final Queries q;
    private final PreferenceManager preferenceManager;
    private EditText amount;

    public EditTextDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_edittext);

        preferenceManager = new PreferenceManager(context);
        DatabaseHelper db = new DatabaseHelper(context);
        q = new Queries(db);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        TextView portfolio = (TextView) view.findViewById(R.id.portfolio);
        portfolio.setText("Portfolio: $" + Utils.roundUp(preferenceManager.getBalance()));

        amount = (EditText) view.findViewById(R.id.amount);

        if (getKey().equals("pref_stop_profit")) {
            amount.setText(Utils.roundUp(q.getStopProfit()));
        } else if (getKey().equals("pref_stop_loss")) {
            amount.setText(Utils.roundUp(q.getStopLoss()));
        }

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getKey().equals("pref_stop_profit")) {
                    if (!amount.getText().toString().equals("")) {
                        if (Double.valueOf(amount.getText().toString()) < preferenceManager.getBalance()) {
                            amount.setError(getContext().getString(R.string.higher));
                        } else {
                            amount.setError(null);
                        }
                    }
                } else if (getKey().equals("pref_stop_loss")) {
                    if (!amount.getText().toString().equals("")) {
                        if (Double.valueOf(amount.getText().toString()) > preferenceManager.getBalance()) {
                            amount.setError(getContext().getString(R.string.lower));
                        } else {
                            amount.setError(null);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            if (!amount.getText().toString().equals("")) {
                if (getKey().equals("pref_stop_profit")) {
                    if (Double.valueOf(amount.getText().toString()) > preferenceManager.getBalance()) {
                        q.setStopProfit(Double.valueOf(amount.getText().toString()));
                        setSummary("$" + amount.getText().toString());
                    }
                } else if (getKey().equals("pref_stop_loss")) {
                    if (Double.valueOf(amount.getText().toString()) < preferenceManager.getBalance()) {
                        q.setStopLoss(Double.valueOf(amount.getText().toString()));
                        setSummary("$" + amount.getText().toString());
                    }
                }
            } else {
                amount.setError(getContext().getString(R.string.parameter));
                amount.requestFocus();
            }
        }
    }
}
