package com.nps.npsartsadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ShowAlluserProfile extends AppCompatActivity {
  private   CircularImageView showProPic;
  private   TextView showNoOfPost, showName;
  private   DatabaseReference postRef, userRef,reportRef,onlineLikeRef,noteRef;
  private   String postKeyInAllProfile;
  private   RecyclerView recycleOtherPostInProfile;
  private   String currentUserIdss, userName, userProLink;
  private   int userNoOfPost;
  private   String heading,articles;
  private   Boolean likechecker=false;
  private   Boolean bookmarkChecker=false;
  private   FirebaseAuth mAuthInAllProfile;
  private   String currentUserInAllProfile;
  private   Boolean shareButtonChecker=false;
  private Toolbar setToolbarInAllProfile;
  private   ImageView onBackAllPro;
  private   TextView setProfileTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alluser_profile);
        postKeyInAllProfile = getIntent().getExtras().get("postKey").toString();
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        showProPic = (CircularImageView) findViewById(R.id.otheruserProfilePic);
        showNoOfPost = (TextView) findViewById(R.id.othercontainNumberOfPost);
        showName = (TextView) findViewById(R.id.otherusername);
        setToolbarInAllProfile=(Toolbar)findViewById(R.id.otherprofileActivityTool);
        setSupportActionBar(setToolbarInAllProfile);
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.custom_toolbar_otherprofile,null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(view);
        setProfileTitle=(TextView)findViewById(R.id.othershowOwnerProName);
        onBackAllPro=(ImageView)findViewById(R.id.onBackOtherPro);
        onBackAllPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        setProfileTitle.setTypeface(roboto);
        recycleOtherPostInProfile = (RecyclerView) findViewById(R.id.otherprofilePostRecycle);
        recycleOtherPostInProfile.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycleOtherPostInProfile.setLayoutManager(layoutManager);
        mAuthInAllProfile=FirebaseAuth.getInstance();
        currentUserInAllProfile=mAuthInAllProfile.getCurrentUser().getUid();
        recycleOtherPostInProfile.setLayoutManager(layoutManager);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        reportRef=FirebaseDatabase.getInstance().getReference().child("Reports");
        onlineLikeRef=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        noteRef=FirebaseDatabase.getInstance().getReference().child("Notification");
        showUserProfile();
        showProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShowAlluserProfile.this, ShowPostImage.class);
                intent.putExtra("name",userName);
                intent.putExtra("url",userProLink);
                startActivity(intent);
            }
        });
    }
    private void showNumberOFPost() {
        postRef.orderByChild("uid").startAt(currentUserIdss).endAt(currentUserIdss+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int noOfPosts=(int)dataSnapshot.getChildrenCount();
                    showNoOfPost.setText(noOfPosts+" Posts");
                }
                else {
                    showNoOfPost.setText("0 Post");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUserProfile() {
        postRef.child(postKeyInAllProfile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserIdss = dataSnapshot.child("uid").getValue().toString();
                    if (currentUserIdss.isEmpty()){
                        Toast.makeText(ShowAlluserProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        userRef.child(currentUserIdss).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userName = dataSnapshot.child("fullname").getValue().toString();
                                userProLink = dataSnapshot.child("profileLink").getValue().toString();
                                showName.setText(userName);
                                setProfileTitle.setText(userName+"'s Profile");
                                Picasso.with(ShowAlluserProfile.this).load(userProLink).placeholder(R.drawable.profile).into(showProPic);
                                showAllUserPost();
                                showNumberOFPost();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void showAllUserPost() {
        Query profileOwnerPostss=postRef.orderByChild("uid").startAt(currentUserIdss).endAt(currentUserIdss+"\uf8ff");
        FirebaseRecyclerAdapter<AllPostModel, ShowPost.ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllPostModel, ShowPost.ViewHolder>
                (AllPostModel.class, R.layout.show_post_layout, ShowPost.ViewHolder.class,profileOwnerPostss) {
            @Override
            protected void populateViewHolder(final ShowPost.ViewHolder viewHolder, final AllPostModel model, int position) {

                final String postKey=getRef(position).getKey();
                viewHolder.setUserName(getApplicationContext(),model.getUid());
                viewHolder.setUserProLink(getApplicationContext(),model.getUid());
                viewHolder.setHeading(getApplicationContext(),model.getHeading(),model.getPostType());
                viewHolder.setBackGround(model.getBackGround());
                viewHolder.setArticles(getApplicationContext(),model.getArticles(),model.getPostType(),model.getMessage(),model.getImageUrl());
                //  viewHolder.setFontSize(model.getFontSize());
                viewHolder.setDate(getApplicationContext(),model.getTime(),model.getDate());
                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setHeight(model.getHeight(),model.getPostType()

                );
                viewHolder.shareButtonClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareButtonChecker=true;
                        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (shareButtonChecker.equals(true)){
                                    if (model.getPostType().equals("normal")){
                                        heading=dataSnapshot.child(postKey).child("heading").getValue().toString();
                                        articles=dataSnapshot.child(postKey).child("articles").getValue().toString();
                                        Intent intent=new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_SUBJECT,heading);
                                        intent.putExtra(Intent.EXTRA_TEXT,articles+"\n\nWritten By:"+model.getUserName());
                                        startActivity(Intent.createChooser(intent,"Share using Social Media"));
                                        shareButtonChecker=false;
                                    }
                                    if (model.getPostType().equals("message")){
                                        Toast.makeText(ShowAlluserProfile.this, "Message Type Post Can't be shared", Toast.LENGTH_SHORT).show();
                                        shareButtonChecker=false;
                                    }
                                    if (model.getPostType().equals("image")){
                                        shareItem(model.getImageUrl());
                                        shareButtonChecker=false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
                viewHolder.setphoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowAlluserProfile.this, ShowPostImage.class);
                        intent.putExtra("name",model.getUserName());
                        intent.putExtra("url",model.getImageUrl());
                        startActivity(intent);
                    }
                });

                viewHolder.showLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                        ShowLikesBottomSheet.postKey=postKey;
                        showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
                    }
                });
                viewHolder.setBottomState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowAlluserProfile.this, PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.setColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowAlluserProfile.this, PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.bookMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bookmarkChecker=true;
                        postRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (bookmarkChecker.equals(true)){
                                    if (dataSnapshot.child(postKey).hasChild(currentUserInAllProfile)){
                                        postRef.child(postKey).child(currentUserInAllProfile).removeValue();
                                        bookmarkChecker=false;
                                        String sendNotificationTo=model.getUid();
                                        String randomForNotification=postKey+currentUserInAllProfile+"fav";
                                        if (!sendNotificationTo.equals(currentUserInAllProfile)){
                                            noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification).removeValue();
                                       return;
                                        }

                                    }
                                    else {
                                        postRef.child(postKey).child(currentUserInAllProfile).setValue(currentUserInAllProfile);
                                        bookmarkChecker=false;
                                        String sendNotificationTo=model.getUid();
                                        String randomForNotification=postKey+currentUserInAllProfile+"fav";
                                        if (!sendNotificationTo.equals(currentUserInAllProfile)){
                                            if (model.getPostType().equals("normal")){
                                                HashMap putNotData=new HashMap();
                                                putNotData.put("postKey",postKey);
                                                putNotData.put("username",userName);
                                                putNotData.put("proLink",userProLink);
                                                putNotData.put("notificationType","fav");
                                                putNotData.put("heading",model.getHeading());
                                                putNotData.put("currentUserss",currentUserInAllProfile);
                                                noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                                        .updateChildren(putNotData);

                                            }
                                            if (model.getPostType().equals("message")){
                                                HashMap putNotData=new HashMap();
                                                putNotData.put("postKey",postKey);
                                                putNotData.put("username",userName);
                                                putNotData.put("proLink",userProLink);
                                                putNotData.put("notificationType","fav");
                                                putNotData.put("heading","Message");
                                                putNotData.put("currentUserss",currentUserInAllProfile);
                                                noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                                        .updateChildren(putNotData);

                                            }
                                            if (model.getPostType().equals("image")){
                                                HashMap putNotData=new HashMap();
                                                putNotData.put("postKey",postKey);
                                                putNotData.put("username",userName);
                                                putNotData.put("proLink",userProLink);
                                                putNotData.put("notificationType","fav");
                                                putNotData.put("heading","Photo");
                                                putNotData.put("currentUserss",currentUserInAllProfile);
                                                noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                                        .updateChildren(putNotData);

                                            }
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
                viewHolder.setBookMarkStatus(postKey);
                viewHolder.commentButtonClick.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommentButtomSheet commentButtomSheet=new CommentButtomSheet();
                                CommentButtomSheet.heading=model.getHeading();
                                CommentButtomSheet.postKey=postKey;
                                CommentButtomSheet.uidKey=model.getUid();
                                CommentButtomSheet.postType=model.getPostType();
                                commentButtomSheet.show(getSupportFragmentManager(),"BottomSheet");
                            }
                        });
                viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likechecker=true;
                        onlineLikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likechecker.equals(true)){
                                    if (dataSnapshot.child(postKey).hasChild(currentUserInAllProfile)){
                                        onlineLikeRef.child(postKey).child(currentUserInAllProfile).removeValue();
                                        likechecker=false;
                                        String sendNotificationTo=model.getUid();
                                        String randomForNotification=postKey+currentUserInAllProfile+"like";
                                        if (!sendNotificationTo.equals(currentUserInAllProfile)){
                                            noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification).removeValue();
                                           return;
                                        }
                                    }
                                    else {
                                        HashMap putLikeData=new HashMap();
                                        putLikeData.put("nameInLike",userName);
                                        putLikeData.put("proLinkInLike",userProLink);
                                        putLikeData.put("uid",currentUserInAllProfile);
                                        onlineLikeRef.child(postKey).child(currentUserInAllProfile).updateChildren(putLikeData);
                                        likechecker=false;
                                        String sendNotificationTo=model.getUid();
                                        String randomForNotification=postKey+currentUserInAllProfile+"like";
                                        if (!sendNotificationTo.equals(currentUserInAllProfile)){
                                            if (model.getPostType().equals("normal")){
                                                HashMap putNotData=new HashMap();
                                                putNotData.put("postKey",postKey);
                                                putNotData.put("username",userName);
                                                putNotData.put("proLink",userProLink);
                                                putNotData.put("notificationType","like");
                                                putNotData.put("heading",model.getHeading());
                                                putNotData.put("currentUserss",currentUserInAllProfile);
                                                noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                                        .updateChildren(putNotData);

                                            }
                                            if (model.getPostType().equals("message")){
                                                HashMap putNotData=new HashMap();
                                                putNotData.put("postKey",postKey);
                                                putNotData.put("username",userName);
                                                putNotData.put("proLink",userProLink);
                                                putNotData.put("notificationType","like");
                                                putNotData.put("heading","Message");
                                                putNotData.put("currentUserss",currentUserInAllProfile);
                                                noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                                        .updateChildren(putNotData);

                                            }
                                            if (model.getPostType().equals("image")){
                                                HashMap putNotData=new HashMap();
                                                putNotData.put("postKey",postKey);
                                                putNotData.put("username",userName);
                                                putNotData.put("proLink",userProLink);
                                                putNotData.put("notificationType","like");
                                                putNotData.put("heading","Photo");
                                                putNotData.put("currentUserss",currentUserInAllProfile);
                                                noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification)
                                                        .updateChildren(putNotData);

                                            }
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
                viewHolder.showOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu=new PopupMenu(ShowAlluserProfile.this,viewHolder.showOption);
                        popupMenu.getMenuInflater().inflate(R.menu.button_infleter,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item){
                                switch (item.getItemId()){
                                    case R.id.viewProfile:
                                        Intent intent=new Intent(ShowAlluserProfile.this, ShowAlluserProfile.class);
                                        intent.putExtra("postKey",postKey);
                                        startActivity(intent);
                                        break;
                                    case R.id.deletePosts:
                                        final AlertDialog.Builder builder=new AlertDialog.Builder(ShowAlluserProfile.this);
                                        builder.setTitle("Deleting Post");
                                        builder.setMessage("Are you sure to delete this post?");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                postRef.child(postKey).removeValue();
                                            }
                                        });
                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                        break;
                                    case R.id.verifyGiggleItPost:
                                        postRef.child(postKey).child("verifiedPost").setValue("yes");
                                        Toast.makeText(ShowAlluserProfile.this, "Post Verified as Giggle IT", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        recycleOtherPostInProfile.setAdapter(firebaseRecyclerAdapter);
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
            bmpUri = FileProvider.getUriForFile(ShowAlluserProfile.this, BuildConfig.APPLICATION_ID + ".provider",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
