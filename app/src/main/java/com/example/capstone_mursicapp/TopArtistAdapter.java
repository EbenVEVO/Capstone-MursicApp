package com.example.capstone_mursicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class TopArtistAdapter extends RecyclerView.Adapter<TopArtistAdapter.ViewHolder> {
    Context context;
    List<ArtistModel> artist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public TopArtistAdapter(List<ArtistModel> artist){
        this.artist = artist;
    }

    public void setArtist(List<ArtistModel> artist) {
        this.artist = artist;
    }
    @NonNull
    @Override
    public TopArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artistlayout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistAdapter.ViewHolder holder, int position) {
        ArtistModel artistModel = artist.get(position);
        holder.bind(artistModel);
    }

    @Override
    public int getItemCount() {
        if(artist == null){
            this.artist = new ArrayList<>();
        }
        else {
            this.artist = artist;
        }
        return artist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView artistName;
        CircularImageView artistImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artistname);
            artistImage = itemView.findViewById(R.id.artistimg);
        }

        public void bind(ArtistModel artistModel) {
            artistName.setText(artistModel.artistName);

            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(artistModel.artistImage, artistImage);
        }

    }


}
