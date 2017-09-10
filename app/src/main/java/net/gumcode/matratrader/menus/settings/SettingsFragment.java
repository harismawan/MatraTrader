package net.gumcode.matratrader.menus.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import net.gumcode.matratrader.R;
import net.gumcode.matratrader.configs.Constants;
import net.gumcode.matratrader.databases.DatabaseHelper;
import net.gumcode.matratrader.databases.Queries;
import net.gumcode.matratrader.menus.account.LoginActivity;
import net.gumcode.matratrader.models.Account;
import net.gumcode.matratrader.utilities.HTTPHelper;
import net.gumcode.matratrader.utilities.JSONParser;
import net.gumcode.matratrader.utilities.PreferenceManager;
import net.gumcode.matratrader.utilities.Utils;
import net.gumcode.matratrader.views.EditTextDialogPreference;
import net.gumcode.matratrader.views.SeekbarDialogPreference;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    private PreferenceManager preferenceManager;
    private Preference balance, marketFilter, logout;
    private SeekbarDialogPreference tradingBalance, profit, cutloss, cutloss2;
    private EditTextDialogPreference stopProfit, stopLoss;
    private AlertDialog dialog;
    private Queries q;
    private ProgressDialog progress;
    private Account account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        DatabaseHelper db = new DatabaseHelper(getActivity());
        q = new Queries(db);

        preferenceManager = new PreferenceManager(getActivity());
        account = preferenceManager.getUser();

        balance = findPreference("pref_balance");
        balance.setSummary("$" + Utils.roundUp(preferenceManager.getBalance()));
        balance.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                refresh();
                return false;
            }
        });

//        marketFilter = findPreference("pref_market_filter");
//        marketFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent change = new Intent(getActivity(), MarketFilterActivity.class);
//                startActivity(change);
//                return false;
//            }
//        });

        logout = findPreference("pref_logout");
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                View header = getActivity().getLayoutInflater().inflate(R.layout.dialog_header, null);
                TextView title = (TextView) header.findViewById(R.id.title);
                title.setText(getString(R.string.sign_out));

                View content = getActivity().getLayoutInflater().inflate(R.layout.dialog_content, null);
                TextView text = (TextView) content.findViewById(R.id.text);
                text.setText(getString(R.string.promt_sign_out));

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
                        disconnect();
                    }
                });


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCustomTitle(header);
                builder.setView(content);

                dialog = builder.create();
                dialog.show();

                return false;
            }
        });

        setSummary();
        refresh();
    }

    private void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            private boolean status;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(getActivity(), "", "Loading...", true);
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
                    q.deleteAllToken();
                    q.deleteAll();
                    preferenceManager.setBalance(0);
                    preferenceManager.setTradingBalance(0);
                    preferenceManager.setHighestProfit(0);
                    preferenceManager.setProposalId("");

                    preferenceManager.setConnected(false);
                    preferenceManager.setProcess(false);
                    preferenceManager.signOut();
                    showAlert(getString(R.string.action_sign_out), getString(R.string.sign_out_success));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        }.execute();
    }

    private void showAlert(String ttl, String ctn) {
        View header = getActivity().getLayoutInflater().inflate(R.layout.dialog_header, null);
        TextView title = (TextView) header.findViewById(R.id.title);
        title.setText(ttl);

        View content = getActivity().getLayoutInflater().inflate(R.layout.dialog_content_one, null);
        TextView text = (TextView) content.findViewById(R.id.text);
        text.setText(ctn);

        Button ok = (Button) content.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(header);
        builder.setView(content);

        dialog = builder.create();
        dialog.show();
    }
    
    private void setSummary() {
        tradingBalance = (SeekbarDialogPreference) findPreference("pref_trading_balance");
        tradingBalance.setSummary(preferenceManager.getTradingBalance() + "% ($" + Utils.roundUp(Utils.calculatePercent(preferenceManager.getTradingBalance(),
                preferenceManager.getBalance())) + ")");

        profit = (SeekbarDialogPreference) findPreference("pref_profit");
        profit.setSummary(q.getProfit() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getProfit(),
                Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");

        cutloss = (SeekbarDialogPreference) findPreference("pref_cutloss");
        cutloss.setSummary(q.getCutloss() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getCutloss(),
                Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");

        cutloss2 = (SeekbarDialogPreference) findPreference("pref_cutloss2");
        cutloss2.setSummary(q.getCutloss2() + "% ($" + Utils.roundUp(Utils.calculatePercent(q.getCutloss2(),
                Utils.calculatePercent(preferenceManager.getTradingBalance(), preferenceManager.getBalance()))) + ")");

        stopProfit = (EditTextDialogPreference) findPreference("pref_stop_profit");
        stopProfit.setSummary("$" + q.getStopProfit());

        stopLoss = (EditTextDialogPreference) findPreference("pref_stop_loss");
        stopLoss.setSummary("$" + q.getStopLoss());
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (HTTPHelper.connectWebSocket()) {
                    Constants.webSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            super.onTextMessage(websocket, text);
                            final Account a = JSONParser.parseAccountInfo(text);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    balance.setSummary("$" + Utils.roundUp(a.balance));
                                    preferenceManager.setBalance(a.balance);

                                    setSummary();
                                }
                            });
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            super.onConnectError(websocket, exception);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    Constants.webSocket.sendText("{ \"authorize\": \"" + q.getActiveToken() + "\" }");
                }
            }
        }).start();
    }
}
