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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ShowFavourate extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
   private RecyclerView recycleFacourite;
   private DatabaseReference postRefff,userRef,reportRef,onlineLikeRef,postRefForBookMark;
   private FirebaseAuth mAuthInHeart;
   private String currentUserInHeart;
   private Boolean likechecker=false;
   private Boolean bookmarkChecker=false;
   private String articles,heading,userProLink,userName;
   private SwipeRefreshLayout swipeRefreshLayout;
   private Boolean shareButtonChecker=false;
   private Toolbar favTool;
   RelativeLayout noFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favourate);
        mAuthInHeart=FirebaseAuth.getInstance();
        currentUserInHeart=mAuthInHeart.getCurrentUser().getUid();
        recycleFacourite=(RecyclerView)findViewById(R.id.recycleFavourate);
        recycleFacourite.setHasFixedSize(true);
        noFav=(RelativeLayout)findViewById(R.id.noFav);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        recycleFacourite.setLayoutManager(layoutManager);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.refreshFav);
        swipeRefreshLayout.setOnRefreshListener(this);
        recycleFacourite.setLayoutManager(layoutManager);
        favTool=(Toolbar)findViewById(R.id.heart);
        setSupportActionBar(favTool);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setText("Your Favourite Posts");
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        postRefff= FirebaseDatabase.getInstance().getReference().child("Posts");
        postRefForBookMark=FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        reportRef=FirebaseDatabase.getInstance().getReference().child("Reports");
        onlineLikeRef=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        getCurrentUserInfo(currentUserInHeart);
        postRefff.orderByChild(currentUserInHeart).startAt(currentUserInHeart).endAt(currentUserInHeart+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ShowFavourite();
                    noFav.setVisibility(View.GONE);
                }
                else {
                    noFav.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getCurrentUserInfo(String currenUser) {
        userRef.child(currenUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profileLink")){
                        userProLink=dataSnapshot.child("profileLink").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("fullname")){
                        userName=dataSnapshot.child("fullname").getValue().toString();
                    }
                }
                else {
                    Toast.makeText(ShowFavourate.this, "No User Data", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ShowFavourate.this,GetUserInfo.class));
                       finish();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void ShowFavourite(){
        Query userFav=postRefff.orderByChild(currentUserInHeart).startAt(currentUserInHeart).endAt(currentUserInHeart+"\uf8ff");
        FirebaseRecyclerAdapter<AllPostModel,ShowPost.ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllPostModel, ShowPost.ViewHolder>
                (AllPostModel.class,R.layout.show_post_layout,ShowPost.ViewHolder.class,userFav)
        {
            @Override
            protected void populateViewHolder(final ShowPost.ViewHolder viewHolder, final AllPostModel model, int position) {
                final String postKey=getRef(position).getKey();
                viewHolder.setUserName(getApplicationContext(),model.getUid());
                viewHolder.setUserProLink(getApplicationContext(),model.getUid());
                viewHolder.setHeading(getApplicationContext(),model.getHeading(),model.getPostType());
                viewHolder.setBackGround(model.getBackGround());
                viewHolder.setArticles(getApplicationContext(),model.getArticles(),model.getPostType(),model.getMessage(),model.getImageUrl());
                //viewHolder.setFontSize(model.getFontSize());
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
                        postRefff.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        Toast.makeText(ShowFavourate.this, "Message Type Post Can't be shared", Toast.LENGTH_SHORT).show();
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
                viewHolder.setBottomState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowFavourate.this,PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.setColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowFavourate.this,PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.setphoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowFavourate.this,ShowPostImage.class);
                        intent.putExtra("name",model.getUserName());
                        intent.putExtra("url",model.getImageUrl());
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
                                    if (dataSnapshot.child(postKey).hasChild(currentUserInHeart)){
                                        postRefForBookMark.child(postKey).child(currentUserInHeart).removeValue();
                                        bookmarkChecker=false;

                                    }
                                    else {
                                        postRefForBookMark.child(postKey).child(currentUserInHeart).setValue(currentUserInHeart);
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
                                    if (dataSnapshot.child(postKey).hasChild(currentUserInHeart)){
                                        onlineLikeRef.child(postKey).child(currentUserInHeart).removeValue();
                                        likechecker=false;
                                    }
                                    else {

                                        HashMap putLikeData=new HashMap();
                                        putLikeData.put("nameInLike",userName);
                                        putLikeData.put("proLinkInLike",userProLink);
                                        putLikeData.put("uid",currentUserInHeart);
                                        onlineLikeRef.child(postKey).child(currentUserInHeart).updateChildren(putLikeData);
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
                viewHolder.profileClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowFavourate.this,ShowAlluserProfile.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uid",model.getUid());
                        startActivity(intent);
                    }
                });
                viewHolder.userNameClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowFavourate.this,ShowAlluserProfile.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uid",model.getUid());
                        startActivity(intent);
                    }
                });
                viewHolder.showOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu=new PopupMenu(ShowFavourate.this,viewHolder.showOption);
                        popupMenu.getMenuInflater().inflate(R.menu.button_infleter,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.viewProfile:
                                        Intent intent=new Intent(ShowFavourate.this,ShowAlluserProfile.class);
                                        intent.putExtra("postKey",postKey);
                                        startActivity(intent);
                                        break;
                                    case R.id.deletePosts:
                                        final AlertDialog.Builder builder=new AlertDialog.Builder(ShowFavourate.this);
                                        builder.setTitle("Deleting Post");
                                        builder.setMessage("Are you sure to delete this post?");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                postRefff.child(postKey).removeValue();
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
                                        postRefff.child(postKey).child("verifiedPost").setValue("yes");
                                        Toast.makeText(ShowFavourate.this, "Post Verified as Giggle IT", Toast.LENGTH_SHORT).show();
                                        break;

                                }
                                return true;
                            }
                        });
                        popupMenu.show();


                    }
                });

                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        recycleFacourite.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onRefresh() {
        getCurrentUserInfo(currentUserInHeart);
        ShowFavourite();

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
            bmpUri = FileProvider.getUriForFile(ShowFavourate.this, BuildConfig.APPLICATION_ID + ".provider",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
