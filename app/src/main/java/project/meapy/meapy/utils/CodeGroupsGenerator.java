package project.meapy.meapy.utils;

import java.util.Random;

/**
 * Created by senoussi on 19/03/18.
 */

public class CodeGroupsGenerator {
    private static String numbers = "0123456789";
    private static String alpha   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int CODE_SIZE = 5;
    public static String generate(){
        String all = numbers + alpha;
        StringBuilder builder = new StringBuilder(CODE_SIZE);
        for(int i = 0 ; i < CODE_SIZE ; i++){
            int n = new Random().nextInt(all.length() -1);
            n = (n*n)^(1/2);
            builder.append(all.charAt(n));
        }
        return builder.toString();
    }
}
