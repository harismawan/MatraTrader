package net.gumcode.matratrader.menus.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.models.Market;

import java.util.ArrayList;

/**
 * Created by A. Fauzi Harismawan on 4/17/2016.
 */
public class MarketFilterFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private Queries q;
    private SwitchPreference r25, r50, r75, r100, rBear, rBull, rMars, rMoon, rSun, rVenus, rYin, rYang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.market_filter);

        DatabaseHelper db = new DatabaseHelper(getActivity());
        q = new Queries(db);

        ArrayList<Market> markets = q.getMarket();

        r25 = (SwitchPreference) findPreference("pref_r_25");
        r25.setChecked(markets.get(0).activated);
        r25.setOnPreferenceChangeListener(this);

        r50 = (SwitchPreference) findPreference("pref_r_50");
        r50.setChecked(markets.get(1).activated);
        r50.setOnPreferenceChangeListener(this);

        r75 = (SwitchPreference) findPreference("pref_r_75");
        r75.setChecked(markets.get(2).activated);
        r75.setOnPreferenceChangeListener(this);

        r100 = (SwitchPreference) findPreference("pref_r_100");
        r100.setChecked(markets.get(3).activated);
        r100.setOnPreferenceChangeListener(this);

        rBear = (SwitchPreference) findPreference("pref_r_bear");
        rBear.setChecked(markets.get(4).activated);
        rBear.setOnPreferenceChangeListener(this);

        rBull = (SwitchPreference) findPreference("pref_r_bull");
        rBull.setChecked(markets.get(5).activated);
        rBull.setOnPreferenceChangeListener(this);

        rMars = (SwitchPreference) findPreference("pref_r_mars");
        rMars.setChecked(markets.get(6).activated);
        rMars.setOnPreferenceChangeListener(this);

        rMoon = (SwitchPreference) findPreference("pref_r_moon");
        rMoon.setChecked(markets.get(7).activated);
        rMoon.setOnPreferenceChangeListener(this);

        rSun = (SwitchPreference) findPreference("pref_r_sun");
        rSun.setChecked(markets.get(8).activated);
        rSun.setOnPreferenceChangeListener(this);

        rVenus = (SwitchPreference) findPreference("pref_r_venus");
        rVenus.setChecked(markets.get(9).activated);
        rVenus.setOnPreferenceChangeListener(this);

        rYin = (SwitchPreference) findPreference("pref_r_yin");
        rYin.setChecked(markets.get(10).activated);
        rYin.setOnPreferenceChangeListener(this);

        rYang = (SwitchPreference) findPreference("pref_r_yang");
        rYang.setChecked(markets.get(11).activated);
        rYang.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "pref_r_25":
                q.updateMarket(Constants.MARKET[0], (boolean) newValue);
                break;
            case "pref_r_50":
                q.updateMarket(Constants.MARKET[1], (boolean) newValue);
                break;
            case "pref_r_75":
                q.updateMarket(Constants.MARKET[2], (boolean) newValue);
                break;
            case "pref_r_100":
                q.updateMarket(Constants.MARKET[3], (boolean) newValue);
                break;
            case "pref_r_bear":
                q.updateMarket(Constants.MARKET[4], (boolean) newValue);
                break;
            case "pref_r_bull":
                q.updateMarket(Constants.MARKET[5], (boolean) newValue);
                break;
            case "pref_r_mars":
                q.updateMarket(Constants.MARKET[6], (boolean) newValue);
                break;
            case "pref_r_moon":
                q.updateMarket(Constants.MARKET[7], (boolean) newValue);
                break;
            case "pref_r_sun":
                q.updateMarket(Constants.MARKET[8], (boolean) newValue);
                break;
            case "pref_r_venus":
                q.updateMarket(Constants.MARKET[9], (boolean) newValue);
                break;
            case "pref_r_yin":
                q.updateMarket(Constants.MARKET[10], (boolean) newValue);
                break;
            case "pref_r_yang":
                q.updateMarket(Constants.MARKET[11], (boolean) newValue);
                break;
        }
        return false;
    }
}
