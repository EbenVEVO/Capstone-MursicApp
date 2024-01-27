package com.example.capstone_mursicapp;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

public class ImageLoader {
    private final Context context;
    String imageURL;

    ImageView imageView;
    public ImageLoader(Context context){
        this.context = context;
    }


    public void loadImage(String imageURL, ImageView imageView){
        Glide.with(context)
                .load(imageURL)
                .placeholder(R.drawable.default_pfp)
                .error(R.drawable.default_pfp)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView);
    }
}
