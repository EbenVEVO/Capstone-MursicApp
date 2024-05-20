package com.example.capstone_mursicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class SongPromptConfrim extends Fragment {
    private static final String ARG_prompt = "";

    String prompt, songName , songImageUrl, artists;
    List<String> artistNames;
    ImageView songImage;
    TextView songNameView, artistNameView, promptView;
    public SongPromptConfrim() {
        // Required empty public constructor
    }

    public static SongPromptConfrim newInstance(String getprompt){
        SongPromptConfrim fragment = new SongPromptConfrim();
        Bundle args = new Bundle();
        args.putString(ARG_prompt, getprompt);
        fragment.setArguments(args);
        return fragment;
    }

    private ActivityResultLauncher<Intent> songActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.i("IN", "IN");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String songId = data.getStringExtra("songId");
                        if (songId != null) {
                            setSongInfo(songId);
                        }
                    }
                }
            }
    );
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            prompt = getArguments().getString(ARG_prompt);
        }
        Log.d("prompt", prompt);
        Intent intent = new Intent(getContext(), SongActivity.class);
        songActivityLauncher.launch(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_prompt_confrim, container, false);

        promptView = view.findViewById(R.id.prompt);
        songNameView = view.findViewById(R.id.songName);
        artistNameView = view.findViewById(R.id.artistname);
        songImage = view.findViewById(R.id.songImage);

        promptView.setText(prompt);
        songNameView.setText(songName);
        if (artistNames.size()>1) {
            for (String artist : artistNames) {
                artists = artist + ", ";
            }
        }
        else {
            for (String artist : artistNames) {
                artists = artist;
            }
        }
        artistNameView.setText(artists);


        return view;
    }

    public void setSongInfo(String songID){
        SongRetriever songRetriever = new SongRetriever();
        songImageUrl = songRetriever.getSongImage(songID);
        artistNames = songRetriever.getArtistName(songID);
        songName = songRetriever.getSongName(songID);
    }
}