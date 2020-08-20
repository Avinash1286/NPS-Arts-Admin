package com.nps.npsartsadmin;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawingManagement extends Fragment {


    public DrawingManagement() {
        // Required empty public constructor
    }

    DatabaseReference handRef,checkRef;
    RelativeLayout noInternet,votingInActive;
    RecyclerView recycleVmHand;
     LoadingView loadingView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_vmdrawing, container, false);
        handRef= FirebaseDatabase.getInstance().getReference().child("VotingSection").child("Drawing");
        checkRef= FirebaseDatabase.getInstance().getReference().child("VotingSection");
        noInternet=(RelativeLayout)view.findViewById(R.id.noInternetHolder);
        votingInActive=(RelativeLayout)view.findViewById(R.id.showInActiveMessage);
        recycleVmHand=(RecyclerView)view.findViewById(R.id.recycleManageHandwriting);
        recycleVmHand.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingView=(LoadingView)view.findViewById(R.id.loadingView);
        loadingView.start();
        loadData();
        return view;
    }

    public void loadData(){
        if (checkForConnctoin()){
            noInternet.setVisibility(View.GONE);
            checkRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("Drawing")){
                        votingInActive.setVisibility(View.GONE);
                        FirebaseRecyclerAdapter<DrawingModel, VMHandViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<DrawingModel,VMHandViewHolder>
                                (DrawingModel.class,R.layout.voting_management_layout, VMHandViewHolder.class,handRef) {
                            @Override
                            protected void populateViewHolder(final VMHandViewHolder drawingViewHolder, final DrawingModel handwritingAndDrawingModel, final int i) {
                                final String voteRef=getRef(i).getKey();
                                drawingViewHolder.setVmImage(getActivity(),handwritingAndDrawingModel.getImageOFDrawing());
                                drawingViewHolder.setVmName(getActivity(),handwritingAndDrawingModel.getArtistName());
                                drawingViewHolder.setVmNoOFvots(getActivity(),voteRef);
                                drawingViewHolder.mViews.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getActivity(),ShowVotingImage.class);
                                        intent.putExtra("name",handwritingAndDrawingModel.getArtistName());
                                        intent.putExtra("image",handwritingAndDrawingModel.getImageOFDrawing());
                                        Pair[]  pairs=new Pair[2];
                                        pairs[0]=new Pair<View,String>(drawingViewHolder.showVmName,"nameTrans");
                                        pairs[1]=new Pair<View,String>(drawingViewHolder.showVmImage,"imageTrans");
                                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(getActivity());
                                        startActivity(intent,options.toBundle());
                                    }
                                });
                                drawingViewHolder.showVmOPtions.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final PopupMenu popupMenu=new PopupMenu(getActivity(),drawingViewHolder.showVmOPtions);
                                        popupMenu.getMenuInflater().inflate(R.menu.show_vm_options,popupMenu.getMenu());
                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()){
                                                    case R.id.vmOption:
                                                        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                                        builder.setTitle("Deleting Post");
                                                        builder.setMessage("Are you sure to delete candidate?");
                                                        builder.setCancelable(false);
                                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                handRef.child(voteRef).removeValue();
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
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                        recycleVmHand.setAdapter(firebaseRecyclerAdapter);

                    }
                    else {
                        loadingView.setVisibility(View.GONE);
                        loadingView.stop();
                        votingInActive.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            loadingView.stop();
            loadingView.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);

        }
    }
    public static class VMHandViewHolder extends RecyclerView.ViewHolder{
        View mViews;
        TextView showVmName,showNoOfVots;
        ImageView showVmImage,showVmOPtions;
        DatabaseReference handRefCounter;
        public VMHandViewHolder(@NonNull View itemView) {
            super(itemView);
            mViews=itemView;
            showVmImage=(ImageView)mViews.findViewById(R.id.manageHandImage);
            showVmName=(TextView)mViews.findViewById(R.id.manageHoldeName);
            showVmOPtions=(ImageView) mViews.findViewById(R.id.showHeadinginManage);
            showNoOfVots=(TextView)mViews.findViewById(R.id.showNOOfVots);
            handRefCounter= FirebaseDatabase.getInstance().getReference().child("DrawingVotersForOne");
        }
        public void setVmImage(Context context,String imageUrl){
            if (!imageUrl.isEmpty()){
                Picasso.with(context).load(imageUrl).placeholder(R.drawable.image_placeholder).into(showVmImage);
                return;
            }
        }
        public void setVmName(Context context,String getName){
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            showVmName.setText(getName);
            showVmName.setTypeface(roboto);
        }
        public void setVmNoOFvots(final Context context, final String key){
            handRefCounter.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long counter=dataSnapshot.child(key).getChildrenCount();
                    Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
                    showNoOfVots.setTypeface(roboto);
                    showNoOfVots.setText(String.valueOf(counter));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }


    private boolean checkForConnctoin() {
        ConnectivityManager cm=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){

            return true;

        }
        else {
            return false;
        }

    }

}
