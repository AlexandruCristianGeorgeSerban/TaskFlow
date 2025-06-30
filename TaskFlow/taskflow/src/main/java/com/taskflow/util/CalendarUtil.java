package com.taskflow.util;

import java.util.Calendar;
import java.util.Date;

// Utilitar pentru operații comune cu Calendar.
public class CalendarUtil {

    // Returnează un Calendar setat pe data dată
    public static Calendar fromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    // Returnează un Calendar nou, adăugând zile la cel original
    public static Calendar addDays(Calendar original, int days) {
        Calendar cal = (Calendar) original.clone();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal;
    }

    // Verifică dacă data este înainte de ziua de azi
    public static boolean isPast(Calendar date) {
        Calendar today = Calendar.getInstance();
        clearTime(today); // Resetare componente oră
        Calendar copy = (Calendar) date.clone();
        clearTime(copy);
        return copy.before(today);
    }

    // Șterge componentele de timp (oră, minut, secundă, milisecundă)
    public static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    // Returnează un Calendar pentru azi, cu ora resetată
    public static Calendar today() {
        Calendar cal = Calendar.getInstance();
        clearTime(cal);
        return cal;
    }
}
