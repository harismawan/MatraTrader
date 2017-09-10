package net.gumcode.matratrader.menus.status;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.menus.settings.SettingsActivity;
import net.gumcode.matratrader.models.Account;
import net.gumcode.matratrader.services.RegistrationIntentService;
import net.gumcode.matratrader.utilities.HTTPHelper;
import net.gumcode.matratrader.utilities.JSONParser;
import net.gumcode.matratrader.utilities.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private Queries q;

    private AlertDialog dialog;
    private TextView token, detailCR, email, tokenS, validity, emailS, pingT;
    private Button connect;
    private PreferenceManager preferenceManager;
    private Account account;
    private ProgressDialog progress;
    private WebSocket ws;
    private Handler timer;
    private boolean stopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        token = (TextView) findViewById(R.id.token);
        detailCR = (TextView) findViewById(R.id.detailCR);
        email = (TextView) findViewById(R.id.email);
        tokenS = (TextView) findViewById(R.id.tokenS);
        validity = (TextView) findViewById(R.id.validity);
        emailS = (TextView) findViewById(R.id.emailS);
        pingT = (TextView) findViewById(R.id.ping);

        DatabaseHelper db = new DatabaseHelper(this);
        q = new Queries(db);

        preferenceManager = new PreferenceManager(this);
        account = preferenceManager.getUser();

        initiate();

        connect = (Button) findViewById(R.id.connect);
        if (preferenceManager.getConnected()) {
            connect.setBackgroundResource(R.drawable.rounded_1);
            connect.setText("DISCONNECT");
        } else {
            connect.setBackgroundResource(R.drawable.rounded_2);
            connect.setText("CONNECT");
        }
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferenceManager.getConnected()) {
                    if (preferenceManager.getAvailable()) {
                        showTermCondition();
                    } else {
                        showAlert3();
                    }
                } else {
                    disconnect();
                }
            }
        });

        timer = new Handler();
        timer.postDelayed(new Runnable() {
            long awal;

            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketFactory factory = new WebSocketFactory();
                        try {
                            ws = factory.createSocket(Constants.SOCKET_URL);
                            ws.connect();

                            ws.addListener(new WebSocketAdapter() {
                                @Override
                                public void onTextMessage(WebSocket websocket, final String text) throws Exception {
                                    super.onTextMessage(websocket, text);
                                    Log.d("PING", text);
                                    final long akhir = System.currentTimeMillis();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pingT.setText(Long.toString(akhir - awal));
                                        }
                                    });
                                }
                            });

                            awal = System.currentTimeMillis();
                            ws.sendText("{ \"ping\": 1 }");
                        } catch (IOException | WebSocketException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                if (!stopped) {
                    timer.postDelayed(this, 10000);
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopped = true;
        ws.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopped = true;
        ws.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        account = preferenceManager.getUser();
    }

    private void initiate() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(MainActivity.this, "", "Loading...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            InputStream response = HTTPHelper.sendBasicAuthGETRequest(Constants.SIGN_IN_URL, account.email, account.password);
                            if (response != null) {
                                Account temp = JSONParser.parseSignInResponse(response);
                                if (temp != null) {
                                    temp.password = account.password;
                                    preferenceManager.signIn(temp);
                                    account = preferenceManager.getUser();
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                                }
                            }

                            final Account a = JSONParser.parseAccountInfo(text);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    token.setText(a.binaryToken);
                                    detailCR.setText(a.account);
                                    email.setText(a.email);

                                    tokenS.setText(account.serverToken);
                                    validity.setText(account.validity);
                                    emailS.setText(account.email);

                                    progress.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            super.onConnectError(websocket, exception);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
    }

    private void showTermCondition() {
        View header = getLayoutInflater().inflate(R.layout.dialog_header, null);
        TextView title = (TextView) header.findViewById(R.id.title);
        title.setText(getString(R.string.terms));

        View content = getLayoutInflater().inflate(R.layout.dialog_content, null);
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
                if (!preferenceManager.getConnected()) {
                    if (q.getStopProfit() == 0 || q.getStopLoss() == 0 || q.getProfit() == 0 ||
                            q.getCutloss() == 0 || preferenceManager.getTradingBalance() == 0) {
                        showAlert2();
                    } else {
                        connect();
                    }
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(header);
        builder.setView(content);

        dialog = builder.create();
        dialog.show();
    }

    private void connect() {
        new AsyncTask<Void, Void, Void>() {
            private boolean status;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(MainActivity.this, "", "Loading...", true);
                Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
                startService(intent);
            }

            @Override
            protected Void doInBackground(Void... params) {
//                Looper.prepare();
                synchronized (Constants.SYNC) {
                    try {
                        Constants.SYNC.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    String data = URLEncoder.encode("reg_id", "UTF-8") + "=" + URLEncoder.encode(Constants.REG_ID, "UTF-8");
                    InputStream response = HTTPHelper.sendBasicAuthPOSTRequest(Constants.CONNECT_URL, data, account.email, account.password);
                    if (response != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(response);
                        status = rootNode.get("status").asBoolean();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (status) {
                    preferenceManager.setConnected(true);
                    connect.setBackgroundResource(R.drawable.rounded_1);
                    connect.setText("DISCONNECT");
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        }.execute();
    }

    private void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            private boolean status;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(MainActivity.this, "", "Loading...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
//                Looper.prepare();
                try {
                    InputStream response = HTTPHelper.sendBasicAuthGETRequest(Constants.DISCONNECT_URL, account.email, account.password);
                    if (response != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(response);
                        status = rootNode.get("status").asBoolean();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (status) {
                    preferenceManager.setConnected(false);
                    connect.setBackgroundResource(R.drawable.rounded_2);
                    connect.setText("CONNECT");
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        }.execute();
    }

    private void showAlert2() {
        View header = getLayoutInflater().inflate(R.layout.dialog_header, null);
        header.setBackgroundColor(Color.RED);
        TextView title = (TextView) header.findViewById(R.id.title);
        title.setText(getString(R.string.error));

        View content = getLayoutInflater().inflate(R.layout.dialog_content_one, null);
        TextView text = (TextView) content.findViewById(R.id.text);
        text.setText(getString(R.string.parameter));

        Button ok = (Button) content.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent change = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(change);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(header);
        builder.setView(content);

        dialog = builder.create();
        dialog.show();
    }

    private void showAlert3() {
        View header = getLayoutInflater().inflate(R.layout.dialog_header, null);
        header.setBackgroundColor(Color.RED);
        TextView title = (TextView) header.findViewById(R.id.title);
        title.setText(getString(R.string.error));

        View content = getLayoutInflater().inflate(R.layout.dialog_content_one, null);
        TextView text = (TextView) content.findViewById(R.id.text);
        text.setText(getString(R.string.limit));

        Button ok = (Button) content.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(header);
        builder.setView(content);

        dialog = builder.create();
        dialog.show();
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            showAlert();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                initiate();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    //
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        Intent change;
//        if (id == R.id.nav_trading_progress) {
//            change = new Intent(MainActivity.this, ProgressActivity.class);
//            startActivity(change);
//        } else if (id == R.id.nav_trading_report) {
//            change = new Intent(MainActivity.this, ReportActivity.class);
//            startActivity(change);
//        } else if (id == R.id.nav_settings) {
//            change = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(change);
//        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}