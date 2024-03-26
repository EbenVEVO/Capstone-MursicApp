package com.example.capstone_mursicapp;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.reactbutton.ReactButton;
import com.amrdeveloper.reactbutton.Reaction;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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

        holder.like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("React", "pressed");
                return false;
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postID = postModel.getUserID();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                CommentsFrag dialog = new CommentsFrag(postID);
                dialog.show(activity.getSupportFragmentManager(),"");
            }
        });

    }

    interface PostLikedCallback {
        void onPostLiked(boolean isLiked);
    }
    public void isPostLiked(String postID, PostLikedCallback callback) {
        DocumentReference documentReference = db.collection("Posts").document(postID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    List<Map<String,Object>> likes = (List<Map<String, Object>>) doc.get("Likes");
                    boolean isLiked = false;
                    if(likes != null && !likes.isEmpty()) {
                        for (Map<String, Object> likesMap : likes) {
                            String userID = (String) likesMap.get("User");
                            if (currentUser.getUid().equals(userID)) {
                                isLiked = true;
                                break;
                            }
                        }
                        callback.onPostLiked(isLiked);
                    }
                    else{
                        callback.onPostLiked(isLiked);
                    }
                }
            } else {
                Log.e("isPostLiked", "Error getting document", task.getException());
                callback.onPostLiked(false); // Handle error case
            }
        });
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
        ReactButton like;
        ImageButton comment;
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
            long currentTime = System.currentTimeMillis();
            long timePassed = currentTime - postModel.pTime;
            long hours = TimeUnit.MILLISECONDS.toHours(timePassed) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timePassed) % 60;

            String timeStamp;
            if (hours < 1){
                timeStamp = String.valueOf(minutes) + "ms ago";
            }
            else {
                timeStamp = String.valueOf(hours) + "hrs ago";
            }

            timeTextView.setText(timeStamp);

            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(postModel.getpProfilePic(), profilePic);
            imageLoader.loadImage(postModel.getpImage(), postImage);

            String postID = postModel.getUserID();



        }
    }

}
