package com.letslive.letslearnbackend.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class TimeUtils {
    public static LocalDateTime convertStringToLocalDateTime(String isoString) {
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4, 4, SignStyle.ALWAYS)
                .appendPattern("-MM-dd'T'HH:mm:ssXXX")
                .toFormatter(Locale.ENGLISH);

        OffsetDateTime odt = OffsetDateTime.parse("+2017-02-26T00:00:00Z", parser);

        return odt.toLocalDateTime();
    }
}
