package com.taskflow.util;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

    public static Calendar fromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Calendar addDays(Calendar original, int days) {
        Calendar cal = (Calendar) original.clone();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal;
    }

    public static boolean isPast(Calendar date) {
        Calendar today = Calendar.getInstance();
        clearTime(today);
        Calendar copy = (Calendar) date.clone();
        clearTime(copy);
        return copy.before(today);
    }

    public static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static Calendar today() {
        Calendar cal = Calendar.getInstance();
        clearTime(cal);
        return cal;
    }
}
