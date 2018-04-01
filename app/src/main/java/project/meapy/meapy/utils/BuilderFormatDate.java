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
}
