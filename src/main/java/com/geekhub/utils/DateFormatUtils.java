package com.geekhub.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtils {

    public static String format(LocalDateTime date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
}
