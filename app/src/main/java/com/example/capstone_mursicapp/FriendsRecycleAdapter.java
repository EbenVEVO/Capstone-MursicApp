package com.example.capstone_mursicapp;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FriendsRecycleAdapter extends RecyclerView.Adapter<FriendsRecycleAdapter.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<UserListModel> userList, filteredList;

    boolean noFriends;

    int buttonType;

    public FriendsRecycleAdapter(List<UserListModel> userList){
        this.userList = userList;
        this.filteredList = new ArrayList<>(userList);
    }
    public void setUserList(List<UserListModel> userList) {
        this.userList = userList;
        this.filteredList = new ArrayList<>(userList);

    }

    public void setNoFriends(boolean noFriends) {
        this.noFriends = noFriends;
    }

    public void filter(String query){
        filteredList.clear();
        query = query.toLowerCase(Locale.getDefault());
        System.out.println(query);
        if(query.length()==0){
            if (!noFriends)
                filteredList.addAll(userList);
        }
        else {

            for(UserListModel user: userList){
                if(user.getUsername().toLowerCase(Locale.getDefault()).contains(query)){
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setButtonType(int buttonType){
        this.buttonType = buttonType;
    }

    public int getButtonType() {
        return buttonType;
    }

    public List<UserListModel> getFilteredList(){
        return filteredList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_search_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecycleAdapter.ViewHolder holder, int position) {
        UserListModel userModel = filteredList.get(position);
        holder.bind(userModel);

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFrag profileFrag = new ProfileFrag();
                profileFrag.setIsOwnProfile(false);
                profileFrag.setUser(userModel);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFrag).commitNow();


            }
        });

        holder.usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFrag profileFrag = new ProfileFrag();
                profileFrag.setIsOwnProfile(false);
                profileFrag.setUser(userModel);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, profileFrag).commitNow();
            }
        });
        if(userModel.getButtonType()==0){
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = userModel.getUsername();
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
                                            userModel.setButtonType(1);
                                            Log.d("Firestore", "document updated");
                                        }
                                    });


                                }
                            }
                        }
                    });
                }
            });
        }
        if(userModel.getButtonType()==1){
            holder.addFriend.setText("Requested");
        }

        if (getButtonType()==2){
            holder.addFriend.setText("Chat");
        }

    }

    @Override
    public int getItemCount() {
        if (filteredList == null|| filteredList.isEmpty()) {
            this.filteredList = new ArrayList<>();
        } else {
            this.filteredList = filteredList;
        }
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView usernameTextView;
        private CircularImageView profilePic;

        private Button addFriend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            profilePic = itemView.findViewById(R.id.profilepic);
            addFriend = itemView.findViewById(R.id.addfriend);
        }
        public void bind(UserListModel userModel){
            usernameTextView.setText(userModel.getUsername());
            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(userModel.getProfilePic(), profilePic);

        }
    }
}
