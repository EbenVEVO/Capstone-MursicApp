package com.example.capstone_mursicapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.concurrent.atomic.AtomicBoolean;


public class EditProfileFrag extends Fragment {
    TextView changepfp;
    Uri imageUri;

    Toolbar toolbar;
    TextInputEditText usernameInput, bioInput;

    FirebaseStorage storage = FirebaseStorage.getInstance();;

    CircularImageView profilePic;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Fragment currentFragment = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadUserData();
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        AppCompatActivity activity= (AppCompatActivity) getActivity();

        profilePic = view.findViewById(R.id.profileImg);
        changepfp = view.findViewById(R.id.changepfp);
        toolbar = view.findViewById(R.id.editprofile_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String username;
                switch (item.getItemId()){
                    case R.id.confirm:
                        Log.d("TEST", "confim pressed");
                        if (currentUser != null) {
                            String userID = currentUser.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = db.collection("Users").document(userID);
                            if (usernameexist()){
                                Toast.makeText(getContext(), "This username is already in use", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            String currentUsername = documentSnapshot.getString("Username");
                                            String newUsername = usernameInput.getText().toString();
                                            if (!newUsername.equals(currentUsername)) {
                                                documentReference.update("Username", newUsername);
                                                Toast.makeText(getContext(), "Username changed", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                    }
                                });
                            }
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        String userBio = documentSnapshot.getString("Bio");
                                        String newBio = bioInput.getText().toString();
                                        if(!userBio.equals(newBio)) {
                                            documentReference.update("Bio", newBio);
                                            Toast.makeText(getContext(), "Bio changed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                        return true;


                }
                return false;
            }
        });
        usernameInput = view.findViewById(R.id.usernameinput);
        bioInput = view.findViewById(R.id.bioinput);
        changepfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePic();
            }
        });
     return view;
    }


    public boolean usernameexist(){
        AtomicBoolean usernameexist = new AtomicBoolean(false);
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String userID = documentSnapshot.getId();
                    if (!userID.equals(FirebaseAuth.getInstance().getUid())) {
                        String username = documentSnapshot.getString("Username");
                        if (username.equals(usernameInput.getText().toString())) {
                           usernameexist.set(true);
                        }
                        else {
                            usernameexist.set(false);
                        }
                    }
                }

            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
        return usernameexist.get();
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

    public void loadUserData(){
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
                            String bio = document.getString("Bio");
                            String pfp = document.getString("profilePicture");
                            if (username != null) {
                                usernameInput.setText(username);
                            }
                            if (bio != null){
                                bioInput.setText(bio);
                            }
                            else {
                                bioInput.setText("");
                            }
                            ImageLoader imageLoader = new  ImageLoader(currentFragment.getContext());
                            imageLoader.loadImage(pfp, profilePic);


                        }
                    }
                }
            });

        }
    }
}