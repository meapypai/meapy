package project.meapy.meapy.utils.search;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Post;

public class PostDiscIdCriter implements Criter<Post>{
    private int discId;
    public PostDiscIdCriter(int idDisc){
        this.discId = idDisc;
    }

    @Override
    public List<Post> match(List<Post> list) {
        List<Post> res = new ArrayList<>();
        for(Post p : list){
            if(p.getDisciplineId() == discId){
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public boolean match(Post obj) {
        if(obj.getDisciplineId() == discId){
            return true;
        }
        return false;
    }
}
