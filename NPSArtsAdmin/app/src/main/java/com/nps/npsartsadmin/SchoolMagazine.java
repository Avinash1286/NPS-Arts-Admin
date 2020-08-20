package com.nps.npsartsadmin;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
public class SchoolMagazine extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public SchoolMagazine() {
    }
    RecyclerView recycleGiigleIT;
    Toolbar giggleITTool;
    SwipeRefreshLayout refreshGiggleIT;
    private DatabaseReference postRefff,userRef,reportRef,onlineLikeRef,postRefForBookMark;
    private FirebaseAuth mAuthInHeart;
    private String currentUserInHeart;
    private Boolean likechecker=false;
    TextView nopublish;
    private Boolean bookmarkChecker=false;
    private String articles,heading,userProLink,userName;
    private Boolean shareButtonChecker=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_school_magazine, container, false);
        recycleGiigleIT=(RecyclerView)view.findViewById(R.id.recycleGiggleItPost);
        recycleGiigleIT.setHasFixedSize(true);
        recycleGiigleIT.setLayoutManager(new LinearLayoutManager(getActivity()));
        giggleITTool=(Toolbar)view.findViewById(R.id.giggleItTool);
        refreshGiggleIT=(SwipeRefreshLayout)view.findViewById(R.id.refreshGiggleIt);
        refreshGiggleIT.setOnRefreshListener(this);
        mAuthInHeart= FirebaseAuth.getInstance();
        nopublish=(TextView)view.findViewById(R.id.nopublishbutton);
        currentUserInHeart=mAuthInHeart.getCurrentUser().getUid();
        postRefff= FirebaseDatabase.getInstance().getReference().child("Posts");
        postRefForBookMark=FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        reportRef=FirebaseDatabase.getInstance().getReference().child("Reports");
        onlineLikeRef=FirebaseDatabase.getInstance().getReference().child("LikeOfPosts");
        getCurrentUserInfo(currentUserInHeart);
        nopublish.setVisibility(View.VISIBLE);
        ShowFavourite();
        return view; 
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
                    Toast.makeText(getActivity(), "No User Data", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(),GetUserInfo.class));
                    getActivity().finish();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ShowFavourite(){
        Query userFav=postRefff.orderByChild("verifiedPost").startAt("yes").endAt("yes"+"\uf8ff");
        FirebaseRecyclerAdapter<AllPostModel,ShowPost.ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllPostModel, ShowPost.ViewHolder>
                (AllPostModel.class,R.layout.show_post_layout,ShowPost.ViewHolder.class,userFav)
        {
            @Override
            protected void populateViewHolder(final ShowPost.ViewHolder viewHolder, final AllPostModel model, int position) {
                final String postKey=getRef(position).getKey();
                viewHolder.setUserName(getActivity(),model.getUid());
                viewHolder.setUserProLink(getActivity(),model.getUid());
                viewHolder.setHeading(getActivity(),model.getHeading(),model.getPostType());
                viewHolder.setBackGround(model.getBackGround());
                viewHolder.setArticles(getActivity(),model.getArticles(),model.getPostType(),model.getMessage(),model.getImageUrl());
                //viewHolder.setFontSize(model.getFontSize());
                viewHolder.setDate(getActivity(),model.getTime(),model.getDate());
                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setHeight(model.getHeight(),model.getPostType());
                viewHolder.showLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowLikesBottomSheet showLikesBottomSheet=new ShowLikesBottomSheet();
                        ShowLikesBottomSheet.postKey=postKey;
                        showLikesBottomSheet.show(getActivity().getSupportFragmentManager(),"LikeBottomSheet");
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
                                        Toast.makeText(getActivity(), "Message Type Post Can't be shared", Toast.LENGTH_SHORT).show();
                                        shareButtonChecker=false;
                                    }
                                    if (model.getPostType().equals("image")){
                                        Toast.makeText(getActivity(), "Share Image From Main Post", Toast.LENGTH_LONG).show();
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
                        Intent intent=new Intent(getActivity(),PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.setColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),PostDetails.class);
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("uidKey",model.getUid());
                        intent.putExtra("heading",model.getHeading());
                        startActivity(intent);
                    }
                });
                viewHolder.setphoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ShowPostImage.class);
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
                        commentButtomSheet.show(getActivity().getSupportFragmentManager(),"BottomSheet");
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
                        Intent intent=new Intent(getActivity(),ShowAlluserProfile.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                    }
                });
                viewHolder.userNameClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ShowAlluserProfile.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                    }
                });
                viewHolder.showOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu=new PopupMenu(getActivity(),viewHolder.showOption);
                        popupMenu.getMenuInflater().inflate(R.menu.giggleit_menu,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.gviewProfile:
                                        Intent intent=new Intent(getActivity(),ShowAlluserProfile.class);
                                        intent.putExtra("postKey",postKey);
                                        startActivity(intent);
                                        break;
                                    case R.id.gdeletePosts:
                                        postRefff.child(postKey).removeValue();
                                        break;
                                    case R.id.gverifyGiggleItPost:
                                        postRefff.child(postKey).child("verifiedPost").setValue("no");
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
        nopublish.setVisibility(View.GONE);
        recycleGiigleIT.setAdapter(firebaseRecyclerAdapter);
        if (refreshGiggleIT.isRefreshing()){
            refreshGiggleIT.setRefreshing(false);
        }

    }
    @Override
    public void onRefresh() {
        getCurrentUserInfo(currentUserInHeart);
        nopublish.setVisibility(View.GONE);
        ShowFavourite();
    }


}
