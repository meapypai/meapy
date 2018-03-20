package project.meapy.meapy.bean;

/**
 * Created by yassi on 20/03/2018.
 */

public class Notifier extends DomainObject{

    private String title;
    private String content;

    public Notifier() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
