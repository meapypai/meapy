package project.meapy.meapy.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import project.meapy.meapy.bean.Comment;
import project.meapy.meapy.bean.Post;
import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 25/04/2018.
 */

public class SortService {

    public static void sortComment(List<Comment> comments){
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment t1) {
                Date d1 = comment.getDate();
                Date d2 = t1.getDate();
                if(d1.before(d2)){
                    return -1;
                }else if(d2.before(d1)){
                    return 1;
                }else {
                    return 0;
                }
            }
        });
    }

    public static void sortMembers(List<User> users){
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getFirstName().toUpperCase().compareTo(u2.getFirstName().toUpperCase());

            }
        });
    }

    public static void sortPosts(List<Post> posts){
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                return - post.getDate().compareTo(t1.getDate());
            }
        });
    }
}
