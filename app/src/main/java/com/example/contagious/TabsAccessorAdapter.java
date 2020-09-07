package com.example.contagious;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.contagious.ChatsFragment;
import com.example.contagious.ContactsFragment;
import com.example.contagious.GroupsFragment;
import com.example.contagious.RequestsFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment=new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment=new ContactsFragment();
                return contactsFragment;
            case 3:
                RequestsFragment requestsFragment=new RequestsFragment();
                return requestsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position)
        {
            case 0:
                return "Allies";
            case 1:
                return "Teams";
            case 2:
                return "Contacts";
            case 3:
                return "Ally Requests";
            default:
                return null;
        }
    }
}
