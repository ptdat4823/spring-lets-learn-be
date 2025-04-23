package com.letslive.letslearnbackend.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class TimeUtils {
    public static LocalDateTime convertStringToLocalDateTime(String isoString) {
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .appendOffset("+HH:MM", "Z")  // Handles both "+01:00" format and "Z"
                .toFormatter(Locale.ENGLISH);

        OffsetDateTime odt = OffsetDateTime.parse(isoString, parser);
        return odt.toLocalDateTime();
    }
}
