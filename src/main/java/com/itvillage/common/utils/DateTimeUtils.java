package com.itvillage.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    // RFC-3339 timestamp으로 변환(ISO-8601)
    public static String formatTimestampWithUtcZoneOffset(LocalDateTime dateTime) {
        return dateTime
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
    }

    public static LocalDate rfc3339ToLocalDate(String rfc3339) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(rfc3339, formatter);

        return zonedDateTime.toLocalDate();
    }
}
