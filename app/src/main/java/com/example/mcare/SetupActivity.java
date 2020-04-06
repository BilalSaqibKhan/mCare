package com.example.mcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private Toolbar setupToolbar;
    private CircleImageView setupImage;
    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private boolean isChanged = false;  //checks if the image is changed or not
    private Uri mainImageURI = null;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        user_id = firebaseAuth.getCurrentUser().getUid();

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);




        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists())
                {

                    String name = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    mainImageURI = Uri.parse(image);

                    setupName.setText(name);

                    RequestOptions placeHolderRequest = new RequestOptions();
                    placeHolderRequest.placeholder(R.drawable.default_image);

                    Glide.with(SetupActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image).into(setupImage);

                }
                else
                {
                    Toast.makeText(SetupActivity.this, "Setup Your Account", Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });










        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();

                if(!TextUtils.isEmpty(user_name) || mainImageURI != null)
                {
                    setupProgress.setVisibility(View.VISIBLE);

                    final StorageReference image_path = storageReference.child("profile_image").child(user_id + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                storeFirestore(user_name, image_path);
                            }
                            else
                            {
                                Toast.makeText(SetupActivity.this, "Image upload error", Toast.LENGTH_SHORT).show();
                                setupProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(SetupActivity.this, "Please give proper information", Toast.LENGTH_SHORT).show();
                    setupProgress.setVisibility(View.INVISIBLE);
                }
            }
        });





        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //taking permission for marshmallow
                {
                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        //if permission is not granted
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else
                    {
                        bringImagePicker();
                    }
                }
                else
                {
                    bringImagePicker();
                }
            }
        });


    }

    private void storeFirestore(final String user_name, StorageReference image_path) {



        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri download_uri = uri;

                Map<String, String> userMap = new HashMap<>();
                userMap.put("name", user_name);
                userMap.put("image", download_uri.toString());

                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Account Settings Saved", Toast.LENGTH_SHORT).show();

                            Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        setupProgress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });







    }

    private void bringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
