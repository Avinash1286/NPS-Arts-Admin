package com.nps.npsartsadmin;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    BottomNavigationViewEx bottomNavigationViewEx;
    Toolbar mainToolsbar;
    TextView mainToolHeadingbar;
    ImageView publishing,posting;
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolsbar=(Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolsbar);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
      /*  userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUser).child("verified").getValue().toString().equals("yes")){
                    posting.setVisibility(View.VISIBLE);
                }
                else {
                    posting.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View view=layoutInflater.inflate(R.layout.custom_toolbar_lay_mainact,null);
        actionBar.setCustomView(view);
        publishing=(ImageView)findViewById(R.id.publishWallMagazine);
        posting=(ImageView)findViewById(R.id.postingWallArts);
       /* userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUser).child("permission").getValue().toString().equals("yes")){
                    posting.setVisibility(View.VISIBLE);
                }
                else {
                    posting.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }); */
        publishing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                fragmentTransaction1.addToBackStack(null);
                PublishWallMFrame frame=new PublishWallMFrame();
                fragmentTransaction1.add(R.id.showPublishingFrame,frame);
                fragmentTransaction1.commit();
            }
        });
        posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostWallBottomSheet postWallBottomSheet=new PostWallBottomSheet();
                postWallBottomSheet.show(getSupportFragmentManager(),"postWallMagazine");
            }
        });
        mainToolHeadingbar=(TextView)findViewById(R.id.mainactHeading);
        mainToolHeadingbar.setTypeface(roboto);
        bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomnavInArticles);
      tabLayout=(TabLayout)findViewById(R.id.tablayout);
      viewPager=(ViewPager)findViewById(R.id.viewpager);
      setUpBottomNav();
      ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
      viewPager.setAdapter(viewPagerAdapter);
      tabLayout.setupWithViewPager(viewPager);
      tabLayout.setTabIndicatorFullWidth(false);
      tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
      tabLayout.setSelectedTabIndicator(R.drawable.tab_indicatior);
      tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
      tabLayout.setTabTextColors(Color.parseColor("#F44336"),Color.parseColor("#F44336"));
      tabLayout.setSelectedTabIndicatorHeight((int)(3*getResources().getDisplayMetrics().density));
    }
    public void setUpBottomNav(){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
        bottomNavigationViewEx.setTypeface(roboto);
        enableNavigation();
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);
    }
    public void enableNavigation(){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(MainActivity.this,ShowPost.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postsearch:
                        startActivity(new Intent(MainActivity.this,SearchPeople.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.offlineArticles:
                        break;
                    case R.id.postlikes:
                        startActivity(new Intent(MainActivity.this,HeartActivity.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;

                    case R.id.postprofile:
                        startActivity(new Intent(MainActivity.this,AdminControll.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

}
