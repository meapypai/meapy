package project.meapy.meapy.groups.discussions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import project.meapy.meapy.CreateGroupActivity;
import project.meapy.meapy.R;
import project.meapy.meapy.groups.GroupHolder;

/**
 * Created by sansaoui on 19/02/18.
 */

public class DiscussionGroupAdapter extends ArrayAdapter<DiscussionGroup> {

    private Context context;

    public DiscussionGroupAdapter(@NonNull Context context, int resource, @NonNull List<DiscussionGroup> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupHolder holder = null;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_view,parent,false);

            holder = new GroupHolder();
            holder.drawable = (ImageView)convertView.findViewById(R.id.imgGroup);
            holder.nameGroup = (TextView)convertView.findViewById(R.id.nameGroup);
            holder.summary = (TextView)convertView.findViewById(R.id.summaryGroup);

            convertView.setTag(holder);
        }
        else {
            holder = (GroupHolder) convertView.getTag();
        }

        DiscussionGroup g = getItem(position);


        //si aucune image a été choisie lors de la création du groupe, on insère  l'image par défaut
        if(g.getImageName().equals(CreateGroupActivity.DEFAULT_IMAGE_GROUP)) {
            holder.drawable.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.group_default));
        }
        else {
            StorageReference ref = FirebaseStorage.getInstance().getReference("data_groups/" + g.getId() + "/" + g.getImageName());
            //        Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().override(50,50).centerCrop().into(holder.drawable); //image à partir de la réference passée
            Glide.with(context).using(new FirebaseImageLoader()).load(ref).asBitmap().into(holder.drawable); //image à partir de la réference passée
        }

        holder.nameGroup.setText(g.getName());
        holder.summary.setText(g.getSummary());

        return convertView;
    }
}
