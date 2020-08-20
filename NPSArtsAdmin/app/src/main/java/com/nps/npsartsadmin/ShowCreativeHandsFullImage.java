package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.github.chrisbanes.photoview.PhotoView;
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
public class ShowCreativeHandsFullImage extends AppCompatActivity {
    Toolbar setUpToolbar;
    TextView setNameInCreatAct,setLikeOnCreatAct;
    ImageView setLikerImage,commentImage,shareImage,onBack;
    DatabaseReference creativeRef;
    String creativeKey,creativeNameHolder,creativePictureHolder,currentUsers,username,userProLink;
    DatabaseReference onlineLikeReff,userReff,commentRef;
    PhotoView photoView;
    Boolean likechecker;
    FirebaseAuth mAuthInDetails;
    RelativeLayout containLikesAndComment;
    FragmentManager fragmentManager;
    int noOfLikes;
   Boolean shareButtonChecker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_creative_hands_full_image);
        creativeKey=getIntent().getExtras().get("creativeKey").toString();
        setUpToolbar=(Toolbar)findViewById(R.id.fullImageTool);
        setActionBar(setUpToolbar);
        ActionBar actionBar=getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.toolbar_layout_creativehands,null);
        actionBar.setCustomView(view);
        containLikesAndComment=(RelativeLayout)findViewById(R.id.conatinLikesAndCommentsButton);
        setNameInCreatAct=(TextView)findViewById(R.id.showArtistName);
        setLikeOnCreatAct=(TextView)findViewById(R.id.creativelikesText);
        setLikerImage=(ImageView)findViewById(R.id.creativelikeButtonClick);
        commentImage=(ImageView)findViewById(R.id.creativecommentButton);
        shareImage=(ImageView)findViewById(R.id.creativeshareButton);
        onBack=(ImageView)findViewById(R.id.onBackCreativeHands);
        photoView=(PhotoView) findViewById(R.id.showCreativeImageFull);
        mAuthInDetails= FirebaseAuth.getInstance();
        currentUsers=mAuthInDetails.getCurrentUser().getUid();
        userReff= FirebaseDatabase.getInstance().getReference().child("Users");
        onlineLikeReff=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        commentRef=FirebaseDatabase.getInstance().getReference().child("CommentRefWallMagazine");
        creativeRef= FirebaseDatabase.getInstance().getReference().child("CreativeHandsInfo");
     /*   containCreativeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolfadein);
                containLikesAndComment.setAnimation(animation);
                containLikesAndComment.setVisibility(View.VISIBLE);
                setUpToolbar.setVisibility(View.VISIBLE);
                setUpToolbar.setAnimation(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setUpToolbar.setVisibility(View.GONE);
                        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolfadeout);
                        setUpToolbar.setAnimation(animation);
                        containLikesAndComment.setAnimation(animation);
                        containLikesAndComment.setVisibility(View.GONE);
                    }
                },2000);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpToolbar.setVisibility(View.GONE);
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolfadeout);
                setUpToolbar.setAnimation(animation);
                containLikesAndComment.setVisibility(View.GONE);
                containLikesAndComment.setAnimation(animation);
            }
        },2000);
*/

     shareImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             shareItem(creativePictureHolder);
         }
     });
        fragmentManager=getSupportFragmentManager();
        showUserProfile();
        setLikeButtonStatus();
        commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ShowWallCommentBottomSheet showWallCommentBottomSheet=new ShowWallCommentBottomSheet();
             ShowWallCommentBottomSheet.postKey=creativeKey;
             showWallCommentBottomSheet.show(getSupportFragmentManager(),"Comments");
            }
        });
        setLikerImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                ShowLikesBottomSheet.postKey=creativeKey;
                showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
                return true;
            }
        });
        setLikeOnCreatAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                ShowLikesBottomSheet.postKey=creativeKey;
                showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
            }
        });
        setLikerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likechecker=true;
                onlineLikeReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (likechecker.equals(true)){
                            if (dataSnapshot.child(creativeKey).hasChild(currentUsers)){
                                onlineLikeReff.child(creativeKey).child(currentUsers).removeValue();
                                likechecker=false;
                            }
                            else {
                                likechecker=false;
                                HashMap putLikeData=new HashMap();
                                putLikeData.put("nameInLike",username);
                                putLikeData.put("proLinkInLike",userProLink);
                                putLikeData.put("uid",currentUsers);
                                onlineLikeReff.child(creativeKey).child(currentUsers).updateChildren(putLikeData);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        creativeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    creativeNameHolder=dataSnapshot.child(creativeKey).child("name").getValue().toString();
                    creativePictureHolder=dataSnapshot.child(creativeKey).child("image").getValue().toString();
                    setNameInCreatAct.setText(creativeNameHolder);
                    Picasso.with(ShowCreativeHandsFullImage.this).load(creativePictureHolder).placeholder(R.drawable.image_placeholder).into(photoView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void  setLikeButtonStatus(){
        onlineLikeReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(creativeKey).hasChild(currentUsers)){
                    noOfLikes=(int)dataSnapshot.child(creativeKey).getChildrenCount();
                    setLikerImage.setImageResource(R.drawable.liker);
                    setLikeOnCreatAct.setText(noOfLikes+" Likes");
                }
                else {
                    noOfLikes=(int)dataSnapshot.child(creativeKey).getChildrenCount();
                    setLikerImage.setImageResource(R.drawable.unlike);
                    setLikeOnCreatAct.setText(noOfLikes+" Likes");
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

                    username = dataSnapshot.child("fullname").getValue().toString();
                    userProLink = dataSnapshot.child("profileLink").getValue().toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ShowCreativeHandsFullImage.this,"Error "+e,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void shareItem(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(ShowCreativeHandsFullImage.this, BuildConfig.APPLICATION_ID + ".provider",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
