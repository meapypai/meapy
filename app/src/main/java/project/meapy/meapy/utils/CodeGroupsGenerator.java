package project.meapy.meapy.utils;

import java.util.Random;

/**
 * Created by senoussi on 19/03/18.
 */

public class CodeGroupsGenerator {
    private static String numbers = "0123456789";
    private static String alpha   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static int CODE_SIZE = 6;
    public static String generate(){
        String all = numbers + alpha;
        StringBuilder builder = new StringBuilder(CODE_SIZE);
        for(int i = 0 ; i < CODE_SIZE ; i++){
            Random r = new Random();
            int max = all.length() - 1; int min = 0;
            int n = r.nextInt((max - min) + 1) + min;
            builder.append(all.charAt(n));
        }
        return builder.toString();
    }
}
