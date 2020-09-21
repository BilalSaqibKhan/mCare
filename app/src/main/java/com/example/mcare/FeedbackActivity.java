package com.example.mcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private EditText txtMail, txtCountry, txtDesc, txtImprovment;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtMail = findViewById(R.id.txt_email);
        txtCountry = findViewById(R.id.txt_country);
        txtDesc = findViewById(R.id.txt_desc);
        txtImprovment = findViewById(R.id.txt_improvment);
        btnSubmit = findViewById(R.id.btn_submit);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });




    }




    private void sendMail()
    {

        String mail = txtMail.getText().toString();

        String country = txtCountry.getText().toString();
        String desc = txtDesc.getText().toString();
        String improve = txtImprovment.getText().toString();

        if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(country) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(improve))
        {

            String subject = "Mcare Feedback";

            String message = "Country: "+ country + " \n Description: " + desc + "\n Improvements required: " + improve;

            JavaMailAPI javaMailAPI = new JavaMailAPI(FeedbackActivity.this, mail, subject, message);
            javaMailAPI.execute();

        }
        else
        {
            Toast.makeText(this, "All Fields are Required!", Toast.LENGTH_SHORT).show();
        }

    }



}
