package project.meapy.meapy.bean;

import android.content.Context;

import project.meapy.meapy.R;

public class PostNotifier extends Notifier{
    public PostNotifier(Post post, Groups group, Context context){
        super();
        String newPostIn = context.getString(R.string.new_post_in_group)+" ";
        title =  newPostIn + group.getName();
        content = post.getTitle() ;
    }
}
