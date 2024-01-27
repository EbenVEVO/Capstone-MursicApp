package com.example.capstone_mursicapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RequestRecycleAdapter extends RecyclerView.Adapter<RequestRecycleAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<UserListModel> userList;


    public RequestRecycleAdapter(List<UserListModel> userList){
        this.userList = userList;
    }
    public void setUserList(List<UserListModel> userList) {
        this.userList = userList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_layout, parent, false);
        return new RequestRecycleAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestRecycleAdapter.ViewHolder holder, int position) {
        UserListModel userModel = userList.get(position);
        holder.bind(userModel);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requesterUsername = userModel.getUsername();
                String requestedUserId;


                Map<String, Object> userNewFriends = new HashMap<>();
                Map<String, Object> requestedNewFriends = new HashMap<>();

                Query query = db.collection("Users").whereEqualTo("Username", requesterUsername);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String requestedUserID = doc.getId();
                                requestedNewFriends.put("User", currentUser.getUid());
                                DocumentReference documentReference = db.collection("Users").document(requestedUserID);
                                documentReference.update("friends", FieldValue.arrayUnion(requestedNewFriends)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Firestore", "added current user to requesters friends");
                                        DocumentReference documentReference = db.collection("Users").document(currentUser.getUid());
                                        userNewFriends.put("User", requestedUserID);
                                        documentReference.update("friends", FieldValue.arrayUnion(userNewFriends)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Firestore", "added requester to users friends");

                                                DocumentReference documentReference = db.collection("Users").document(currentUser.getUid());
                                                Map<String, Object> removeRequestMap = new HashMap<>();
                                                removeRequestMap.put("User", requestedUserID);
                                                documentReference.update("friendRequests",  FieldValue.arrayRemove(removeRequestMap)).addOnSuccessListener(new OnSuccessListener<Void>() {

                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("Firestore", "removed requester for current users requests");

                                                    }

                                                });

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }

                });
            }
        });
    }
    @Override
    public int getItemCount() {
        if (userList == null) {
            this.userList = new ArrayList<>();
        } else {
            this.userList = userList;
        }
        return userList.size();
    }


public class ViewHolder extends RecyclerView.ViewHolder{
    private TextView usernameTextView;
    private CircularImageView profilePic;

    private Button accept;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        usernameTextView = itemView.findViewById(R.id.usernameTextView);
        profilePic = itemView.findViewById(R.id.profilepic);
        accept = itemView.findViewById(R.id.accept);
    }
    public void bind(UserListModel userModel){
        usernameTextView.setText(userModel.getUsername());
        ImageLoader imageLoader = new ImageLoader(itemView.getContext());
        imageLoader.loadImage(userModel.getProfilePic(), profilePic);

    }
}
                    }

