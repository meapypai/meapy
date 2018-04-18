package project.meapy.meapy.utils.lists;

import java.util.ArrayList;

import project.meapy.meapy.bean.Discipline;

public class DisciplineList extends ArrayList<Discipline>{

    public boolean containsDisciplineByName(String name){
        for(Discipline d: this) {
            if(d.getName().toUpperCase().equals(name.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
