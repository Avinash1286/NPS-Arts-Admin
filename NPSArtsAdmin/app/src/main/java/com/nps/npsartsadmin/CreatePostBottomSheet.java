package com.nps.npsartsadmin;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreatePostBottomSheet extends BottomSheetDialogFragment {
  TextView createPostHeading,artsPOstHeading,creativePostHeading;
  ImageView postCreativeHands,postArticles;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.create_post_bottomsheet,container,false);
        Typeface robot=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        createPostHeading=(TextView)view.findViewById(R.id.createPostTitle);
        artsPOstHeading=(TextView)view.findViewById(R.id.artsPostHeading);
        creativePostHeading=(TextView)view.findViewById(R.id.imagepostHeading);
        postArticles=(ImageView)view.findViewById(R.id.artspostImage);
        postCreativeHands=(ImageView)view.findViewById(R.id.imagePostImage);
        createPostHeading.setTypeface(robot);
        artsPOstHeading.setTypeface(robot);
        creativePostHeading.setTypeface(robot);
        postCreativeHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostingDrawing.class));
            }
        });
        postArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostArticlesActivity.class));
            }
        });
        return view;
    }

}
