package com.example.capstone_mursicapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class PostActivity extends AppCompatActivity {
    Uri imageUri;
    RoundedImageView postImage;
    TextView chooseSongPrompt;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ImageButton back, music, addMusic;
    File photoFile;
    String fileName = "photo";

    Button post;

    private String selectedSongId = "";
    //To get selected position back
    private ActivityResultLauncher<Intent> songActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.i("IN", "IN");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String songId = data.getStringExtra("songId");
                        if (songId != null) {
                            selectedSongId = songId;
                            updatePostButton();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        post = findViewById(R.id.postButton);
        postImage = findViewById(R.id.postImage);
        addMusic = findViewById(R.id.addMusic);
        chooseSongPrompt = findViewById(R.id.choosesongprompt);

        ActivityResultLauncher<Intent> resultLauncher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    //Intent data = result.getData();
                    Bitmap img  = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    img = Bitmap.createScaledBitmap(img, 450, 450, true);
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
        try {
            photoFile = getPhotoFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri fileProvider =FileProvider.getUriForFile(getApplicationContext(), "com.example.capstone_mursicapp.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        resultLauncher.launch(intent);

        updatePostButton();
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
                            Timestamp timeStamp = Timestamp.now();
                            StorageReference profilePicsRef = storage.getReference().child("Posts/" + userID + "_" + timeStamp);
                            profilePicsRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                                profilePicsRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                    String downloadUrl = uri.toString();
                                    PostModel postModel = new PostModel(downloadUrl, timeStamp, selectedSongId, userID);

                                    Map<String, Object> postData = new HashMap<>();
                                    postData.put("pImage", postModel.pImage);
                                    postData.put("songId", postModel.songID); //NEED TO FIX LATER, JUST USE POST MODEL BUT CANT FIGURE OUT>>>
                                    postData.put("userID", postModel.userID);
                                    postData.put("timeStamp", postModel.timeStamp);


                                    db.collection("Posts").document(userID)
                                            .set(postData).addOnSuccessListener(aVoid -> {
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


        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ButtonActivity", "addMusic Click");
                Intent intent = new Intent(PostActivity.this, SongActivity.class);
                songActivityLauncher.launch(intent);
            }
        });




    }
    public void updatePostButton(){
        if(selectedSongId != null && !selectedSongId.isEmpty()) {
            post.setVisibility(View.VISIBLE);
            chooseSongPrompt.setVisibility(View.GONE);
        }
        else {
            post.setVisibility(View.GONE);
            chooseSongPrompt.setVisibility(View.VISIBLE);
        }
    }
    public File getPhotoFile(String fileName) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
