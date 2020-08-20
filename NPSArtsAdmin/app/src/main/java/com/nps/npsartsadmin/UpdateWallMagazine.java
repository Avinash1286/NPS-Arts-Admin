package com.nps.npsartsadmin;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateWallMagazine extends Fragment {
    public static String previousTitle,previousPost,postKey="",previousName;
    TextInputEditText topicEditText,artEditText,nameEditText;
    CardView cardCancle,cardUpdate;
    DatabaseReference postRef;
    ProgressDialog progressDialog;

    public UpdateWallMagazine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_update_wall_magazine, container, false);
        topicEditText=(TextInputEditText)view.findViewById(R.id.getUpdatedTopic);
        artEditText=(TextInputEditText)view.findViewById(R.id.getUpdatedArts);
        nameEditText=(TextInputEditText)view.findViewById(R.id.getUpdatedName);
        cardCancle=(CardView)view.findViewById(R.id.cancleCard);
        cardUpdate=(CardView)view.findViewById(R.id.updateCard);
        progressDialog=new ProgressDialog(getActivity());
        TextView titile=(TextView)view.findViewById(R.id.updatinpostitle);
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        titile.setTypeface(roboto);
        postRef= FirebaseDatabase.getInstance().getReference().child("WallMagazinesArticles");
        topicEditText.setText(previousTitle);
        artEditText.setText(previousPost);
        nameEditText.setText(previousName);
        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForConnctoin()){
                    String getTitle=topicEditText.getText().toString();
                    String getArts=artEditText.getText().toString();
                    String getName=nameEditText.getText().toString();
                    if (getArts.isEmpty()){
                        Toast.makeText(getActivity(), "Topic can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getTitle.isEmpty()){
                        Toast.makeText(getActivity(), "Article can't be empty", Toast.LENGTH_SHORT).show();
                    }

                    if (getName.isEmpty()){
                        Toast.makeText(getActivity(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (!postKey.isEmpty()){
                            progressDialog.setMessage("Updating WallMagazine");
                            progressDialog.show();
                            HashMap putPostInfo=new HashMap();
                            putPostInfo.put("arts",getArts);
                            putPostInfo.put("topic",getTitle);
                            putPostInfo.put("name",getName);
                            postRef.child(postKey).updateChildren(putPostInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Your Post Updated Successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        getActivity().onBackPressed();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        getActivity().onBackPressed();
                                    }

                                }
                            });
                        }
                        else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                    }
                }
                else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

            }
        });
        cardCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

}
