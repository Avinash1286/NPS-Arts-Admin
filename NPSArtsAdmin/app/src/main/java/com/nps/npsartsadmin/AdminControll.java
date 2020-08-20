package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class AdminControll extends AppCompatActivity {
    CardView cardQuiz,cardVoting,cardReport,cardPendingPost,cardManageUser,cardManaeVoting;
    TextView t1,t2,t3,t4,t5,t6;
    Toolbar adminToolbar;
    TextView adminHeading;
    BottomNavigationViewEx bottomNavigationViewEx;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_controll);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        adminToolbar=(Toolbar)findViewById(R.id.adminTool);
        setSupportActionBar(adminToolbar);
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
             view=layoutInflater.inflate(R.layout.admin_tool,null);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(view);
        }
        catch (NullPointerException e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        ImageView openProfile=(ImageView)view.findViewById(R.id.openProfile);
        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminControll.this,ProfileActivity.class));
            }
        });
        ImageView addUser=(ImageView)findViewById(R.id.addUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminControll.this,AddAdminUser.class));
            }
        });
        bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomnav);
        adminHeading=(TextView)view.findViewById(R.id.adminHeading);
        adminHeading.setTypeface(roboto);
        cardQuiz=(CardView)findViewById(R.id.card1);
        cardVoting=(CardView)findViewById(R.id.card2);
        cardManaeVoting=(CardView)findViewById(R.id.card3);
        cardPendingPost=(CardView)findViewById(R.id.card4);
        cardReport=(CardView)findViewById(R.id.card5);
        cardManageUser=(CardView)findViewById(R.id.card6);
        t1=(TextView)findViewById(R.id.adminTextCreatWeeklyQuizHeading);
        t2=(TextView)findViewById(R.id.adminVotingHeading);
        t3=(TextView)findViewById(R.id.adminTextManageVoting);
        t4=(TextView)findViewById(R.id.adminPendingPostHeading);
        t5=(TextView)findViewById(R.id.adminTextReportzHeading);
        t6=(TextView)findViewById(R.id.adminManageUserHeading);
        t1.setTypeface(roboto);
        t2.setTypeface(roboto);
        t3.setTypeface(roboto);
        t4.setTypeface(roboto);
        t5.setTypeface(roboto);
        t6.setTypeface(roboto);
        setUpBottomNav();
        cardManaeVoting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminControll.this,VotingManagement.class));
            }
        });
        cardPendingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminControll.this,PendingPostVerification.class));
            }
        });
        cardQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminControll.this,CreateQuizCompetation.class));
            }
        });
        cardVoting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           ShowVotingOptionsBottomSheet showVotingOptionsBottomSheet=new ShowVotingOptionsBottomSheet();
           showVotingOptionsBottomSheet.show(getSupportFragmentManager(),"VotingOptions");
            }
        });
      cardReport.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(AdminControll.this,ShowReports.class));
          }
      });
      cardManageUser.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(AdminControll.this,GiggleItVerification.class));
          }
      });

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
        MenuItem menuItem=menu.getItem(4);
        menuItem.setChecked(true);
        menuItem.setIcon(R.drawable.final_nav_profile_on);
    }
    public void enableNavigation(){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(AdminControll.this,ShowPost.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postsearch:
                        startActivity(new Intent(AdminControll.this,SearchPeople.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.offlineArticles:
                        startActivity(new Intent(AdminControll.this,MainActivity.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postlikes:
                        startActivity(new Intent(AdminControll.this,HeartActivity.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;

                    case R.id.postprofile:
                        break;
                }
                return false;
            }
        });
    }


}
