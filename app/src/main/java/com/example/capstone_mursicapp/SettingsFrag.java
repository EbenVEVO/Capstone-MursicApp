package com.example.capstone_mursicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class SettingsFrag extends Fragment {

    RelativeLayout connectedAccounts;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        connectedAccounts = view.findViewById(R.id.connectedaccounts);

        connectedAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectAccounts connectAccounts = new ConnectAccounts();
                if (!getActivity().isDestroyed())
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.settingslayout, connectAccounts).commit();
            }
        });
        return view;
    }
}