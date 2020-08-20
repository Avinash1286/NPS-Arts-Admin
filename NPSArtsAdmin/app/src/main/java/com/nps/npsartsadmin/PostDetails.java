package com.nps.npsartsadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PostDetails extends AppCompatActivity {
 private Toolbar customDetailTool;
 private    int noOfLikes;
 private    RelativeLayout middle;
 private    SeekBar fontSizeSetter;
 private    DatabaseReference onlineLikeReff,postReff,userReff,noteRef;
 private    TextView setColor,dateandTime,showLikes,showName,showTopic,showOwnername;
 private    String postKey;
 private    ImageView likeButton,showOption,onBackD,commentButtonClick,shareButtonClick,bookMark,showVerified;
 private    CircularImageView proImageView;
 private    FirebaseAuth mAuthInDetails;
 private    String currentUsers,uidKey;
 private    String articles,date,time,userProLink,username,backGround,fontSize,heading,mainHeading,currentUsername,currentProLink,uid;
 private    Boolean likechecker=false;
 private    Boolean bookmarkChecker=false;
 private    RelativeLayout backSet;
 private    Boolean shareButtonChecker=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        customDetailTool=(Toolbar)findViewById(R.id.postDetailsToolbar);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        setSupportActionBar(customDetailTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.detail_custom_toolbar,null);
        actionBar.setCustomView(view);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        onBackD=(ImageView)findViewById(R.id.onBackDetail);
        onBackD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fontSizeSetter=(SeekBar)findViewById(R.id.increaseFontSizeInDetails);
        showOwnername=(TextView)findViewById(R.id.showOwnerName);
        showOwnername.setTypeface(roboto);
        mAuthInDetails=FirebaseAuth.getInstance();
        currentUsers=mAuthInDetails.getCurrentUser().getUid();
        postKey=getIntent().getExtras().get("postKey").toString();
        userReff= FirebaseDatabase.getInstance().getReference().child("Users");
        noteRef=FirebaseDatabase.getInstance().getReference().child("Notification");
        uidKey=getIntent().getExtras().get("uidKey").toString();
        mainHeading=getIntent().getExtras().get("heading").toString();
        postReff=FirebaseDatabase.getInstance().getReference().child("Posts");
        backSet=(RelativeLayout)findViewById(R.id.middleInDetails);
        showName=(TextView)findViewById(R.id.userNameHereInDetails);
        showTopic=(TextView)findViewById(R.id.userHeadingHereInDetails);
        setColor=(TextView)findViewById(R.id.containArtssInDetails);
        setColor.setTypeface(roboto);
        showName.setTypeface(roboto);
        showTopic.setTypeface(roboto);
        dateandTime=(TextView)findViewById(R.id.dateandtimeInDetail);
        dateandTime.setTypeface(roboto);
        likeButton=(ImageView)findViewById(R.id.likeButtonClickInDetails);
        showOption=(ImageView)findViewById(R.id.optionsInDetails);
        commentButtonClick=(ImageView)findViewById(R.id.commentButtonInDetails);
        shareButtonClick=(ImageView)findViewById(R.id.shareButtonInDetails);
        bookMark=(ImageView)findViewById(R.id.bookmarkButtonInDetails);
        showLikes=(TextView)findViewById(R.id.likesTextInDetails);
        showLikes.setTypeface(roboto);
        showVerified=(ImageView)findViewById(R.id.verified);
        onlineLikeReff=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        proImageView=(CircularImageView)findViewById(R.id.userProInDetails);
        showUserProfile();
        getPostInfo();
        handelClickes();
        setBookMarkStatus(postKey);
        setLikeButtonStatus(postKey);
        fontSizeSetter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               setColor.setTextSize(progress+20);
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });

        
    }
    private void handelClickes() {

        showLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                ShowLikesBottomSheet.postKey=postKey;
                showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
            }
        });
        proImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostDetails.this,ShowAlluserProfile.class);
                intent.putExtra("postKey",postKey);
                startActivity(intent);
            }
        });
        showName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostDetails.this,ShowAlluserProfile.class);
                intent.putExtra("postKey",postKey);
                startActivity(intent);
            }
        });
       shareButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButtonChecker=true;
                postReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (shareButtonChecker.equals(true)){
                            heading=dataSnapshot.child(postKey).child("heading").getValue().toString();
                            articles=dataSnapshot.child(postKey).child("articles").getValue().toString();
                            Intent intent=new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT,heading);
                            intent.putExtra(Intent.EXTRA_TEXT,articles);
                            startActivity(Intent.createChooser(intent,"Share using Social Media"));
                            shareButtonChecker=false;
                        }


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            }
        });

        bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarkChecker=true;
                postReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (bookmarkChecker.equals(true)){
                            if (dataSnapshot.child(postKey).hasChild(currentUsers)){
                                postReff.child(postKey).child(currentUsers).removeValue();
                                bookmarkChecker=false;
                                String sendNotificationTo=uidKey;
                                String randomForNotification=postKey+currentUsers+"fav";
                                if (!sendNotificationTo.equals(currentUsers)){
                                    noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification).removeValue();
                                }

                            }
                            else {
                                postReff.child(postKey).child(currentUsers).setValue(currentUsers);
                                bookmarkChecker=false;
                                String sendNotificationTo=uidKey;
                                String randomForNotification=postKey+currentUsers+"fav";
                                if (!sendNotificationTo.equals(currentUsers)){
                                    HashMap putNotData=new HashMap();
                                    putNotData.put("postKey",postKey);
                                    putNotData.put("username",currentUsername);
                                    putNotData.put("proLink",currentProLink);
                                    putNotData.put("notificationType","fav");
                                    putNotData.put("heading",mainHeading);
                                    putNotData.put("currentUserss",currentUsers);
                                    noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                            .updateChildren(putNotData);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        commentButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentButtomSheet commentButtomSheet=new CommentButtomSheet();
                CommentButtomSheet.heading=heading;
                CommentButtomSheet.postKey=postKey;
                CommentButtomSheet.uidKey=uidKey;
                CommentButtomSheet.postType="normal";
                commentButtomSheet.show(getSupportFragmentManager(),"BottomSheet");
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likechecker=true;
                onlineLikeReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (likechecker.equals(true)){
                            if (dataSnapshot.child(postKey).hasChild(currentUsers)){
                                onlineLikeReff.child(postKey).child(currentUsers).removeValue();
                                likechecker=false;
                                String sendNotificationTo=uidKey;
                                String randomForNotification=postKey+currentUsers+"like";
                                if (!sendNotificationTo.equals(currentUsers)){
                                    noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification).removeValue();
                                }

                            }
                            else {
                                likechecker=false;
                                HashMap putLikeData=new HashMap();
                                putLikeData.put("nameInLike",currentUsername);
                                putLikeData.put("proLinkInLike",currentProLink);
                                putLikeData.put("uid",currentUsers);
                                onlineLikeReff.child(postKey).child(currentUsers).updateChildren(putLikeData);
                                String sendNotificationTo=uidKey;
                                String randomForNotification=postKey+currentUsers+"like";
                                if (!sendNotificationTo.equals(currentUsers)){
                                    HashMap putNotData=new HashMap();
                                    putNotData.put("postKey",postKey);
                                    putNotData.put("username",currentUsername);
                                    putNotData.put("proLink",currentProLink);
                                    putNotData.put("notificationType","like");
                                    putNotData.put("heading",mainHeading);
                                    putNotData.put("currentUserss",currentUsers);
                                    noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                            .updateChildren(putNotData);

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
       proImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostDetails.this,ShowAlluserProfile.class);
                intent.putExtra("postKey",postKey);
                startActivity(intent);
            }
        });
        showName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostDetails.this,ShowAlluserProfile.class);
                intent.putExtra("postKey",postKey);
                startActivity(intent);
            }
        });

        showOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popupMenu=new PopupMenu(PostDetails.this,showOption);
                popupMenu.getMenuInflater().inflate(R.menu.button_infleter,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.reportButton:
                                FragmentManager fragmentManager=getSupportFragmentManager();
                                ReportingFragment fragment=new ReportingFragment();
                                FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                                fragmentTransaction1.add(R.id.holdeReportFragInPostDetails,fragment);
                                fragmentTransaction1.addToBackStack(null);
                                fragment.postKeyValue=postKey;
                                fragment.postTitle=heading;
                                fragment.reporterName=currentUsername;
                                fragment.reporterProLink=currentProLink;
                                fragment.postUserName=username;
                                fragment.uidKey=uidKey;
                                fragmentTransaction1.commit();
                                break;

                            case R.id.viewProfile:
                                Intent intent=new Intent(PostDetails.this,ShowAlluserProfile.class);
                                intent.putExtra("postKey",postKey);
                                startActivity(intent);
                                break;

                        }

                        return true;

                    }
                });
                popupMenu.show();




            }
        });

    }


    public void getPostInfo(){

        postReff.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    articles=dataSnapshot.child("articles").getValue().toString();
                    date=dataSnapshot.child("date").getValue().toString();
                    time=dataSnapshot.child("time").getValue().toString();
                    uid=dataSnapshot.child("uid").getValue().toString();
                    userReff.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userProLink=dataSnapshot.child("profileLink").getValue().toString();
                            username=dataSnapshot.child("fullname").getValue().toString();
                            showName.setText(username);
                            setColor.setText(articles);
                            Picasso.with(getApplicationContext()).load(userProLink).placeholder(R.drawable.grayback).into(proImageView);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    backGround=dataSnapshot.child("backGround").getValue().toString();
                    fontSize=dataSnapshot.child("fontSize").getValue().toString();
                    heading=dataSnapshot.child("heading").getValue().toString();
                    showOwnername.setText(heading);
                    showTopic.setText("");
                    showName.setText(username);
                    setColor.setText(articles);
                    dateandTime.setText(time+" "+date);
                    Picasso.with(PostDetails.this).load(userProLink).placeholder(R.drawable.profile).into(proImageView);
                    if (backGround!=null){
                      //  setColor.setTextSize(Integer.valueOf(fontSize));
                        switch (Integer.valueOf(backGround)){

                            case 0:
                                backSet.setBackgroundResource(R.drawable.white);
                                setColor.setTextColor(Color.BLACK);

                                break;
                            case 1:
                                backSet.setBackgroundResource(R.drawable.gradient);
                                setColor.setTextColor(Color.WHITE);
                                break;
                            case 2:
                                backSet.setBackgroundResource(R.drawable.gradient2);
                                setColor.setTextColor(Color.WHITE);

                                break;
                            case 3:
                                backSet.setBackgroundResource(R.drawable.gradient3);
                                setColor.setTextColor(Color.WHITE);
                                break;

                            case 4:
                                backSet.setBackgroundResource(R.drawable.gradient4);
                                setColor.setTextColor(Color.BLACK);

                                break;

                            case 5:
                                backSet.setBackgroundResource(R.drawable.gradient5);
                                setColor.setTextColor(Color.BLACK);
                                break;

                            case 6:
                                backSet.setBackgroundResource(R.drawable.gradient6);
                                setColor.setTextColor(Color.WHITE);

                                break;

                            case 7:
                                backSet.setBackgroundResource(R.drawable.gradient7);
                                setColor.setHintTextColor(Color.BLACK);
                                break;

                            case 8:
                                backSet.setBackgroundResource(R.drawable.gradient8);
                                setColor.setTextColor(Color.WHITE);
                                break;

                            case 9:
                                backSet.setBackgroundResource(R.drawable.gradient9);
                                setColor.setTextColor(Color.WHITE);
                                break;

                            case 10:
                                backSet.setBackgroundResource(R.drawable.gradient10);
                                setColor.setTextColor(Color.BLACK);
                                break;

                            case 11:
                                backSet.setBackgroundResource(R.drawable.gradient11);
                                setColor.setTextColor(Color.WHITE);
                                break;

                            case 12:
                                backSet.setBackgroundResource(R.drawable.gradient12);
                                setColor.setTextColor(Color.WHITE);
                                break;


                            case 13:
                                backSet.setBackgroundResource(R.drawable.gradient13);
                                setColor.setTextColor(Color.WHITE);
                                break;

                            case 14:
                                backSet.setBackgroundResource(R.drawable.gradient14);
                                setColor.setTextColor(Color.WHITE);
                                break;

                            case 15:
                                backSet.setBackgroundResource(R.drawable.gradient15);
                                setColor.setTextColor(Color.WHITE);
                                break;
                            case 16:
                                backSet.setBackgroundResource(R.drawable.gradient1);
                                setColor.setTextColor(Color.WHITE);
                                break;
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        public void setBookMarkStatus(final String postKey){

            postReff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    if (dataSnapshot.child(postKey).hasChild(currentUsers)){
                        bookMark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    }
                    else {
                        bookMark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void  setLikeButtonStatus(final String postKey){
            onlineLikeReff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postKey).hasChild(currentUsers)){
                        noOfLikes=(int)dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.liker);
                        showLikes.setText(noOfLikes+" Likes");
                    }
                    else {
                        noOfLikes=(int)dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.unlike);
                        showLikes.setText(noOfLikes+" Likes");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    private void showUserProfile() {
        userReff.child(currentUsers).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    currentUsername = dataSnapshot.child("fullname").getValue().toString();
                    currentProLink = dataSnapshot.child("profileLink").getValue().toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PostDetails.this,"Error "+e,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

