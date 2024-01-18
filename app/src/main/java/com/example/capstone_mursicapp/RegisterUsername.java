package com.example.capstone_mursicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterUsername extends AppCompatActivity {
    TextInputEditText given_username;
    Button next;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_username);

        given_username = findViewById(R.id.username);
        login = findViewById(R.id.loginHL);
        next = findViewById(R.id.next_username);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username;
                username= String.valueOf(given_username.getText());
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterUsername.this, "Enter a Username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(username.length()>20){
                    Toast.makeText(RegisterUsername.this, "Username must be smaller than 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(username.contains(" ")){
                    Toast.makeText(RegisterUsername.this, "Username has invalid characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(RegisterUsername.this, DOBRegister.class);
                i.putExtra("username", username);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterUsername.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


    }
}