package com.example.mcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewImage extends AppCompatActivity {

    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        PhotoView photoView = findViewById(R.id.photo_view);

        photo = getIntent().getStringExtra("photo");

        Glide.with(ViewImage.this).load(photo).into(photoView);

    }
}
