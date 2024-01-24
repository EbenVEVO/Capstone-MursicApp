package com.example.capstone_mursicapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class FriendsFrag extends Fragment {
    Toolbar toolbar;
    String userID;

    FriendsRecycleAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MenuItem profileMenuItem;
    Fragment currentFragment = this;

    RecyclerView friendsView;

    List<String> usernameList;
    SearchView searchView;
    CircularImageView pfpIcon;
    TextInputEditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        toolbar = view.findViewById(R.id.friend_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        searchView = view.findViewById(R.id.friendssearch);
        friendsView = view.findViewById(R.id.friendsresults);


        adapter = new FriendsRecycleAdapter(usernameList);
        friendsView.setAdapter(adapter);

        getAllUsernames();
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                if (!getActivity().isDestroyed()) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFrag).commitNow();
                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void loadProfileImage() {
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

    public void getAllUsernames() {

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> list = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String username = documentSnapshot.getString("Username");
                    if (username != null) {
                        list.add(username);
                    }
                    updateAdapter(list);
                }
                System.out.println(list);
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }


    private void updateAdapter(ArrayList<String> usernames) {
        if (adapter != null) {
            adapter.setUserList(usernames);
            adapter.notifyDataSetChanged();
        }
    }
}