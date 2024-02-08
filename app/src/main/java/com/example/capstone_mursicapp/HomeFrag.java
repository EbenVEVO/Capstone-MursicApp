package com.example.capstone_mursicapp;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

public class HomeFrag extends Fragment {
    Toolbar toolbar;
    MenuItem profileMenuItem;
    CircularImageView pfpIcon;
    ImageView test;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Fragment currentFragment = this;
    String userID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        toolbar = view.findViewById(R.id.home_toolbar);
        AppCompatActivity activity= (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);



        return view;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_home, menu);
        profileMenuItem = menu.findItem(R.id.h_profile);
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View actionview = layoutInflater.inflate(R.layout.profile_menu_layout,null);
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void loadProfileImage(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

                        Glide.with(currentFragment)
                                .load(pfp)
                                .placeholder(R.drawable.default_pfp)
                                .error(R.drawable.default_pfp)
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .into(pfpIcon);

                    }
                }
            }
        });
    }

}
