package com.example.chatbox.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatbox.fragments.chat_fragment;
import com.example.chatbox.fragments.friend_fragment;
import com.example.chatbox.fragments.request_fragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return  new chat_fragment();
            case 1: return new friend_fragment();
            case 2: return  new request_fragment();

            default: return  new chat_fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;

            if(position==0)
                title="CHATS";
            else if(position==1)
                title="FRIENDS";
            else if(position==2)
                title="ADD";

            return title;
        }
    }
