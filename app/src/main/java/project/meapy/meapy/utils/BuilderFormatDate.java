package project.meapy.meapy.utils;

import android.content.Context;

import java.util.Date;

import project.meapy.meapy.R;

/**
 * Created by yassi on 02/04/2018.
 */

public class BuilderFormatDate {

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
}
