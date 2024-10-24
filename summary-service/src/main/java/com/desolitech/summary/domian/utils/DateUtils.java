package com.desolitech.summary.domian.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
    }

    public static Date getFirstDayOfCurrentMonth() {

        // get a reference to today
        LocalDateTime today = LocalDateTime.now();
        // having today,
        LocalDateTime firstDayOfMonth = today
                // and take the first day of that month
                .withDayOfMonth(1)
                .withHour(14)
                .withMinute(0)
                .withSecond(0);

        ZoneId systemTimeZone = ZoneId.of("UTC");
        return Date.from(firstDayOfMonth.atZone(systemTimeZone).toInstant());
    }

    public static Date getFirstDayOfNextMonth() {

        var nextMonth = LocalDateTime.now().plusMonths(1).getMonthValue();

        // get a reference to today
        LocalDateTime today = LocalDateTime.now();
        // having today,
        LocalDateTime firstDayOfNextMonth = today
                // add one to the month
                .withMonth(nextMonth)
                // and take the first day of that month
                .withDayOfMonth(1)
                .withHour(14)
                .withMinute(0)
                .withSecond(0);

        ZoneId systemTimeZone = ZoneId.of("UTC");
        return Date.from(firstDayOfNextMonth.atZone(systemTimeZone).toInstant());
    }

    public static int getDifferenceDays(Date lastPaymentDate, Date currentDate) {
        int daysdiff = 0;
        long diff = currentDate.getTime() - lastPaymentDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }
}
