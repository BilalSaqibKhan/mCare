package com.example.mcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class DetailsActivity extends AppCompatActivity {

    private Toolbar detailsToolbar;
    private ImageView detailsImage, detailsSaveBtn, detailsShareBtn;
    private TextView detailsTitle, DetailsDesc;

    private String image_data, title_data, desc_data;


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
        detailsSaveBtn = findViewById(R.id.details_save_btn);
        detailsShareBtn = findViewById(R.id.details_share_btn);

        image_data = getIntent().getStringExtra("image");
        title_data = getIntent().getStringExtra("title");
        desc_data = getIntent().getStringExtra("desc");

        //setting the data
        Glide.with(DetailsActivity.this).load(image_data).into(detailsImage);
        detailsTitle.setText(title_data);
        DetailsDesc.setText(desc_data);




        //View photo feature
        detailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPhotoIntent = new Intent(DetailsActivity.this, ViewImage.class);
                viewPhotoIntent.putExtra("photo", image_data);
                startActivity(viewPhotoIntent);
            }
        });



        //save photo feature
        detailsSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable draw = (BitmapDrawable) detailsImage.getDrawable();
                Bitmap bitmap = draw.getBitmap();

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/M.Care");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outFile));
                sendBroadcast(intent);

                Toast.makeText(DetailsActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();

            }
        });


        //share photo feature
        detailsShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = ((BitmapDrawable) detailsImage.getDrawable()).getBitmap();
                File file = new File(getExternalCacheDir(), "sample.png");
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                    fout.flush();
                    fout.close();
                    file.setReadable(true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("image/png");
                String s = title_data + "\n" + desc_data;
                shareIntent.putExtra(Intent.EXTRA_TEXT, s);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(shareIntent, "Share via"));

            }
        });


    }
}
