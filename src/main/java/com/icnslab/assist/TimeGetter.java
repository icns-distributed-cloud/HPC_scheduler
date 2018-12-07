package com.icnslab.assist;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alicek106 on 2017-08-06.
 */
public class TimeGetter {
    public static String getCurrentDate(){
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String str = dayTime.format(new Date(time));
        return str;
    }
}
