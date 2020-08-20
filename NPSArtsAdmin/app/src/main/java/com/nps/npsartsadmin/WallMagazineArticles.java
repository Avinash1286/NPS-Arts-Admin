package com.nps.npsartsadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class WallMagazineArticles extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    FloatingActionButton posteWmArts;
    DatabaseReference databaseReference,userReff,publishingStatus;
    RecyclerView showArtsofWallMagazine;
    FirebaseAuth mAuth;
    SwipeRefreshLayout refreshLayout;
    TextView nopublish;
    private GestureDetectorCompat gestureDetectorCompat=null;
    public WallMagazineArticles() {
        // Required empty public constructor
    }
    String getPermission,currentUser;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wall_magazine_articles, container, false);
        posteWmArts=(FloatingActionButton)view.findViewById(R.id.clickToPostWmArts);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("WallMagazinesArticles");
        publishingStatus= FirebaseDatabase.getInstance().getReference().child("WallPublishStatus");
        showArtsofWallMagazine=(RecyclerView)view.findViewById(R.id.showWallMagazine);
        nopublish=(TextView)view.findViewById(R.id.nopublishbutton);
        userReff=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        showArtsofWallMagazine.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2);
         showArtsofWallMagazine.setLayoutManager(gridLayoutManager);
        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refreshWallArts);
        refreshLayout.setOnRefreshListener(this);
     showArtsofWallMagazine.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && posteWmArts.getVisibility() == View.VISIBLE) {
                        posteWmArts.hide();
                    } else if (dy < 0 && posteWmArts.getVisibility() != View.VISIBLE) {
                        posteWmArts.show();
                    }

            }
        });
        posteWmArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),PostingWallMagazine.class);
                startActivity(intent);
            }
        });
        loadData();
        return view;
    }

    private void loadData() {
        FirebaseRecyclerAdapter<WallMagazineModel,WallViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<WallMagazineModel, WallViewHolder>
                (WallMagazineModel.class,R.layout.layout_wall_magazine,WallViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(WallViewHolder viewHolder, final WallMagazineModel model, int position) {
                final String postKey=getRef(position).getKey();
                viewHolder.setTopic(getContext(),model.getTopic());
                viewHolder.setArts(getContext(),model.getArts());
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CharSequence option[]=new CharSequence[]{
                                "Edit",
                                "Delete"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle("Select Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    UpdateWallMagazine updatePost=new UpdateWallMagazine();
                                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction=fragmentManager.beginTransaction();
                                    transaction.add(R.id.showPublishingFrame,updatePost);
                                    UpdateWallMagazine.postKey=postKey;
                                    UpdateWallMagazine.previousPost=model.getArts();
                                    UpdateWallMagazine.previousTitle=model.getTopic();
                                    UpdateWallMagazine.previousName=model.getName();
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                                if (which==1){
                                    final android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getActivity());
                                    builder.setTitle("Deleting Article");
                                    builder.setMessage("Are you sure to delete this Article?");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            databaseReference.child(postKey).removeValue();
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
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ShowWallMagazineDetails.class);
                        intent.putExtra("heading",model.getTopic());
                        intent.putExtra("arts",model.getArts());
                        intent.putExtra("name",model.getName());
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.fragment_anim, R.anim.fadein);
                     /*
                        show_detail_wallArts show_detail_wallArts=new show_detail_wallArts();
                        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fragment_anim,R.anim.fragment_anim_out,R.anim.fragment_anim,R.anim.fragment_anim_out);
                        fragmentTransaction.replace(R.id.wallFragmentHolder,show_detail_wallArts);
                        show_detail_wallArts.headingInDetailFragment=model.getTopic();
                        show_detail_wallArts.artsInDetailFragment=model.getArts();
                        show_detail_wallArts.nameInDetailFragment=model.getName();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit(); */
                    }
                });
            }
        };
        showArtsofWallMagazine.setAdapter(firebaseRecyclerAdapter);
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        loadData();

    }
    public static class WallViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public WallViewHolder(@NonNull View itemView) {
            super(itemView);
           mView=itemView;
        }

        public void setArts(Context context, String arts) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTRegular.otf");
            TextView setWallArts=(TextView)mView.findViewById(R.id.wallArts);
            setWallArts.setTypeface(roboto);
            setWallArts.setText(arts);
        }

        public void setTopic(Context context,String topic) {
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            TextView setWallArtHeading=(TextView)mView.findViewById(R.id.wallHeading);
            setWallArtHeading.setTypeface(roboto);
            setWallArtHeading.setText(topic);
        }

    }
    }





