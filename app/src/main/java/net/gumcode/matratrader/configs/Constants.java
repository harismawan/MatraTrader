package net.gumcode.matratrader.configs;

import com.neovisionaries.ws.client.WebSocket;

/**
 * Created by A. Fauzi Harismawan on 3/29/2016.
 */
public class Constants {

    public static final Object SYNC = new Object();
    public static final Object NOTIF = new Object();
    public static String REG_ID;

    public static WebSocket webSocket;
    private static String SERVER = "http://beta.intibinarindo.com/";
    public static String SOCKET_URL = "wss://ws.binaryws.com/websockets/v3";
    public static String SIGN_IN_URL = SERVER + "index.php/mobile/login";
    public static String SIGN_UP_URL = SERVER + "index.php/mobile/register";
    public static String CONNECT_URL = SERVER + "index.php/mobile/connect_server";
    public static String DISCONNECT_URL = SERVER + "index.php/mobile/disconnect_server";

    public static String PREFERENCES_KEY = "pref";

    public static String[] MARKET = {"r_25", "r_50", "r_75", "r_100", "r_bear", "r_bull", "r_mars", "r_moon", "r_sun", "r_venus", "r_yin", "r_yang"};

    public static String SENDER_ID = "350469758798";
}
