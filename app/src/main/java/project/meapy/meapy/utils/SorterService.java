package project.meapy.meapy.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import project.meapy.meapy.bean.Message;

public class SorterService {
    public static void sortMessages(List<Message> messages){
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message message, Message t1) {
                if(message.getDate().after(t1.getDate()))
                    return 1;
                else if (message.getDate().before(t1.getDate())){
                    return -1;
                }return 0;

            }
        });
    }
}
