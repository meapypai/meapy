package project.meapy.meapy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 11/03/2018.
 */

public class UserMapper extends Mapper {

    @Override
    public DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public Class getClassObject() {
        return User.class;
    }
}
