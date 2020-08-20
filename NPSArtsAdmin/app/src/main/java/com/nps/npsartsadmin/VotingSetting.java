package com.nps.npsartsadmin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class VotingSetting extends Fragment {

    RelativeLayout cancleFragment;
    Switch startHand,startEssay,startDrawing,startPoem,showNoVots;
    Boolean checker=false;
    StorageReference storeHandWritingAndDrawing;
    ProgressDialog progressDialog;
    public VotingSetting() {
        // Required empty public constructor
    }
    DatabaseReference onVotingRef,sendNotification,endNotification,checkVitingSection,votingSectionRef,drawingVotersAll,drawingVotersOne,handVotersOne,handVotersAll,poemVotersAll,poemVotersOne,essayVotersOne,essayVotersAll;
    TextView endVotingButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         final View view=inflater.inflate(R.layout.fragment_blank, container, false);
        onVotingRef= FirebaseDatabase.getInstance().getReference().child("VotingStatus");
        endVotingButton=(TextView)view.findViewById(R.id.endVotingButton);
        votingSectionRef= FirebaseDatabase.getInstance().getReference().child("VotingSection");
        checkVitingSection= FirebaseDatabase.getInstance().getReference();
        checkVitingSection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("VotingSection")){
                    endVotingButton.setVisibility(View.VISIBLE);
                }
                else {
                    endVotingButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        drawingVotersAll=FirebaseDatabase.getInstance().getReference().child("DrawingVotersForAll");
        drawingVotersOne=FirebaseDatabase.getInstance().getReference().child("DrawingVotersForOne");
        handVotersAll=FirebaseDatabase.getInstance().getReference().child("HandWritingVotersForAll");
        handVotersOne=FirebaseDatabase.getInstance().getReference().child("HandWritingVotersForOne");
        poemVotersAll= FirebaseDatabase.getInstance().getReference().child("PoemVotersForAll");
        poemVotersOne= FirebaseDatabase.getInstance().getReference().child("PoemVotersForOne");
        essayVotersAll= FirebaseDatabase.getInstance().getReference().child("EssayVotersForAll");
        essayVotersOne= FirebaseDatabase.getInstance().getReference().child("EssayVotersForOne");
        sendNotification=FirebaseDatabase.getInstance().getReference().child("messagesvote");
        endNotification=FirebaseDatabase.getInstance().getReference().child("messagesVoteResult");
        storeHandWritingAndDrawing= FirebaseStorage.getInstance().getReference();
        cancleFragment=(RelativeLayout)view.findViewById(R.id.parentSetting);
        progressDialog=new ProgressDialog(getActivity());
        startHand=(Switch)view.findViewById(R.id.handSwitch);
        startEssay=(Switch)view.findViewById(R.id.essaySwitch);
        startDrawing=(Switch)view.findViewById(R.id.drawingSwitch);
        startPoem=(Switch)view.findViewById(R.id.poemSwitch);
        ImageView sendNotificationImage=(ImageView)view.findViewById(R.id.sendNotification);
        sendNotificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification.push().setValue("messagesent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Notification Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        endVotingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("Warning");
                builder.setMessage("All the voting data will be erased and can't be recovered,do you want to continue..");
                builder.setCancelable(false);
                builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                     votingSectionRef.removeValue();
                     drawingVotersAll.removeValue();
                     drawingVotersOne.removeValue();
                     handVotersAll.removeValue();
                     handVotersOne.removeValue();
                     poemVotersAll.removeValue();
                     poemVotersOne.removeValue();
                     essayVotersAll.removeValue();
                     onVotingRef.child("HandStatus").setValue("no");
                     onVotingRef.child("DrawingStatus").setValue("no");
                     onVotingRef.child("EssayStatus").setValue("no");
                     onVotingRef.child("PoemStatus").setValue("no");
                     onVotingRef.child("NoOfVotsCon").setValue("yes");
                     essayVotersOne.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            endNotification.push().setValue("sent");
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                         }
                     });
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
        });
        showNoVots=(Switch)view.findViewById(R.id.noOfVotsSwitch);
        onVotingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String handCon=dataSnapshot.child("HandStatus").getValue().toString();
             String essayCon=dataSnapshot.child("EssayStatus").getValue().toString();
             String drawingCon=dataSnapshot.child("DrawingStatus").getValue().toString();
             String poemCon=dataSnapshot.child("PoemStatus").getValue().toString();
             String noOfVotsCon=dataSnapshot.child("NoOfVotsCon").getValue().toString();
             if (handCon.equals("yes")){
                 startHand.setChecked(true);
             }
             else {
                 startHand.setChecked(false);
             }
             if (essayCon.equals("yes")){
                 startEssay.setChecked(true);
             }
             else {
                 startEssay.setChecked(false);
             }
             if (drawingCon.equals("yes")){
                 startDrawing.setChecked(true);
             }
             else {
                 startDrawing.setChecked(false);
             }
             if (poemCon.equals("yes")){
                 startPoem.setChecked(true);
             }
             else {
                 startPoem.setChecked(false);
             }
             if (noOfVotsCon.equals("yes")){
                 showNoVots.setChecked(true);
             }
             else {
                 showNoVots.setChecked(false);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        cancleFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        startHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                onVotingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (checker){
                                if (dataSnapshot.child("HandStatus").getValue().toString().equals("yes")){
                                    onVotingRef.child("HandStatus").setValue("no");
                                    startHand.setChecked(false);
                                   checker=false;
                                }
                                else {
                                    onVotingRef.child("HandStatus").setValue("yes");
                                    startHand.setChecked(true);
                                    checker=false;
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        startPoem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                onVotingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (checker){
                            if (dataSnapshot.child("PoemStatus").getValue().toString().equals("yes")){
                                onVotingRef.child("PoemStatus").setValue("no");
                                startPoem.setChecked(false);
                                checker=false;
                            }
                            else {
                                onVotingRef.child("PoemStatus").setValue("yes");
                                startPoem.setChecked(true);
                                checker=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        startDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                onVotingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (checker){
                            if (dataSnapshot.child("DrawingStatus").getValue().toString().equals("yes")){
                                onVotingRef.child("DrawingStatus").setValue("no");
                                startDrawing.setChecked(false);
                                checker=false;
                            }
                            else {
                                onVotingRef.child("DrawingStatus").setValue("yes");
                                startDrawing.setChecked(true);
                                checker=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        startEssay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                onVotingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (checker){
                            if (dataSnapshot.child("EssayStatus").getValue().toString().equals("yes")){
                                onVotingRef.child("EssayStatus").setValue("no");
                                startEssay.setChecked(false);
                                checker=false;
                            }
                            else {
                                onVotingRef.child("EssayStatus").setValue("yes");
                                startEssay.setChecked(true);
                                checker=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        showNoVots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker=true;
                onVotingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (checker){
                            if (dataSnapshot.child("NoOfVotsCon").getValue().toString().equals("yes")){
                                onVotingRef.child("NoOfVotsCon").setValue("no");
                                showNoVots.setChecked(false);
                                checker=false;
                            }
                            else {
                                onVotingRef.child("NoOfVotsCon").setValue("yes");
                                showNoVots.setChecked(true);
                                checker=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
          return view;
    }
}
