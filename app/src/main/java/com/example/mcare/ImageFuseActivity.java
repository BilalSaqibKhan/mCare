package com.example.mcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFuseActivity extends AppCompatActivity {



    Button btnLoadImage1, btnLoadImage2;
    TextView textSource1, textSource2;
    Button btnProcessing;
    ImageView imageResult;
    ProgressDialog pd;

    final int RQS_IMAGE1 = 1;
    final int RQS_IMAGE2 = 2;

    Uri source1, source2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fuse);


        btnLoadImage1 = findViewById(R.id.loadimage1);
        btnLoadImage2 = findViewById(R.id.loadimage2);
        textSource1 = findViewById(R.id.sourceuri1);
        textSource2 = findViewById(R.id.sourceuri2);
        btnProcessing = findViewById(R.id.processing);
        imageResult = findViewById(R.id.result);

        pd = new ProgressDialog(ImageFuseActivity.this);
        pd.setTitle("Fusing Images");
        pd.setMessage("Loading...");


        btnLoadImage1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }});

        btnLoadImage2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE2);
            }});

        btnProcessing.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                pd.show();

                if(source1 != null && source2 != null)
                {
                    Bitmap processedBitmap = ProcessingBitmap();
                    if(processedBitmap != null)
                    {
                        imageResult.setImageBitmap(processedBitmap);

                        //thread
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                            }
                        }, 5000);
                        //end thread
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Something wrong in processing!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Select both image!", Toast.LENGTH_LONG).show();
                }

            }});



        imageResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable draw = (BitmapDrawable) imageResult.getDrawable();
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

                Toast.makeText(ImageFuseActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
            }
        });




    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source1 = data.getData();
                    textSource1.setText("Image 1 selected");
                    break;
                case RQS_IMAGE2:
                    source2 = data.getData();
                    textSource2.setText("Image 2 selected");
                    break;
            }
        }
    }


    private Bitmap ProcessingBitmap(){
        Bitmap bm1 = null;
        Bitmap bm2 =  null;
        Bitmap newBitmap = null;

        try {
            bm1 = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(source1));
            bm2 = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(source2));

            int w;
            if(bm1.getWidth() >= bm2.getWidth()){
                w = bm1.getWidth();
            }else{
                w = bm2.getWidth();
            }

            int h;
            if(bm1.getHeight() >= bm2.getHeight()){
                h = bm1.getHeight();
            }else{
                h = bm2.getHeight();
            }

            Bitmap.Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(w, h, config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1, 0, 0, null);

            Paint paint = new Paint();

            //int selectedPos = spinnerMode.getSelectedItemPosition();
            //PorterDuff.Mode selectedMode = arrayMode[selectedPos];
            PorterDuff.Mode selectedMode = PorterDuff.Mode.ADD;


            paint.setXfermode(new PorterDuffXfermode(selectedMode));
            newCanvas.drawBitmap(bm2, 0, 0, paint);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newBitmap;
    }





}
