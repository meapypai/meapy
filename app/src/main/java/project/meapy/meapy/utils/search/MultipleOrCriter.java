package project.meapy.meapy.utils.search;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.Post;

public class MultipleOrCriter<T> implements Criter<T>{
    protected List<Criter> criters;
    public MultipleOrCriter(List<Criter> criters){
        this.criters = criters;
    }
    public MultipleOrCriter(){
        criters = new ArrayList<>();
    }

    public void addCriter(Criter criter){
        synchronized (criters){
            criters.add(criter);
        }
    }
    @Override
    public List<T> match(List<T> list) {
        List<T> res = new ArrayList<>();
        synchronized (criters) {
            if(criters.size() != 0) {
                for (Criter criter : criters) {
                    res.addAll(criter.match(list));
                }
            }
        }
        return res;
    }

    @Override
    public boolean match(T obj) {
        synchronized (criters) {
            if(criters.size() == 0)return true;
            int nMatching = 0;
            if(criters.size() != 0) {
                for (Criter criter : criters) {
                    if(!criter.match(obj)){
                        nMatching++;
                    }
                }
            }
        return nMatching != criters.size();
        }
    }
}
