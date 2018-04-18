package project.meapy.meapy.utils.search;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Post;

public class UsernamePostContainsCriter implements Criter<Post> {
    private String str;
    public UsernamePostContainsCriter(String str){
        this.str = str.toLowerCase();
    }
    @Override
    public List<Post> match(List<Post> list) {
        List<Post> res = new ArrayList<>();
        for(Post p : list){
            if(p.getUser().toLowerCase().contains(str)){
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public boolean match(Post obj) {
        if(obj.getUser().toLowerCase().contains(str)){
            return true;
        }
        return false;
    }
}
