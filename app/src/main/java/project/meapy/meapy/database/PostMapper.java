package project.meapy.meapy.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Post;

/**
 * Created by tarek on 08/03/18.
 */

public class PostMapper extends Mapper{
    public DatabaseReference getReference(){
        return FirebaseDatabase.getInstance().getReference("posts");
    }

    public Class getClassObject() {
        return Post.class;
    }
}
