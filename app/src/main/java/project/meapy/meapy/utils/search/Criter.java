package project.meapy.meapy.utils.search;

import java.util.List;

public interface Criter<T> {

    List<T> match(List<T> list);

    boolean match(T obj);
}
