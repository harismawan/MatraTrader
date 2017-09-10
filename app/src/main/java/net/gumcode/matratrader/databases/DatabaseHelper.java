package net.gumcode.matratrader.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.gumcode.matratrader.utilities.PreferenceManager;

/**
 * Created by A. Fauzi Harismawan on 3/30/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "trade";
    private static int SCHEMA_VERSION = 5;

    public static String TABLE_TOKEN = "TOKEN";
    public static String TABLE_CONTRACT = "CONTRACT";
    public static String TABLE_PROFIT = "PROFIT";
    public static String TABLE_CUTLOSS = "CUTLOSS";
    public static String TABLE_STOPPROFIT = "STOPPROFIT";
    public static String TABLE_STOPLOSS = "STOPLOSS";
    public static String TABLE_CUTLOSS2 = "CUTLOSS2";
    public static String TABLE_MARKET = "MARKET";
    public static String TABLE_DATE = "DTE";

    public static String COLUMN_ID = "ID";
    public static String COLUMN_TOKEN = "TOKEN";
    public static String COLUMN_ACTIVATED = "ACTIVATED";
    public static String COLUMN_CONTRACT_ID = "CONTRACT_ID";
    public static String COLUMN_LONGCODE = "LONGCODE";
    public static String COLUMN_PROFIT = "PROFIT";
    public static String COLUMN_CUTLOSS = "CUTLOSS";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TOKEN + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TOKEN + " VARCHAR NOT NULL, "
                + COLUMN_ACTIVATED + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTRACT + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_CONTRACT_ID + " VARCHAR NOT NULL, "
                + COLUMN_LONGCODE + " VARCHAR NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROFIT + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_PROFIT + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUTLOSS + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_CUTLOSS + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUTLOSS2 + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_CUTLOSS + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STOPPROFIT + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_PROFIT + " DOUBLE NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STOPLOSS + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_CUTLOSS + " DOUBLE NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MARKET + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_LONGCODE + " VARCHAR NOT NULL, "
                + COLUMN_ACTIVATED + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DATE + " ("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_LONGCODE + " LONG NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTRACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUTLOSS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUTLOSS2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPPROFIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPLOSS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATE);

        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setBalance(0);
        preferenceManager.setTradingBalance(0);
        preferenceManager.setHighestProfit(0);
        preferenceManager.setProposalId("");

        preferenceManager.setConnected(false);
        preferenceManager.setProcess(false);
        preferenceManager.signOut();
    }
}
