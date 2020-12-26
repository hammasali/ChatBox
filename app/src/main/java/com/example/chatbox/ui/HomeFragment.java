package com.example.chatbox.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.chatbox.R;
import com.example.chatbox.adapters.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeFragment extends Fragment {


    TabLayout tabLayout;
    ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = root.findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);

        tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        return root;
    }
}
