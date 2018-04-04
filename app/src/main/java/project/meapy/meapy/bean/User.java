package project.meapy.meapy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yassi on 11/03/2018.
 */

public class User implements Parcelable {

    protected String email;
    protected String firstName;
    protected String lastName;
    protected String uid;
    protected String nameImageProfil;
    protected String chatBubbleColor;
    protected int rank; //to see if the user is admin or not

    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        uid = in.readString();
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
