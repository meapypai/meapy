package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.bean.User;

/**
 * Created by yassi on 16/03/2018.
 */

public class SearchUserActivity extends AppCompatActivity {

    public static final String EXTRA_ARRAY_USERS = "array_users";

    private ListView listSearchUser;
    private List<String> data = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private FloatingActionButton validUsersToAddSearchActivity;

    private ArrayList<String> usersSelectedArray = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_user);

        listSearchUser = (ListView)findViewById(R.id.listSearchUser);
        validUsersToAddSearchActivity = (FloatingActionButton)findViewById(R.id.validUsersToAddSearchActivity);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,data);
        listSearchUser.setAdapter(adapter);

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        //ajout des données concernant les users dans la listview
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = (User)dataSnapshot.getValue(User.class);
                data.add(user.getEmail());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listSearchUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String val = (String) parent.getItemAtPosition(position);
                if(usersSelectedArray.contains(val)) { //on supprime si déjà sélectionnée
                    usersSelectedArray.remove(val);
                }
                else {
                    usersSelectedArray.add(val);
                }
            }
        });

        validUsersToAddSearchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retour à l'activité de création de groupe avec les données créées
                Intent intent = new Intent();
                intent.putStringArrayListExtra(EXTRA_ARRAY_USERS,usersSelectedArray);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_user, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        //listener de searchView lors d'un changement de texte
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
