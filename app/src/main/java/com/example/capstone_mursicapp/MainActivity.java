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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.capstone_mursicapp.data.remote.spotify.AuthenticationManager;
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager;
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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainLayout);
    String userID;
    Button signout;

    BottomNavigationView bottomNav;
    private AuthenticationManager authManager = new AuthenticationManager();
    private SpotifyManager spotManager = new SpotifyManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ____IF YOU ENTER THE APP VIA CALLBACK URI  SpotifyConstants REDIRECT_URI --> "apibasictest://callback"
        // ____CALLBACK URI DEFINED IN AuthenticationManager.KT --> "redirect_uri"
        // ____INTENT FILTER IN AndroidManifest.xml
        Uri uri = getIntent().getData();
        if (uri != null && "apibasictest".equals(uri.getScheme()) && "callback".equals(uri.getHost())) {
            // handle callback on uri to get code
            String code = authManager.handleCallback(uri);
            if (code != null) {
                spotManager.getAccessToken(code);
            }
        }

        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    spotManager.refreshAccessToken();
                }
            }, 10*1000, 3600 * 1000);
        }
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFrag).addToBackStack(null).commit();
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
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFrag).addToBackStack(null).commit();
                        return true;
                    case R.id.friends:
                        FriendsFrag friendsFrag = new FriendsFrag();
                       if (!getSupportFragmentManager().isDestroyed())
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, friendsFrag).addToBackStack(null).commit();
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


