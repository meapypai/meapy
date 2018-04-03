package project.meapy.meapy.utils;

/**
 * Created by yassi on 03/04/2018.
 */

public class BuilderColor {

    public static String generateHexaColor() {
        String color = "#";
        for(int i = 0; i < 6; i++) {
            int randomNumber0to9 = 48 + (int)(Math.random() * ((57- 48) + 1));
            int randomNumberAtoZ = 65 + (int)(Math.random() * ((70 - 65) + 1));
            int randomNumberNumberOrLetter = 0 + (int)(Math.random() * ((1 - 0) + 1));
            if(randomNumberNumberOrLetter == 1)
                color += (char)randomNumber0to9;
            else {
                color += (char)randomNumberAtoZ;
            }
            System.out.println(color);
        }
        return color;
    }
}
