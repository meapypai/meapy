package project.meapy.meapy.utils.search;

import java.util.ArrayList;
import java.util.List;

public class MultipleAndCriter<T> implements Criter<T>{
    protected List<Criter> criters;
    public MultipleAndCriter(List<Criter> criters){
        this.criters = criters;
    }
    public MultipleAndCriter(){
        criters = new ArrayList<>();
    }

    public void addCriter(Criter criter){
        synchronized (criters){
            criters.add(criter);
        }
    }
    @Override
    public List<T> match(List<T> list) {
        synchronized (criters) {
            if(criters.size() != 0) {
                for (Criter criter : criters) {
                    list = criter.match(list);
                }
            }else{
                list = new ArrayList<>();
            }
        }
        return list;
    }

    @Override
    public boolean match(T obj) {
        synchronized (criters) {
            for (Criter criter : criters) {
                if(!criter.match(obj)){
                    return false;
                }

            }
        }
        return true;
    }

    public void add(Criter c){
        synchronized (criters){
            criters.add(c);
        }
    }
}
