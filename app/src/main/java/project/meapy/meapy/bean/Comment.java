package project.meapy.meapy.bean;

import java.sql.Timestamp;

/**
 * Created by tarek on 08/03/18.
 */

public class Comment extends DomainObject {
    private int postId;
    private int userId;
    private Timestamp date;

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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
