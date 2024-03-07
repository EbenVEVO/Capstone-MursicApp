package com.example.capstone_mursicapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;

import javax.annotation.Nullable;

public class PostActivity extends AppCompatActivity {
    Uri imageUri;
    RoundedImageView postImage;
    FirebaseStorage storage = FirebaseStorage.getInstance();;

    ImageButton back, music;

    Button post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        post = findViewById(R.id.postButton);
        postImage = findViewById(R.id.postImage);
        ActivityResultLauncher<Intent> resultLauncher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bitmap img  = (Bitmap) (data.getExtras().get("data"));
                    imageUri= getImageUri(getApplicationContext(),img);
                    Log.d("Camera", String.valueOf(imageUri));

                    postImage.setImageURI(imageUri);
                }
                else {
                    finish();
                    Log.d("Camera", "no image  found");
                }
            }
        });

        if(ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PostActivity.this, new String[] {Manifest.permission.CAMERA},100);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        resultLauncher.launch(intent);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = db.collection("Users").document(userID);
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("Username");
                            String profilePic = documentSnapshot.getString("profilePicture");

                            String timeStamp = String.valueOf(System.currentTimeMillis());


                            StorageReference profilePicsRef = storage.getReference().child("Posts/" + userID + "_" + timeStamp);
                            profilePicsRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                                profilePicsRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                    String downloadUrl = uri.toString();
                                    PostModel postModel = new PostModel(username, downloadUrl, timeStamp, profilePic);
                                    db.collection("Posts").document(userID)
                                            .set(postModel).addOnSuccessListener(aVoid->{

                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getApplicationContext(), "Failed to post image", Toast.LENGTH_SHORT).show();
                                            });

                                });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Failed to post image to storage", Toast.LENGTH_SHORT).show();
                            });
                        }

                    });
                }
                finish();
            }
});


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
