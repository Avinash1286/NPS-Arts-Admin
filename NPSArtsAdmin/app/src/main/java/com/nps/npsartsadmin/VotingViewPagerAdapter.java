package com.nps.npsartsadmin;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class VotingViewPagerAdapter extends FragmentPagerAdapter {


    public VotingViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                HandwritingVoting handwritingVoting=new HandwritingVoting();
                return handwritingVoting;
            case 1:
                DrawingVoting drawingVoting=new DrawingVoting();
                return drawingVoting;
            case 2:
                PoemVoting poemVoting=new PoemVoting();
                return poemVoting;
            case 3:
                EssayVot essayVot=new EssayVot();
                return essayVot;

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
                return "Vote for Handwriting";
            case 1:
                return "Vote for Drawing";
            case 2:
                return "Vote for Poem";
            case 3:
                return "Vote for Essay";
                default:
                    return null;

        }
    }
}
