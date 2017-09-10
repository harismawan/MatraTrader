# MatraTrader

### This project is using the following library:
+ <a href="https://github.com/ISchwarz23/SortableTableView">SortableTableView</a>
+ <a href="https://github.com/FasterXML/jackson-databind">jackson-databind</a>
+ <a href="https://github.com/TakahikoKawasaki/nv-websocket-client">nv-websocket-client</a>

### Configuration:
+ Open ``` Constants.java ``` file
+ Change this line with your server address
```java
    private static String SERVER = "http://beta.intibinarindo.com/";
```
+ Change this line with <a href="https://www.binary.com/">binary.com</a> websocket server address
```java
    public static String SOCKET_URL = "wss://ws.binaryws.com/websockets/v3";
```
+ Change this line with <a href="https://www.binary.com/">binary.com</a> market you want to monitor
```java
    public static String[] MARKET = {"r_25", "r_50", "r_75", "r_100", "r_bear", "r_bull", "r_mars", "r_moon", "r_sun", "r_venus", "r_yin", "r_yang"};
```
+ Change this line with your GCM ```sender_id```
```java
    public static String SENDER_ID = "<your_sender_id>";
```


### Screenshot:
+ Login Screen

![alt text](https://github.com/AFHarismawan/MatraTrader/blob/master/screenshot/Screenshot_1505031997.png)
+ Main Menu

![alt text](https://github.com/AFHarismawan/MatraTrader/blob/master/screenshot/Screenshot_1505031912.png)
