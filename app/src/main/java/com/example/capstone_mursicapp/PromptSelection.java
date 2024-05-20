package com.example.capstone_mursicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PromptSelection extends AppCompatActivity implements OnPromptClickListener {

    RecyclerView promptsView;

    List<String> prompts = Arrays.asList("My anthem" ,
            "Song I'll always love" ,
            "Song on repeat " ,
            "Song stuck in my head",
            "Song that makes me happy",
            "Song I can go bar for bar to",
            "Song I'll always sing along to ",
            "Song by my favorite artist",
            "Song for the late night drives",
            "Underrated song",
            "Song that's just real",
            "My throwback song",
            "Karaoke go to",
            "Play this at my wedding");
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_selection);

        promptsView = findViewById(R.id.prompts);
        PromptAdapter promptAdapter = new PromptAdapter(prompts, this);
        promptsView.setAdapter(promptAdapter);

        VerticalItemDecorator itemDecorator = new VerticalItemDecorator(50);
        promptsView.addItemDecoration(itemDecorator);
    }

    @Override
    public void onClick(String prompt) {
        Fragment frag = SongPromptConfrim.newInstance(prompt);
        getSupportFragmentManager().beginTransaction().replace(R.id.promptselection, frag).addToBackStack(null).commit();
    }
}