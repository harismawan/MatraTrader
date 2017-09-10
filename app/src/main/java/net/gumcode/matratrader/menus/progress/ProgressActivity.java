package net.gumcode.matratrader.menus.progress;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.adapters.ColumnContentAdapter2;
import net.gumcode.matratrader.adapters.ColumnContentAdapter3;
import net.gumcode.matratrader.adapters.ColumnHeaderAdapter;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.models.Progress;
import net.gumcode.matratrader.models.Stream;
import net.gumcode.matratrader.utilities.JSONParser;

import java.io.IOException;
import java.util.ArrayList;

import de.codecrafters.tableview.SortableTableView;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class ProgressActivity extends AppCompatActivity {

    private TextView status;
    private Queries q;
    private WebSocket ws, wsSell;
    private SortableTableView tableView, tableView2;
    private ArrayList<Progress> r;
    private ColumnContentAdapter2 adapter;
    private Button sell;
    private ProgressDialog progress;
    private Stream a;
    private ColumnContentAdapter3 adapter2;
//    private TextView bidPrice;
//    private TextView entrySpot;
//    private TextView currentSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = new DatabaseHelper(this);
        q = new Queries(db);

        status = (TextView) findViewById(R.id.status);
        if (q.getContract().longcode != null) {
            status.setText(q.getContract().longcode);
        }

        tableView = (SortableTableView) findViewById(R.id.table);
        tableView.setHeaderAdapter(new ColumnHeaderAdapter(this, getResources().getStringArray(R.array.table_title2)));

        tableView2 = (SortableTableView) findViewById(R.id.table2);
        tableView2.setHeaderAdapter(new ColumnHeaderAdapter(this, getResources().getStringArray(R.array.table_title3)));

        sell = (Button) findViewById(R.id.sell);
        sell.setEnabled(false);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sell();
            }
        });

        r = new ArrayList<>();
        Progress p = new Progress();
        r.add(p);
        adapter = new ColumnContentAdapter2(ProgressActivity.this, r);
        tableView.setDataAdapter(adapter);

        adapter2 = new ColumnContentAdapter3(ProgressActivity.this, r);
        tableView2.setDataAdapter(adapter2);
//        bidPrice = (TextView) findViewById(R.id.bid_price);
//        entrySpot = (TextView) findViewById(R.id.entry_spot);
//        currentSpot = (TextView) findViewById(R.id.current_spot);

        initiate();

        final Handler pasd = new Handler();
        pasd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (q.getContract().id != null) {
                    pasd.postDelayed(this, 1000);
                } else {
                    status.setText(getString(R.string.waiting));
                    r.get(0).entrySpot = 0;
                    r.get(0).currentSpot = 0;
                    r.get(0).bidPrice = 0;
                    r.get(0).entryTime = 0;
                    r.get(0).currentTime = 0;
                    r.get(0).expiry = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sell.setBackgroundResource(R.drawable.rounded_1);
                            sell.setEnabled(false);
                            adapter.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                        }
                    });
                }
            }
        }, 1000);
    }

    private void sell() {
        progress = ProgressDialog.show(ProgressActivity.this, "", "Loading...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (a.isValidSale == 1) {
                    WebSocketFactory factory = new WebSocketFactory();
                    try {
                        wsSell = factory.createSocket(Constants.SOCKET_URL);
                        wsSell.connect();

                        wsSell.addListener(new WebSocketAdapter() {
                            @Override
                            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                                super.onTextMessage(websocket, text);
                                if (JSONParser.parseSellResponse(text)) {
                                    q.deleteContract();
                                    wsSell.disconnect();
                                    progress.dismiss();
                                    Log.d("RESPONSE", "SELLED");
                                } else {
                                    wsSell.sendText(
                                            "{ \"sell\": \"" + q.getContract().id + "\", " +
                                                    "\"price\": " + r.get(0).bidPrice + " }");
                                }
                            }

                            @Override
                            public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                                super.onConnectError(websocket, exception);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Toast.makeText(ProgressActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        wsSell.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                    } catch (IOException | WebSocketException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(ProgressActivity.this, "Cannot sell contract yet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void initiate() {
        new AsyncTask<Void, Void, Void>() {

            private int count = 0;
            private boolean aaa = true;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(ProgressActivity.this, "", "Loading...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                WebSocketFactory factory = new WebSocketFactory();
                try {
                    ws = factory.createSocket(Constants.SOCKET_URL);
                    ws.connect();

                    ws.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            Log.d("RESPONSE", text);
                            if (q.getContract().id != null) {
                                if (aaa) {
                                    ws.sendText(
                                            "{ \"proposal_open_contract\": 1," +
                                                    "\"contract_id\": " + q.getContract().id + ", " +
                                                    "\"subscribe\": 1 }");
                                    aaa = false;
                                }

                                a = JSONParser.parseStream(text);
                                if (a != null) {
                                    count++;
                                    if (count == 30) {
                                        Log.d("STREAM", "RESET");
                                        count = 0;
                                        ws.disconnect();
                                        ws.connect();
                                        aaa = true;
                                        ws.addListener(this);
                                        ws.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                                    }

                                    if (a.isExpired == 1) {
                                        ws.disconnect();
                                    }

                                    progress.dismiss();

                                    if (a.isValidSale == 1) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                sell.setBackgroundResource(R.drawable.rounded_2);
                                                sell.setEnabled(true);
                                            }
                                        });
                                    }

                                    r.get(0).entrySpot = a.entrySpot;
                                    r.get(0).currentSpot = a.currentSpot;
                                    r.get(0).bidPrice = a.bidPrice;
                                    r.get(0).entryTime = a.entrySpotTime;
                                    r.get(0).currentTime = a.currentSpotTime;
                                    r.get(0).expiry = a.expireTime;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                            adapter2.notifyDataSetChanged();
                                        }
                                    });

//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {

//                                            bidPrice.setText("$" + a.bidPrice);
//                                            entrySpot.setText(Double.toString(a.entrySpot));
//                                            currentSpot.setText(Double.toString(a.currentSpot));
//                                        }
//                                    });
                                }
                            } else {
                                progress.dismiss();
                                ws.disconnect();
                            }
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            super.onConnectError(websocket, exception);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(ProgressActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    });
                    ws.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                } catch (IOException | WebSocketException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
