package project.meapy.meapy.utils;

import java.util.Date;

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

    public static String getNbDayPastSinceToday(Date d) {
        Date dToday = new Date();
        Long s = (dToday.getTime() - d.getTime())/1000;
        int hours = (int) (s / 3600);
        int minutes = (int) (s / 60);
        int seconds = (int) (minutes % 60);
        int days = hours / 24;
        if(days == 0) {
            if(hours == 0) {
                if(minutes == 0) {
                    if(seconds == 1 || seconds == 0)
                        return seconds + " second ago";
                    else {
                        return seconds + " seconds ago";
                    }
                }
                else if (minutes == 1) {
                    return "1 minute ago";
                }
                else {
                    return minutes + " minutes ago";
                }
            }
            else if(hours == 1) {
                return "1 hour ago";
            }
            else {
                return (hours % 24) + " hours ago";
            }
        }
        else if(days == 1) {
            return  "1 day ago";
        }
        else {
            return days + " days ago";
        }
    }
}
