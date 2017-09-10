package net.gumcode.matratrader.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import net.gumcode.matratrader.configs.Constants;

import java.io.IOException;

/**
 * Created by A. Fauzi Harismawan on 4/9/2016.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            // request token that will be used by the server to send push notifications
            Constants.REG_ID = instanceID.getToken(Constants.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + Constants.REG_ID);
            // pass along this data
            synchronized (Constants.SYNC) {
                Constants.SYNC.notify();
                Log.d("SYNC", "NOTIFY");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void sendToServer() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    String data = URLEncoder.encode("reg_id", "UTF-8") + "=" + URLEncoder.encode(Constants.REG_ID, "UTF-8");
//                    InputStream response = HTTPHelper.sendBasicAuthPOSTRequest(Constants.CONNECT_URL, data, account.email, account.password);
//                    if (response != null) {
//                        ObjectMapper mapper = new ObjectMapper();
//                        JsonNode rootNode = mapper.readTree(response);
//                        status = rootNode.get("status").asBoolean();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (status) {
//                    connect.setBackgroundColor(getResources().getColor(R.color.disconnected));
//                    connect.setText("DISCONNECT");
//                } else {
//                    Toast.makeText(MainActivity.this, getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.execute();
//    }
}
