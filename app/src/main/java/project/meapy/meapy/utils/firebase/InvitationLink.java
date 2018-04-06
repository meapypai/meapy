package project.meapy.meapy.utils.firebase;

import com.google.firebase.database.FirebaseDatabase;

import project.meapy.meapy.bean.Groups;

/**
 * Created by tarek on 06/04/18.
 */

public class InvitationLink {
    public static void setInviteCode(Groups group, String generatedCode){
        FirebaseDatabase.getInstance().getReference("groups/"+group.getId()+"/codeToJoin/").setValue(generatedCode);
        FirebaseDatabase.getInstance().getReference("codeToGroups/"+generatedCode+"/").setValue(group.getId());
    }
}
