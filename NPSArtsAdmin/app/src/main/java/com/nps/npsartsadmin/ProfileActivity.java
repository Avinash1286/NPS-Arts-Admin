package com.nps.npsartsadmin;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
public class ProfileActivity extends AppCompatActivity {
   private CircularImageView showProPicInProfile;
   private TextView showNoOfPostInProfile, showNameInProfile,showEmail;
   private DatabaseReference postRef, userRef,reportRef,onlineLikeRef;
   private RecyclerView recyclePostInProfile;
   private String currentUserId, userName, userProLink;
   private int userNoOfPost;
   private String heading,articles,email;
   private Boolean likechecker=false;
   private Boolean bookmarkChecker=false;
   private FirebaseAuth mAuthInAllProfile;
   private TextView editProfileButton,AddPost;
   private Boolean shareButtonChecker=false;
   private Toolbar addActtoolbar;
   private TextView editProfil,showFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        showProPicInProfile=(CircularImageView)findViewById(R.id.userProfilePic);
        showNoOfPostInProfile=(TextView)findViewById(R.id.containNumberOfPost);
        recyclePostInProfile=(RecyclerView)findViewById(R.id.profilePostRecycle);
        recyclePostInProfile.setHasFixedSize(true);
          editProfileButton=(TextView)findViewById(R.id.editButton);
          showFav=(TextView)findViewById(R.id.favOpen);
          showFav.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(ProfileActivity.this,ShowFavourate.class));
              }
          });
          editProfileButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(ProfileActivity.this, editActivity.class));
              }
          });
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        addActtoolbar=(Toolbar)findViewById(R.id.profileActivityTool);
        setSupportActionBar(addActtoolbar);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.my_profile_tool_layout,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.myprofileheading);
        headingPendingPost.setTypeface(roboto);
        ImageView openComplain=(ImageView)view.findViewById(R.id.complainBox);
        ImageView openPending=(ImageView)view.findViewById(R.id.compOpen);
        openComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           onBackPressed();
            }
        });
       openPending.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(ProfileActivity.this,DigitalComplainBox.class));
           }
       });
        recyclePostInProfile.setLayoutManager(layoutManager);
        showEmail=(TextView)findViewById(R.id.userEmail);
        showNameInProfile=(TextView)findViewById(R.id.username);
        mAuthInAllProfile=FirebaseAuth.getInstance();
        try {
            currentUserId=mAuthInAllProfile.getCurrentUser().getUid();
        }
        catch (Exception e){
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        onlineLikeRef=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        if (currentUserId.isEmpty()){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this,GetUserInfo.class));
            finish();
            return;
        }
        else {

            showUserProfile();
            showNumberOFPost();
        }
        showProPicInProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,ShowPostImage.class);
                intent.putExtra("name",userName);
                intent.putExtra("url",userProLink);
                startActivity(intent);
            }
        });
    }

    private void showNumberOFPost() {
        postRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int noOfPosts=(int)dataSnapshot.getChildrenCount();
                    showNoOfPostInProfile.setText(noOfPosts+" Posts");
                }
                else {
                    showNoOfPostInProfile.setText("0 Post");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void ShowAllProfilePost(){
        Query onleyIdOwnerPost=postRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId+"\uf8ff");
        FirebaseRecyclerAdapter<AllPostModel,ShowPost.ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllPostModel, ShowPost.ViewHolder>
                (AllPostModel.class,R.layout.show_post_layout,ShowPost.ViewHolder.class,onleyIdOwnerPost) {
            @Override
            protected void populateViewHolder(final ShowPost.ViewHolder viewHolder,final AllPostModel model, int position) {
                final String postKey=getRef(position).getKey();
                viewHolder.setUserName(getApplicationContext(),model.getUid());
                viewHolder.setUserProLink(getApplicationContext(),model.getUid());
                viewHolder.setHeading(getApplicationContext(),model.getHeading(),model.getPostType());
                viewHolder.setBackGround(model.getBackGround());
                viewHolder.setArticles(getApplicationContext(),model.getArticles(),model.getPostType(),model.getMessage(),model.getImageUrl());
                viewHolder.setDate(getApplicationContext(),model.getTime(),model.getDate());
                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setHeight(model.getHeight(),model.getPostType());
                viewHolder.showLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                        ShowLikesBottomSheet.postKey=postKey;
                        showLikesBottomSheet.show(getSupportFragmentManager(),"LikeBottomSheet");
                    }
                });
                viewHolder.shareButtonClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareButtonChecker=true;
                        postRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (shareButtonChecker.equals(true)){
                                    if (model.getPostType().equals("normal")){
                                        heading=dataSnapshot.child(postKey).child("heading").getValue().toString();
                                        articles=dataSnapshot.child(postKey).child("articles").getValue().toString();
                                        Intent intent=new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_SUBJECT,heading);
                                        intent.putExtra(Intent.EXTRA_TEXT,articles+"\n\nWritten By: "+model.getUserName());
                                        startActivity(Intent.createChooser(intent,"Share using Social Media"));
                                        shareButtonChecker=false;
                                    }
                                    if (model.getPostType().equals("message")){
                                        Toast.makeText(ProfileActivity.this, "Message Type Post Can't be shared", Toast.LENGTH_SHORT).show();
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
                        Intent intent=new Intent(ProfileActivity.this,ShowPostImage.class);
                        intent.putExtra("name",model.getUserName());
                        intent.putExtra("url",model.getImageUrl());
                        startActivity(intent);
                    }
                });

                viewHolder.setBottomState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ProfileActivity.this,PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.setColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ProfileActivity.this,PostDetails.class);
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
                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)){
                                        postRef.child(postKey).child(currentUserId).removeValue();
                                        bookmarkChecker=false;
                                    }
                                    else {
                                        postRef.child(postKey).child(currentUserId).setValue(currentUserId);
                                        bookmarkChecker=false;
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
                viewHolder.commentButtonClick.setOnClickListener(new View.OnClickListener() {
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
                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)){
                                        onlineLikeRef.child(postKey).child(currentUserId).removeValue();
                                        likechecker=false;
                                    }
                                    else {
                                        HashMap putLikeData=new HashMap();
                                        putLikeData.put("nameInLike",userName);
                                        putLikeData.put("proLinkInLike",userProLink);
                                        putLikeData.put("uid",currentUserId);
                                        onlineLikeRef.child(postKey).child(currentUserId).updateChildren(putLikeData);
                                        likechecker=false;
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
                        if (model.getPostType().equals("image") || model.getPostType().equals("message")){
                            final PopupMenu popupMenu=new PopupMenu(ProfileActivity.this,viewHolder.showOption);
                            popupMenu.getMenuInflater().inflate(R.menu.deleteoption,popupMenu.getMenu());
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()){
                                        case R.id.delete:
                                            final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
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
                                    }
                                    return true;
                                }
                            });
                            popupMenu.show();
                        }
                        else {
                            final PopupMenu popupMenu=new PopupMenu(ProfileActivity.this,viewHolder.showOption);
                            popupMenu.getMenuInflater().inflate(R.menu.profile_in_post,popupMenu.getMenu());
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()){
                                        case R.id.delete:
                                            final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
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
                                        case R.id.edit:
                                            UpdatePost updatePost=new UpdatePost();
                                            FragmentManager fragmentManager=getSupportFragmentManager();
                                            FragmentTransaction transaction=fragmentManager.beginTransaction();
                                            transaction.add(R.id.holdEditFrame,updatePost);
                                            UpdatePost.postKey=postKey;
                                            UpdatePost.previousPost=model.getArticles();
                                            UpdatePost.previousTitle=model.getHeading();
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                            break;
                                    }
                                    return true;
                                }
                            });
                            popupMenu.show();
                        }

                    }
                });
            }
        };
         recyclePostInProfile.setAdapter(firebaseRecyclerAdapter);
    }

    private void showUserProfile() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ShowAllProfilePost();
                userName = String.valueOf(dataSnapshot.child("fullname").getValue());
                userProLink = String.valueOf(dataSnapshot.child("profileLink").getValue());
                if (userName.isEmpty()){
                     startActivity(new Intent(ProfileActivity.this,GetUserInfo.class));
                     finish();
                    return;
                }
                if (userProLink.isEmpty()){
                    startActivity(new Intent(ProfileActivity.this,GetUserInfo.class));
                    finish();
                    return;
                }
                else {

                    showNameInProfile.setText(userName);
                    Picasso.with(ProfileActivity.this).load(userProLink).placeholder(R.drawable.profile).into(showProPicInProfile);

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
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
