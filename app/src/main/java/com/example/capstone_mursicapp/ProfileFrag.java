package com.example.capstone_mursicapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;


import java.util.Map;


public class ProfileFrag extends Fragment {

    TextView usernameTextView;
    Uri imageUri;
    ImageView profilePic;

    Button add;

    Fragment currentFragment = this;
    FirebaseStorage storage = FirebaseStorage.getInstance();;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        AppCompatActivity activity= (AppCompatActivity) getActivity();

        usernameTextView = view.findViewById(R.id.username);
        profilePic = view.findViewById(R.id.profileImg);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null){
            String userID = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference documentReference= db.collection("Users").document(userID);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("Username");
                            String pfp = document.getString("profilePicture");
                            if (username != null) {
                                usernameTextView.setText(username);
                            }
                            Glide.with(currentFragment)
                                    .load(pfp)
                                    .placeholder(R.drawable.default_pfp) // Placeholder image while loading
                                    .error(R.drawable.default_pfp) // Image to display in case of error
                                    .into(profilePic);


                        }
                    }
                }
            });

        }

        add = view.findViewById(R.id.pfpadd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePic();

            }
        });
        return view;
    }

    public void chooseProfilePic(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(i.ACTION_GET_CONTENT);
        resultLauncher.launch(i);

    }
    ActivityResultLauncher<Intent> resultLauncher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    profilePic.setImageURI(imageUri);
                    uploadProfilePic(imageUri);
                }
                else {
                    Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getContext(), "Image selection canceled", Toast.LENGTH_SHORT).show();
            }
        }

    });

    public void uploadProfilePic(Uri imageUri){
        if(imageUri != null){
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference profilePicsRef = storage.getReference().child("profile_pics/" + userID + ".jpg");

            profilePicsRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                profilePicsRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    String downloadUrl = uri.toString();
                    updateProfilePicture(downloadUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to uploade image", Toast.LENGTH_SHORT).show();
            });


        }
    }

    public  void updateProfilePicture(String downloadUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference documentReference = db.collection("Users").document(userID);

            documentReference.update("profilePicture", downloadUrl).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Profile Pic Updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to update image", Toast.LENGTH_SHORT).show();

            });

        }

    }

}



