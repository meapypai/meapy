package project.meapy.meapy.groups;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Groups;

public class GroupsList extends ArrayList<Groups> {
    public GroupsList(){
        super();
    }

    @Override
    public boolean contains(Object grp){
        if(!(grp instanceof Groups))
            return false;
        synchronized (this){
            for(Groups group : this)
                if(group.equals(grp))
                    return true;
            return false;
        }
    }

    @Override
    public boolean add(Groups grp){
        boolean toAdd = true;
        synchronized (this){
            for(Groups group : this)
                if(group.equals(grp))
                    toAdd = false;
            if(toAdd)
                super.add(grp);
        }
        return true;
    }

    @Override
    public boolean remove(Object grp){
        if(!(grp instanceof Groups))
            return false;
        List<Groups> toRemove = new ArrayList<>();
        synchronized (this){
            for(Groups group : this)
                if(group.equals(grp))
                    toRemove.add(group);
            for(Groups g : toRemove)
                super.remove(g);
        }
        return true;
    }
}
