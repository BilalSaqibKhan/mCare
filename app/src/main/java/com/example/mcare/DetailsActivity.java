package com.example.mcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar detailsToolbar;
    private ImageView detailsImage;
    private TextView detailsTitle;
    private TextView DetailsDesc;

    private String image_data;
    private String title_data;
    private String desc_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsToolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(detailsToolbar);
        getSupportActionBar().setTitle("Report Details");

        detailsImage = findViewById(R.id.details_image);
        detailsTitle = findViewById(R.id.txt_title);
        DetailsDesc = findViewById(R.id.txt_description);

        image_data = getIntent().getStringExtra("image");
        title_data = getIntent().getStringExtra("title");
        desc_data = getIntent().getStringExtra("desc");

        //setting the data
        Glide.with(DetailsActivity.this).load(image_data).into(detailsImage);
        detailsTitle.setText(title_data);
        DetailsDesc.setText(desc_data);


        detailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPhotoIntent = new Intent(DetailsActivity.this, ViewImage.class);
                viewPhotoIntent.putExtra("photo", image_data);
                startActivity(viewPhotoIntent);
            }
        });


    }
}
