package com.example.capstone_mursicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.ViewHolder> {

    List<String> prompts;

    public PromptAdapter(List<String> prompts){
        this.prompts = prompts;
    }
    @NonNull
    @Override
    public PromptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promptlayout, parent, false);
        return new PromptAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromptAdapter.ViewHolder holder, int position) {
        String item = prompts.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return prompts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView prompt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prompt = itemView.findViewById(R.id.prompt);
        }

        public void bind(String item) {
            prompt.setText(item);
        }

    }
}
