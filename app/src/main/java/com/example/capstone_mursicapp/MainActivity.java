package com.example.capstone_mursicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainLayout);
    String userID;
    Button signout;

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
                    startActivity(i);
                    finish();
                } else {
                    bottomNav = findViewById(R.id.bottom_nav);
                    bottomNav.setSelectedItemId(R.id.home);

                    HomeFrag homeFrag = new HomeFrag();
                    if (!getSupportFragmentManager().isDestroyed())
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFrag).commit();
                }


            }


        };
        firebaseAuth.addAuthStateListener(authStateListener);

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        HomeFrag homeFrag = new HomeFrag();
                        if (!getSupportFragmentManager().isDestroyed())
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFrag).commit();
                        return true;
                    case R.id.friends:
                        FriendsFrag friendsFrag = new FriendsFrag();
                       if (!getSupportFragmentManager().isDestroyed())
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, friendsFrag).commit();
                        return true;

                }
                return false;
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat:
                return true;
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            case R.id.notis:
                return true;
            case R.id.shazam:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}


