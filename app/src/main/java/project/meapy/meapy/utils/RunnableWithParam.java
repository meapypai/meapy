package project.meapy.meapy.utils;

/**
 * Created by tarek on 16/03/18.
 */

public abstract class RunnableWithParam implements Runnable {
    private Object param;
    public void setParam(Object o){param=o;}
    public Object getParam(){return param;}

}
