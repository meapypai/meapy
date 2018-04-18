package project.meapy.meapy.utils;

import android.widget.Toast;

import project.meapy.meapy.MyApplication;
import project.meapy.meapy.bean.Discipline;
import project.meapy.meapy.bean.Groups;
import project.meapy.meapy.utils.firebase.DisciplineLink;

public class DisciplineService {
    public static void createDiscipline(String discName, Groups grp){
        Discipline disc = new Discipline();
        disc.setName(discName);
        disc.setColor(BuilderColor.generateHexaColor());
        DisciplineLink.addDiscipline(grp, disc);
    }
}
