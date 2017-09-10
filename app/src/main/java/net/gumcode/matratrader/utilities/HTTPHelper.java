package net.gumcode.matratrader.utilities;

import android.util.Base64;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import net.gumcode.matratrader.configs.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by A. Fauzi Harismawan on 3/29/2016.
 */
public class HTTPHelper {


    public static boolean connectWebSocket() {
        WebSocketFactory factory = new WebSocketFactory();
        try {
            Constants.webSocket = factory.createSocket(Constants.SOCKET_URL);
            Constants.webSocket.connect();
            return true;
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void disconnectWebSocket() {
        Constants.webSocket.disconnect();
    }

    public static InputStream sendPOSTRequest(String ur, String data) {
        try {
            URL url = new URL(ur);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(data);
            streamWriter.flush();

            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream sendBasicAuthPOSTRequest(String ur, String data, String username, String password) {
        String credentials = (username + ":" + password);
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        try {
            URL url = new URL(ur);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
            connection.setDoOutput(true);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(data);
            streamWriter.flush();

            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream sendBasicAuthGETRequest(String ur, String username, String password) {
        String credentials = (username + ":" + password);
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        try {
            URL url = new URL(ur);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);

            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static InputStream sendJSONtoServer(String url, Content content) {
//        try {
//            URL ur = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) ur.openConnection();
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//
//            ObjectMapper mapper = new ObjectMapper();
//            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
//            mapper.writeValue(wr, content);
//            wr.flush();
//            wr.close();
//            Log.d("VALUE", mapper.writeValueAsString(content));
//
//            return conn.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
