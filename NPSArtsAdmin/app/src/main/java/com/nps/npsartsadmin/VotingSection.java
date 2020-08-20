package com.nps.npsartsadmin;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
public class VotingSection extends AppCompatActivity {
    TabLayout votingTabLayout;
    ViewPager votingViewPager;
    Toolbar votingTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_section);
        votingViewPager=(ViewPager)findViewById(R.id.votingViewpager);
        votingTabLayout=(TabLayout)findViewById(R.id.votingTablayout);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        votingTool=(Toolbar)findViewById(R.id.votingSectionTool);
        setSupportActionBar(votingTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.votingmanagment_tools,null);
        actionBar.setCustomView(view);
        ImageView onBack=(ImageView)view.findViewById(R.id.vmhandBack);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView mainToolHeadingbar=(TextView)findViewById(R.id.vmhandHeading);
        mainToolHeadingbar.setTypeface(roboto);
        mainToolHeadingbar.setText("Voting Section");
        VotingViewPagerAdapter votingViewPagerAdapter=new VotingViewPagerAdapter(getSupportFragmentManager());
        votingViewPager.setAdapter(votingViewPagerAdapter);
        votingTabLayout.setupWithViewPager(votingViewPager);
        votingTabLayout.setTabIndicatorFullWidth(false);
        votingTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        votingTabLayout.setSelectedTabIndicator(R.drawable.tab_indicatior);
        votingTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        votingTabLayout.setTabTextColors(Color.parseColor("#FFD3CFD1"),Color.parseColor("#FF5722"));
        votingTabLayout.setSelectedTabIndicatorHeight((int)(3*getResources().getDisplayMetrics().density));
    }
}
