package com.nps.npsartsadmin;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cdflynn.android.library.checkview.CheckView;

public class AboutToVote extends Fragment {
    public AboutToVote() {

    }
    CheckView checkView;
    TextView headingConfirm,confirm;
    DatabaseReference addVotersForAll,addVotersForOne;
    RelativeLayout relBack;
    public static String currentUserss,handKey,name,votersForAll,votersForOne;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_about_to_vote, container, false);
        addVotersForAll=FirebaseDatabase.getInstance().getReference().child(votersForAll);
        addVotersForOne=FirebaseDatabase.getInstance().getReference().child(votersForOne);
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        checkView=(CheckView)view.findViewById(R.id.check);
        checkView.check();
        relBack=(RelativeLayout)view.findViewById(R.id.relCancle);
        relBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        headingConfirm=(TextView)view.findViewById(R.id.headingConfirm);
        headingConfirm.setText("You are about to vote \n"+name);
        headingConfirm.setTypeface(roboto);
        Animation animation= AnimationUtils.loadAnimation(getActivity(),R.anim.scale_animation);
        confirm=(TextView)view.findViewById(R.id.confirmButton);
        confirm.setAnimation(animation);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVotersForAll.child(currentUserss).setValue("participants");
                addVotersForOne.child(handKey).child(currentUserss).setValue("participants");
                getActivity().onBackPressed();
            }
        });

        return view;
    }

}
