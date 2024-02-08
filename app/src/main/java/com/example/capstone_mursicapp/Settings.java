package com.example.capstone_mursicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsFrag settingsFrag = new SettingsFrag();
        if (!getSupportFragmentManager().isDestroyed())
            getSupportFragmentManager().beginTransaction().replace(R.id.settingslayout, settingsFrag).commit();
    }
}