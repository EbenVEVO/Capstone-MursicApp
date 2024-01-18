package com.example.capstone_mursicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
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
                if(user==null){
                    System.out.println(getSupportFragmentManager().isDestroyed());

                    Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
                    startActivity(i);
                    finish();
                }
                else{
                    bottomNav = findViewById(R.id.bottom_nav);
                    bottomNav.setSelectedItemId(R.id.home);

                    HomeFrag homeFrag = new HomeFrag();
                    if(!getSupportFragmentManager().isDestroyed())
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFrag).commit();
                }


        }


        };
        firebaseAuth.addAuthStateListener(authStateListener);

        signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        HomeFrag homeFrag = new HomeFrag();
                        if(!getSupportFragmentManager().isDestroyed())
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFrag).commit();
                        return true;
                    case R.id.friends:
                        FriendsFrag friendsFrag = new FriendsFrag();
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, friendsFrag).commit();
                        return true;

                }
                return false;
            }
        });

    }

    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat:
                return true;
            case R.id.notis:
                return true;
            case R.id.shazam:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean loadFragment(Fragment fragment, FragmentManager fm) {
        //switching fragment
        if (fragment != null) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.mainLayout, fragment);

            if (!fm.isDestroyed())
                transaction.commit();
            return true;
        }
        return false;
    }
}