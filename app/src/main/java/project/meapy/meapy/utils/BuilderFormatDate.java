package project.meapy.meapy.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import project.meapy.meapy.R;

/**
 * Created by yassi on 02/04/2018.
 */

public class BuilderFormatDate {

    /**
     * return the date in the format: X minutes or X hours or X day...
     * @param d : the date will be formated
     * @return
     */
    public static String formatDate(Date d) {
        //deprecated
        int hours = d.getHours();
        int minutes = d.getMinutes();
        String m = "";
        String h = "";
        if(hours < 10) {
            h += "0" + hours;
        }
        else {
            h += hours;
        }
        if(minutes < 10) {
            m += "0" + minutes;
        }
        else {
            m += minutes;
        }
        return h + ":" + m;
    }

    /**
     * return the number of day pasted since the date
     * @param context
     * @param d : the date
     * @return
     */
    public static String getNbDayPastSinceToday(Context context, Date d) {
        Date dToday = new Date();
        Long s = (dToday.getTime() - d.getTime())/1000;
        int hours = (int) (s / 3600);
        int minutes = (int) (s / 60);
        int seconds = (int) (s % 60);
        int days = hours / 24;
        if(days == 0) {
            if(hours == 0) {
                if(minutes == 0) {
                    if(seconds == 1 || seconds == 0)
                        return seconds + " " + context.getResources().getString(R.string.second_text_post);
                    else {
                        return seconds + " " + context.getResources().getString(R.string.seconds_text_post);
                    }
                }
                else if (minutes == 1) {
                    return "1 " + context.getResources().getString(R.string.minute_text_post);
                }
                else {
                    return minutes + " " + context.getResources().getString(R.string.minutes_text_post);
                }
            }
            else if(hours == 1) {
                return "1 " + context.getResources().getString(R.string.hour_text_post);
            }
            else {
                return (hours % 24) + " " + context.getResources().getString(R.string.hours_text_post);
            }
        }
        else if(days == 1) {
            return  "1 " + context.getResources().getString(R.string.day_text_post);
        }
        else {
            return days + " " + context.getResources().getString(R.string.days_text_post);
        }
    }


    /**
     *  return the date in a developped format
     * @param date : the date will be formated
     * @return
     */
    public static String formatDateInLongFr(Context c, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) + " " + getNameOfMonth(c,calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR);
    }


    public static String getNameOfMonth(Context c, int month) {
        String str = "";
        switch(month) {
            case 0:
                str = c.getResources().getString(R.string.month_0);
                break;
            case 1:
                str = c.getResources().getString(R.string.month_1);
                break;
            case 2:
                str = c.getResources().getString(R.string.month_2);
                break;
            case 3:
                str = c.getResources().getString(R.string.month_3);
                break;
            case 4:
                str = c.getResources().getString(R.string.month_4);
                break;
            case 5:
                str = c.getResources().getString(R.string.month_5);
                break;
            case 6:
                str = c.getResources().getString(R.string.month_6);
                break;
            case 7:
                str = c.getResources().getString(R.string.month_7);
                break;
            case 8:
                str = c.getResources().getString(R.string.month_8);
                break;
            case 9:
                str = c.getResources().getString(R.string.month_9);
                break;
            case 10:
                str = c.getResources().getString(R.string.month_10);
                break;
            case 11:
                str = c.getResources().getString(R.string.month_11);
                break;
        }
        return str;
    }
}
