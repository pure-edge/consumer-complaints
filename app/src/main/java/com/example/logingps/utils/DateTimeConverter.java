package com.example.logingps.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeConverter {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    private static int ONE_MINUTE = 1;
    private static int TWO_MINUTES = 2;
    private static int ONE_HOUR = ONE_MINUTE * 60;
    private static int TWO_HOURS = ONE_HOUR * 2;
    private static int ONE_DAY = ONE_HOUR * 24;
    private static int TWO_DAYS = ONE_DAY * 2;
    private static int ONE_WEEK = ONE_DAY * 7;

    public static Date toDateObject(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static Date toDateObject(long dateMilliseconds) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMilliseconds);
        return calendar.getTime();
    }

    public static String toDateString(Date date) {
        return dateFormat.format(date);
    }

    public static long toDateInMilliseconds(Date date) {
        return date.getTime();
    }

    public static String forNotificationDateString(Date date) {
        long currentTimeinMillis = Calendar.getInstance().getTimeInMillis();
        final String time = new SimpleDateFormat("hh:mm a").format(date);

        int difference = (int) ((currentTimeinMillis - date.getTime()) / 60000);    // difference in minutes
        if (difference < ONE_MINUTE)
            return "Just Now";
        else if (difference >= ONE_MINUTE && difference < TWO_MINUTES)
            return "A minute ago";
        else if (difference >= TWO_MINUTES && difference < ONE_HOUR) {
            return difference + " minutes ago";
        }
        else if (difference >= ONE_HOUR && difference < TWO_HOURS) {
            return "An hour ago";
        }
        else if (difference >= TWO_HOURS && difference < ONE_DAY) {
            int diffHours = difference / ONE_HOUR;
            return diffHours + "  hours ago";
        }
        else if (difference > ONE_DAY && difference < TWO_DAYS) {
            return "Yesterday at " + time;
        }
        else if (difference >= TWO_DAYS && difference < ONE_WEEK) {
            String day = new SimpleDateFormat("EEE").format(date); // ex: Sun, Mon, Tue...
            return day + " at " + time;
        }
        else { // beyond a week
            String dateWithoutYear = new SimpleDateFormat("MMM dd").format(date);  // ex: Jan. 1
            return dateWithoutYear + " at " + time;
        }
    }
}
