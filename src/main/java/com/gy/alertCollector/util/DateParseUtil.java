package com.gy.alertCollector.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gy on 2018/6/2.
 */
public class DateParseUtil {

    public Date parseStringAsDate(String string) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
