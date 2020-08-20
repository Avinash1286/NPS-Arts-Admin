package com.nps.npsartsadmin;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
public class VotingManagement extends AppCompatActivity {
    TabLayout votingTabLayout;
    ViewPager votingViewPager;
    Toolbar votingTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_management);
        votingTool=(Toolbar)findViewById(R.id.vmTool);
        setSupportActionBar(votingTool);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View view=layoutInflater.inflate(R.layout.votingmanagement_tool_layouts,null);
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
        ImageView votingSetting=(ImageView)findViewById(R.id.votingSetting);
        votingSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getSupportFragmentManager();
                VotingSetting aboutToVote=new VotingSetting();
                FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                fragmentTransaction1.add(R.id.votingSettingFrame,aboutToVote);
                fragmentTransaction1.addToBackStack(null);
                fragmentTransaction1.commit();
            }
        });
        votingViewPager=(ViewPager)findViewById(R.id.vmViewPager);
        votingTabLayout=(TabLayout)findViewById(R.id.vmTab);
        VotingManagementViewPager votingManagementViewPager=new VotingManagementViewPager(getSupportFragmentManager());
        votingViewPager.setAdapter(votingManagementViewPager);
        votingTabLayout.setupWithViewPager(votingViewPager);
        votingTabLayout.setTabIndicatorFullWidth(false);
        votingTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        votingTabLayout.setSelectedTabIndicator(R.drawable.tab_indicatior);
        votingTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        votingTabLayout.setTabTextColors(Color.parseColor("#FFD3CFD1"),Color.parseColor("#FF5722"));
        votingTabLayout.setSelectedTabIndicatorHeight((int)(3*getResources().getDisplayMetrics().density));
    }
}
