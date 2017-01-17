package org.lpw.tephra.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lpw
 */
@Component("tephra.util.date-time")
public class DateTimeImpl implements DateTime {
    @Inject
    private Converter converter;

    @Override
    public Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Date getStart(Date date) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    @Override
    public Date getEnd(Date date) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    @Override
    public Timestamp toTime(String string) {
        return toTimestamp(converter.toDate(string));
    }

    @Override
    public Timestamp toTime(String string, String pattern) {
        return toTimestamp(converter.toDate(string, pattern));
    }

    private Timestamp toTimestamp(Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    @Override
    public int compare(Object x, Object y) {
        if (x == null && y == null)
            return 0;

        if (x == null)
            return -1;

        if (y == null)
            return 1;

        return converter.toString(x).compareTo(converter.toString(y));
    }
}
