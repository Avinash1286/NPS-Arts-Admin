package com.nps.npsartsadmin;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
public class AddUserFragment extends Fragment {
    EditText getNumber;
    Button sendNumber;
    DatabaseReference adminUserRef;
    RelativeLayout cancle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_user, container, false);
        getNumber=(EditText)view.findViewById(R.id.numberHolder);
        sendNumber=(Button)view.findViewById(R.id.addNumberButton);
        cancle=(RelativeLayout)view.findViewById(R.id.cancleFrag);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        adminUserRef= FirebaseDatabase.getInstance().getReference().child("AdminNumber");
        sendNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkForConnctoin()){
                    String getNumbers=getNumber.getText().toString();
                    if (getNumbers.isEmpty()){
                        getNumber.setError("Number can't be empty");
                        getNumber.setFocusable(true);
                        return;
                    }
                    if (getNumbers.length()>10){
                        getNumber.setError("Number can't more than 10 digits");
                        getNumber.setFocusable(true);

                    }
                    if (getNumbers.length()<10){
                        getNumber.setError("Number can't less than 10 digits");
                        getNumber.setFocusable(true);

                    }
                    else {
                        HashMap putNumber=new HashMap();
                        putNumber.put("Number",getNumbers);
                        adminUserRef.child(getNumbers).updateChildren(putNumber).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(getActivity(), "Number added successfully", Toast.LENGTH_SHORT).show();
                               getActivity().onBackPressed();
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
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
