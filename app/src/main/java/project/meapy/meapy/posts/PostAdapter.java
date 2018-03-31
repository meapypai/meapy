package project.meapy.meapy.posts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import project.meapy.meapy.R;
import project.meapy.meapy.bean.Post;

/**
 * Created by tarek on 13/03/18.
 */

public class PostAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> list;
    public PostAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);
        this.context = context;
        list=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_view_one_group, parent, false);
            holder = new PostHolder();
            holder.title       = (TextView)convertView.findViewById(R.id.titlePostViewOneGroup);
            holder.discipline  = (TextView) convertView.findViewById(R.id.discPostViewOneGroup);
            holder.description = (TextView)convertView.findViewById(R.id.descrPostViewOneGroup);
            holder.user        = (TextView)convertView.findViewById(R.id.userPost);
//            holder.diffUpDown  = (TextView)convertView.findViewById(R.id.diffUpDown);
        }
        else {
            holder = (PostHolder) convertView.getTag();
        }


        Post currentPost = getItem(position);

        holder.title.setText(currentPost.getTitle());
        holder.discipline.setText(currentPost.getDisciplineName());
        holder.description.setText(currentPost.getTextContent());
        holder.user.setText(currentPost.getUser());
//        holder.diffUpDown.setText(currentPost.getDiffUpDown());

//        String descrReducted = currentPost.getTextContent();
//
//        if(descrReducted.length() > 50)
//            descrReducted = descrReducted.substring(0,50)+"...";
//        descr.setText(descrReducted);

        return convertView;
    }
}
