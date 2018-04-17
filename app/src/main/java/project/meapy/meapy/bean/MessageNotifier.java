package project.meapy.meapy.bean;

public class MessageNotifier extends Notifier{
    public MessageNotifier(Message msg, Groups group, int idNotifier){
        super();
        title = getTitleMessageNotification(group,msg);
        content = msg.getContent();
        id = idNotifier;
    }

    private String getTitleMessageNotification(Groups grp, Message msg){
        return grp.getName() + " : "+ msg.getNameUser();
    }
}
