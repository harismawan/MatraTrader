package net.gumcode.binarymobile.menus.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.gumcode.binarymobile.R;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

//        Preference tradingBalance = findPreference("pref_trading_balance");
    }

}
