package com.example.capstone_mursicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class DOBRegister extends AppCompatActivity {
    String username;
    DatePicker datePicker;
    Button next;
    TextView login, greeting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dob_register);

        Intent i = getIntent();
        username = i.getStringExtra("username");
        next = findViewById(R.id.next);
        datePicker = findViewById(R.id.dob);
        login = findViewById(R.id.loginHL);
        greeting = findViewById(R.id.greeting);

        greeting.setText("Hi " + username + ", when's your birthday?");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int userAge= datePicker.getYear();

                if((currentYear - userAge)<14){
                    Toast.makeText(DOBRegister.this, "Not old enough to use this app", Toast.LENGTH_SHORT).show();
                    return;
                }

                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int year = datePicker.getYear();

                String date_of_birth = day+"/"+month+"/"+year;

                Intent i = new Intent(DOBRegister.this, RegisterActivity.class);
                i.putExtra("username", username);
                i.putExtra("date_of_birth", date_of_birth);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DOBRegister.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


}