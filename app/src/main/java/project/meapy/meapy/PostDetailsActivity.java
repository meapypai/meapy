package project.meapy.meapy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import project.meapy.meapy.bean.Post;

public class PostDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Post post = (Post) getIntent().getSerializableExtra("POST");

    }
}
