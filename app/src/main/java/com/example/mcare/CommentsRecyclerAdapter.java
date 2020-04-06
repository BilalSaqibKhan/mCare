package com.example.mcare;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;

    private FirebaseFirestore firebaseFirestore;


    public CommentsRecyclerAdapter(List<Comments> commentsList){
        this.commentsList = commentsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);


        String userId = commentsList.get(position).getUser_id();

        //user data
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setCommentUserData(userName, userImage);
                }
                else
                {
                    //handle exceptions
                }
            }
        });

        //fetching date
        //long millisecond = commentsList.get(position).getTimestamp().getTime();
        //String dateString = DateFormat.format("MM/dd/yyyy HH:mm:ss", new Date(millisecond)).toString();
        //holder.setTime(dateString);

    }

    @Override
    public int getItemCount() {

        if(commentsList != null)
        {
            return commentsList.size();
        }
        else
        {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView commentUserName;
        private CircleImageView commentUserImage;

        private TextView commentDate;

        private TextView comment_message;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
        }


        public void setComment_message(String message)
        {
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }


        public void setCommentUserData(String userName, String userImage)
        {
            commentUserName = mView.findViewById(R.id.comment_user_name);
            commentUserImage = mView.findViewById(R.id.comment_user_image);

            commentUserName.setText(userName);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_image);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(commentUserImage);
        }

        /*
        public void setTime(String dateString)
        {
            commentDate = mView.findViewById(R.id.comment_date);
            commentDate.setText(dateString);
        }
        */
    }
}
