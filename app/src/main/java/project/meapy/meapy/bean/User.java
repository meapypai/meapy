package project.meapy.meapy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yassi on 11/03/2018.
 */

public class User implements Parcelable {

    public static final String DEFAULT_IMAGE_USER_NAME = "default_avatar.png";
    public static final int DEFAULT_NUMBER_COINS = 3;

    protected String email;
    protected String firstName;
    protected String lastName;
    protected String uid;
    protected String nameImageProfil;
    protected String chatBubbleColor;
    protected Date timeRegistration = new Date();
    protected int coins;
    protected int rank; //to see if the user is admin or not

    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        uid = in.readString();
        this.setCoins(DEFAULT_NUMBER_COINS);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(uid);
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
        super();
    }

    public User(String firstName, String lastName, String email) {
        super();
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.setNameImageProfil(DEFAULT_IMAGE_USER_NAME);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameImageProfil() {
        return nameImageProfil;
    }

    public void setNameImageProfil(String nameImageProfil) {
        this.nameImageProfil = nameImageProfil;
    }

    public String getChatBubbleColor() {
        return chatBubbleColor;
    }

    public void setChatBubbleColor(String chatBubbleColor) {
        this.chatBubbleColor = chatBubbleColor;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Date getTimeRegistration() {
        return timeRegistration;
    }

    public void setTimeRegistration(Date timeRegistration) {
        this.timeRegistration = timeRegistration;
    }
}
