package project.meapy.meapy.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by senoussi on 12/03/18.
 */

public class Discipline extends DomainObject implements Parcelable {
    String name;
    public Discipline(){super();}

    protected Discipline(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Discipline> CREATOR = new Creator<Discipline>() {
        @Override
        public Discipline createFromParcel(Parcel in) {
            return new Discipline(in);
        }

        @Override
        public Discipline[] newArray(int size) {
            return new Discipline[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){return name;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
