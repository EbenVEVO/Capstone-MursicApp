package com.example.capstone_mursicapp;

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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<PostModel> posts;

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }

    public PostAdapter(List<PostModel> posts){
        this.posts = posts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlayout, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        PostModel postModel = posts.get(position);
        holder.bind(postModel);

    }

    @Override
    public int getItemCount() {
        if(posts == null){
            this.posts = new ArrayList<>();
        }
        else {
            this.posts = posts;
        }
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView usernameTextView, timeTextView;
        CircularImageView profilePic;
        ImageView postImage;
        ImageButton like, comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            profilePic = itemView.findViewById(R.id.pfp);
            timeTextView = itemView.findViewById(R.id.time);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);


        }

        public void bind(PostModel postModel){
            usernameTextView.setText(postModel.getpUsername());
            timeTextView.setText(postModel.getpTime());

            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(postModel.getpProfilePic(), profilePic);
            imageLoader.loadImage(postModel.getpImage(), postImage);

        }
    }

}
