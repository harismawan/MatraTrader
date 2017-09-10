package net.gumcode.matratrader.views;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.utilities.PreferenceManager;
import net.gumcode.matratrader.utilities.Utils;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class SeekbarDialogPreference extends DialogPreference {

    private Queries q;
    private PreferenceManager preferenceManager;
    private SeekBar seekBar;

    public SeekbarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_seekbar);
        preferenceManager = new PreferenceManager(context);
        DatabaseHelper db = new DatabaseHelper(context);
        q = new Queries(db);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        final TextView percent = (TextView) view.findViewById(R.id.percent);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);

        if (getKey().equals("pref_trading_balance")) {
            seekBar.setProgress(preferenceManager.getTradingBalance());
            percent.setText(preferenceManager.getTradingBalance() + "% ($" + Utils.roundUp(Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance())) + ")");
        } else if (getKey().equals("pref_profit")) {
            seekBar.setProgress(q.getProfit());
            seekBar.setMax(90);
            percent.setText(q.getProfit() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getProfit(), Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
        } else if (getKey().equals("pref_cutloss")) {
            seekBar.setProgress(q.getCutloss());
            seekBar.setMax(45);
            percent.setText(q.getCutloss() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getCutloss(), Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
        } else if (getKey().equals("pref_cutloss2")) {
            seekBar.setProgress(q.getCutloss2());
            seekBar.setMax(45);
            percent.setText(q.getCutloss2() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getCutloss2(), Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (getKey().equals("pref_trading_balance") || getKey().equals("pref_stop_profit") || getKey().equals("pref_stop_loss")) {
                    percent.setText(progress + "% ($" + Utils.roundUp(Utils.calculatePercent(progress,
                            preferenceManager.getBalance())) + ")");
                } else {
                    percent.setText(progress + "% ($" + Utils.roundUp(Utils.calculatePercent(progress,
                            Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
                }
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
            if (getKey().equals("pref_trading_balance")) {
                preferenceManager.setTradingBalance(seekBar.getProgress());
                setSummary(seekBar.getProgress() + "% ($" + Utils.roundUp(Utils.calculatePercent(seekBar.getProgress(), preferenceManager.getBalance())) + ")");

                SeekbarDialogPreference a = (SeekbarDialogPreference) findPreferenceInHierarchy("pref_profit");
                a.setSummary(q.getProfit() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getProfit(),
                        Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");

                SeekbarDialogPreference b = (SeekbarDialogPreference) findPreferenceInHierarchy("pref_cutloss");
                b.setSummary(q.getCutloss() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getCutloss(),
                        Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
            } else if (getKey().equals("pref_profit")) {
                q.setProfit(seekBar.getProgress());
                setSummary(seekBar.getProgress() + "% ($" + Utils.roundUp(Utils.calculatePercent(seekBar.getProgress(), Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
            } else if (getKey().equals("pref_cutloss")) {
                q.setCutloss(seekBar.getProgress());
                setSummary(seekBar.getProgress() + "% ($" + Utils.roundUp(Utils.calculatePercent(seekBar.getProgress(), Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
            } else if (getKey().equals("pref_cutloss2")) {
                q.setCutloss2(seekBar.getProgress());
                setSummary(seekBar.getProgress() + "% ($" + Utils.roundUp(Utils.calculatePercent(seekBar.getProgress(), Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");
            }
        }
    }
}
