package project.meapy.meapy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import project.meapy.meapy.activities.MyAppCompatActivity;
import project.meapy.meapy.bean.User;
import project.meapy.meapy.search.SearchUserAdapter;

/**
 * Created by yassi on 16/03/2018.
 */

public class SearchUserActivity extends MyAppCompatActivity {

    public static final String EXTRA_ARRAY_USERS = "array_users";

    private ListView listSearchUser;
    private List<User> data = new ArrayList<>();
    private SearchUserAdapter adapter;

    private FloatingActionButton validUsersToAddSearchActivity;

    private ArrayList<User> usersSelectedArray = new ArrayList<User>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_user);

        listSearchUser = (ListView)findViewById(R.id.listSearchUser);
        validUsersToAddSearchActivity = (FloatingActionButton)findViewById(R.id.validUsersToAddSearchActivity);

        adapter = new SearchUserAdapter(this,R.layout.one_user_search_view,data);
        listSearchUser.setAdapter(adapter);

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        //ajout des données concernant les users dans la listview
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = (User)dataSnapshot.getValue(User.class);
                if(!user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) //pour ne pas s'ajouter dans son propre groupe :-)
                    data.add(user);
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

//        listSearchUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkedUser);
//                checkBox.setChecked(true);
//
//                User u = (User) parent.getItemAtPosition(position);
//                if(usersSelectedArray.contains(u)) { //on supprime si déjà sélectionnée
//                    usersSelectedArray.remove(u);
//                }
//                else {
//                    usersSelectedArray.add(u);
//                }
//            }
//        });

        validUsersToAddSearchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retour à l'activité de création de groupe avec les données créées
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(EXTRA_ARRAY_USERS,adapter.getUsersSelected());
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
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //change widgets'color in terms of settings
        validUsersToAddSearchActivity.setBackgroundTintList(ContextCompat.getColorStateList(this,colorId));
    }
}
