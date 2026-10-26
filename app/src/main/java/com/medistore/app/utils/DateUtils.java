package com.medistore.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String MONTH_YEAR_FORMAT = "MM/yyyy";
    
    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    }

    private static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
    }

    private static SimpleDateFormat getMonthYearFormat() {
        return new SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.getDefault());
    }

    public static String getTodayDate() {
        return getDateFormat().format(new Date());
    }

    public static String getCurrentDateTime() {
        return getDateTimeFormat().format(new Date());
    }

    public static String getCurrentMonthYear() {
        return getMonthYearFormat().format(new Date());
    }

    public static boolean isExpired(String expiryDate) {
        try {
            Date expiry = getDateFormat().parse(expiryDate);
            Date today = getDateFormat().parse(getTodayDate());
            if (expiry != null && today != null) {
                return expiry.before(today);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isExpiringSoon(String expiryDate, int days) {
        try {
            Date expiry = getDateFormat().parse(expiryDate);
            Date today = getDateFormat().parse(getTodayDate());
            if (expiry != null && today != null) {
                if (expiry.before(today)) return false; // Already expired
                
                long diffInMillies = Math.abs(expiry.getTime() - today.getTime());
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                return diffInDays <= days;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getDaysUntilExpiry(String expiryDate) {
        try {
            Date expiry = getDateFormat().parse(expiryDate);
            Date today = getDateFormat().parse(getTodayDate());
            if (expiry != null && today != null) {
                long diffInMillies = expiry.getTime() - today.getTime();
                return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatDate(long timestamp) {
        return getDateFormat().format(new Date(timestamp));
    }

    public static Date parseDate(String dateStr) {
        try {
            return getDateFormat().parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getThresholdDate(int daysAhead) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysAhead);
        return getDateFormat().format(calendar.getTime());
    }
}
