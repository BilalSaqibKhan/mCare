package com.example.mcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private ImageView newPostImageBtn;
    private EditText newPostTitle, newPostDesc;
    private Button newPostBtn;
    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;

    private Bitmap compressedImageFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Post a medical report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostProgress = findViewById(R.id.new_post_progress);

        newPostImageBtn = findViewById(R.id.new_post_image);
        newPostTitle = findViewById(R.id.new_post_title);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();


        //click on image button
        newPostImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);

            }
        });



        //click on submit/publish post button
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title = newPostTitle.getText().toString();
                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && mImageUri != null)
                {
                    //progressBar VISIBLE
                    newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    final StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
                    filePath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                            if(task.isSuccessful())
                            {
                                //used library to get compressed file
                                File newImageFile = new File(convertMediaUriToPath(mImageUri));

                                try {
                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxWidth(720)
                                            .setMaxHeight(720)
                                            .setQuality(50)
                                            .compressToBitmap(newImageFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                final byte[] thumbData = baos.toByteArray();


                                final StorageReference ref_compressed = storageReference.child("post_images/thumbs").child(randomName+".jpg");
                                final UploadTask uploadTask = ref_compressed.putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                                        //get uploadTask uri

                                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }

                                                // Continue with the task to get the download URL
                                                return ref_compressed.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                    final Uri uriThumbnailImage = task.getResult();


                                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uriActualImage) {

                                                        Map<String, Object> postMap = new HashMap<>();
                                                        postMap.put("image_url", String.valueOf(uriActualImage));
                                                        postMap.put("image_thumb", String.valueOf(uriThumbnailImage));
                                                        postMap.put("title", title);
                                                        postMap.put("desc", desc);
                                                        postMap.put("user_id", current_user_id);
                                                        postMap.put("timestamp", FieldValue.serverTimestamp());


                                                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(NewPostActivity.this, "Report Published!", Toast.LENGTH_SHORT).show();

                                                                    Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                                    startActivity(mainIntent);
                                                                    finish();
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(NewPostActivity.this, "Action Failed!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                //INVISIBLE
                                                                newPostProgress.setVisibility(View.INVISIBLE);
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewPostActivity.this, "thumbnail upload error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //compressed upload ENDS

                            }
                            else
                            {
                                //progressBar INVISIBLE
                                Toast.makeText(NewPostActivity.this, "Image upload error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(NewPostActivity.this, "All fields are necessary", Toast.LENGTH_SHORT).show();
                }

            }
        });








    }


    public String convertMediaUriToPath(Uri uri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            newPostImageBtn.setImageURI(mImageUri);
        }

    }





}
