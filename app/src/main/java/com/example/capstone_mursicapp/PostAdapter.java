package com.example.capstone_mursicapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    Context context;

    String reactionType;

    interface PostLikedCallback {
        void onPostLiked(boolean isLiked);
    }

    public void isPostLiked(String postID, PostLikedCallback callback) {
        DocumentReference documentReference = db.collection("Posts").document(postID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    List<Map<String, Object>> likes = (List<Map<String, Object>>) doc.get("Likes");
                    boolean isLiked = false;
                    if (likes != null && !likes.isEmpty()) {
                        for (Map<String, Object> likesMap : likes) {
                            String userID = (String) likesMap.get("User");
                            if (currentUser.getUid().equals(userID)) {
                                isLiked = true;
                                break;
                            }
                        }
                        callback.onPostLiked(isLiked);
                    } else {
                        callback.onPostLiked(isLiked);
                    }
                }
            } else {
                Log.e("isPostLiked", "Error getting document", task.getException());
                callback.onPostLiked(false); // Handle error case
            }
        });
    }

    interface reactionCallback {
        void reactionFound(String reactionType);
    }

    public void getReactionType(String postID, reactionCallback callback) {
        DocumentReference documentReference = db.collection("Posts").document(postID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    List<Map<String, Object>> likes = (List<Map<String, Object>>) doc.get("Likes");
                    if (likes != null && !likes.isEmpty()) {
                        for (Map<String, Object> likesMap : likes) {
                            String user = (String) likesMap.get("User");
                            if (user.equals(currentUser.getUid())) {
                                reactionType = (String) likesMap.get("ReactionType");
                                callback.reactionFound(reactionType);
                            }
                        }
                    }
                }
            }

        });
        if (reactionType == null) {
            callback.reactionFound("");
        }

    }

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }

    public PostAdapter(List<PostModel> posts) {
        this.posts = posts;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlayout, parent, false);
        context = view.getContext();
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        PostModel postModel = posts.get(position);
        holder.bind(postModel);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("post", "Clicked");
                String postID = postModel.getUserID();
                Map<String, Object> likes = new HashMap<>();

                isPostLiked(postID, new PostLikedCallback() {
                    @Override
                    public void onPostLiked(boolean isLiked) {
                        if (!isLiked) {
                            Log.d("Post", "Post not Liked by user");
                            likes.put("User", currentUser.getUid());
                            likes.put("ReactionType", "like");
                            DocumentReference documentReference = db.collection("Posts").document(postID);
                            documentReference.update("Likes", FieldValue.arrayUnion(likes)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Post", "Post Liked");
                                }
                            });
                        }
                        getReactionType(postID, new reactionCallback() {
                            @Override
                            public void reactionFound(String reactionType) {
                                if (reactionType.equals("like") && isLiked) {
                                    Log.d("Post", "Post already liked");
                                    String userID = currentUser.getUid();
                                    DocumentReference documentReference = db.collection("Posts").document(postID);
                                    Map<String, Object> removeLike = new HashMap<>();
                                    removeLike.put("User", userID);
                                    removeLike.put("ReactionType", reactionType);
                                    documentReference.update("Likes", FieldValue.arrayRemove(removeLike)).addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "removed like");

                                        }

                                    });

                                }
                            }
                        });
                    }
                });
            }
        });
        holder.like.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Reactions reactions = new Reactions(postModel.getUserID());
                reactions.show(activity.getSupportFragmentManager(), reactions.getClass().getSimpleName());
                return false;
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postID = postModel.getUserID();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                CommentsFrag dialog = new CommentsFrag(postID);
                dialog.show(activity.getSupportFragmentManager(), "");
            }
        });

    }


    @Override
    public int getItemCount() {
        if (posts == null) {
            this.posts = new ArrayList<>();
        } else {
            this.posts = posts;
        }
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, timeTextView, testtext;
        CircularImageView profilePic;
        ImageView postImage, songImage;
        ImageButton like, comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            profilePic = itemView.findViewById(R.id.pfp);
            timeTextView = itemView.findViewById(R.id.time);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            songImage = itemView.findViewById(R.id.songImage);

        }

        public void bind(PostModel postModel) {
            String postID = postModel.getUserID();
            DocumentReference documentReference = db.collection("Users").document(postID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("Username");
                            String pfp = document.getString("profilePicture");
                            if (pfp == null) {
                                int defaultProfilePicResId = R.drawable.default_pfp;
                                pfp = String.valueOf(defaultProfilePicResId);
                            }
                            usernameTextView.setText(username);
                            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
                            imageLoader.loadImage(pfp, profilePic);

                        }

                    }

                }
            });

            like.setBackgroundResource(R.drawable.baseline_thumb_up_24);
            Timestamp currentTime = Timestamp.now();
            Timestamp pTime = postModel.gettimeStamp();

            long timePassed = currentTime.toDate().getTime() - pTime.toDate().getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(timePassed);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timePassed) % 60;

            String timeStamp;
            if (hours < 1) {
                timeStamp = String.valueOf(minutes) + "ms ago";
            } else {
                timeStamp = String.valueOf(hours) + "hrs ago";
            }

            timeTextView.setText(timeStamp);
            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(postModel.getpImage(), postImage);
            getReactionType(postID, new reactionCallback() {
                @Override
                public void reactionFound(String reactionType) {
                    Log.d("test", "reaction type found, entering switch");
                    switch (reactionType) {
                        case "like":
                            like.setBackgroundResource(R.drawable.likedthumb_up_24);
                            break;
                        case "heart":
                            like.setBackgroundResource(R.drawable.heart_reaction);

                            break;
                        case "love":
                            like.setBackgroundResource(R.drawable.love_reaction);
                            break;
                        case "laugh":
                            like.setBackgroundResource(R.drawable.laugh_reaction);
                            break;
                        case "sleep":
                            like.setBackgroundResource(R.drawable.sleep_reaction);
                            break;
                        case "trash":
                            like.setBackgroundResource(R.drawable.trash_reaction);
                            break;


                    }
                }
            });

            String songID = postModel.getSongID();


        }


    }
}