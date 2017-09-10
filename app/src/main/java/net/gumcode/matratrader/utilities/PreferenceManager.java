package net.gumcode.matratrader.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.models.Account;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class PreferenceManager {

    private static String KEY_ID = "ID";
    private static String KEY_USERNAME = "USERNAME";
    private static String KEY_PASSWORD = "PASSWORD";
    private static String KEY_SERVER_TOKEN = "SERVER_TOKEN";
    private static String KEY_VALIDITY = "VALIDITY";
    private static String KEY_PROPOSAL_ID = "PROPOSAL_ID";
    private static String KEY_CONNECTED = "CONNECTED";
    private static String KEY_PROCESS = "PROCESS";
    private static String KEY_IS_AVAILABLE = "AVAILABLE";

    private static String KEY_BALANCE = "BALANCE";
    private static String KEY_TRADING_BALANCE = "TRADING_BALANCE";
    private static String KEY_HIGHEST_PROFIT = "HIGHEST_PROFIT";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        pref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void signIn(Account account) {
        editor = pref.edit();
        editor.putInt(KEY_ID, account.id);
        editor.putString(KEY_USERNAME, account.email);
        editor.putString(KEY_PASSWORD, account.password);
        editor.putString(KEY_SERVER_TOKEN, account.serverToken);
        editor.putString(KEY_VALIDITY, account.validity);
        editor.commit();
    }

    public Account getUser() {
        Account instance = new Account();
        instance.id = pref.getInt(KEY_ID, -1);
        instance.email = pref.getString(KEY_USERNAME, "");
        instance.password = pref.getString(KEY_PASSWORD, "");
        instance.serverToken = pref.getString(KEY_SERVER_TOKEN, "");
        instance.validity = pref.getString(KEY_VALIDITY, "");
        return instance;
    }

    public void signOut() {
        editor = pref.edit();
        editor.putInt(KEY_ID, -1);
        editor.putString(KEY_USERNAME, "");
        editor.putString(KEY_PASSWORD, "");
        editor.putString(KEY_SERVER_TOKEN, "");
        editor.putString(KEY_VALIDITY, "");
        editor.commit();
    }

    public void setBalance(double balance) {
        editor = pref.edit();
        editor.putFloat(KEY_BALANCE, (float) balance);
        editor.commit();
    }

    public double getBalance() {
        return pref.getFloat(KEY_BALANCE, 0);
    }

    public void setTradingBalance(int percent) {
        editor = pref.edit();
        editor.putInt(KEY_TRADING_BALANCE, percent);
        editor.commit();
    }

    public int getTradingBalance() {
        return pref.getInt(KEY_TRADING_BALANCE, 0);
    }

    public void setProposalId(String id) {
        editor = pref.edit();
        editor.putString(KEY_PROPOSAL_ID, id);
        editor.commit();
    }

    public String getProposalId() {
        return pref.getString(KEY_PROPOSAL_ID, "");
    }

    public void setHighestProfit(double profit) {
        editor = pref.edit();
        editor.putFloat(KEY_HIGHEST_PROFIT, (float) profit);
        editor.commit();
    }

    public double getHighestProfit() {
        return pref.getFloat(KEY_HIGHEST_PROFIT, 0);
    }

    public void setConnected(boolean connected) {
        editor = pref.edit();
        editor.putBoolean(KEY_CONNECTED, connected);
        editor.commit();
    }

    public boolean getConnected() {
        return pref.getBoolean(KEY_CONNECTED, false);
    }

    public void setProcess(boolean process) {
        editor = pref.edit();
        editor.putBoolean(KEY_PROCESS, process);
        editor.commit();
    }

    public boolean getProcess() {
        return pref.getBoolean(KEY_PROCESS, false);
    }

    public void setAvailable(boolean process) {
        editor = pref.edit();
        editor.putBoolean(KEY_IS_AVAILABLE, process);
        editor.commit();
    }

    public boolean getAvailable() {
        return pref.getBoolean(KEY_IS_AVAILABLE, true);
    }
}