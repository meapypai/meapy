package project.meapy.meapy.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.R;
import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 20/03/2018.
 */

public class SearchUserAdapter extends ArrayAdapter<User> implements Filterable{

    private Context context;
    private TextFilter filter;
    private List<User> users;
    private List<User> filterUsers;
    private ArrayList<User> usersSelected;


    public SearchUserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.users = objects;
        this.filterUsers =  objects;
        this.usersSelected = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchUserHolder holder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.one_user_search_view,parent,false);
            holder = new SearchUserHolder();
            holder.name = (TextView)convertView.findViewById(R.id.nameUser);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkedUser);

            convertView.setTag(holder);
        }
        else {
            holder = (SearchUserHolder) convertView.getTag();
        }

        final User user = getItem(position);
        holder.name.setText(user.getEmail());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!usersSelected.contains(user)) {
                    usersSelected.add(user);
                }
                else  {
                    usersSelected.remove(user);
                }
            }
        });
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new TextFilter();
        }
        return  filter;
    }

    private class TextFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<User> filterList = new ArrayList<User>();
                for (int i = 0; i < filterUsers.size(); i++) {
                    if ((filterUsers.get(i).getEmail().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        User user = new User(filterUsers.get(i).getFirstName(), filterUsers.get(i).getLastName(),filterUsers.get(i).getEmail());
                        filterList.add(user);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filterUsers.size();
                results.values = filterUsers;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((ArrayList<User>)results.values);
            notifyDataSetChanged();
        }
    }

    public ArrayList<User> getUsersSelected() {
        return this.usersSelected;
    }
}
