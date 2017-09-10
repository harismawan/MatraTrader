package net.gumcode.matratrader.services;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.gcm.GcmListenerService;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.models.Account;
import net.gumcode.matratrader.models.Contract;
import net.gumcode.matratrader.utilities.HTTPHelper;
import net.gumcode.matratrader.utilities.JSONParser;
import net.gumcode.matratrader.utilities.PreferenceManager;
import net.gumcode.matratrader.utilities.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


/**
 * Created by A. Fauzi Harismawan on 9/15/2015.
 */

public class GCMMessageHandler extends GcmListenerService {


    private PreferenceManager preferenceManager;
    private Queries q;
    private String contract_type;
    private String symbol;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        DatabaseHelper db = new DatabaseHelper(this);
        q = new Queries(db);

        preferenceManager = new PreferenceManager(this);

        contract_type = data.getString("contract_type");
        symbol = data.getString("symbol");

        if (symbol.equals("KICK")) {
            preferenceManager.setConnected(false);
        } else {
            if (preferenceManager.getAvailable()) {
                if (!preferenceManager.getProcess()) {
                    Log.d("PROCESS", "FALSE");
                    if (q.getContract().id == null) {
                        if (preferenceManager.getBalance() <= q.getStopProfit() && q.getStopLoss() <= preferenceManager.getBalance()) {
                            preferenceManager.setProcess(true);
                            updateBalance();
                        } else {
                            Calendar cal = Calendar.getInstance();
                            preferenceManager.setAvailable(false);
                            q.setDate(cal.getTimeInMillis());
                            disconnect();
                        }
//                    boolean process = false;
//                    ArrayList<Market> markets = q.getActiveMarket();
//                    for (int i = 0; i < markets.size(); i++) {
//                        if (markets.get(i).market.equals(symbol)) {
//                            process = true;
//                            updateBalance();
//                            break;
//                        }
//                    }
//                    preferenceManager.setProcess(process);
                    }
                } else {
                    Log.d("PROCESS", "TRUE");
                }
            }
        }
    }

    private void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            private boolean status;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Account account = preferenceManager.getUser();
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
                } else {
                    Toast.makeText(GCMMessageHandler.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateBalance() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            Account ac = JSONParser.parseAccountInfo(text);
                            Log.d("RESPONSE", text);
                            if (ac != null) {
                                preferenceManager.setBalance(ac.balance);
                                Log.d("NEW BALANCE", " " + ac.balance);
                                purchase();
                            }
                        }

//                        @Override
//                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
//                            super.onConnectError(websocket, exception);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    progress.dismiss();
//                                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
                    });
                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                }
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progress.dismiss();
//                            Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
                return null;
            }
        }.execute();
    }

    private void purchase() {
        new AsyncTask<Void, Void, Void>() {
            boolean buy = false;

            @Override
            protected Void doInBackground(Void... params) {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            Log.d("RESPONSE", "RESPONSE RECEIVED");
                            Log.d("RESPONSE", text);
                            if (!buy) {
                                final String a = JSONParser.parseProposal(text);
                                if (!TextUtils.isEmpty(a)) {
                                    preferenceManager.setProposalId(a);
                                    Log.d("PROPOSAL_ID", preferenceManager.getProposalId());
                                    Constants.webSocket.sendText(
                                            "{ \"buy\": \"" + preferenceManager.getProposalId() + "\"," +
                                                    "\"price\": \"" + (Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance())) + "\" }");
                                    Log.d("SEND",
                                            "{ \"buy\": \"" + preferenceManager.getProposalId() + "\"," +
                                                    "\"price\": \"" + (Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance())) + "\" }");
                                    Log.d("PROSES", "BUY");
                                    buy = true;
                                }
                            } else {
                                Contract c = JSONParser.parseContract(text);
                                if (c != null) {
//                                    preferenceManager.setContract(c);
                                    q.setContract(c);
                                    Log.d("CONTRACT_ID", q.getContract().id);
                                    Intent service = new Intent(getBaseContext(), StreamService.class);
                                    startService(service);
                                    buy = false;
                                    preferenceManager.setProcess(false);
                                }
                            }
                        }
//
//                        @Override
//                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
//                            super.onConnectError(websocket, exception);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    progress.dismiss();
//                                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
                    });
                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                    Constants.webSocket.sendText(
                            "{ \"proposal\": \"1\"," +
                                    "\"amount\": \"" + (Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance())) + "\"," +
                                    "\"basis\": \"stake\"," +
                                    "\"contract_type\": \"" + contract_type + "\"," +
                                    "\"currency\": \"USD\"," +
                                    "\"duration\": \"20\"," +
                                    "\"duration_unit\": \"m\"," +
                                    "\"symbol\": \"" + symbol + "\" }");
                    Log.d("SEND",
                            "{ \"proposal\": \"1\"," +
                                    "\"amount\": \"" + (Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance())) + "\"," +
                                    "\"basis\": \"stake\"," +
                                    "\"contract_type\": \"" + contract_type + "\"," +
                                    "\"currency\": \"USD\"," +
                                    "\"duration\": \"20\"," +
                                    "\"duration_unit\": \"m\"," +
                                    "\"symbol\": \"" + symbol + "\" }");
                }
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progress.dismiss();
//                            Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
                return null;
            }
        }.execute();
    }
}