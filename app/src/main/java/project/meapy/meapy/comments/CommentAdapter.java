package project.meapy.meapy.comments;

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
import project.meapy.meapy.bean.Comment;

/**
 * Created by tarek on 15/03/18.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    private Context context;
    private List<Comment> list;
    public CommentAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        this.context = context;
        list=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.comment_post_details_view,parent,false);

        Comment currentComment = list.get(position);
        TextView tvAuthor = listItem.findViewById(R.id.authorComment);
        tvAuthor.setText(currentComment.getAuthorStr());

        TextView tvContent = listItem.findViewById(R.id.contentComment);
        tvContent.setText(currentComment.getContent());

        return listItem;
    }
}
