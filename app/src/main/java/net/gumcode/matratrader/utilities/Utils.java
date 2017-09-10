package net.gumcode.matratrader.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by A. Fauzi Harismawan on 3/30/2016.
 */
public class Utils {

    public static double calculatePercent(int percent, double number) {
        return (number * percent) / 100;
    }

    public static String roundUp(double number) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }
}
