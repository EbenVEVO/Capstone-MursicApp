package com.example.capstone_mursicapp;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.ViewHolder> {

    List<String> prompts;
    OnPromptClickListener listener;

    public PromptAdapter(List<String> prompts, OnPromptClickListener listener){
        this.prompts = prompts;
        this.listener = listener;
    }


    @NonNull
    @Override
    public PromptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promptlayout, parent, false);
        return new PromptAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromptAdapter.ViewHolder holder, int position) {
        String prompt = prompts.get(position);
        holder.bind(prompt);

        holder.promptClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(prompt);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return prompts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView prompt;
        LinearLayout promptClick;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prompt = itemView.findViewById(R.id.prompt);
            promptClick = itemView.findViewById(R.id.promptclick);
        }

        public void bind(String item) {
            prompt.setText(item);

        }

    }
}
