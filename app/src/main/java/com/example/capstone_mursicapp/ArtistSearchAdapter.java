package com.example.capstone_mursicapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ArtistSearchAdapter extends RecyclerView.Adapter<ArtistSearchAdapter.ViewHolder> {
    List<ArtistModel> artist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    onSelection listener;

    public ArtistSearchAdapter(List<ArtistModel> artist){
        this.artist = artist;
    }

    public void setArtist(List<ArtistModel> artist) {
        this.artist = artist;
    }

    public void sortArtist(){
        if(artist!= null){
            Collections.sort(artist, new Comparator<ArtistModel>() {
                @Override
                public int compare(ArtistModel o1, ArtistModel o2) {
                    return Integer.compare(o2.popularity, o1.popularity);
                }
            });
        }
    }

    @NonNull
    @Override
    public ArtistSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artistsearchlayout, parent, false);

        return new ArtistSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistSearchAdapter.ViewHolder holder, int position) {
        ArtistModel artistModel = artist.get(position);
        holder.bind(artistModel);

        holder.artistClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> topArtist = new HashMap<>();

                topArtist.put("artistName", artistModel.artistName);
                topArtist.put("artistImage", artistModel.artistImage);
                topArtist.put("artistURI", artistModel.artistURI);

                DocumentReference ref = db.collection("Users").document(currentUser.getUid());

                ref.update("topArtist", FieldValue.arrayUnion(topArtist)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "document updated");

                    }
                });
              if(listener!=null){
                  listener.onArtistSelection();
              }
            }

        });

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
        LinearLayout artistClick;
        CircularImageView artistImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artistname);
            artistImage = itemView.findViewById(R.id.artistimg);
            artistClick = itemView.findViewById(R.id.artist);
        }

        public void bind(ArtistModel artistModel) {
            artistName.setText(artistModel.artistName);

            ImageLoader imageLoader = new ImageLoader(itemView.getContext());
            imageLoader.loadImage(artistModel.artistImage, artistImage);
        }

    }

    public interface onSelection{
        void onArtistSelection();
    }
}
