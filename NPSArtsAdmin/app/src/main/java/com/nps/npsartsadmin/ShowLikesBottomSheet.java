package com.nps.npsartsadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ShowLikesBottomSheet extends BottomSheetDialogFragment {
    private RecyclerView recycleLikers;
    private DatabaseReference likeRef;
    private FirebaseAuth mAuthInLike;
    public static   String postKey;
    private   String uidKey,currentUserInLikes;
    private TextView likeHeading;
    private ImageView onBackImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.show_likes_bottom_sheet,container,false);
        Typeface aveny=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        recycleLikers=(RecyclerView)view.findViewById(R.id.recycleAllLikes);
        recycleLikers.setHasFixedSize(true);
        likeHeading=(TextView)view.findViewById(R.id.likesHeader);
        likeHeading.setTypeface(aveny);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycleLikers.setLayoutManager(layoutManager);
        mAuthInLike=FirebaseAuth.getInstance();
        currentUserInLikes=mAuthInLike.getCurrentUser().getUid();
        likeRef= FirebaseDatabase.getInstance().getReference().child("LikeOfPosts").child(postKey);
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot.exists()){
               int noOfLikes=(int)dataSnapshot.getChildrenCount();
               likeHeading.setText(noOfLikes+" Likes");
           }
           else {
               likeHeading.setText("0 Likes");
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    public static class LikesViewHolder extends RecyclerView.ViewHolder{
        View mView;
        DatabaseReference userReff;
        public LikesViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            userReff=FirebaseDatabase.getInstance().getReference().child("Users");
        }
        public void setNameInLike(String userId) {
            userReff.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child("fullname").getValue().toString();
                    TextView userNameInMainPost=(TextView)mView.findViewById(R.id.showNameInLikes);
                    userNameInMainPost.setText(name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setProLinkInLike(final Context context,String userId) {
            userReff.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageLink=dataSnapshot.child("profileLink").getValue().toString();
                    CircularImageView setImage=(CircularImageView)mView.findViewById(R.id.containProfileInLikes);
                    Picasso.with(context).load(imageLink).placeholder(R.drawable.profile).into(setImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setUid(String uid) {

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<LikesModel, LikesViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<LikesModel,LikesViewHolder>
                (LikesModel.class,R.layout.likes_layout,LikesViewHolder.class,likeRef) {
            @Override
            protected void populateViewHolder(final LikesViewHolder viewHolder, final LikesModel model, int position) {
                final String userId=getRef(position).getKey();
                viewHolder.setNameInLike(model.getUid());
                viewHolder.setProLinkInLike(getActivity().getApplicationContext(),model.getUid());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ShowProOfLCandS.class);
                        intent.putExtra("userId",userId);
                        startActivity(intent);
                    }
                });
            }
        };
        recycleLikers.setAdapter(firebaseRecyclerAdapter);
    }
}
