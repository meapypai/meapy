package project.meapy.meapy.utils.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tarek on 06/04/18.
 */

public class NotificationLink {
    public static void removeNotificationFromCurrentUser(int idNotification){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/notifications/"+idNotification);
        ref.removeValue();
    }
}
