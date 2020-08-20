package com.nps.npsartsadmin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ShowPost extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {
private Toolbar toolbar;
private   BottomNavigationViewEx bottomNavigationViewEx;
private   ImageView imageView,comLogo;
private   RecyclerView recycleAllThePost;
private   DatabaseReference noteRef, userRef,postRef,reportRef,onlineLikeRef,onlineCommentRef,postRefForBookMark;
private   FirebaseAuth mAuth;
private   String currentUser;
private   String userProLink,userName;
private   CircularImageView mainProfile;
private   SwipeRefreshLayout swipeRefreshLayout;
private   Boolean likechecker=false;
private   Boolean bookmarkChecker=false;
private   String heading, articles;
private   Boolean shareChecker=false;
private   Animation setAnim;
private   TextView headings;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
private ShimmerFrameLayout setShimmer;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_show_post);
     if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
          NotificationChannel notificationChannel=new NotificationChannel("nps","nps", NotificationManager.IMPORTANCE_DEFAULT);
          NotificationManager manager=getSystemService(NotificationManager.class);
          manager.createNotificationChannel(notificationChannel);
      }
      setShimmer=(ShimmerFrameLayout)findViewById(R.id.showPostShimmer);
      setShimmer.setVisibility(View.VISIBLE);
      setShimmer.startShimmer();
   //   FirebaseMessaging.getInstance().subscribeToTopic("general");
      Window window=getWindow();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
      }
      FirebaseMessaging.getInstance().subscribeToTopic("pendingNotify");
      swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);
      recycleAllThePost=(RecyclerView)findViewById(R.id.mainPost);
      recycleAllThePost.setHasFixedSize(true);
      LinearLayoutManager layoutManager=new LinearLayoutManager(this);
      layoutManager.setReverseLayout(true);
      layoutManager.setStackFromEnd(true);
      recycleAllThePost.setLayoutManager(layoutManager);
      bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomnav);
      bottomNavigationViewEx.setItemIconTintList(null);
      mAuth=FirebaseAuth.getInstance();
      try {
          currentUser=mAuth.getCurrentUser().getUid();
      }
      catch (Exception e){
          startActivity(new Intent(ShowPost.this, LogInOptions.class));
          finish();
      }
      userRef= FirebaseDatabase.getInstance().getReference().child("Users");
      postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
      reportRef=FirebaseDatabase.getInstance().getReference().child("Reports");
      onlineLikeRef=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
      onlineCommentRef=FirebaseDatabase.getInstance().getReference().child("CommentsOfPosts");
      postRefForBookMark=FirebaseDatabase.getInstance().getReference().child("Posts");
      noteRef=FirebaseDatabase.getInstance().getReference().child("Notification");
      toolbar=(Toolbar)findViewById(R.id.postToolbar);
      setSupportActionBar(toolbar);
      ActionBar actionBar=getSupportActionBar();
      actionBar.setDisplayShowCustomEnabled(true);
      LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View actionview=layoutInflater.inflate(R.layout.custom_action_bar,null);
      actionBar.setCustomView(actionview);
     // mainProfile=(CircularImageView)findViewById(R.id.mainPro);
      imageView=(ImageView)findViewById(R.id.startPostActivity);
      comLogo=(ImageView)findViewById(R.id.compButton);
      headings=(TextView)findViewById(R.id.appheading);
      Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
      headings.setTypeface(roboto);
      comLogo.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
          CompBottomSheet compBottomSheet=new CompBottomSheet();
          compBottomSheet.show(getSupportFragmentManager(),"Competition");
          }
      });
      getCurrentUserInfo();
      imageView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
          CreatePostBottomSheet createPostBottomSheet=new CreatePostBottomSheet();
          createPostBottomSheet.show(getSupportFragmentManager(),"CreatePost");

          }
      });
     swipeRefreshLayout.setOnRefreshListener(this);
      setUpBottomNav();
      DisplayAllUsersPost();

  }
  private void getCurrentUserInfo() {
      userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                  if (dataSnapshot.hasChild("profileLink")){
                      userProLink=dataSnapshot.child("profileLink").getValue().toString();
              //        Picasso.with(ShowPost.this).load(userProLink).placeholder(R.drawable.profile).into(mainProfile);
                  }
                 if (dataSnapshot.hasChild("fullname")){
                     userName=dataSnapshot.child("fullname").getValue().toString();
                 }
                 else {
                     startActivity(new Intent(ShowPost.this, GetUserInfo.class));
                     finish();
                 }
              }
          }
          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
  }
  private void DisplayAllUsersPost() {
      Query postInDecendingOrder=postRef.orderByChild("counter");
      FirebaseRecyclerAdapter<AllPostModel,ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllPostModel, ViewHolder>
          (AllPostModel.class, R.layout.show_post_layout,ViewHolder.class,postInDecendingOrder) {
      @Override
      protected void populateViewHolder(final ViewHolder viewHolder, final AllPostModel model, final int position) {
          final String postKey=getRef(position).getKey();
          viewHolder.setUserName(getApplicationContext(),model.getUid());
          viewHolder.setUserProLink(getApplicationContext(),model.getUid());
          viewHolder.setHeading(getApplicationContext(),model.getHeading(),model.getPostType());
          viewHolder.setBackGround(model.getBackGround());
          viewHolder.setArticles(getApplicationContext(),model.getArticles(),model.getPostType(),model.getMessage(),model.getImageUrl());
       //   viewHolder.setFontSize(model.getFontSize());
          viewHolder.setDate(getApplicationContext(),model.getTime(),model.getDate());
          viewHolder.setLikeButtonStatus(postKey);
          viewHolder.setHeight(model.getHeight(),model.getPostType());
          viewHolder.setVerifiedLogo(model.getUid());
          viewHolder.shareButtonClick.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
               shareChecker=true;
                      postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             if (shareChecker.equals(true)){
                                 if (model.getPostType().equals("normal")){
                                     heading=dataSnapshot.child(postKey).child("heading").getValue().toString();
                                     articles=dataSnapshot.child(postKey).child("articles").getValue().toString();
                                     Intent intent=new Intent(Intent.ACTION_SEND);
                                     intent.setType("text/plain");
                                     intent.putExtra(Intent.EXTRA_SUBJECT,heading);
                                     intent.putExtra(Intent.EXTRA_TEXT,articles+"\n\nWritten By:"+model.getUserName());
                                     startActivity(Intent.createChooser(intent,"Share using Social Media"));
                                     shareChecker=false;
                                 }
                                 if (model.getPostType().equals("message")){
                                     Toast.makeText(ShowPost.this, "Message Type Post Can't be shared", Toast.LENGTH_SHORT).show();
                                     shareChecker=false;
                                 }
                                 if (model.getPostType().equals("image")){
                                    shareItem(model.getImageUrl());
                                 }
                             }
                          }
                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {
                          }
                      });
              }
          });
          viewHolder.setBottomState.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent=new Intent(ShowPost.this, PostDetails.class);
                  intent.putExtra("postKey",postKey);
                  intent.putExtra("uidKey",model.getUid());
                  intent.putExtra("heading",model.getHeading());
                  startActivity(intent);
              }
          });
          viewHolder.setColor.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent=new Intent(ShowPost.this, PostDetails.class);
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
                  postRefForBookMark.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                          if (bookmarkChecker.equals(true)){
                              if (dataSnapshot.child(postKey).hasChild(currentUser)){
                                  postRefForBookMark.child(postKey).child(currentUser).removeValue();
                                    bookmarkChecker=false;
                                  String sendNotificationTo=model.getUid();
                                  String randomForNotification=postKey+currentUser+"fav";
                                  if (!sendNotificationTo.equals(currentUser)){
                                      noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification).removeValue();
                                 return;
                                  }
                              }
                              else {
                                  postRefForBookMark.child(postKey).child(currentUser).setValue(currentUser);
                                  bookmarkChecker=false;
                                  String sendNotificationTo=model.getUid();
                                  String randomForNotification=postKey+currentUser+"fav";
                                  if (!sendNotificationTo.equals(currentUser)){
                                      if (model.getPostType().equals("normal")){
                                          HashMap putNotData=new HashMap();
                                          putNotData.put("postKey",postKey);
                                          putNotData.put("username",userName);
                                          putNotData.put("proLink",userProLink);
                                          putNotData.put("notificationType","fav");
                                          putNotData.put("heading",model.getHeading());
                                          putNotData.put("currentUserss",currentUser);
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
                                          putNotData.put("currentUserss",currentUser);
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
                                          putNotData.put("currentUserss",currentUser);
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
                  Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.likeanim);
                  animation.setFillAfter(true);
                  viewHolder.likeButton.setAnimation(animation);
                  onlineLikeRef.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if (likechecker.equals(true)){
                              if (dataSnapshot.child(postKey).hasChild(currentUser)){
                                  String sendNotificationTo=model.getUid();
                                  String randomForNotification=postKey+currentUser+"likes";
                                     onlineLikeRef.child(postKey).child(currentUser).removeValue();
                                    likechecker=false;
                                    if (!sendNotificationTo.equals(currentUser)){
                                        noteRef.child(sendNotificationTo).child("AllNotification").child(randomForNotification).removeValue();
                                    return;
                                    }
                              }
                              else {
                                  String sendNotificationTo=model.getUid();
                                  String randomForNotification=postKey+currentUser+"likes";
                                  likechecker=false;
                                  HashMap putLikeData=new HashMap();
                                  putLikeData.put("nameInLike",userName);
                                  putLikeData.put("proLinkInLike",userProLink);
                                  putLikeData.put("uid",currentUser);
                                  onlineLikeRef.child(postKey).child(currentUser).updateChildren(putLikeData);
                               if (!sendNotificationTo.equals(currentUser)){
                                   if (model.getPostType().equals("normal")){
                                       HashMap putNotData=new HashMap();
                                       putNotData.put("postKey",postKey);
                                       putNotData.put("username",userName);
                                       putNotData.put("proLink",userProLink);
                                       putNotData.put("notificationType","like");
                                       putNotData.put("heading",model.getHeading());
                                       putNotData.put("currentUserss",currentUser);
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
                                       putNotData.put("currentUserss",currentUser);
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
                                       putNotData.put("currentUserss",currentUser);
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
          viewHolder.setphoto.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent=new Intent(ShowPost.this, ShowPostImage.class);
                  intent.putExtra("name",model.getUserName());
                  intent.putExtra("url",model.getImageUrl());
                  startActivity(intent);
              }
          });
          viewHolder.profileClick.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                      Intent intent=new Intent(ShowPost.this, ShowAlluserProfile.class);
                      intent.putExtra("postKey",postKey);
                      startActivity(intent);
              }
          });
          viewHolder.userNameClick.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent=new Intent(ShowPost.this, ShowAlluserProfile.class);
                  intent.putExtra("postKey",postKey);
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

          viewHolder.showOption.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  final PopupMenu popupMenu=new PopupMenu(ShowPost.this,viewHolder.showOption);
                  popupMenu.getMenuInflater().inflate(R.menu.button_infleter,popupMenu.getMenu());
                  popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem item) {
                          switch (item.getItemId()){
                              case R.id.viewProfile:
                                  Intent intent=new Intent(ShowPost.this, ShowAlluserProfile.class);
                                  intent.putExtra("postKey",postKey);
                                  startActivity(intent);
                                  break;
                              case R.id.deletePosts:
                                  final AlertDialog.Builder builder=new AlertDialog.Builder(ShowPost.this);
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
                                  Toast.makeText(ShowPost.this, "Post Verified as Giggle IT", Toast.LENGTH_SHORT).show();
                                  break;
                          }
                          return true;
                      }
                  });
                  popupMenu.show();
              }
          });
         setShimmer.setVisibility(View.GONE);
         setShimmer.stopShimmer();
          if (swipeRefreshLayout.isRefreshing()){
              swipeRefreshLayout.setRefreshing(false);
          }
      }
  };
  recycleAllThePost.setAdapter(firebaseRecyclerAdapter);
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
      MenuItem menuItem=menu.getItem(0);
      menuItem.setChecked(true);
      menuItem.setIcon(R.drawable.final_home_on);
  }
  public void enableNavigation(){
      bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
              switch (menuItem.getItemId()){
                  case R.id.home:
                     break;
                  case R.id.postsearch:
                      startActivity(new Intent(ShowPost.this, SearchPeople.class));
                      overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                      finish();
                      break;
                  case R.id.offlineArticles:
                      startActivity(new Intent(ShowPost.this, MainActivity.class));
                      overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                      finish();
                      break;
                  case R.id.postlikes:
                      startActivity(new Intent(ShowPost.this, HeartActivity.class));
                      overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                      finish();
                      break;
                  case R.id.postprofile:
                      startActivity(new Intent(ShowPost.this, AdminControll.class));
                      overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                      finish();
                      break;
              }
return false;
          }
      });
  }
  @Override
  public void onRefresh() {
      swipeRefreshLayout.setRefreshing(true);
      DisplayAllUsersPost();
  }
  public static class ViewHolder extends RecyclerView.ViewHolder{
      View mView;
      int noOfLikes;
      RelativeLayout setBottomState;
      RelativeLayout heightofMiddle;
      DatabaseReference onlineLikeReff,postRefg,userReff;
      TextView setColor,dateandTime,userNameClick,showLikes,setMessage;
      String currentUser;
      ImageView likeButton,showOption,commentButtonClick,shareButtonClick,bookMark,showVerified,setphoto;
      CircularImageView  profileClick;
      public ViewHolder(@NonNull View itemView) {
          super(itemView);
          mView=itemView;
          setBottomState=(RelativeLayout)mView.findViewById(R.id.bottomLayout);
          heightofMiddle=(RelativeLayout)mView.findViewById(R.id.middle);
          setphoto=(ImageView)mView.findViewById(R.id.showCreativePost);
          setMessage=(TextView) mView.findViewById(R.id.containMessagesInPost);
          setColor=(TextView)mView.findViewById(R.id.containArtss);
          dateandTime=(TextView)mView.findViewById(R.id.dateandtime);
          userNameClick=(TextView)mView.findViewById(R.id.userNameHere);
          showLikes=(TextView)mView.findViewById(R.id.likesText);
          likeButton=(ImageView)mView.findViewById(R.id.likeButtonClick);
          showOption=(ImageView)mView.findViewById(R.id.options);
          commentButtonClick=(ImageView)mView.findViewById(R.id.commentButton);
          shareButtonClick=(ImageView)mView.findViewById(R.id.shareButton);
          bookMark=(ImageView)mView.findViewById(R.id.bookmarkButton);
          profileClick=(CircularImageView) mView.findViewById(R.id.userProInMain);
          showVerified=(ImageView)mView.findViewById(R.id.verified);
          onlineLikeReff=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
          currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
          postRefg=FirebaseDatabase.getInstance().getReference().child("Posts");
          userReff=FirebaseDatabase.getInstance().getReference().child("Users");
      }

      public void setBookMarkStatus(final String postKey){
          postRefg.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (dataSnapshot.child(postKey).hasChild(currentUser)){
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

           if (dataSnapshot.child(postKey).hasChild(currentUser)){
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


      public void setUserName(final Context context,String userId) {
          userReff.child(userId).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String name=dataSnapshot.child("fullname").getValue().toString();
                  TextView userNameInMainPost=(TextView)mView.findViewById(R.id.userNameHere);
                  Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
                  userNameInMainPost.setTypeface(roboto);
                  userNameInMainPost.setText(name);
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
      }
      public void setUserProLink(final Context context,String userId) {
          userReff.child(userId).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String imageLink=dataSnapshot.child("profileLink").getValue().toString();
                  CircularImageView circularImageViewProfile=(CircularImageView) mView.findViewById(R.id.userProInMain);
                  Picasso.with(context).load(imageLink).placeholder(R.drawable.profile).into(circularImageViewProfile);
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {
              }
          });
      }
      public void setArticles(Context context,String articles,String type,String message,String imgUrl) {
          if (type.equals("message")){
              setMessage.setVisibility(View.VISIBLE);
              setphoto.setVisibility(View.GONE);
              setColor.setVisibility(View.GONE);
              Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTRegular.otf");
              setMessage.setTypeface(roboto);
              setMessage.setText(message);
              setMessage.setTextSize(25);
              setMessage.setHeight(400);
              setMessage.setGravity(Gravity.CENTER);
          }
          if (type.equals("image")){
              setMessage.setVisibility(View.GONE);
              setColor.setVisibility(View.GONE);
              setphoto.setVisibility(View.VISIBLE);
              Picasso.with(context).load(imgUrl).placeholder(R.drawable.grayback).into(setphoto);
          }
          if (type.equals("normal")){
              setMessage.setVisibility(View.GONE);
              setphoto.setVisibility(View.GONE);
              setColor.setVisibility(View.VISIBLE);
              Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTRegular.otf");
              setColor.setTypeface(roboto);
              setColor.setText(articles);
          }
      }
      public void setBackGround(String backGround) {

          RelativeLayout backSet=(RelativeLayout)mView.findViewById(R.id.middle);
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

   /*   public void setFontSize(String fontSize) {
          setColor.setTextSize(Integer.valueOf(fontSize));
      }
*/
      public void setHeading(Context context,String heading,String type) {
          TextView setHead=(TextView)mView.findViewById(R.id.userHeadingHere);
          if (type.equals("normal")){
              setHead.setVisibility(View.VISIBLE);
              Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
              setHead.setTypeface(roboto);
              setHead.setText("Topic: "+heading);
          }
          if (type.equals("image")){
              setHead.setVisibility(View.VISIBLE);
              Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTRegular.otf");
              setHead.setTypeface(roboto);
              setHead.setText(heading);
          }
         if (type.equals("message")){
             setHead.setVisibility(View.GONE);
         }

      }
      public void setDate(Context context,String time,String date) {
          Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/FacebookNarrow_A_Rg.ttf");
          dateandTime.setTypeface(roboto);
          dateandTime.setText(time+" "+date);
      }

      public void setHeight(String height,String postType) {
          RelativeLayout setBottom=(RelativeLayout)mView.findViewById(R.id.bottomLayout);
          if (postType.equals("normal")){
              if (Integer.valueOf(height)>=600){
                  setBottom.setVisibility(View.VISIBLE);
              }
              else {
                  setBottom.setVisibility(View.GONE);
              }
          }
          else {
              setBottom.setVisibility(View.GONE);
          }
      }

      public void setMessage(String message) {


      }
      public void setVerifiedLogo(String uid){
          userReff.child(uid).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String verifiedValue=dataSnapshot.child("verified").getValue().toString();
               if (verifiedValue.equals("yes")){
                   showVerified.setVisibility(View.VISIBLE);
               }
               else {
                   showVerified.setVisibility(View.GONE);
               }
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {
              }
          });
      }
  }
  @Override
  protected void onStart() {
      super.onStart();
      FirebaseUser firebaseUser=mAuth.getCurrentUser();
      if (firebaseUser!=null){
          userRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (!dataSnapshot.hasChild(currentUser)){
                      startActivity(new Intent(ShowPost.this, GetUserInfo.class));
                      finish();
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
      }
      else {
          startActivity(new Intent(ShowPost.this, LogInOptions.class));
          finish();
      }

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
            bmpUri = FileProvider.getUriForFile(ShowPost.this, BuildConfig.APPLICATION_ID + ".provider",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
