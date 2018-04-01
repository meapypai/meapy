package project.meapy.meapy.utils.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.utils.RunnableWithParam;

/**
 * Created by tarek on 16/03/18.
 */

public class FileLink {

    public static void insertFile(final RunnableWithParam onSucess, File file, Groups newGroup){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final Uri uriFile = Uri.fromFile(file);
        StorageReference filesRef = storage.getReference();

        StorageReference groupFiles = filesRef.child("data_groups/" + newGroup.getId() + "/" + uriFile.getLastPathSegment());
        groupFiles.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            onSucess.run();
            }
        });

    public static void insertFile(final RunnableWithParam onSucess, File file, FirebaseUser user){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final Uri uriFile = Uri.fromFile(file);
        StorageReference filesRef = storage.getReference();

        StorageReference groupFiles = filesRef.child("data_groups/" + user.getUid() + "/" + uriFile.getLastPathSegment());
        groupFiles.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                onSucess.run();
            }
        });
    }
}
