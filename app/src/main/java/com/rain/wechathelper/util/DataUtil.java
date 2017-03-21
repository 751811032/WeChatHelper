package com.rain.wechathelper.util;

import java.util.Date;

/**
 * Created by USER on 2017/3/18.
 */

public class DataUtil {

    //获取当前日期的秒数
    public static int getCurrentUnixTime() {
        return getUnixTimeByDate(new Date());
    }

    /**
     * 将Date型时间转换成int型时间(1970年至今的秒数
     *            1970年至今的秒数
     * @return
     */
    public static int getUnixTimeByDate(Date date) {
        return (int) (date.getTime() / 1000);
    }
}
