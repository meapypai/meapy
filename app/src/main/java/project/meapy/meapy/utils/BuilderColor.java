package project.meapy.meapy.utils;

/**
 * Created by yassi on 03/04/2018.
 */

public class BuilderColor {

    public static String generateHexaColor() {
        String color = "#";
        for(int i = 0; i < 6; i++) {
            int randomNumber = 65 + (int)(Math.random() * ((70 - 65) + 1));
            color += (char)randomNumber;
        }
        return color;
    }
}
