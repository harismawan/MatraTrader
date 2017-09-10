package net.gumcode.matratrader.databases;

import android.content.ContentValues;
import android.database.Cursor;

import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.models.Contract;
import net.gumcode.matratrader.models.Market;
import net.gumcode.matratrader.models.Token;

import java.util.ArrayList;

/**
 * Created by A. Fauzi Harismawan on 3/30/2016.
 */
public class Queries {

    private DatabaseHelper db;

    public Queries(DatabaseHelper db) {
        this.db = db;
    }

    public void insertToken(String token, int activated) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TOKEN, token);
        values.put(DatabaseHelper.COLUMN_ACTIVATED, activated);
        db.getWritableDatabase().insert(DatabaseHelper.TABLE_TOKEN, null, values);
    }

    public String getActiveToken() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TOKEN
                + " WHERE " + DatabaseHelper.COLUMN_ACTIVATED + " = 1" , null);
        mCursor.moveToFirst();
        String token = "";
        if (mCursor.getCount() != 0) {
            token = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
        }

        mCursor.close();
        return token;
    }

    public void setContract(Contract contract) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_CONTRACT_ID, contract.id);
        values.put(DatabaseHelper.COLUMN_LONGCODE, contract.longcode);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_CONTRACT, null, values);
    }

    public Contract getContract() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CONTRACT
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        Contract contract = new Contract();
        if (mCursor.getCount() != 0) {
            contract.id = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_CONTRACT_ID));
            contract.longcode = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_LONGCODE));
        }

        mCursor.close();
        return contract;
    }

    public void deleteAll() {
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_CONTRACT);
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_PROFIT);
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_CUTLOSS);
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_STOPPROFIT);
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_STOPLOSS);
    }

    public void deleteContract() {
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_CONTRACT);
    }

    public void setProfit(int profit) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_PROFIT, profit);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_PROFIT, null, values);
    }

    public int getProfit() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROFIT
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        int profit = 0;
        if (mCursor.getCount() != 0) {
            profit = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_PROFIT));
        }

        mCursor.close();
        return profit;
    }

    public void setDate(long date) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_LONGCODE, date);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_DATE, null, values);
    }

    public long getDate() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DATE
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        long date = 0;
        if (mCursor.getCount() != 0) {
            date = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.COLUMN_LONGCODE));
        }

        mCursor.close();
        return date;
    }

    public void deleteDate() {
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_DATE);
    }

    public void setStopProfit(double profit) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_PROFIT, profit);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_STOPPROFIT, null, values);
    }

    public double getStopProfit() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_STOPPROFIT
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        double profit = 0;
        if (mCursor.getCount() != 0) {
            profit = mCursor.getDouble(mCursor.getColumnIndex(DatabaseHelper.COLUMN_PROFIT));
        }

        mCursor.close();
        return profit;
    }

    public void setStopLoss(double profit) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_CUTLOSS, profit);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_STOPLOSS, null, values);
    }

    public double getStopLoss() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_STOPLOSS
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        double profit = 0;
        if (mCursor.getCount() != 0) {
            profit = mCursor.getDouble(mCursor.getColumnIndex(DatabaseHelper.COLUMN_CUTLOSS));
        }

        mCursor.close();
        return profit;
    }

    public void setCutloss(int cutloss) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_CUTLOSS, cutloss);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_CUTLOSS, null, values);
    }

    public int getCutloss() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CUTLOSS
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        int cutloss = 0;
        if (mCursor.getCount() != 0) {
            cutloss = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_CUTLOSS));
        }

        mCursor.close();
        return cutloss;
    }

    public void setCutloss2(int cutloss) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_CUTLOSS, cutloss);
        db.getWritableDatabase().replace(DatabaseHelper.TABLE_CUTLOSS2, null, values);
    }

    public int getCutloss2() {
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CUTLOSS2
                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
        mCursor.moveToFirst();
        int cutloss = 0;
        if (mCursor.getCount() != 0) {
            cutloss = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_CUTLOSS));
        }

        mCursor.close();
        return cutloss;
    }

//    public void setDaily(Daily daily) {
//        ContentValues values = new ContentValues();
//        values.put(DatabaseHelper.COLUMN_ID, 1);
//        values.put(DatabaseHelper.COLUMN_PROFIT, daily.stopProfit);
//        values.put(DatabaseHelper.COLUMN_CUTLOSS, daily.stopLoss);
//        values.put(DatabaseHelper.COLUMN_DAY, daily.day);
//        db.getWritableDatabase().replace(DatabaseHelper.TABLE_DAILY, null, values);
//    }
//
//    public Daily getDaily() {
//        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DAILY
//                + " WHERE " + DatabaseHelper.COLUMN_ID + " = 1" , null);
//        mCursor.moveToFirst();
//        Daily daily = new Daily();
//        if (mCursor.getCount() != 0) {
//            daily.stopProfit = mCursor.getDouble(mCursor.getColumnIndex(DatabaseHelper.COLUMN_PROFIT));
//            daily.stopLoss = mCursor.getDouble(mCursor.getColumnIndex(DatabaseHelper.COLUMN_CUTLOSS));
//            daily.day = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.COLUMN_DAY));
//        }
//
//        mCursor.close();
//        return daily;
//    }

    public void deleteAllToken() {
        db.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.TABLE_TOKEN);
    }

    public void deleteToken(int id) {
        String[] args = { Integer.toString(id) };
        db.getWritableDatabase().delete(DatabaseHelper.TABLE_TOKEN, DatabaseHelper.COLUMN_ID + " = ?", args);
    }

    public ArrayList<Token> getTokenList() {
        ArrayList<Token> list = new ArrayList<>();
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TOKEN, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Token entry = new Token();
                entry.token = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
                entry.activated = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_ACTIVATED));
                list.add(entry);
            } while (mCursor.moveToNext());
        }

        mCursor.close();
        return list;
    }

    public void initMarket() {
        for (int i = 0; i < Constants.MARKET.length; i++) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID, i);
            values.put(DatabaseHelper.COLUMN_LONGCODE, Constants.MARKET[i]);
            values.put(DatabaseHelper.COLUMN_ACTIVATED, 1);
            db.getWritableDatabase().insert(DatabaseHelper.TABLE_MARKET, null, values);
        }
    }

    public ArrayList<Market> getMarket() {
        ArrayList<Market> list = new ArrayList<>();
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MARKET, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Market entry = new Market();
                entry.market = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_LONGCODE));
                entry.activated = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_ACTIVATED)) == 1;
                list.add(entry);
            } while (mCursor.moveToNext());
        }

        mCursor.close();
        return list;
    }

    public ArrayList<Market> getActiveMarket() {
        ArrayList<Market> list = new ArrayList<>();
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MARKET +
                " WHERE " + DatabaseHelper.COLUMN_ACTIVATED + " = 1", null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Market entry = new Market();
                entry.market = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_LONGCODE));
                entry.activated = true;
                list.add(entry);
            } while (mCursor.moveToNext());
        }

        mCursor.close();
        return list;
    }

    public void updateMarket(String market, boolean activated) {
        ContentValues values = new ContentValues();
        String[] args = { market };
        if (activated) {
            values.put(DatabaseHelper.COLUMN_ACTIVATED, 1);
        } else {
            values.put(DatabaseHelper.COLUMN_ACTIVATED, 0);
        }
        db.getWritableDatabase().update(DatabaseHelper.TABLE_MARKET, values, DatabaseHelper.COLUMN_LONGCODE + " = ?", args);
    }
}
