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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cdflynn.android.library.checkview.CheckView;

public class HandwritingVoting extends Fragment {

    RecyclerView recycleHandVoting;
    DatabaseReference drawingRef,checkRef,addVotersForAll,addVotersForOne,checkVoting;
    TextView showNumberVotes;
    RelativeLayout showNoInternetMessage,showInActiveVoting,showActiveVoting;
    FirebaseAuth mAuth;
    String currentUsers;
    Boolean checkExistance=false;
    String holdHandKey;
    ShimmerFrameLayout handShimmer;
    CheckView checkView;
    LoadingView loadingView;
    public HandwritingVoting() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_handwriting_voting, container, false);
        drawingRef= FirebaseDatabase.getInstance().getReference().child("VotingSection").child("Handwriting");
        checkVoting= FirebaseDatabase.getInstance().getReference().child("VotingStatus");
        checkRef= FirebaseDatabase.getInstance().getReference().child("VotingSection");
        addVotersForAll=FirebaseDatabase.getInstance().getReference().child("HandWritingVotersForAll");
        addVotersForOne=FirebaseDatabase.getInstance().getReference().child("HandWritingVotersForOne");
        mAuth= FirebaseAuth.getInstance();
        currentUsers=mAuth.getCurrentUser().getUid();
        showNoInternetMessage=(RelativeLayout)view.findViewById(R.id.noInternetHolder);
        showInActiveVoting=(RelativeLayout)view.findViewById(R.id.showInActiveMessage);
        recycleHandVoting=(RecyclerView)view.findViewById(R.id.recycleHandVoting);
        recycleHandVoting.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingView=(LoadingView)view.findViewById(R.id.loadingView);
        loadingView.start();
        checkVoting.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           String condition=dataSnapshot.child("HandStatus").getValue().toString();
           if (condition.equals("yes")){
               loadData();
           }
           else {
               loadingView.stop();
               loadingView.setVisibility(View.GONE);
               showInActiveVoting.setVisibility(View.VISIBLE);
           }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
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
    public void loadData(){
        if (checkForConnctoin()){
            showNoInternetMessage.setVisibility(View.GONE);
            checkRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("Handwriting")){
                        showInActiveVoting.setVisibility(View.GONE);
                        FirebaseRecyclerAdapter<HandwritingModel, VMHandViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<HandwritingModel,VMHandViewHolder>
                                (HandwritingModel.class,R.layout.voting_layout,VMHandViewHolder.class,drawingRef) {
                            @Override
                            protected void populateViewHolder(final VMHandViewHolder drawingViewHolder, final HandwritingModel handwritingAndDrawingModel, final int i) {
                                final String voteKey=getRef(i).getKey();
                                drawingViewHolder.setVmImage(getActivity(),handwritingAndDrawingModel.getImageOFHandwriting());
                                drawingViewHolder.setVmName(getActivity(),handwritingAndDrawingModel.getHandwritingWriterName());
                                drawingViewHolder.setVmNoOFvots(getActivity(),voteKey);
                                Animation animation= AnimationUtils.loadAnimation(getActivity(),R.anim.scale_animation);
                                animation.setRepeatCount(Animation.INFINITE);
                                drawingViewHolder.voteButton.setAnimation(animation);
                                drawingViewHolder.setTextVisibility(getActivity());
                                drawingViewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkExistance=true;
                                        addVotersForAll.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (checkExistance){
                                                    if (!dataSnapshot.hasChild(currentUsers)){
                                                        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                                                        AboutToVote aboutToVote=new AboutToVote();
                                                        FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                                                        fragmentTransaction1.add(R.id.votingConfirmationFramHand,aboutToVote);
                                                        AboutToVote.handKey=voteKey;
                                                        AboutToVote.currentUserss=currentUsers;
                                                        AboutToVote.votersForOne="HandWritingVotersForOne";
                                                        AboutToVote.votersForAll="HandWritingVotersForAll";
                                                        AboutToVote.name=handwritingAndDrawingModel.getHandwritingWriterName();
                                                        fragmentTransaction1.addToBackStack(null);
                                                        fragmentTransaction1.commit();
                                                        checkExistance=false;
                                                    }
                                                    else {
                                                        Toast.makeText(getActivity(), "You have already taken part", Toast.LENGTH_SHORT).show();
                                                        checkExistance=false;
                                                    }

                                                }

                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });
                                drawingViewHolder.mViews.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getActivity(),ShowVotingImage.class);
                                        intent.putExtra("name",handwritingAndDrawingModel.getHandwritingWriterName());
                                        intent.putExtra("image",handwritingAndDrawingModel.getImageOFHandwriting());
                                        Pair[]  pairs=new Pair[2];
                                        pairs[0]=new Pair<View,String>(drawingViewHolder.showVmName,"nameTrans");
                                        pairs[1]=new Pair<View,String>(drawingViewHolder.showVmImage,"imageTrans");
                                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(getActivity());
                                        startActivity(intent,options.toBundle());
                                    }
                                });

                            }
                        };
                        recycleHandVoting.setAdapter(firebaseRecyclerAdapter);
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);

                    }
                    else {

                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                        showInActiveVoting.setVisibility(View.VISIBLE);
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
            showNoInternetMessage.setVisibility(View.VISIBLE);

        }
    }

    public static class VMHandViewHolder extends RecyclerView.ViewHolder{
        View mViews;
        TextView showVmName,showNoOfVots,voteButton;
        ImageView showVmImage,showVmOPtions;
        DatabaseReference handRefCounter,votingTextVisibilityRef;
        public VMHandViewHolder(@NonNull View itemView) {
            super(itemView);
            mViews=itemView;
            showVmImage=(ImageView)mViews.findViewById(R.id.manageHandImage);
            showVmName=(TextView)mViews.findViewById(R.id.manageHoldeName);
            showVmOPtions=(ImageView) mViews.findViewById(R.id.showHeadinginManage);
            showNoOfVots=(TextView)mViews.findViewById(R.id.showNOOfVots);
            voteButton=(TextView)mViews.findViewById(R.id.voteNowButton);
            handRefCounter= FirebaseDatabase.getInstance().getReference().child("HandWritingVotersForOne");
            votingTextVisibilityRef= FirebaseDatabase.getInstance().getReference().child("VotingStatus");
        }
        public void setVmImage(Context context,String imageUrl){
            if (!imageUrl.isEmpty()){
                Picasso.with(context).load(imageUrl).placeholder(R.drawable.image_placeholder).into(showVmImage);
                return;
            }
        }
        public void setVmName(Context context,String getName){
            //  Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            showVmName.setText(getName);
            // showVmName.setTypeface(roboto);
        }
        public void setTextVisibility(Context context){
            votingTextVisibilityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("NoOfVotsCon").getValue().toString().equals("yes")){
                        showNoOfVots.setVisibility(View.VISIBLE);
                    }
                    else {
                        showNoOfVots.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
}
