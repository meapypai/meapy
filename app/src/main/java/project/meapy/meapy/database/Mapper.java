package project.meapy.meapy.database;

import android.app.PendingIntent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.meapy.meapy.bean.DomainObject;
import project.meapy.meapy.bean.Post;

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

    public DomainObject findById(final int id) {
        DomainObject obj = null;
        getReference().child(id+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DomainObject obj = (DomainObject)dataSnapshot.getValue(getClassObject());
                objects.put(id,obj);
                objects.notifyAll();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
        return objects.get(id);
    }

    public abstract Class getClassObject();

    public List<DomainObject> findAll(){
        return null;
    }


}
