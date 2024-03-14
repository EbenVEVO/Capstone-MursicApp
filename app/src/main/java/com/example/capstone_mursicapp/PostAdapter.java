package com.example.capstone_mursicapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = postModel.getpUsername();
                Map<String, Object> likes = new HashMap<>();
                Query query = db.collection("Posts").whereEqualTo("pUsername", username);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                String postID = doc.getId();
                                likes.put("User", currentUser.getUid());
                                DocumentReference documentReference = db.collection("Post").document(postID);
                                documentReference.update("Likes", FieldValue.arrayUnion(likes)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Post", "Post Liked");
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

    }
    public boolean isPostLiked(String postID){
        DocumentReference documentReference = db.collection("Posts")

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
