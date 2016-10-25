package com.ethanco.clockface.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作工具类.
 *
 * @author shimiso
 */

public class Utils {

    public static String FORMAT_HM = "HH:mm";

    public static Calendar calendar = null;
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
        if (d == null) {
            return null;
        }
        if (format == null || format.length() == 0) {
            format = FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(d);
        return s;
    }


    /**
     * 功能描述：返回月
     *
     * @param date Date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }


    /**
     * 功能描述：返回日
     *
     * @param date Date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 功能描述：返回小
     *
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }


    /**
     * 功能描述：返回分
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    //获取时间是AM还是PM
    public static String getAmPm(Date date) {
        int hour = Utils.getHour(date);
        if (hour > 0 && hour < 12) {
            return "AM";
        } else {
            return "PM";
        }
    }

    /*获取星期几*/
    public static String getWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    public static Lunar getLunar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Lunar(calendar);
        //lunar.animalsYear();//生肖
        //lunar.cyclical();//农历的干支甲子
        //lunar.toString();//农历的月日
    }


    /**
     * 返回秒钟
     *
     * @param date Date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        calendar = Calendar.getInstance();

        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
