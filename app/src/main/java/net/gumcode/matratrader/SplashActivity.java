package net.gumcode.matratrader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.menus.account.AddTokenActivity;
import net.gumcode.matratrader.menus.account.LoginActivity;
import net.gumcode.matratrader.utilities.PreferenceManager;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class SplashActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DatabaseHelper db = new DatabaseHelper(this);
        final Queries q = new Queries(db);

        preferenceManager = new PreferenceManager(this);
        Log.d("SESSION", preferenceManager.getUser().email + preferenceManager.getUser().password);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
//                    if (q.getMarket().size() == 0) {
//                        q.initMarket();
//                    }

                    if (TextUtils.isEmpty(preferenceManager.getUser().email) && TextUtils.isEmpty(preferenceManager.getUser().password)) {
                        Intent change = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(change);
                    } else if (TextUtils.isEmpty(q.getActiveToken())) {
                        Intent change = new Intent(SplashActivity.this, AddTokenActivity.class);
                        startActivity(change);
                    } else {
                        Intent change = new Intent(SplashActivity.this, MainMenuActivity.class);
                        startActivity(change);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
    }
}
