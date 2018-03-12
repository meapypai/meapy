package project.meapy.meapy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import project.meapy.meapy.bean.Post;

public class PostDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Post post = (Post) getIntent().getSerializableExtra("POST");
        TextView contentPostTv = findViewById(R.id.contentPostDetails);
        contentPostTv.setText(post.getTextContent());

        TextView titlePostTv = findViewById(R.id.titlePostDetails);
        titlePostTv.setText(post.getTitle());

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_detail_menu, menu);
        return true;
    }
}
