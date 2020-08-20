package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ShowPendingPosts extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference userRef,pendingPost,postRef;
    RecyclerView recyclePending;
    public long postCounter;
    Toolbar toolbarVerification;
    FirebaseAuth mAuth;
    String currentUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pending_posts);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshShowPenddingPost);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        toolbarVerification=(Toolbar)findViewById(R.id.propenverificationTool);
        setSupportActionBar(toolbarVerification);
        mAuth=FirebaseAuth.getInstance();
        currentUsers=mAuth.getCurrentUser().getUid();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclePending=(RecyclerView)findViewById(R.id.propenrecyclePendingPosts);
        recyclePending.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclePending.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        pendingPost=FirebaseDatabase.getInstance().getReference().child("PendingPosts");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        DisplayAllPendingPosts();
    }
 public void    DisplayAllPendingPosts(){
     Query pendingFilter=pendingPost.orderByChild("uid").startAt(currentUsers).endAt(currentUsers+"\uf8ff");
     FirebaseRecyclerAdapter<AllPostModel,ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllPostModel, ViewHolder>
             (AllPostModel.class,R.layout.show_post_layout,ViewHolder.class,pendingFilter) {
         @Override
         protected void populateViewHolder(final ViewHolder viewHolder, AllPostModel allPostModel, int i) {
           final String postKey=getRef(i).getKey();
             viewHolder.setUserName(getApplicationContext(),allPostModel.getUserName());
           viewHolder.setPhotos(getApplicationContext(),allPostModel.getImageUrl());
           viewHolder.setUserProLink(getApplicationContext(),allPostModel.getUserProLink());
           viewHolder.setHeadings(getApplicationContext(),allPostModel.getHeading());
           viewHolder.setVerifiedLogo(allPostModel.getUid());
           viewHolder.setDate(getApplicationContext(),allPostModel.getTime(),allPostModel.getDate());
           viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(ShowPendingPosts.this, "You can't like pending post", Toast.LENGTH_SHORT).show();
               }
           });
           viewHolder.commentButtonClick.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(ShowPendingPosts.this, "You can't comment pending post", Toast.LENGTH_SHORT).show();
               }
           });
           viewHolder.shareButtonClick.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(ShowPendingPosts.this, "You can't share pending post", Toast.LENGTH_SHORT).show();
               }
           });
           viewHolder.bookMark.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(ShowPendingPosts.this, "You can't bookmark pending post", Toast.LENGTH_SHORT).show();
               }
           });
             viewHolder.showOption.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     final PopupMenu popupMenu=new PopupMenu(ShowPendingPosts.this,viewHolder.showOption);
                     popupMenu.getMenuInflater().inflate(R.menu.button_infleter,popupMenu.getMenu());
                     popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                         @Override
                         public boolean onMenuItemClick(MenuItem item) {
                             switch (item.getItemId()){
                                 case R.id.delete_pending:
                                     final AlertDialog.Builder builder=new AlertDialog.Builder(ShowPendingPosts.this);
                                     builder.setTitle("Deleting Post");
                                     builder.setMessage("Are you sure to delete this post?");
                                     builder.setCancelable(false);
                                     builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             pendingPost.child(postKey).removeValue();
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
             });
         }
     };
     recyclePending.setAdapter(firebaseRecyclerAdapter);
     if (swipeRefreshLayout.isRefreshing()){
         swipeRefreshLayout.setRefreshing(false);
     }

    }

    @Override
    public void onRefresh() {
        DisplayAllPendingPosts();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        int noOfLikes;
        RelativeLayout setBottomState;
        RelativeLayout heightofMiddle;
        DatabaseReference onlineLikeReff,pendingRef,userReff;
        TextView setColor,dateandTime,userNameClick,showLikes,setMessage;
        String currentUser;
        ImageView likeButton,showOption,commentButtonClick,shareButtonClick,bookMark,showVerified,setphoto;
        CircularImageView profileClick;
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
            currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
            pendingRef=FirebaseDatabase.getInstance().getReference().child("PendingPosts");
            userReff=FirebaseDatabase.getInstance().getReference().child("Users");
        }
        public void setUserName(Context context, String userName) {
            TextView userNameInMainPost = (TextView) mView.findViewById(R.id.userNameHere);
            Typeface roboto = Typeface.createFromAsset(context.getAssets(), "font/AvenyTMedium.otf");
            userNameInMainPost.setTypeface(roboto);
            userNameInMainPost.setText(userName);
        }
        public void setUserProLink(Context context, String userProLink) {
            CircularImageView circularImageViewProfile = (CircularImageView) mView.findViewById(R.id.userProInMain);
            Picasso.with(context).load(userProLink).placeholder(R.drawable.profile).into(circularImageViewProfile);
        }
        public void setHeadings(Context context, String heading) {
            TextView setHead = (TextView) mView.findViewById(R.id.userHeadingHere);
            Typeface roboto = Typeface.createFromAsset(context.getAssets(), "font/AvenyTMedium.otf");
            setHead.setTypeface(roboto);
            setHead.setText(heading);
        }
        public void setDate(Context context, String time, String date) {
            Typeface roboto = Typeface.createFromAsset(context.getAssets(), "font/FacebookNarrow_A_Rg.ttf");
            dateandTime.setTypeface(roboto);
            dateandTime.setText(time + " " + date);
        }
        public void setPhotos(Context context,String url) {
            setphoto.setVisibility(View.VISIBLE);
            Picasso.with(context).load(url).placeholder(R.drawable.image_placeholder).into(setphoto);
        }
        public void setVerifiedLogo(String uid) {
            userReff.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String verifiedValue = dataSnapshot.child("verified").getValue().toString();
                    if (verifiedValue.equals("yes")) {
                        showVerified.setVisibility(View.VISIBLE);
                    } else {
                        showVerified.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
