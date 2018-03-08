package project.meapy.meapy.database;

import com.google.firebase.database.DatabaseReference;

import project.meapy.meapy.bean.DomainObject;
import project.meapy.meapy.bean.Post;

/**
 * Created by tarek on 08/03/18.
 */

public abstract class Mapper {

    public abstract DatabaseReference getReference();

    public void insert(DomainObject o) {
        DatabaseReference ref = getReference();
        DatabaseReference postRef = ref.child(o.getId()+"");
        postRef.setValue(o);
    }

    public void update(DomainObject o) {
        insert(o);
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

    public DomainObject findById(int id) {
        return null;
    }
}
