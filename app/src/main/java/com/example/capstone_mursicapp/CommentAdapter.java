package com.example.capstone_mursicapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<CommentsModel> comments;

    public void setComments(List<CommentsModel> comments) {
        this.comments = comments;
    }

    public CommentAdapter(List<CommentsModel> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentlayout, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentsModel commentsModel = comments.get(position);
        holder.bind(commentsModel);
    }

    @Override
    public int getItemCount() {
        if(comments == null){
            this.comments = new ArrayList<>();
        }
        else {
            this.comments = comments;
        }
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, comment, timeText;
        CircularImageView pfp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            pfp = itemView.findViewById(R.id.pfp);
            timeText = itemView.findViewById(R.id.timestamp);
        }

        public void bind(CommentsModel commentsModel) {
            username.setText(commentsModel.getUsername());
            comment.setText(commentsModel.getComment());
            long currentTime = System.currentTimeMillis();
            long timePassed = currentTime - commentsModel.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(timePassed) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timePassed) % 60;

            String timeStamp;
            if (hours < 1){
                timeStamp = String.valueOf(minutes) + "ms ago";
            }
            else {
                timeStamp = String.valueOf(hours) + "hrs ago";
            }

            timeText.setText(timeStamp);


            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(commentsModel.getProfilePic(), pfp);
        }

    }
}
