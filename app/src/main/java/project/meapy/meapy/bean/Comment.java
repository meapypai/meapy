package project.meapy.meapy.bean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by tarek on 08/03/18.
 */

public class Comment extends DomainObject {
    private int postId;
    private int userId;
    private Date date;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment(){super();}

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
