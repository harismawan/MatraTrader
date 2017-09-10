package net.gumcode.matratrader.menus.report;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.adapters.ColumnContentAdapter;
import net.gumcode.matratrader.adapters.ColumnHeaderAdapter;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.models.Account;
import net.gumcode.matratrader.models.Report;
import net.gumcode.matratrader.utilities.HTTPHelper;
import net.gumcode.matratrader.utilities.JSONParser;
import net.gumcode.matratrader.utilities.Utils;

import java.util.List;

import de.codecrafters.tableview.SortableTableView;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class ReportActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private Queries q;
    private List<Report> r;
    private SortableTableView tableView;
    private double total;
    private TextView totalT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = new DatabaseHelper(this);
        q = new Queries(db);

        initiate();

        tableView = (SortableTableView) findViewById(R.id.table);
        tableView.setHeaderAdapter(new ColumnHeaderAdapter(this, getResources().getStringArray(R.array.table_title)));

        totalT = (TextView) findViewById(R.id.total);
    }

    private void initiate() {
        new AsyncTask<Void, Void, Void>() {

            private boolean status = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(ReportActivity.this, "", "Loading...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            Log.d("RESPONSE", text);
                            if (status) {
                                r = JSONParser.parseReportList(text);
                                if (r != null) {
                                    Log.d("MASOOOOOOKKK", "ADADJSJDSJDLSJLKDJSLKDJLKSJDKLJDLJKLSDJLKDJKSJDKLJDLSJLDK");
                                    for (int i = 0; i < 10; i++) {
                                        if (r.get(i).sell > r.get(i).buy) {
                                            total += r.get(i).sell - r.get(i).buy;
                                        } else if (r.get(i).sell < r.get(i).buy) {
                                            total -= r.get(i).buy - r.get(i).sell;
                                        }
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalT.setText("$" + Utils.roundUp(total));
                                            tableView.setDataAdapter(new ColumnContentAdapter(ReportActivity.this, r));
                                            progress.dismiss();
                                        }
                                    });
//                                    for (int i = 0; i < r.size(); i++) {
//                                        process(i, r.get(i).market);
//                                        Log.d("PROCESS", i + " - " + r.get(i).market);
//                                    }
                                }
                            }

                            Account temp = JSONParser.parseAccountInfo(text);
                            if (temp != null) {
                                status = true;
                                Constants.webSocket.sendText("{ \"profit_table\": 1 }");
                            }
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            super.onConnectError(websocket, exception);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(ReportActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ReportActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
    }

//    private void process(final int index, final String id) {
//        new AsyncTask<Void, Void, Void>() {
//
//            private boolean status = false;
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                if (HTTPHelper.connectWebSocket()) {
//                    Constants.webSocket.addListener(new WebSocketAdapter() {
//                        @Override
//                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
//                            super.onTextMessage(websocket, text);
//                            Log.d("RESPONSE", text);
//
//                            if (status) {
//                                String market = JSONParser.parseMarket(text);
//                                if (!TextUtils.isEmpty(market)) {
//                                    r.get(index).market = market;
//                                    Log.d("MARKET", r.get(index).market);
//                                }
//                            }
//
//                            Account temp = JSONParser.parseAccountInfo(text);
//                            if (temp != null) {
//                                status = true;
//                                Constants.webSocket.sendText(
//                                        "{ \"proposal_open_contract\": 1," +
//                                                "\"contract_id\": " + id + " }");
//                            }
//                        }
//
//                        @Override
//                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
//                            super.onConnectError(websocket, exception);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    progress.dismiss();
//                                    Toast.makeText(ReportActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    });
//                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progress.dismiss();
//                            Toast.makeText(ReportActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                return null;
//            }
//        }.execute();
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
