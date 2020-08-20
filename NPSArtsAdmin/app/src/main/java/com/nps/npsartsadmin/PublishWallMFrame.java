package com.nps.npsartsadmin;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PublishWallMFrame extends Fragment {


    public PublishWallMFrame() {
        // Required empty public constructor
    }

    RelativeLayout onBackRel;
    Switch publish;
    DatabaseReference publishRef,sendNotification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view=inflater.inflate(R.layout.fragment_publish_wall_mframe, container, false);
        publish=(Switch)view.findViewById(R.id.publishButton);
        onBackRel=(RelativeLayout)view.findViewById(R.id.wallPublishing);
        onBackRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        publishRef= FirebaseDatabase.getInstance().getReference().child("WallPublishStatus");
        sendNotification=FirebaseDatabase.getInstance().getReference().child("WallMagazineNotification");
        publishRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Status").getValue().toString().equals("yes")){
                    publish.setChecked(true);
                }
                else {
                    publish.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Status").getValue().toString().equals("yes")){
                            publish.setChecked(false);
                            publishRef.child("Status").setValue("no");
                        }
                        else {
                            publishRef.child("Status").setValue("yes");
                            sendNotification.push().setValue("sent");
                            publish.setChecked(true);
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
