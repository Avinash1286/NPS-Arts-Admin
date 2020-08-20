package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ShowWallMagazineDetails extends AppCompatActivity {
    String headingInDetailFragment,currentUsers,nameInDetailFragment,artsInDetailFragment,postKeys,username,userProLink;
    TextView showHeadingInDetails,showNameInDetail,showArtsInDetail,likeTextWallMagazine;
    ImageView cancleIcon,likeWallMagazine,commentWallMagazine,shareWallMagazine;
    GestureDetectorCompat gestureDetectorCompat=null;
    FirebaseAuth mAuthInDetails;
    DatabaseReference userReff,onlineLikeReff,commentRef;
    int noOfLikes;
    Boolean likechecker;
    FragmentManager fragmentManager;
    SeekBar fornSizeSetter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wall_magazine_details);
        headingInDetailFragment=String.valueOf(getIntent().getExtras().get("heading"));
        nameInDetailFragment=String.valueOf(getIntent().getExtras().get("name"));
        artsInDetailFragment=String.valueOf(getIntent().getExtras().get("arts"));
        postKeys=String.valueOf(getIntent().getExtras().get("postKey"));
        // Create a common gesture listener object.
        GestureHandlerForShowMagazineDetails gestureListener = new GestureHandlerForShowMagazineDetails();
        // Set activity in the listener.
        gestureListener.setActivity(this);
        //Create the gesture detector with the gesture listener.
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
        fragmentManager=getSupportFragmentManager();
        showArtsInDetail=(TextView)findViewById(R.id.showWallArts);
        showHeadingInDetails=(TextView)findViewById(R.id.showWallArtsTopic);
        showNameInDetail=(TextView)findViewById(R.id.showAuthorname);
        cancleIcon=(ImageView)findViewById(R.id.cancleIconInShowDetailWall);
        likeTextWallMagazine=(TextView)findViewById(R.id.wallArtsDetailikesText);
        likeWallMagazine=(ImageView)findViewById(R.id.wallArtsDetaillikeButtonClick);
        commentWallMagazine=(ImageView)findViewById(R.id.wallArtsDetailcommentButton);
        shareWallMagazine=(ImageView)findViewById(R.id.wallArtDetailsshareButton);
        fornSizeSetter=(SeekBar)findViewById(R.id.increaseFontSizeInDetails);
        mAuthInDetails= FirebaseAuth.getInstance();
        currentUsers=mAuthInDetails.getCurrentUser().getUid();
        userReff= FirebaseDatabase.getInstance().getReference().child("Users");
        onlineLikeReff=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        commentRef=FirebaseDatabase.getInstance().getReference().child("CommentRefWallMagazine");
        fornSizeSetter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showArtsInDetail.setTextSize(progress+20);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cancleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
               finish();
                overridePendingTransition(R.anim.comment_in,R.anim.fragment_anim_out);
            }
        });
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        showArtsInDetail.setTypeface(roboto);
        showNameInDetail.setTypeface(roboto);
        showHeadingInDetails.setTypeface(roboto);
        showNameInDetail.setText(nameInDetailFragment);
        showArtsInDetail.setText(artsInDetailFragment);
        showHeadingInDetails.setText("Topic: "+headingInDetailFragment);
        showUserProfile();
        setLikeButtonStatus();
        shareWallMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,headingInDetailFragment);
                intent.putExtra(Intent.EXTRA_TEXT,artsInDetailFragment+"\n\nWritten By:"+nameInDetailFragment);
                startActivity(Intent.createChooser(intent,"Share using Social Media"));
            }
        });
        commentWallMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowWallCommentBottomSheet showWallCommentBottomSheet=new ShowWallCommentBottomSheet();
                ShowWallCommentBottomSheet.postKey=postKeys;
                showWallCommentBottomSheet.show(getSupportFragmentManager(),"Comments");
            }
        });
        likeTextWallMagazine.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                ShowLikesBottomSheet.postKey=postKeys;
                showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
                return true;
            }
        });
        likeTextWallMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                ShowLikesBottomSheet.postKey=postKeys;
                showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
            }
        });
        likeWallMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likechecker=true;
                onlineLikeReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (likechecker.equals(true)){
                            if (dataSnapshot.child(postKeys).hasChild(currentUsers)){
                                onlineLikeReff.child(postKeys).child(currentUsers).removeValue();
                                likechecker=false;
                            }
                            else {
                                likechecker=false;
                                HashMap putLikeData=new HashMap();
                                putLikeData.put("nameInLike",username);
                                putLikeData.put("proLinkInLike",userProLink);
                                putLikeData.put("uid",currentUsers);
                                onlineLikeReff.child(postKeys).child(currentUsers).updateChildren(putLikeData);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass activity on touch event to the gesture detector.
        gestureDetectorCompat.onTouchEvent(event);
        // Return true to tell android OS that event has been consumed, do not pass it to other event listeners.
        return true;
    }
    public void  setLikeButtonStatus(){
        onlineLikeReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postKeys).hasChild(currentUsers)){
                    noOfLikes=(int)dataSnapshot.child(postKeys).getChildrenCount();
                    likeWallMagazine.setImageResource(R.drawable.liker);
                    likeTextWallMagazine.setText(noOfLikes+" Likes");
                }
                else {
                    noOfLikes=(int)dataSnapshot.child(postKeys).getChildrenCount();
                    likeWallMagazine.setImageResource(R.drawable.nonelike);
                    likeTextWallMagazine.setText(noOfLikes+" Likes");
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

                    username =String.valueOf(dataSnapshot.child("fullname").getValue());
                    userProLink =String.valueOf(dataSnapshot.child("profileLink").getValue());
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ShowWallMagazineDetails.this,"Error "+e,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
