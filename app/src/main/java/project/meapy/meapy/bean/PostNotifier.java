package project.meapy.meapy.bean;

public class PostNotifier extends Notifier{
    public PostNotifier(Post post, Groups group){
        super();
        title = "New Post in " + group.getName();
        content = post.getTitle() + " by " + post.getUser();
    }
}
