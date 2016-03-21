package net.gumcode.binarymobile.models;

/**
 * Created by A. Fauzi Harismawan on 3/21/2016.
 */
public class Report {

    public String market;
    public int buy;
    public int sell;
    public int balance;

    public Report(String market, int buy, int sell, int balance) {
        this.market = market;
        this.buy = buy;
        this.sell = sell;
        this.balance = balance;
    }
}
