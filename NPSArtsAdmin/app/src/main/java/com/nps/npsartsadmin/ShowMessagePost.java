package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.loadingview.LoadingView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowMessagePost extends AppCompatActivity {
    Toolbar showMessagePostTool;
    TextView setMessage;
    String postKeys,userId;
    DatabaseReference postRef,userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message_post);
        postKeys=getIntent().getExtras().get("postKey").toString();
        userId=getIntent().getExtras().get("userId").toString();
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        showMessagePostTool=(Toolbar)findViewById(R.id.showMessageTool);
        setSupportActionBar(showMessagePostTool);
      final   Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        final TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(roboto);
        headingPendingPost.setText("");
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setMessage=(TextView)findViewById(R.id.containMessagesInPost);
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String name=dataSnapshot.child("fullname").getValue().toString();
             headingPendingPost.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        postRef.child(postKeys).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String getMessage=dataSnapshot.child("message").getValue().toString();
             String back=dataSnapshot.child("backGround").getValue().toString();
                setMessage.setTypeface(roboto);
                setMessage.setText(getMessage);
                setMessage.setTextSize(25);
                setMessage.setHeight(400);
                setMessage.setGravity(Gravity.CENTER);
                setBackGround(back);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setBackGround(String backGround) {
        RelativeLayout backSet=(RelativeLayout)findViewById(R.id.backMessage);
        switch (Integer.valueOf(backGround)){

            case 0:
                backSet.setBackgroundResource(R.drawable.white);
                setMessage.setTextColor(Color.BLACK);

                break;
            case 1:
                backSet.setBackgroundResource(R.drawable.gradient);
                setMessage.setTextColor(Color.WHITE);
                break;
            case 2:
                backSet.setBackgroundResource(R.drawable.gradient2);
                setMessage.setTextColor(Color.WHITE);

                break;
            case 3:
                backSet.setBackgroundResource(R.drawable.gradient3);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 4:
                backSet.setBackgroundResource(R.drawable.gradient4);
                setMessage.setTextColor(Color.BLACK);

                break;

            case 5:
                backSet.setBackgroundResource(R.drawable.gradient5);
                setMessage.setTextColor(Color.BLACK);
                break;

            case 6:
                backSet.setBackgroundResource(R.drawable.gradient6);
                setMessage.setTextColor(Color.WHITE);

                break;

            case 7:
                backSet.setBackgroundResource(R.drawable.gradient7);
                setMessage.setHintTextColor(Color.BLACK);
                break;

            case 8:
                backSet.setBackgroundResource(R.drawable.gradient8);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 9:
                backSet.setBackgroundResource(R.drawable.gradient9);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 10:
                backSet.setBackgroundResource(R.drawable.gradient10);
                setMessage.setTextColor(Color.BLACK);
                break;

            case 11:
                backSet.setBackgroundResource(R.drawable.gradient11);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 12:
                backSet.setBackgroundResource(R.drawable.gradient12);
                setMessage.setTextColor(Color.WHITE);
                break;


            case 13:
                backSet.setBackgroundResource(R.drawable.gradient13);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 14:
                backSet.setBackgroundResource(R.drawable.gradient14);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 15:
                backSet.setBackgroundResource(R.drawable.gradient15);
                setMessage.setTextColor(Color.WHITE);
                break;

            case 16:
                backSet.setBackgroundResource(R.drawable.gradient1);
                setMessage.setTextColor(Color.WHITE);
                break;
        }



    }
}
