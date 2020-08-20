package com.nps.npsartsadmin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class TimeUp extends Fragment {
    RelativeLayout endFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment_time_up, container, false);
      endFrame=(RelativeLayout)view.findViewById(R.id.timeUpParent);
      endFrame.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              getActivity().onBackPressed();
          }
      });

      return view;
    }

}
