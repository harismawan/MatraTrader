package net.gumcode.matratrader.menus.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import net.gumcode.matratrader.MainMenuActivity;
import net.gumcode.matratrader.R;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.models.Account;
import net.gumcode.matratrader.utilities.HTTPHelper;
import net.gumcode.matratrader.utilities.JSONParser;
import net.gumcode.matratrader.utilities.PreferenceManager;

/**
 * Created by A. Fauzi Harismawan on 4/7/2016.
 */
public class AddTokenActivity extends AppCompatActivity {

    private Queries q;
    private EditText addToken;
    private ProgressDialog progress;
    private PreferenceManager preferenceManager;
    private Account account;
    private AlertDialog dialog;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_token);

        DatabaseHelper db = new DatabaseHelper(this);
        q = new Queries(db);

        preferenceManager = new PreferenceManager(this);
        account = preferenceManager.getUser();

        addToken = (EditText) findViewById(R.id.add_token);
        addToken.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ok || id == EditorInfo.IME_NULL) {
                    initiate();
                    return true;
                }
                return false;
            }
        });

        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiate();
            }
        });
    }

    private void initiate() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addToken.getWindowToken(), 0);
                token = addToken.getText().toString();
                progress = ProgressDialog.show(AddTokenActivity.this, "", "Loading...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            final Account a = JSONParser.parseAccountInfo(text);
                            if (a != null) {
                                q.insertToken(a.binaryToken, 1);
                                // kirim ke server lewat api

                                progress.dismiss();
                                Intent change = new Intent(AddTokenActivity.this, MainMenuActivity.class);
                                startActivity(change);
                                finish();
                            } else {
                                progress.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlert(getString(R.string.error), getString(R.string.invalid_token));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            super.onConnectError(websocket, exception);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(AddTokenActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    Log.d("TOKEN", "__" + token);
                    //Wb6eabI4V2o0uOg Wb6eabl4V2o0uOg
                    Constants.webSocket.sendText("{ \"authorize\": \"" + token + "\" }");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(AddTokenActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
    }

    private void showAlert(String ttl, String ctn) {
        View header = getLayoutInflater().inflate(R.layout.dialog_header, null);
        header.setBackgroundColor(Color.RED);
        TextView title = (TextView) header.findViewById(R.id.title);
        title.setText(ttl);

        View content = getLayoutInflater().inflate(R.layout.dialog_content_one, null);
        content.setBackgroundColor(Color.WHITE);
        TextView text = (TextView) content.findViewById(R.id.text);
        text.setTextColor(Color.DKGRAY);
        text.setText(ctn);

        Button ok = (Button) content.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(AddTokenActivity.this);
        builder.setCustomTitle(header);
        builder.setView(content);

        dialog = builder.create();
        dialog.show();
    }
}
