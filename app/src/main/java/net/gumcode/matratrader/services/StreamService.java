package net.gumcode.matratrader.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.models.Stream;
import net.gumcode.matratrader.utilities.HTTPHelper;
import net.gumcode.matratrader.utilities.JSONParser;
import net.gumcode.matratrader.utilities.PreferenceManager;
import net.gumcode.matratrader.utilities.Utils;

/**
 * Created by A. Fauzi Harismawan on 4/8/2016.
 */
public class StreamService extends Service {

    private Queries q;
    private PreferenceManager preferenceManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseHelper db = new DatabaseHelper(StreamService.this);
        q = new Queries(db);

        preferenceManager = new PreferenceManager(this);

        stream();
        return START_STICKY;
    }

//    private void sell(final long bidPrice) {
//        new AsyncTask<Void, Void, Void>() {
//
//            private boolean first = true;
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                WebSocketFactory factory = new WebSocketFactory();
//                try {
//                    sellWS = factory.createSocket(Constants.SOCKET_URL);
//                    sellWS.connect();
//
//                    sellWS.addListener(new WebSocketAdapter() {
//                        @Override
//                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
//                            super.onTextMessage(websocket, text);
//                            Log.d("RESPONSE_SELL", text);
//                            sellWS.sendText(
//                                    "{ \"sell\": \"" + q.getContract().id + "\", " +
//                                            "\"price\": " + bidPrice + " }");
//
//                            if (!first) {
//                                if (JSONParser.parseSellResponse(text)) {
//                                    sellWS.disconnect();
//                                    q.deleteContract();
//                                    stopSelf();
//                                } else {
//                                    sellWS.sendText(
//                                            "{ \"sell\": \"" + q.getContract().id + "\", " +
//                                                    "\"price\": " + bidPrice + " }");
//                                }
//                            } else {
//                                first = false;
//                            }
//                        }
//
//                        @Override
//                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
//                            super.onConnectError(websocket, exception);
//                            Toast.makeText(StreamService.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
//                } catch (IOException | WebSocketException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
//    }

    private void stream() {
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                        .setContentTitle(getString(R.string.trading_in_progress))
                        .setContentText(q.getContract().longcode);

        startForeground(940408, notification.build());

        new AsyncTask<Void, Void, Void>() {

            boolean first = true;
            boolean second = true;
            boolean active = false;
            int count = 0;

            @Override
            protected Void doInBackground(Void... params) {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            if (q.getContract().id != null) {
                                Log.d("RESPONSE", text);

                                if (second) {
                                    Constants.webSocket.sendText(
                                            "{ \"proposal_open_contract\": 1," +
                                                    "\"contract_id\": " + q.getContract().id + ", " +
                                                    "\"subscribe\": 1 }");
                                    second = false;
                                }

                                Stream a = JSONParser.parseStream(text);
                                if (a != null) {
                                    count++;
                                    if (count == 30) {
                                        Log.d("STREAM", "RESET");
                                        count = 0;
                                        HTTPHelper.disconnectWebSocket();
                                        HTTPHelper.connectWebSocket();
                                        second = true;
                                        Constants.webSocket.addListener(this);
                                        Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                                    }

                                    if (a.isExpired == 1) {
                                        stopSelf();
                                    }

                                    if (a.isValidSale == 1) {
                                        if (first) {
                                            preferenceManager.setHighestProfit(a.buyPrice);
                                            first = false;
                                        } else if (a.bidPrice > preferenceManager.getHighestProfit()) {
                                            preferenceManager.setHighestProfit(a.bidPrice);
                                        }

                                        if (a.bidPrice >= (a.buyPrice + Utils.calculatePercent(q.getCutloss(), a.buyPrice) + (Utils.calculatePercent(5, a.buyPrice))) && !active) {
                                            active = true;
                                        }

                                        if (a.bidPrice >= (a.buyPrice + Utils.calculatePercent(q.getProfit(), a.buyPrice))) {
                                            Log.d("STREAM", "PROFIT");
                                            Constants.webSocket.sendText(
                                                    "{ \"sell\": \"" + q.getContract().id + "\", " +
                                                            "\"price\": " + a.bidPrice + " }");
//                                        if (JSONParser.parseSellResponse(text)) {
                                            HTTPHelper.disconnectWebSocket();
                                            q.deleteContract();
                                            stopSelf();
//                                        }
//                                        } else if (a.bidPrice <= (Utils.calculatePercent(50, a.buyPrice))) {
//                                            Log.d("STREAM", "STOPLOSS");
//                                            Constants.webSocket.sendText(
//                                                    "{ \"sell\": \"" + q.getContract().id + "\", " +
//                                                            "\"price\": " + a.bidPrice + " }");
////                                        if (JSONParser.parseSellResponse(text)) {
//                                            HTTPHelper.disconnectWebSocket();
//                                            q.deleteContract();
//                                            stopSelf();
////                                        }
                                        } else if (a.bidPrice <= (a.buyPrice - Utils.calculatePercent(q.getCutloss2(), a.buyPrice))) {
                                            Log.d("STREAM", "CUTLOSS2");
                                            Constants.webSocket.sendText(
                                                    "{ \"sell\": \"" + q.getContract().id + "\", " +
                                                            "\"price\": " + a.bidPrice + " }");
//                                        if (JSONParser.parseSellResponse(text)) {
                                            HTTPHelper.disconnectWebSocket();
                                            q.deleteContract();
                                            stopSelf();
//                                        }
                                        } else if (a.bidPrice <= (preferenceManager.getHighestProfit() - Utils.calculatePercent(q.getCutloss(), a.buyPrice))) {
                                            Log.d("STREAM", "CUTLOSS");
                                            if (active) {
                                                Constants.webSocket.sendText(
                                                        "{ \"sell\": \"" + q.getContract().id + "\", " +
                                                                "\"price\": " + a.bidPrice + " }");
//                                        if (JSONParser.parseSellResponse(text)) {
                                                HTTPHelper.disconnectWebSocket();
                                                q.deleteContract();
                                                stopSelf();
//                                        }
                                            }
                                        }
                                    }
//                                if (a.bidPrice >= (a.buyPrice + Utils.calculatePercent(preferenceManager.getProfit(), Utils.calculatePercent(preferenceManager.getTradingBalance(),
//                                        preferenceManager.getBalance())))) {
//                                    Log.d("STREAM", "PROFIT");
//                                } else if (a.bidPrice <= (preferenceManager.getHighestProfit() - (Utils.calculatePercent(preferenceManager.getCutloss(),
//                                        Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))))) {
//                                    Log.d("STREAM", "CUTLOSS");
//                                } else if (a.bidPrice <= (Utils.calculatePercent(50, Utils.calculatePercent(preferenceManager.getCutloss(),
//                                        Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))))) {
//                                    Log.d("STREAM", "STOPLOSS");
//                                }

                                    Log.d("STREAM", "A : " + (a.buyPrice + Utils.calculatePercent(q.getProfit(), a.buyPrice)));
                                    Log.d("STREAM", "B : " + (preferenceManager.getHighestProfit() - (Utils.calculatePercent(q.getCutloss(), a.buyPrice))));
                                    Log.d("STREAM", "C : " + (Utils.calculatePercent(50, a.buyPrice)));

                                    Log.d("STREAM", "IS_VALID_TO_SELL : " + a.isValidSale);
                                    Log.d("STREAM", "BID_PRICE : " + a.bidPrice);
                                    Log.d("STREAM", "HIGHEST_PRICE : " + preferenceManager.getHighestProfit());
                                    Log.d("STREAM", "STATUS NUM : " + (a.buyPrice + Utils.calculatePercent(q.getCutloss(), a.buyPrice) + (Utils.calculatePercent(5, a.buyPrice))));
                                    Log.d("STREAM", "STATUS : " + active);
                                    Log.d("STREAM", "COUNT : " + count);
                                }
                            } else {
                                stopSelf();
                            }

                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            super.onConnectError(websocket, exception);
                            Toast.makeText(StreamService.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                    Log.d("CONTRACT_ID_SERVICE", q.getContract().id);
                } else {
                    Toast.makeText(StreamService.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
