package com.nps.npsartsadmin;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class VMPoem extends Fragment {
    DatabaseReference essayRef,checkRef,essayRefForOne;
    RecyclerView recycleManageEssay;
    RelativeLayout showNoInternet,showInActive;
    FirebaseAuth mAth;
    String currentUsers;
    LoadingView loadingView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_vmpoem, container, false);
        essayRef= FirebaseDatabase.getInstance().getReference().child("VotingSection").child("Poem");
        checkRef= FirebaseDatabase.getInstance().getReference().child("VotingSection");
        essayRefForOne=FirebaseDatabase.getInstance().getReference().child("PoemVotersForOne");
        showNoInternet=(RelativeLayout)view.findViewById(R.id.noInternetHolder);
        showInActive=(RelativeLayout)view.findViewById(R.id.showInActiveMessage);
        recycleManageEssay=(RecyclerView)view.findViewById(R.id.recycleManageEssay);
        mAth=FirebaseAuth.getInstance();
        currentUsers=mAth.getCurrentUser().getUid();
        recycleManageEssay.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingView=(LoadingView)view.findViewById(R.id.loadingView);
        loadingView.start();
        showData();
        return view;
    }
    private void showData() {
        if (checkForConnctoin()){
           showNoInternet.setVisibility(View.GONE);
           checkRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Poem")){
                    showInActive.setVisibility(View.GONE);
                    FirebaseRecyclerAdapter<PoemModel, VmEssayViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PoemModel, VmEssayViewHolder>
                            (PoemModel.class,R.layout.vm_essay_layouts,VmEssayViewHolder.class,essayRef) {
                        @Override
                        protected void populateViewHolder(final VmEssayViewHolder vmEssayViewHolder, final PoemModel essayModel, int i) {
                            final String essayKey=getRef(i).getKey();
                            vmEssayViewHolder.setVots(getActivity(),essayKey);
                            vmEssayViewHolder.ShowEssay(getActivity(),essayModel.getPoem());
                            vmEssayViewHolder.ShowHeading(getActivity(),essayModel.getHeading());
                            vmEssayViewHolder.ShowName(getActivity(),essayModel.getComposer());
                            vmEssayViewHolder.vmEssayName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent =new Intent(getActivity(),ReadMoreArtsAndEssay.class);
                                    intent.putExtra("CandidateName",essayModel.getComposer());
                                    intent.putExtra("Essay",essayModel.getPoem());
                                    startActivity(intent);
                                }
                            });
                            vmEssayViewHolder.vmEssayContent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent =new Intent(getActivity(),ReadMoreArtsAndEssay.class);
                                    intent.putExtra("CandidateName",essayModel.getComposer());
                                    intent.putExtra("Essay",essayModel.getPoem());
                                    startActivity(intent);
                                }
                            });
                            vmEssayViewHolder.vmEssayOptions.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PopupMenu popupMenu=new PopupMenu(getActivity(),vmEssayViewHolder.vmEssayOptions);
                                    popupMenu.getMenuInflater().inflate(R.menu.show_vm_options,popupMenu.getMenu());
                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("Deleting Candidate");
                                            builder.setMessage("Are you sure to delete this candidate?");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    essayRefForOne.child(essayKey).removeValue();
                                                }
                                            });
                                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
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
                    recycleManageEssay.setAdapter(firebaseRecyclerAdapter);
                }
                else {
                    loadingView.stop();
                    loadingView.setVisibility(View.GONE);
                    showInActive.setVisibility(View.VISIBLE);
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
            showNoInternet.setVisibility(View.VISIBLE);
        }
    }
    public static class VmEssayViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView vmEssayHeading,vmEssayName,vmEssayVots,vmEssayContent;
        ImageView vmEssayOptions;
        DatabaseReference essayRefForOne;
        public VmEssayViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            vmEssayContent=(TextView)mView.findViewById(R.id.vmessayContent);
            vmEssayHeading=(TextView)mView.findViewById(R.id.vmessayHeading);
            vmEssayName=(TextView)mView.findViewById(R.id.vmessayName);
            vmEssayVots=(TextView)mView.findViewById(R.id.vmessayShowNuberOfVots);
            vmEssayOptions=(ImageView)mView.findViewById(R.id.dotOptions);
            essayRefForOne=FirebaseDatabase.getInstance().getReference().child("PoemVotersForOne");
        }
        public void ShowEssay(Context context,String essay){
            // Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            // vmEssayContent.setTypeface(roboto);
            vmEssayContent.setText(essay);
        }
        public void  ShowHeading(Context context,String headigs){
            // Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            // vmEssayHeading.setTypeface(roboto);
            vmEssayHeading.setText(headigs);
        }
        public void ShowName(Context context,String names){
            //  Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            //  vmEssayName.setTypeface(roboto);
            vmEssayName.setText(names);
        }
        public void setVots(final Context context, final String essayKey){
            essayRefForOne.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
                    long novots=dataSnapshot.child(essayKey).getChildrenCount();
                    vmEssayVots.setTypeface(roboto);
                    vmEssayVots.setText(String.valueOf(novots));
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
