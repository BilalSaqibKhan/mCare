package com.example.mcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView mainBottomNav;

    private DashboardFragment dashboardFragment;
    private MedicalReportsFragment medicalReportsFragment;
    private AccountFragment accountFragment;

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Mcare");

        if(mAuth.getCurrentUser() != null) {


            mainBottomNav = findViewById(R.id.mainBottomNav);

            //Fragments
            dashboardFragment = new DashboardFragment();
            medicalReportsFragment = new MedicalReportsFragment();
            accountFragment = new AccountFragment();

            replaceFragment(medicalReportsFragment);

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.bottom_action_dashboard:
                            replaceFragment(dashboardFragment);
                            return true;

                        case R.id.bottom_action_reports:
                            replaceFragment(medicalReportsFragment);
                            return true;

                        case R.id.bottom_action_account:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }
                }
            });

        }

    }



    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);
        fragmentTransaction.commit();

    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null)
        {
            //User is not logged in
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else
        {
            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(!task.getResult().exists())
                        {
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }







//Embed menu to the main activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //search view
        //MenuItem item = menu.findItem(R.id.action_search_btn);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        //searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        //    @Override
        //    public boolean onQueryTextSubmit(String query) {
                //called when we press search button
                //searchData(query);
        //        return false;
        //    }

        //    @Override
        //    public boolean onQueryTextChange(String newText) {
                //called when we type even a single letter

        //        return false;
        //    }
        //});

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_logout_btn:
                mAuth.signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;

            case R.id.action_settings_btn:
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_feedback_btn:
                Intent feedbackIntent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(feedbackIntent);
                return true;

            case R.id.action_add_btn:
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
                return true;

            default:
                return false;
                //in case of any error
        }

    }
    //options menu ENDS
}
