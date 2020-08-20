package com.nps.npsartsadmin;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class VotingManagementViewPager  extends FragmentPagerAdapter {


    public VotingManagementViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                HandwritingManagement vm_handwriting=new HandwritingManagement();
                return vm_handwriting;
            case 1:
                DrawingManagement drawingVoting=new DrawingManagement();
                return drawingVoting;
            case 2:
                VMEssay essayVot=new VMEssay();
                return essayVot;
            case 3:

                VMPoem poemVoting=new VMPoem();
                return poemVoting;


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
        switch (position){
            case 0:
                return "Handwriting";
            case 1:
                return "Drawing";
            case 2:
                return "Essay";
            case 3:
                return "Poem";
            default:
                return null;

        }
    }
}
