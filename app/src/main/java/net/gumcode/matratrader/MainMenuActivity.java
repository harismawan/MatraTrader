package net.gumcode.matratrader;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.menus.status.MainActivity;
import net.gumcode.matratrader.menus.progress.ProgressActivity;
import net.gumcode.matratrader.menus.report.ReportActivity;
import net.gumcode.matratrader.menus.settings.SettingsActivity;
import net.gumcode.matratrader.utilities.PreferenceManager;

import java.util.Calendar;

/**
 * Created by A. Fauzi Harismawan on 4/11/2016.
 */
public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog dialog;
    private RelativeLayout a1, a2, a3, a4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        DatabaseHelper db = new DatabaseHelper(this);
        Queries q = new Queries(db);
        PreferenceManager pf = new PreferenceManager(this);

        a1 = (RelativeLayout) findViewById(R.id.a1);
        a2 = (RelativeLayout) findViewById(R.id.a2);
        a3 = (RelativeLayout) findViewById(R.id.a3);
        a4 = (RelativeLayout) findViewById(R.id.a4);
        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
        a3.setOnClickListener(this);
        a4.setOnClickListener(this);

        Calendar tdy = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(q.getDate());

        if (q.getDate() != 0) {
            if (tdy.get(Calendar.DAY_OF_YEAR) > cal.get(Calendar.DAY_OF_YEAR)) {
                pf.setAvailable(true);
                q.deleteDate();
            }
        }
    }



    private void showAlert() {
        View header = getLayoutInflater().inflate(R.layout.dialog_header, null);
        TextView title = (TextView) header.findViewById(R.id.title);
        title.setText(getString(R.string.exit));

        View content = getLayoutInflater().inflate(R.layout.dialog_content, null);
        TextView text = (TextView) content.findViewById(R.id.text);
        text.setText(getString(R.string.promt_exit));

        Button cancel = (Button) content.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        Button ok = (Button) content.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
        builder.setCustomTitle(header);
        builder.setView(content);

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    @Override
    public void onClick(View v) {
        Intent change;
        switch (v.getId()) {
            case R.id.a1:
                change = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(change);
                break;
            case R.id.a2:
                change = new Intent(MainMenuActivity.this, ProgressActivity.class);
                startActivity(change);
                break;
            case R.id.a3:
                change = new Intent(MainMenuActivity.this, ReportActivity.class);
                startActivity(change);
                break;
            case R.id.a4:
                change = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(change);
                break;
        }
    }
}
