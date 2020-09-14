package com.example.mcare;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {


    public List<BlogPost> blog_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list)
    {
        this.blog_list = blog_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String title_data = blog_list.get(position).getTitle();
        holder.setTitleText(title_data);

        final String desc_data = blog_list.get(position).getDesc();

        final String image_url = blog_list.get(position).getImage_url();
        String thumbUrl = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUrl);

        //user id of user that have posted
        final String user_id = blog_list.get(position).getUser_id();


        if(user_id.equals(currentUserId)){
            holder.blogPostDeleteBtn.setEnabled(true);
            holder.blogPostDeleteBtn.setVisibility(View.VISIBLE);
        }


        //user data will be retrieved here...........
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);
                }
                else
                {
                    //handle exceptions
                }

            }
        });

        //click on post itself
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent(context, DetailsActivity.class);
                detailsIntent.putExtra("image", image_url);
                detailsIntent.putExtra("title", title_data);
                detailsIntent.putExtra("desc", desc_data);
                context.startActivity(detailsIntent);
            }
        });


        //Comments Feature
        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(context, CommentsActivity.class);
                commentsIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentsIntent);
            }
        });

        //delete post feature
        holder.blogPostDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        blog_list.remove(position);
                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //Share Feature
        holder.blogShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = ((BitmapDrawable) holder.blogImageView.getDrawable()).getBitmap();
                File file = new File(context.getExternalCacheDir(), "sample.png");
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
                //shareIntent.setPackage("com.whatsapp");
                String s = title_data + "\n" + desc_data;
                shareIntent.putExtra(Intent.EXTRA_TEXT, s);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));

            }
        });


        //long millisecond = blog_list.get(position).getTimestamp().getTime();
        //String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        //holder.setTime(dateString);

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        //private TextView blogDate;

        private TextView blogUserName;
        private CircleImageView blogUserImage;

        private ImageView blogCommentBtn;
        private TextView blogCommentCount;
        private ImageView blogShareBtn;

        private ImageView blogPostDeleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            blogCommentBtn = mView.findViewById(R.id.blog_comment_btn);
            blogShareBtn = mView.findViewById(R.id.blog_share_btn);
            blogPostDeleteBtn = mView.findViewById(R.id.blog_post_delete_btn);
        }

        public void setTitleText(String titleText)
        {
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(titleText);
        }

        public void setBlogImage(String downloadUri, String thumbUrl)
        {
            blogImageView = mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).thumbnail(Glide.with(context).load(thumbUrl)).into(blogImageView);
        }

        public void setTime(String date)
        {
            //blogDate = mView.findViewById(R.id.blog_date);
            //blogDate.setText(date);
        }

        public void setUserData(String name, String image)
        {
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);

            blogUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_image);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);
        }

    }


}
