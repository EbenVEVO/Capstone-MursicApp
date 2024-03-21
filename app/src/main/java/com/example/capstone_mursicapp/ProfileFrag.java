package com.example.capstone_mursicapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProfileFrag extends Fragment {

    CircularImageView pfpIcon;
    TextView usernameTextView, bioTextView;
    Uri imageUri;
    ImageView profilePic;
    MenuItem profileMenuItem;
    Boolean isOwnProfile;
    Button editprofile;

    RecyclerView userPost;


    UserListModel user;

    ImageButton addPost;

    List<PostModel> post;
    PostAdapter postAdapter;
    String userID;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Fragment currentFragment = this;
    Toolbar toolbar;
    FirebaseStorage storage = FirebaseStorage.getInstance();;

    public void setIsOwnProfile(Boolean isOwnProfile){
        this.isOwnProfile = isOwnProfile;
    }

    public void setUser(UserListModel user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        AppCompatActivity activity= (AppCompatActivity) getActivity();


        usernameTextView = view.findViewById(R.id.username);
        bioTextView = view.findViewById(R.id.bio);
        profilePic = view.findViewById(R.id.profileImg);
        toolbar = view.findViewById(R.id.profile_toolbar);
        activity.setSupportActionBar(toolbar);
        userPost = view.findViewById(R.id.userpost);
        addPost = view.findViewById(R.id.addpost);

        postAdapter = new PostAdapter(post);
        userPost.setAdapter(postAdapter);

        if(isOwnProfile) {
            loadUserProfile();
            System.out.println(postAdapter.getItemCount());
            if(postAdapter.getItemCount()==0){

                addPost.setVisibility(View.VISIBLE);
                addPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPost();
                    }
                });
            }
            else {
                addPost.setVisibility(View.GONE);
            }

            editprofile = view.findViewById(R.id.editprofile);
            editprofile.setVisibility(View.VISIBLE);
            editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditProfileFrag editProfileFrag = new EditProfileFrag();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, editProfileFrag).commitNow();
                }
            });





        }
        if(!isOwnProfile){
            loadNewUserProfile();
        }

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!isOwnProfile) {
            inflater.inflate(R.menu.toolbar_menu_friends, menu);
            profileMenuItem = menu.findItem(R.id.h_profile);
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View actionview = layoutInflater.inflate(R.layout.profile_menu_layout, null);
            pfpIcon = actionview.findViewById(R.id.menupfp);
            MenuItemCompat.setActionView(profileMenuItem, actionview); // Use MenuItemCompat for older versions
            if (pfpIcon != null) {
                if (getActivity() != null && isAdded()) {
                    loadProfileImage();
                }

            }
            pfpIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFrag profileFrag = new ProfileFrag();
                    profileFrag.setIsOwnProfile(true);
                    if (!getActivity().isDestroyed()) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFrag).commitNow();
                    }
                }
            });
        }
       else{
            inflater.inflate(R.menu.toolbar_menu_profile, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }



    public void loadProfileImage() {

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
                        imageLoader.loadImage(pfp, pfpIcon);
                    }
                }
            }
        });
    }

    public void loadUserProfile(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null){
            String userID = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference documentReference= db.collection("Users").document(userID);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("Username");
                            String bio = document.getString("Bio");
                            if (bio ==null){
                                documentReference.update("Bio", "");
                            }
                            String pfp = document.getString("profilePicture");
                            if (username != null) {
                                usernameTextView.setText(username);
                            }
                            if (bio != null) {
                                bioTextView.setText(bio);
                            }
                            ImageLoader imageLoader = new  ImageLoader(currentFragment.getContext());
                            imageLoader.loadImage(pfp, profilePic);


                        }
                    }
                }
            });


            loadUserPost(userID);

        }
    }

    public void loadNewUserProfile(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference= db.collection("Users").document(user.getUserID());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.getString("Username");
                        String bio = document.getString("Bio");
                        if (bio==null){
                            documentReference.update("Bio", "");
                        }
                        String pfp = document.getString("profilePicture");
                        if (username != null) {
                            usernameTextView.setText(username);
                        }
                        if (bio != null) {
                            bioTextView.setText(bio);
                        }
                        ImageLoader imageLoader = new  ImageLoader(currentFragment.getContext());
                        imageLoader.loadImage(pfp, profilePic);


                    }
                }
            }
        });
        loadUserPost(user.getUserID());
    }

    public void loadUserPost(String userID){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Posts").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("pUsername");
                    long time = documentSnapshot.getLong("pTime");
                    String pfp = documentSnapshot.getString("pProfilePic");
                    if (pfp == null) {
                        int defaultProfilePicResId = R.drawable.default_pfp;
                        pfp = String.valueOf(defaultProfilePicResId);
                    }
                    String postImage = documentSnapshot.getString("pImage");

                    PostModel postModel = new PostModel(username, postImage, time, pfp,userID);
                    post.add(postModel);

                    postAdapter.setPosts(post);
                    postAdapter.notifyDataSetChanged();
                }
                else {
                    Log.d("Post", "No post found for " + userID);
                }
            }

        }).addOnFailureListener(e -> {
            Log.d("Post", "Error loading post");
        });
    }

    public void createPost(){

        Intent intent = new Intent(getActivity(), PostActivity.class);
        startActivity(intent);
    }

}



