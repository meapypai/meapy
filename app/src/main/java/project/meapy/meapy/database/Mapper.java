package project.meapy.meapy.database;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import project.meapy.meapy.bean.DomainObject;

/**
 * Created by tarek on 08/03/18.
 */

public abstract class Mapper {
    Map<Integer,DomainObject> objects = new HashMap<>();

    public abstract DatabaseReference getReference();

    public void insert(DomainObject o) {
        DatabaseReference ref = getReference();
        DatabaseReference postRef = ref.child(o.getId()+"");
        postRef.setValue(o);
    }

    public void delete(DomainObject o) {
        DatabaseReference ref = getReference();
        DatabaseReference postRef = ref.child(o.getId()+"");
        postRef.removeValue();
    }

    public void deleteById(int id) {
        DatabaseReference ref = getReference();
        DatabaseReference postRef = ref.child(id+"");
        postRef.removeValue();
    }
}
