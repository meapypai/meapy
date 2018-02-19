package project.meapy.meapy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import project.meapy.meapy.Group;

import java.util.List;

/**
 * Created by sansaoui on 19/02/18.
 */

public class GroupAdapter extends ArrayAdapter<Group> {

    public GroupAdapter(@NonNull Context context, int resource, @NonNull List<Group> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupHolder holder = null;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_view,parent,false);

            holder = new GroupHolder();
            holder.drawable = (ImageView)convertView.findViewById(R.id.imgGroup);
            holder.nameGroup = (TextView)convertView.findViewById(R.id.nameGroup);
            holder.lastMessage = (TextView)convertView.findViewById(R.id.lastMessage);

            convertView.setTag(holder);
        }
        else {
            holder = (GroupHolder) convertView.getTag();
        }

        Group g = getItem(position);
        holder.drawable.setImageResource(g.getDrawable());
        holder.nameGroup.setText(g.getName());
        holder.lastMessage.setText(g.getLastMessage());

        return convertView;
    }
}
