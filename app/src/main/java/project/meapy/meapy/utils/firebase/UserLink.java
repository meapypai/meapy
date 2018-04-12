package project.meapy.meapy.utils.firebase;

import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import project.meapy.meapy.R;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.utils.RetrieveImage;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by yassi on 08/04/2018.
 */

public class UserLink {
    public static void provideUserForPost(Post post, final RunnableWithParam onChange){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+post.getUser_uid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = (User)dataSnapshot.getValue(User.class);
                if(onChange != null){
                    onChange.setParam(user);
                    onChange.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
