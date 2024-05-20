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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class ProfileFrag extends Fragment {

    CircularImageView pfpIcon;
    TextView usernameTextView, bioTextView, artistmemo, uploadtext, artisttile, noartist;
    Uri imageUri;
    ImageView profilePic;
    MenuItem profileMenuItem;
    Boolean isOwnProfile;
    Button editprofile, addArtist1, addArtist2, addFriend, addSong;

    RecyclerView userPost, artistview, songview;

    Boolean isMusic = false;
    UserListModel user;

    ImageButton addPost;
    LinearLayout linearLayout;

    List<PostModel> post;
    List<ArtistModel> artists;
    PostAdapter postAdapter;
    TopArtistAdapter artistAdapter;
    String userID;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ConstraintLayout constraintLayout;
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

        uploadtext = view.findViewById(R.id.uploadtext);
        editprofile = view.findViewById(R.id.editprofile);
        usernameTextView = view.findViewById(R.id.username);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        linearLayout = view.findViewById(R.id.postholder);

        addSong = view.findViewById(R.id.addsong);
        addFriend = view.findViewById(R.id.addfriend);
        noartist = view.findViewById(R.id.noartist);
        bioTextView = view.findViewById(R.id.bio);
        profilePic = view.findViewById(R.id.profileImg);
        toolbar = view.findViewById(R.id.profile_toolbar);
        activity.setSupportActionBar(toolbar);
        userPost = view.findViewById(R.id.userpost);
        addPost = view.findViewById(R.id.addpost);
        artisttile = view.findViewById(R.id.artisttitle);
        addArtist1 = view.findViewById(R.id.buttonnoartist);
        addArtist2 =view.findViewById(R.id.buttonwartist);
        artistmemo = view.findViewById(R.id.addartistmemo);
        artistview = view.findViewById(R.id.artistview);
        songview = view.findViewById(R.id.songsview);


        postAdapter = new PostAdapter(post);
        userPost.setAdapter(postAdapter);


        HorizontalItemDecorator itemDecorator = new HorizontalItemDecorator(40);
        artistview.addItemDecoration(itemDecorator);

        artistAdapter  = new TopArtistAdapter(artists);
        artistview.setAdapter(artistAdapter);




        if(isOwnProfile && isOwnProfile!=null) {
            loadUserProfile();
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
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFrag).addToBackStack(null).commit();
                    }
                }
            });
        }
       else{
            inflater.inflate(R.menu.toolbar_menu_profile, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    interface friendAddedCallback{
        void isUserRequested(boolean requested);
    }
    public void isUserRequested(String userID,friendAddedCallback callback){
        db.collection("Users").document(userID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    List<Map<String, Object>> friendRequests = (List<Map<String, Object>>) doc.get("friendRequests");
                    if (friendRequests != null && !friendRequests.isEmpty()) {
                        boolean requested = false;
                        for (Map<String, Object> friendRequestMap : friendRequests) {
                            String requesterID = (String) friendRequestMap.get("User");
                            if (requesterID.equals(currentUser.getUid())) {
                                requested = true;
                                break;
                            }
                        }
                        callback.isUserRequested(requested);
                    }
                    else {
                        callback.isUserRequested(false);
                    }
                }
            }
        });
    }
    interface friendCallback{
        void checkIfFriend(boolean isFriend);
    }
    public void checkIfFriend(String userID,friendCallback callback){
        db.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Error", String.valueOf(e));
                }
                if (documentSnapshot != null) {
                    List<Map<String, Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    if (friends != null && !friends.isEmpty()) {
                        boolean isFriend = false;
                        for (Map<String, Object> friendsMap : friends) {
                            Log.d("Firebase", "friends found");
                            String friendID = (String) friendsMap.get("User");
                            if (friendID.equals(currentUser.getUid())) {
                                isFriend = true;
                                break;
                            }
                        }
                        callback.checkIfFriend(isFriend);
                    } else {
                        callback.checkIfFriend(false);
                    }
                } else {
                    callback.checkIfFriend(false);
                }
            }
        });
    }
    interface ifPostCallback{
        void checkForPost(boolean exists);
    }

    public void checkForPost(String userID, ifPostCallback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Posts").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            boolean exists = false;
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        exists = true;
                    }
                }
                callback.checkForPost(exists);
            }
        });
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

            editprofile.setVisibility(View.VISIBLE);
            editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditProfileFrag editProfileFrag = new EditProfileFrag();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, editProfileFrag).addToBackStack(null).commit();
                }
            });


            checkForPost(userID, new ifPostCallback() {
                @Override
                public void checkForPost(boolean exists) {
                    if(!exists){
                        userPost.setVisibility(View.GONE);
                        addPost.setVisibility(View.VISIBLE);
                        uploadtext.setVisibility(View.VISIBLE);

                        ConstraintSet constraintSet = new ConstraintSet();
                        //constraintSet.clone(constraintLayout); //ERROR
                        int marginInDp = 35;
                        int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());
                        constraintSet.connect(artisttile.getId(), ConstraintSet.TOP, R.id.addpost, ConstraintSet.BOTTOM, marginInPx);
                        constraintSet.applyTo(constraintLayout);

                        hasArtist(new hasArtistCallback() {
                            @Override
                            public void hasArtist(boolean hasArtists) {
                                if(hasArtists){
                                    Log.d("test", ":work bitch");
                                    ConstraintSet constraintSet = new ConstraintSet();
                                    constraintSet.clone(constraintLayout);
                                    int marginInDp = 35;
                                    int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());
                                    constraintSet.connect(R.id.buttonwartist, ConstraintSet.TOP, R.id.addpost, ConstraintSet.BOTTOM, marginInPx);
                                    constraintSet.applyTo(constraintLayout);
                                }
                            }
                        });
                        addPost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createPost();
                            }
                        });
                    }

                }
            });

            hasArtist(new hasArtistCallback() {
                @Override
                public void hasArtist(boolean hasArtists) {
                    if(!hasArtists){
                        artistmemo.setVisibility(View.VISIBLE);
                        addArtist1.setVisibility(View.VISIBLE);
                        addArtist1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ArtistSearch.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        addArtist2.setVisibility(View.VISIBLE);
                        addArtist2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ArtistSearch.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });

            addSong.setVisibility(View.VISIBLE);
            addSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PromptSelection.class);
                    startActivity(intent);
                }
            });

            loadUserPost(userID);

            loadTopArtists(userID);


        }
    }

    public void loadNewUserProfile(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addFriend.setVisibility(View.VISIBLE);
        DocumentReference documentReference= db.collection("Users").document(user.getUserID());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("Error", String.valueOf(e));

                }
                if(document!=null){
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

                    isUserRequested(user.getUserID(), new friendAddedCallback() {
                        @Override
                        public void isUserRequested(boolean requested) {
                            if(!requested){
                                checkIfFriend(user.getUserID(), new friendCallback() {
                                    @Override
                                    public void checkIfFriend(boolean isFriend) {
                                        if(!isFriend){
                                            addFriend.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendFriendRequest(user.username);
                                                }
                                            });
                                        }
                                        else {
                                            addFriend.setText("Friends");
                                        }
                                    }
                                });
                            }
                            else {
                                addFriend.setText("Requested");
                            }
                        }
                    });
                }

            }
        });





        loadUserPost(user.getUserID());
        checkForPost(user.getUserID(), new ifPostCallback() {
            @Override
            public void checkForPost(boolean exists) {
                if(!exists){
                    userPost.setVisibility(View.GONE);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    int marginInDp = 35;
                    int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());
                    constraintSet.connect(artisttile.getId(), ConstraintSet.TOP, R.id.postholder, ConstraintSet.BOTTOM, marginInPx);
                    constraintSet.applyTo(constraintLayout);



                }
                else {
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    int marginInDp = 15;
                    int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());
                    constraintSet.connect(R.id.userpost, ConstraintSet.TOP, R.id.addfriend, ConstraintSet.BOTTOM, marginInPx);
                    constraintSet.applyTo(constraintLayout);
                }

            }
        });

        hasArtist(new hasArtistCallback() {
            @Override
            public void hasArtist(boolean hasArtists) {
                if(!hasArtists){
                    noartist.setVisibility(View.VISIBLE);

                }
            }
        });

        loadTopArtists(user.getUserID());
    }

    public void loadUserPost(String userID){
        post = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Posts").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Error", String.valueOf(e));
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("Listner", "-------------");

                    //long time = documentSnapshot.getLong("pTime");
                    String postImage = documentSnapshot.getString("pImage");
                    Timestamp postTime = documentSnapshot.getTimestamp("timeStamp");
                    String songId = documentSnapshot.getString("songId");

                    PostModel postModel = new PostModel(postImage, postTime, songId, userID);
                    post.add(postModel);
                    postAdapter.setPosts(post);
                    postAdapter.notifyDataSetChanged();

                }

            }
        });

    }

    public void createPost(){
        Log.i("CreatePost", "CreatePost");
        Intent intent = new Intent(getActivity(), PostActivity.class);
        startActivity(intent);
    }

    interface hasArtistCallback{
        public void hasArtist(boolean hasArtists);
    }
    public void hasArtist(hasArtistCallback artistCallback){
        db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        boolean hasArtists = false;
                        List<Map<String,Object>> userArtists = (List<Map<String, Object>>) documentSnapshot.get("topArtist");
                        if( userArtists==null || userArtists.isEmpty()){
                            hasArtists = false;
                        }
                        else{
                            hasArtists = true;
                        }
                        artistCallback.hasArtist(hasArtists);
                    }
                    else artistCallback.hasArtist(false);
                }
            }
        });
    }

    public void loadTopArtists(String userID){
        artists = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        List<Map<String, Object>> topArtists = (List<Map<String, Object>>) documentSnapshot.get("topArtist");
                        if(topArtists!=null && !topArtists.isEmpty()){
                            for(Map<String,Object> artist: topArtists){
                                String artistName, artistImage, artistURI;
                                artistName = (String) artist.get("artistName");
                                artistImage = (String) artist.get("artistImage");
                                artistURI = (String) artist.get("artistURI");

                                ArtistModel artistModel = new ArtistModel(artistName,artistImage,artistURI, 0);
                                artists.add(artistModel);
                                artistAdapter.setArtist(artists);
                                artistAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }
            }
        });
    }
    public void sendFriendRequest(String username){
        String requestedUserId;
        List<String> requestedFriends;
        Map<String, Object> friendRequests= new HashMap<>();
        Query query = db.collection("Users").whereEqualTo("Username", username);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult() ){
                        String requestedUserID = doc.getId();

                        friendRequests.put("User", currentUser.getUid());
                        DocumentReference documentReference = db.collection("Users").document(requestedUserID);
                        documentReference.update("friendRequests", FieldValue.arrayUnion(friendRequests)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "document updated");
                            }
                        });
                    }
                }
            }
        });
    }

}



