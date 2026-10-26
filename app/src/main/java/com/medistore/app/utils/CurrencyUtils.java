package com.medistore.app.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    private static final Locale INDIA = new Locale("en", "IN");
    
    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(INDIA);
        return formatter.format(amount).replace("Rs.", "₹");
    }

    public static String formatPrice(double amount) {
        return String.format(INDIA, "₹%.2f", amount);
    }
}
