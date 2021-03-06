package project.meapy.meapy.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.firebase.UserLink;

/**
 * Created by tarek on 06/04/18.
 */

public class StoreBean {
    public static void storeUser(User u, Context c){
        String lastname = u.getLastName();
        String chatBubble = u.getChatBubbleColor();
        String email = u.getEmail();
        String firstName = u.getFirstName();
        String imgProfile = u.getNameImageProfil();
        int rank = u.getRank();
        String uid = u.getUid();
        Date registration = u.getTimeRegistration();

        SharedPreferences prefs = c.getSharedPreferences("USER_PREFS",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LASTNAME",lastname)
                .putString("CHAT_BUBBLE",chatBubble)
                .putString("EMAIL",email)
                .putString("FIRSTNAME",firstName)
                .putString("IMG_PROFILE",imgProfile)
                .putInt("RANK",rank)
                .putString("UID",uid)
                .putLong("DATE_REGISTRATION",registration.getTime());
        editor.commit();
        editor.apply();
    }

    public static User provideUser(Context c){
        SharedPreferences prefs = c.getSharedPreferences("USER_PREFS",0);
        String lastname = prefs.getString("LASTNAME","");
        String chatBubble = prefs.getString("CHAT_BUBBLE","");
        String email = prefs.getString("EMAIL","");
        String firstName = prefs.getString("FIRSTNAME","");
        String imgProfile = prefs.getString("IMG_PROFILE","");
        int rank = prefs.getInt("RANK",0);
        String uid = prefs.getString("UID","");
        Date registration = new Date(prefs.getLong("DATE_REGISTRATION",new Date().getTime()));
        if(lastname.length() != 0 || chatBubble.length() != 0 ||email.length() != 0 || firstName.length() != 0) {
            User user = new User();
            user.setUid(uid);
            user.setChatBubbleColor(chatBubble);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastname);
            user.setNameImageProfil(imgProfile);
            user.setRank(rank);
            user.setTimeRegistration(registration);
            return user;
        }return null;
    }
}
