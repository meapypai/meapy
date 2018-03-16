package project.meapy.meapy.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.bean.User;

/**
 * Created by tarek on 13/03/18.
 */

public class GroupsUserAdder {
    private static GroupsUserAdder INSTANCE;
    private GroupsUserAdder(){}
    public static GroupsUserAdder getInstance(){
        if(INSTANCE == null)
            INSTANCE = new GroupsUserAdder();
        return INSTANCE;
    }

    public void addUserTo(Groups grp){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = fUser.getUid();
        // insert into users the new group
        FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/"+grp.getId()).setValue(new Integer(grp.getId()));
        // insert into group the user
        FirebaseDatabase.getInstance().getReference("groups/"+grp.getId()+"/usersId/"+uid).setValue(uid);
    }

    public void addUserTo(User u, Groups grp){
        String uid = u.getUid();
        // insert into users the new group
        FirebaseDatabase.getInstance().getReference("users/"+uid+"/groupsId/"+grp.getId()).setValue(new Integer(grp.getId()));
        // insert into group the user
        FirebaseDatabase.getInstance().getReference("groups/"+grp.getId()+"/usersId/"+uid).setValue(uid);
    }
}
