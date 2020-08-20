package com.nps.npsartsadmin;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                WallMagazineArticles wallMagazineArticles=new WallMagazineArticles();
                return wallMagazineArticles;
            case 1:
                CreativeHands creativeHands=new CreativeHands();
                return creativeHands;
            case 2:
                SchoolMagazine schoolMagazine=new SchoolMagazine();
                return schoolMagazine;
           default:
               return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Wall Magazine";
            case 1:
                return "Creative Hands";
            case 2:
                return "GiggleIT Posts";
            default:
                return null;
        }
    }
}
