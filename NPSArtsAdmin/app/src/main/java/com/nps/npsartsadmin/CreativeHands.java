package com.nps.npsartsadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CreativeHands extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public CreativeHands() {
    }
    RecyclerView showCreativeHands;
    FloatingActionButton addCreativeHands;
    DatabaseReference databaseReference,userReff,publishingStatus;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    String getPermission="",currentUsers;
    TextView nopublish;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     final View view=inflater.inflate(R.layout.fragment_creative_hands, container, false);
     showCreativeHands=(RecyclerView)view.findViewById(R.id.showCreativeHands);
     mAuth=FirebaseAuth.getInstance();
     currentUsers=mAuth.getCurrentUser().getUid();
     nopublish=(TextView)view.findViewById(R.id.nopublishbutton);
     showCreativeHands.setHasFixedSize(true);
     showCreativeHands.setLayoutManager(new GridLayoutManager(getActivity(),2));
     addCreativeHands=(FloatingActionButton)view.findViewById(R.id.uploadCreativeHands);
     userReff=FirebaseDatabase.getInstance().getReference().child("Users");
        publishingStatus= FirebaseDatabase.getInstance().getReference().child("WallPublishStatus");
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refreshCreative);
        swipeRefreshLayout.setOnRefreshListener(this);
        showCreativeHands.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && addCreativeHands.getVisibility() == View.VISIBLE) {
                        addCreativeHands.hide();
                    } else if (dy < 0 && addCreativeHands.getVisibility() != View.VISIBLE) {
                        addCreativeHands.show();
                    }
            }
        });
      addCreativeHands.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startActivity(new Intent(getActivity(),PostingCreativeHands.class));
         }
     });
        loadData();
     return view;
    }
    private void loadData() {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("CreativeHandsInfo");
        FirebaseRecyclerAdapter<CreativeHandsModel,CreativeViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<CreativeHandsModel, CreativeViewHolder>
                (CreativeHandsModel.class,R.layout.creative_hands_layout,CreativeViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(CreativeViewHolder creativeViewHolder, CreativeHandsModel creativeHandsModel, int i) {
                final String creativeKey=getRef(i).getKey();
                creativeViewHolder.setImage(getActivity(),creativeHandsModel.getImage());
                creativeViewHolder.setName(creativeHandsModel.getName());
                creativeViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CharSequence option[]=new CharSequence[]{
                                "Delete"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle("Select Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    final android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getActivity());
                                    builder.setTitle("Deleting CreativeHand");
                                    builder.setMessage("Are you sure to delete this CreativeHand?");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            databaseReference.child(creativeKey).removeValue();
                                        }
                                    });
                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                }

                            }
                        });
                        builder.show();

                        return true;
                    }
                });
                creativeViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ShowCreativeHandsFullImage.class);
                        intent.putExtra("creativeKey",creativeKey);
                        startActivity(intent);
                    }
                });
            }
        };
        showCreativeHands.setAdapter(firebaseRecyclerAdapter);
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onRefresh() {

        loadData();

    }

    public static class CreativeViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CreativeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name) {
            TextView setArtistname=(TextView)mView.findViewById(R.id.conatinArtistName);
            setArtistname.setText(name);
        }
        public void setImage(Context context,String image) {
            ImageView setCreativeHands=(ImageView) mView.findViewById(R.id.containCreativeHandsImage);
            Picasso.with(context).load(image).placeholder(R.drawable.grayback).into(setCreativeHands);
        }
    }
}
