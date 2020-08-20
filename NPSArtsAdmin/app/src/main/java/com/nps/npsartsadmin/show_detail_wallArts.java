package com.nps.npsartsadmin;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class show_detail_wallArts extends Fragment {

     String headingInDetailFragment,nameInDetailFragment,artsInDetailFragment;
     TextView showHeadingInDetails,showNameInDetail,showArtsInDetail;
     ImageView cancleIcon;
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_show_detail_wall_arts, container, false);
          showArtsInDetail=(TextView)view.findViewById(R.id.showWallArts);
          showHeadingInDetails=(TextView)view.findViewById(R.id.showWallArtsTopic);
          showNameInDetail=(TextView)view.findViewById(R.id.showAuthorname);
          cancleIcon=(ImageView)view.findViewById(R.id.cancleIconInShowDetailWall);
          cancleIcon.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  getActivity().onBackPressed();
              }
          });
          showNameInDetail.setText(nameInDetailFragment);
          showArtsInDetail.setText(artsInDetailFragment);
          showHeadingInDetails.setText("Topic: "+headingInDetailFragment);
         return view;
    }

}