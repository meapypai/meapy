package project.meapy.meapy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Comment;

/**
 * Created by tarek on 08/03/18.
 */

public class CommentMapper extends Mapper {
    @Override
    public DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference("comments");
    }

    public Class getClassObject() {
        return Comment.class;
    }
}
