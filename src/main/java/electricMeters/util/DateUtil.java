package electricMeters.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtil {
    
    public static final DateTimeFormatter PROFILE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");
    public static final DateTimeFormatter DB_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter VIEW_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static LocalDateTime toLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DB_DATE_TIME_FORMAT);
    }
    
    public static String toString(LocalDateTime dateTime) {
        return dateTime.format(VIEW_DATE_TIME_FORMAT);
    }

    public static String intToMonthName(int month) {
        return Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
    }
    
}
