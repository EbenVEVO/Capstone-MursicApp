package com.example.capstone_mursicapp;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CommentsFrag extends BottomSheetDialogFragment {
    BottomSheetDialog dialog;
    BottomSheetBehavior bottomSheetBehavior;

    String userID;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Fragment currentFragment = this;
    View view;

    RecyclerView commentsView;
    CircularImageView profilepic;
    TextView post;
    EditText given_comment;

    String postID;

    List<CommentsModel> comments;
    CommentAdapter commentAdapter;
    public CommentsFrag(String postID){
        this.postID = postID;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadProfilePic();
        loadComments();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        profilepic = view.findViewById(R.id.profilepic);
        post = view.findViewById(R.id.post);
        given_comment = view.findViewById(R.id.commenttextfield);
        commentsView = view.findViewById(R.id.comments);

        VerticalItemDecorator itemDecorator = new VerticalItemDecorator(1);
        commentsView.addItemDecoration(itemDecorator);

        commentAdapter = new CommentAdapter(comments);
        commentsView.setAdapter(commentAdapter);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = currentUser.getUid();
                String comment = String.valueOf(given_comment.getText());
                if (!comment.isEmpty()){
                    DocumentReference documentReference = db.collection("Users").document(userID);
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("Username");
                            String profilePic = documentSnapshot.getString("profilePicture");
                            long time = System.currentTimeMillis();
                            CommentsModel commentsModel = new CommentsModel(userID, username, comment, profilePic, time);
                            db.collection("Posts").document(postID)
                                    .collection("Comments").add(commentsModel).addOnSuccessListener(aVoid->{
                                        Log.d("Comment", "comment posted");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("Comment", "failed to post comment");
                                    });

                        }
                    });
                    given_comment.setText("");
                }
            }
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance){
        super.onViewCreated(view, savedInstance);
        dialog = (BottomSheetDialog) getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.onBackPressed();
    }

    public void loadProfilePic(){
        if (currentUser != null) {
            userID = currentUser.getUid();
        }
        DocumentReference documentReference = db.collection("Users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String pfp = document.getString("profilePicture");
                        ImageLoader imageLoader = new  ImageLoader(currentFragment.getContext());
                        imageLoader.loadImage(pfp, profilepic);
                    }
                }
            }
        });
    }
    public void loadComments(){
        comments = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("Posts").document(postID)
                .collection("Comments");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        String userID, username, comment, pfp;
                        long time;
                        userID = documentSnapshot.getString("userID");
                        username = documentSnapshot.getString("username");
                        comment = documentSnapshot.getString("comment");
                        pfp = documentSnapshot.getString("profilePic");
                        time = documentSnapshot.getLong("time");

                        CommentsModel commentsModel = new CommentsModel(userID, username, comment, pfp, time);
                        comments.add(commentsModel);
                        commentAdapter.setComments(comments);
                        commentAdapter.notifyDataSetChanged();


                    }
                }
                else {
                    Log.d("Comment", "No comment found for " + postID);

                }
            }
        }).addOnFailureListener(e -> {
            Log.d("Comment", "Error loading comments");
        });
    }
    
}