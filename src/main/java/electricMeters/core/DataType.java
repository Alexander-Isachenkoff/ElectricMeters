package electricMeters.core;

import electricMeters.util.DateUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public enum DataType {

    DEFAULT(),

    INTEGER() {
        @Override
        public Object fromString(String string) {
            return Integer.parseInt(string);
        }
    },

    REAL() {
        @Override
        public String toString(Object object, String format) {
            if (format == null) {
                format = "0.000";
            }
            DecimalFormat decimalFormat = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.US));
            return decimalFormat.format(object);
        }

        @Override
        public Object fromString(String string) {
            return Double.parseDouble(string);
        }
    },

    DATE() {
        @Override
        public Object convertFromDB(Object object) {
            return DateUtil.toLocalDate(object.toString());
        }

        @Override
        public String toString(Object object, String format) {
            return DateUtil.toString((LocalDate) object);
        }
    },

    DATE_TIME() {
        @Override
        public Object convertFromDB(Object object) {
            return DateUtil.toLocalDateTime(object.toString());
        }

        @Override
        public String toString(Object object, String format) {
            return DateUtil.toString((LocalDateTime) object);
        }
    },

    MONTH() {
        @Override
        public String toString(Object object, String format) {
            return DateUtil.monthName((Integer) object);
        }
    };

    public Object convertFromDB(Object object) {
        return object;
    }

    public String toString(Object object, String format) {
        return String.valueOf(object);
    }

    public Object fromString(String string) {
        return string;
    }

}

