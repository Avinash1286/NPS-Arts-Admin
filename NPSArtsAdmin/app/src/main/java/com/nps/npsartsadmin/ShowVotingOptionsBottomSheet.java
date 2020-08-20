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

public class ShowVotingOptionsBottomSheet extends BottomSheetDialogFragment {
  private   ImageView selectOption1,selectOption2,selectOption3,selectOption4;
 private    TextView t1,t2,t3,t4,dialogHeading;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.show_voting_option, container, false);
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        selectOption1=(ImageView)view.findViewById(R.id.essayImage);
        selectOption2=(ImageView)view.findViewById(R.id.handImage);
        selectOption3=(ImageView)view.findViewById(R.id.poemImage);
        selectOption4=(ImageView)view.findViewById(R.id.drawingImage);
        t1=(TextView)view.findViewById(R.id.essayHeading);
        t2=(TextView)view.findViewById(R.id.handHeading);
        t3=(TextView)view.findViewById(R.id.poemHeading);
        t4=(TextView)view.findViewById(R.id.drawingHeading);
        dialogHeading=(TextView)view.findViewById(R.id.votingopetionHeading);
        t1.setTypeface(roboto);
        t2.setTypeface(roboto);
        t3.setTypeface(roboto);
        t4.setTypeface(roboto);
        dialogHeading.setTypeface(roboto);
        selectOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),CreatingVoting.class);
                intent.putExtra("type","Essay");

                startActivity(intent);
            }
        });
        selectOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),CreatingVoting.class);
                intent.putExtra("type","Handwriting");

                startActivity(intent);
            }
        });
        selectOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),CreatingVoting.class);
                intent.putExtra("type","Poem");
                startActivity(intent);
            }
        });
        selectOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),CreatingVoting.class);
                intent.putExtra("type","Drawing");
                startActivity(intent);
            }
        });
        return view;
    }
}
