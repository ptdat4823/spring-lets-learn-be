package com.letslive.letslearnbackend.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class TimeUtils {
    public static final LocalDateTime MIN = LocalDateTime.of(1000, 12,31, 23, 59, 59);
    public static final LocalDateTime MAX = LocalDateTime.of(3000, 12,31, 23, 59, 59);

    public static LocalDateTime getCurrentTimeGMT7() {
        ZonedDateTime currentUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime gmt7Time = currentUTC.withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));
        return gmt7Time.toLocalDateTime();
    }

    public static LocalDateTime convertStringToLocalDateTime(String isoString) {
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .appendOffset("+HH:MM", "Z")  // Handles both "+01:00" format and "Z"
                .toFormatter(Locale.ENGLISH);

        OffsetDateTime odt = OffsetDateTime.parse(isoString, parser);
        // Convert to the desired time zone (GMT+7)
        ZoneOffset targetOffset = ZoneOffset.ofHours(7);
        ZonedDateTime gmt7ZonedDateTime = odt.atZoneSameInstant(ZoneOffset.UTC)
                .withZoneSameInstant(targetOffset);

        // Return the LocalDateTime representation in GMT+7
        return gmt7ZonedDateTime.toLocalDateTime();
    }

    public static LocalDateTime convertDateToGMT7Date(LocalDateTime isoDate) {
            return isoDate.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("GMT+7")).toLocalDateTime();
    }
}
