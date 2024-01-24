package com.example.capstone_mursicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class FriendsRecycleAdapter extends RecyclerView.Adapter<FriendsRecycleAdapter.ViewHolder> {

    private List<String> userList;

    public FriendsRecycleAdapter(List<String> userList){
        this.userList = userList;
    }
    public void setUserList(List<String> userList) {
        this.userList = userList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_search_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecycleAdapter.ViewHolder holder, int position) {
        String username = userList.get(position);
        holder.bind(username);
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
        }
        public void bind(String username){
            usernameTextView.setText(username);
        }
    }
}
