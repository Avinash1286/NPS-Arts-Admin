package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

public class ShowVotingImage extends AppCompatActivity {

    ImageView showImage;
    Toolbar toolbar;
    String name,image;
    AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_voting_image);
        toolbar=(Toolbar)findViewById(R.id.showImageTool);
        appBarLayout=(AppBarLayout)findViewById(R.id.showVotingImageAppbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
       name=getIntent().getExtras().get("name").toString();
       image=getIntent().getExtras().get("image").toString();
        showImage=(ImageView)findViewById(R.id.showVotingImageImage);
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolfadein);
                appBarLayout.setAnimation(animation);
                appBarLayout.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        appBarLayout.setVisibility(View.GONE);
                        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolfadeout);
                        appBarLayout.setAnimation(animation);
                    }
                },2000);
            }
        });
        actionBar.setTitle(name);
        Picasso.with(getApplicationContext()).load(image).into(showImage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolfadeout);
                appBarLayout.setAnimation(animation);
                appBarLayout.setVisibility(View.GONE);
            }
        },2000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         onBackPressed();
        return super.onOptionsItemSelected(item);

    }
}
