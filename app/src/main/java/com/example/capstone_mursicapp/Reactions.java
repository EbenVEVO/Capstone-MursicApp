package com.example.capstone_mursicapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reactions extends DialogFragment implements View.OnClickListener {
    View view;
    String postID;
    ImageView like, heart, love, laugh, trash, sleep;
    Map<String,Object> reaction;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public Reactions(String postID){
        this.postID = postID;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        view = inflater.inflate(R.layout.reaction_dialog, container, false);
        like = view.findViewById(R.id.like);
        heart = view.findViewById(R.id.heart);
        love = view.findViewById(R.id.love);
        laugh = view.findViewById(R.id.laugh);
        trash = view.findViewById(R.id.trash);
        sleep = view.findViewById(R.id.sleep);

        like.setOnClickListener(this);
        heart.setOnClickListener(this);
        love.setOnClickListener(this);
        laugh.setOnClickListener(this);
        trash.setOnClickListener(this);
        sleep.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        DocumentReference documentReference = db.collection("Posts").document(postID);
        switch (v.getId()) {
            case R.id.like:
                reaction = new HashMap<>();
                reaction.put("User", currentUser.getUid());
                reaction.put("ReactionType", "like");
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            List<Map<String,Object>> likes = (List<Map<String, Object>>) documentSnapshot.get("Likes");
                            if(likes!=null&& !likes.isEmpty()){
                                for(Map<String,Object> user : likes){
                                    if(user.get("User").equals(currentUser.getUid())){
                                        likes.remove(user);
                                    }
                                }
                                likes.add(reaction);
                                documentReference.update("Likes", likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Reaction Changed", "Reaction changed");
                                    }
                                });
                            }
                        }
                    }
                });
                dismiss();

                break;
            case R.id.heart:
                reaction = new HashMap<>();
                reaction.put("User", currentUser.getUid());
                reaction.put("ReactionType", "heart");
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            List<Map<String,Object>> likes = (List<Map<String, Object>>) documentSnapshot.get("Likes");
                            if(likes!=null&& !likes.isEmpty()){
                                for(Map<String,Object> user : likes){
                                    if(user.get("User").equals(currentUser.getUid())){
                                        likes.remove(user);
                                    }
                                }
                                likes.add(reaction);
                                documentReference.update("Likes", likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Reaction Changed", "Reaction changed");
                                    }
                                });
                            }
                        }
                    }
                });
                dismiss();

                break;
            case R.id.love:
                reaction = new HashMap<>();
                reaction.put("User", currentUser.getUid());
                reaction.put("ReactionType", "love");
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            List<Map<String,Object>> likes = (List<Map<String, Object>>) documentSnapshot.get("Likes");
                            if(likes!=null&& !likes.isEmpty()){
                                for(Map<String,Object> user : likes){
                                    if(user.get("User").equals(currentUser.getUid())){
                                        likes.remove(user);
                                    }
                                }
                                likes.add(reaction);
                                documentReference.update("Likes", likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Reaction Changed", "Reaction changed");
                                    }
                                });
                            }
                        }
                    }
                });
                dismiss();

                break;
            case R.id.laugh:
                reaction = new HashMap<>();
                reaction.put("User", currentUser.getUid());
                reaction.put("ReactionType", "laugh");
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            List<Map<String,Object>> likes = (List<Map<String, Object>>) documentSnapshot.get("Likes");
                            if(likes!=null&& !likes.isEmpty()){
                                for(Map<String,Object> user : likes){
                                    if(user.get("User").equals(currentUser.getUid())){
                                        likes.remove(user);
                                    }
                                }
                                likes.add(reaction);
                                documentReference.update("Likes", likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Reaction Changed", "Reaction changed");
                                    }
                                });
                            }
                        }
                    }
                });
                dismiss();

                break;
            case R.id.trash:
                reaction = new HashMap<>();
                reaction.put("User", currentUser.getUid());
                reaction.put("ReactionType", "trash");
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            List<Map<String,Object>> likes = (List<Map<String, Object>>) documentSnapshot.get("Likes");
                            if(likes!=null&& !likes.isEmpty()){
                                for(Map<String,Object> user : likes){
                                    if(user.get("User").equals(currentUser.getUid())){
                                        likes.remove(user);
                                    }
                                }
                                likes.add(reaction);
                                documentReference.update("Likes", likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Reaction Changed", "Reaction changed");
                                    }
                                });
                            }
                        }
                    }
                });
                dismiss();

                break;
            case R.id.sleep:
                reaction = new HashMap<>();
                reaction.put("User", currentUser.getUid());
                reaction.put("ReactionType", "sleep");
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            List<Map<String,Object>> likes = (List<Map<String, Object>>) documentSnapshot.get("Likes");
                            if(likes!=null && !likes.isEmpty()){
                                for(Map<String,Object> user : likes){
                                    if(user.get("User").equals(currentUser.getUid())){
                                        likes.remove(user);
                                    }
                                }
                                likes.add(reaction);
                                documentReference.update("Likes", likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Reaction Changed", "Reaction changed");
                                    }
                                });
                            }
                        }
                    }
                });
                dismiss();
                break;

        }

    }
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style){
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams manager = new WindowManager.LayoutParams();
        manager.width = WindowManager.LayoutParams.MATCH_PARENT;
        manager.height = WindowManager.LayoutParams.WRAP_CONTENT;
        manager.dimAmount = 0.0f;

        dialog.getWindow().getDecorView().setBackground(getResources().getDrawable(android.R.color.transparent));
        dialog.setCanceledOnTouchOutside(true);
        dialog.onBackPressed();

    }

}
