package project.meapy.meapy.bean;

import java.util.Date;

/**
 * Created by tarek on 08/03/18.
 */

public class Comment extends DomainObject {
    private int postId;
    private String userId;
    private Date date;
    private String content;
    private String authorStr;
    private User user;

    public String getAuthorStr() {
        return authorStr;
    }

    public void setAuthorStr(String authorStr) {
        this.authorStr = authorStr;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
