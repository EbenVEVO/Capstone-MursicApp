package com.example.capstone_mursicapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FriendsFrag extends Fragment {
    Toolbar toolbar;
    String userID;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView emptyFriends, emptyRequest, friendstitle, emptySearch;
    FriendsRecycleAdapter friendsAdapter;
    RequestRecycleAdapter requestAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MenuItem profileMenuItem;
    Fragment currentFragment = this;

    RecyclerView friendsView, requestsView;

    Button addFriend;

    List<UserListModel> userList, friendRequestList, friendsList;
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

        emptyRequest = view.findViewById(R.id.emptyrequestview);
        emptyFriends = view.findViewById(R.id.emptyfriendsview);
        emptySearch = view.findViewById(R.id.emptyusersearch);
        friendstitle = view.findViewById(R.id.friendstitle);


        searchView = view.findViewById(R.id.friendssearch);
        friendsView = view.findViewById(R.id.friendsresults);
        requestsView = view.findViewById(R.id.requestresults);


        HorizontalItemDecorator itemDecorator = new HorizontalItemDecorator(40);
        friendsView.addItemDecoration(itemDecorator);
        VerticalItemDecorator itemDecorator1 = new VerticalItemDecorator(40);
        requestsView.addItemDecoration(itemDecorator1);

        getAllProfiles();
        getFriends();
        getFriendRequest();

        friendsAdapter = new FriendsRecycleAdapter(friendsList);
        friendsView.setAdapter(friendsAdapter);

        requestAdapter = new RequestRecycleAdapter(friendRequestList);
        requestsView.setAdapter(requestAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                friendsAdapter = new FriendsRecycleAdapter(friendsList);
                friendsAdapter.filter(query);
                friendsView.setAdapter(friendsAdapter);
                if(friendsAdapter.getItemCount()==0) {
                    friendstitle.setText("Add Friends");
                    friendsAdapter.setUserList(userList);
                    friendsAdapter.setNoFriends(true);
                    friendsAdapter.filter(query);
                    friendsAdapter.setButtonType(0);
                    for(UserListModel user: friendsAdapter.getFilteredList()){
                        db.collection("Users").document(user.getUserID()).get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    List<Map<String, Object>> friendRequests = (List<Map<String, Object>>) doc.get("friendRequests");
                                    if (friendRequests != null && !friendRequests.isEmpty()) {
                                        for (Map<String, Object> friendRequestMap : friendRequests) {
                                            String requesterID = (String) friendRequestMap.get("User");
                                            if (requesterID.equals(currentUser.getUid())) {
                                                user.setButtonType(1);
                                                friendsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                    friendsAdapter.notifyDataSetChanged();

                    if(friendsAdapter.getItemCount()==0 ){
                        friendsView.setVisibility(View.GONE);
                        emptySearch.setVisibility(View.VISIBLE);
                    }
                    else {
                        emptySearch.setVisibility(View.GONE);
                        friendsView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    friendstitle.setText("Friends");
                    emptySearch.setVisibility(View.GONE);
                    friendsView.setVisibility(View.VISIBLE);
                    friendsAdapter.setButtonType(2);
                    friendsAdapter.setNoFriends(false);
                }
                return true;
            }
        });

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
                profileFrag.setIsOwnProfile(true);
                if (!getActivity().isDestroyed()) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFrag).commit();
                }
            }
        });
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

    public void getAllProfiles() {
        userList = new ArrayList<>();

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String userID = documentSnapshot.getId();
                    if (!userID.equals(FirebaseAuth.getInstance().getUid())) {
                        String username = documentSnapshot.getString("Username");
                        if (username != null) {
                            String profilePic = documentSnapshot.getString("profilePicture");
                            int defaultProfilePicResId = R.drawable.default_pfp;
                            if (profilePic == null) {
                                profilePic = String.valueOf(defaultProfilePicResId);
                            }
                            UserListModel userListModel = new UserListModel(username, userID, profilePic);
                            userList.add(userListModel);
                        }
                    }
                }

            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }

    public void getFriendRequest(){
        friendRequestList = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();
        db.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Error", String.valueOf(e));
                    }
                if(documentSnapshot != null && documentSnapshot.exists()){
                    friendRequestList.clear();
                    List<Map<String, Object>> friendRequests = (List<Map<String, Object>>) documentSnapshot.get("friendRequests");
                    if (friendRequests != null && !friendRequests.isEmpty()) {
                        for (Map<String, Object> friendRequestMap : friendRequests) {
                            String requesterID = (String) friendRequestMap.get("User");
                            db.collection("Users").document(requesterID).get().addOnSuccessListener(requesterSnapshot -> {
                                if (requesterSnapshot.exists()) {
                                    String friendUsername = requesterSnapshot.getString("Username");
                                    if (friendUsername != null) {
                                        String friendProfilePic = requesterSnapshot.getString("profilePicture");
                                        int defaultProfilePicResId = R.drawable.default_pfp;
                                        if (friendProfilePic == null) {
                                            friendProfilePic = String.valueOf(defaultProfilePicResId);
                                        }
                                        UserListModel userListModel = new UserListModel(friendUsername, requesterID, friendProfilePic);
                                        friendRequestList.add(userListModel);
                                    }
                                }
                                requestAdapter.setUserList(friendRequestList);
                                requestAdapter.notifyDataSetChanged();
                            });

                        }

                    }
                    else {
                        //Log.d("Firebase", "No request found");
                        requestsView.setVisibility(View.GONE);
                        emptyRequest.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    Log.d("Firebase", "request document not found");
                }
            }
        });
        if (requestAdapter!= null) {
            requestAdapter.setUserList(friendRequestList);
            requestAdapter.notifyDataSetChanged();
        }
    }

    public void getFriends(){
        System.out.println("Entering get friends");
        friendsList = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();
        db.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Error", String.valueOf(e));
                    }
                    if(documentSnapshot != null){
                            List<Map<String,Object>> friends = (List<Map<String, Object>>) documentSnapshot.get("friends");

                            if (friends != null && !friends.isEmpty()) {
                                for (Map<String, Object> friendsMap : friends) {
                                    Log.d("Firebase", "friends found");
                                    String friendID = (String) friendsMap.get("User");
                                    System.out.println(friendID);
                                    db.collection("Users").document(friendID).get().addOnSuccessListener(friendSnapshot -> {
                                        if (friendSnapshot.exists()) {
                                            String friendUsername = friendSnapshot.getString("Username");
                                            if (friendUsername != null) {
                                                String friendProfilePic = friendSnapshot.getString("profilePicture");
                                                int defaultProfilePicResId = R.drawable.default_pfp;
                                                if (friendProfilePic == null) {
                                                    friendProfilePic = String.valueOf(defaultProfilePicResId);
                                                }
                                                UserListModel userListModel = new UserListModel(friendUsername, friendID, friendProfilePic);
                                                friendsList.add(userListModel);
                                            }
                                        }
                                        friendsAdapter.setUserList(friendsList);
                                        friendsAdapter.setButtonType(2);
                                        friendsAdapter.notifyDataSetChanged();
                                    });

                                }
                            }
                            else {
                                Log.d("Firebase", "No friends found");
                                friendsView.setVisibility(View.GONE);
                                emptyFriends.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            Log.d("Firebase", "friends document not found");
                        }
            }
        });

        if (friendsAdapter!= null) {
            friendsAdapter.setUserList(friendsList);
            friendsAdapter.notifyDataSetChanged();
        }
    }

}