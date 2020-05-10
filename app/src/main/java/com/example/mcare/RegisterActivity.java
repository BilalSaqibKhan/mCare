package com.example.mcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email_field, reg_pass_field, reg_confirm_pass_field, reg_pmdc_field;
    private Button reg_login_button, reg_button;
    private ProgressBar reg_progress;

    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    Boolean hasFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_pass);
        reg_confirm_pass_field = findViewById(R.id.reg_confirm_pass);
        reg_pmdc_field = findViewById(R.id.reg_pmdc);
        reg_login_button = findViewById(R.id.reg_login_btn);
        reg_progress = findViewById(R.id.reg_progress);
        reg_button = findViewById(R.id.reg_btn);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();


        reg_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = reg_email_field.getText().toString();
                final String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();
                final String pmdc_code = reg_pmdc_field.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass) && !TextUtils.isEmpty(pmdc_code))
                {
                    if(pass.equals(confirm_pass))
                    {
                        reg_progress.setVisibility(View.VISIBLE);

                        //PMDC Dummy verification

                        mDatabase.collection("PMDC").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> item;
                                        item = document.getData();
                                        String id = item.get("ID").toString();

                                        if(pmdc_code.trim().equals(id.trim()))
                                        {
                                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                                        startActivity(setupIntent);
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    reg_progress.setVisibility(View.INVISIBLE);
                                                }
                                            });

                                            hasFound = true;
                                            break;
                                        }
                                    }
                                    if (hasFound.equals(false)){
                                        Toast.makeText(RegisterActivity.this, "User not Registered", Toast.LENGTH_SHORT).show();
                                        reg_progress.setVisibility(View.INVISIBLE);
                                    }

                                }
                            }
                        });

                        //////////////////////////



                    }
                    else 
                    {
                        Toast.makeText(RegisterActivity.this, "Type Password Carefully!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Fill in all fields!", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    private void createNewAccount(String email, String pass) {



    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
