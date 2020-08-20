package com.nps.npsartsadmin;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class WelcomeScreen extends AppCompatActivity {
     ImageView animatioHolder;
     AnimationDrawable animationDrawable;
     TextView powerdAnim;
     FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_welcome_screen);
        Typeface avenyInsta=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
         animatioHolder=(ImageView)findViewById(R.id.customLoading);
         animatioHolder.setBackgroundResource(R.drawable.animation);
         mAuth= FirebaseAuth.getInstance();
         animationDrawable=(AnimationDrawable)animatioHolder.getBackground();
         animationDrawable.start();
         powerdAnim=(TextView)findViewById(R.id.poweredText);
         powerdAnim.setTypeface(avenyInsta);
         Animation animation= AnimationUtils.loadAnimation(this,R.anim.powered_anim);
         powerdAnim.setAnimation(animation);
         animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                if (firebaseUser!=null){
                    startActivity(new Intent(WelcomeScreen.this,ShowPost.class));
                    finish();
                }
                else {
                    startActivity(new Intent(WelcomeScreen.this, LogInOptions.class));
                    finish();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
