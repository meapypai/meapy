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
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.post_view_one_group,parent,false);

        Post currentPost = list.get(position);

        TextView title = (TextView)listItem.findViewById(R.id.titlePostViewOneGroup);
        title.setText(currentPost.getTitle());

        TextView disc = (TextView) listItem.findViewById(R.id.discPostViewOneGroup);
        disc.setText(currentPost.getDisciplineName());

        TextView descr = (TextView)listItem.findViewById(R.id.descrPostViewOneGroup);
        String descrReducted = currentPost.getTextContent();
        if(descrReducted.length() > 50)
            descrReducted = descrReducted.substring(0,50)+"...";
        descr.setText(descrReducted);

        return listItem;
    }
}
