package project.meapy.meapy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tarek on 08/03/18.
 */

public class GroupsMapper extends Mapper {
    @Override
    public DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference("groups");
    }
}
