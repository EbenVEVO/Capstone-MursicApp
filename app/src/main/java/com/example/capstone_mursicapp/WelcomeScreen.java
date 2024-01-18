package com.example.capstone_mursicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener{

    Button login, register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        login = findViewById(R.id.OGlogin);
        login.setOnClickListener(this);
        register = findViewById(R.id.OGregister);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.OGlogin){
            Intent i = new Intent(WelcomeScreen.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        if(v.getId()==R.id.OGregister){
            Intent i = new Intent(WelcomeScreen.this, RegisterUsername.class);
            startActivity(i);
            finish();
        }
    }
}