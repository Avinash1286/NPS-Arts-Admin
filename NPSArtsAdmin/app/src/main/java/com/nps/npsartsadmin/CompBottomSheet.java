package com.nps.npsartsadmin;

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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CompBottomSheet extends BottomSheetDialogFragment {
    TextView compTitle,selectQuizHeading,selectVoteHeading;
    ImageView selectQuizImage,selectVoteImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.comp_button_sheet,container,false);
        Typeface robot=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        Typeface aveny=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTRegular.otf");
        compTitle=(TextView)view.findViewById(R.id.compTitle);
        selectQuizHeading=(TextView)view.findViewById(R.id.selectQuizHeading);
        selectVoteHeading=(TextView)view.findViewById(R.id.selectVoteHeading);
        selectQuizImage=(ImageView)view.findViewById(R.id.selectQuizImage);
        selectVoteImage=(ImageView)view.findViewById(R.id.selectVoteImage);
        compTitle.setTypeface(robot);
        selectQuizHeading.setTypeface(aveny);
        selectVoteHeading.setTypeface(aveny);
        selectQuizImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        selectVoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),VotingSection.class));
            }
        });


        return view;
    }
}
