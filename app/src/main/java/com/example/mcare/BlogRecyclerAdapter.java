package com.example.mcare;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String title_data = blog_list.get(position).getTitle();
        holder.setTitleText(title_data);

        final String desc_data = blog_list.get(position).getDesc();

        final String image_url = blog_list.get(position).getImage_url();
        String thumbUrl = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUrl);

        String user_id = blog_list.get(position).getUser_id();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            blogCommentBtn = mView.findViewById(R.id.blog_comment_btn);
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
