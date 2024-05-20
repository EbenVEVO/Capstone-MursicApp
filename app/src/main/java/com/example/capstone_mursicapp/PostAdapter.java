package com.example.capstone_mursicapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private List<PostModel> posts;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Context context;

    private String reactionType;

    private SpotifyManager spotManager;

    public PostAdapter(List<PostModel> posts) {
        this.posts = posts;
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.spotManager = new SpotifyManager();
    }

    public void setPosts(List<PostModel> post) {
    }

    public interface PostLikedCallback {
        void onPostLiked(boolean isLiked);
    }

    public void isPostLiked(String postID, PostLikedCallback callback) {
        db.collection("Posts").document(postID).get().addOnCompleteListener(task -> {
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
            callback.onPostLiked(false);
        }
    });
    }

    public interface ReactionCallback {
        void reactionFound(String reactionType);
    }

    public void getReactionType(String postID, ReactionCallback callback) {
        db.collection("Posts").document(postID).get().addOnCompleteListener(task -> {
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
                            return;
                        }
                    }
                }
            }
            callback.reactionFound("");
        }
    });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlayout, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PostModel postModel = posts.get(position);
        holder.bind(postModel);

        holder.like.setOnClickListener(v -> {
        Log.d("post", "Clicked");
        String postID = postModel.getUserID();
        Map<String, Object> likes = new HashMap<>();

        isPostLiked(postID, isLiked -> {
        if (!isLiked) {
            Log.d("Post", "Post not Liked by user");
            likes.put("User", currentUser.getUid());
            likes.put("ReactionType", "like");
            db.collection("Posts").document(postID).update("Likes", FieldValue.arrayUnion(likes))
                .addOnSuccessListener(aVoid -> Log.d("Post", "Post Liked"));
        }
        getReactionType(postID, reactionType -> {
        if ("like".equals(reactionType) && isLiked) {
            Log.d("Post", "Post already liked");
            String userID = currentUser.getUid();
            Map<String, Object> removeLike = new HashMap<>();
            removeLike.put("User", userID);
            removeLike.put("ReactionType", reactionType);
            db.collection("Posts").document(postID).update("Likes", FieldValue.arrayRemove(removeLike))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "removed like"));
        }
    });
    });
    });

        holder.like.setOnLongClickListener(v -> {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        Reactions reactions = new Reactions(postModel.getUserID());
        reactions.show(activity.getSupportFragmentManager(), reactions.getClass().getSimpleName());
        return false;
    });

        holder.comment.setOnClickListener(v -> {
        String postID = postModel.getUserID();
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        CommentsFrag dialog = new CommentsFrag(postID);
        dialog.show(activity.getSupportFragmentManager(), "");
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
        TextView usernameTextView;
        TextView timeTextView;
        CircularImageView profilePic;
        ImageView postImage;
        ImageView songImage;
        ImageButton like;
        ImageButton comment;

        public ViewHolder(View itemView) {
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
            db.collection("Users").document(postID).get().addOnCompleteListener(task -> {
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
        });

            like.setBackgroundResource(R.drawable.baseline_thumb_up_24);
            Timestamp currentTime = Timestamp.now();
            Timestamp pTime = postModel.gettimeStamp();

            long timePassed = currentTime.toDate().getTime() - pTime.toDate().getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(timePassed);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timePassed) % 60;

            String timeStamp;
            if (hours < 1) {
                timeStamp = minutes + "ms ago";
            } else {
                timeStamp = hours + "hrs ago";
            }

            timeTextView.setText(timeStamp);
            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(postModel.getpImage(), postImage);

            getReactionType(postID, reactionType -> {
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
        });
            String songId = postModel.getSongID();
            Log.e("a", String.valueOf(songId == null));
            SongRetriever songRetriever = new SongRetriever();

        }
    }
}
