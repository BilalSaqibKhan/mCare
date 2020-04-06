package com.example.mcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentsToolbar;

    private EditText comment_field;
    private ImageView comment_post_btn;
    private ProgressBar commentsProgress;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String blog_post_id;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentsToolbar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(commentsToolbar);
        getSupportActionBar().setTitle("Comments");

        commentsProgress = findViewById(R.id.comments_progress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        //RecyclerView firebase list
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        //Query query = firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING);
        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    // Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                } else {

                    if(!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String commentId = doc.getDocument().getId();
                                Comments comments = doc.getDocument().toObject(Comments.class);
                                commentsList.add(comments);
                                commentsRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });


        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = comment_field.getText().toString();

                if(!comment_message.isEmpty())
                {
                    commentsProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());
                    commentsMap.put("message", comment_message);
                    commentsMap.put("user_id", current_user_id);

                    firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(!task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this, "Error Posting Comment", Toast.LENGTH_SHORT).show();
                                commentsProgress.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                comment_field.setText("");
                                Toast.makeText(CommentsActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                                commentsProgress.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
                else
                {
                    Toast.makeText(CommentsActivity.this, "Write Your Comment First!", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
