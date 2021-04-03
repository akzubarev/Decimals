package com.education4all;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    private static final String[] TAB_TITLES = new String[]{"Задания", "Интерфейс"};

    public SectionsPagerAdapter(FragmentActivity fm) {
        super(fm);
    }

    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
            default:
                fragment = new SettingsTaskTab();
                break;
            case 1:
                fragment = new SettingsInterfaceTab();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
