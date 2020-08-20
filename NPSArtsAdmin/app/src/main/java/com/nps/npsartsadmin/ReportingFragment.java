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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ReportingFragment extends Fragment {
    public ReportingFragment() {

    }
    TextInputEditText getReasonText;
    Button sendReportButton;
    RelativeLayout onback;
    TextView headingReport;
    String postKeyValue="",reporterName="",reporterProLink="",postTitle="",postUserName="",reason,uidKey="";
    DatabaseReference reportRef;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_reporting, container, false);
        getReasonText=(TextInputEditText)view.findViewById(R.id.getReason);
        headingReport=(TextView)view.findViewById(R.id.reportingHeading);
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        sendReportButton=(Button)view.findViewById(R.id.sendReport);
        headingReport.setTypeface(roboto);
        onback=(RelativeLayout)view.findViewById(R.id.onBackRel);
        onback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        progressDialog=new ProgressDialog(getActivity());
        reportRef= FirebaseDatabase.getInstance().getReference().child("Reports");
        sendReportButton.setTypeface(roboto);
        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForConnctoin()){
                    reason=getReasonText.getText().toString();
                    if (reason.isEmpty()){
                        Toast.makeText(getActivity(), "Please enter your reason to report", Toast.LENGTH_SHORT).show();
                        getReasonText.setError("reason can't be empty(*)");
                        getReasonText.setFocusable(true);
                        return;
                    }
                    if (uidKey.isEmpty() ||postKeyValue.isEmpty() || reporterName.isEmpty() || reporterProLink.isEmpty() || postTitle.isEmpty() || postUserName.isEmpty()){
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                        return;
                    }
                    else {
                        progressDialog.setMessage("Sending report");
                        progressDialog.show();
                        Calendar calendar=Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                        String date=simpleDateFormat.format(calendar.getTime());
                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm:ss");
                        String time=simpleDateFormat1.format(calendar.getTime());
                        String random=date+time;
                        HashMap putReportInfo=new HashMap();
                        putReportInfo.put("postKeyValue",postKeyValue);
                        putReportInfo.put("reporterName",reporterName);
                        putReportInfo.put("reporterProLink",reporterProLink);
                        putReportInfo.put("postTitle",postTitle);
                        putReportInfo.put("postUserName",postUserName);
                        putReportInfo.put("reason",reason);
                        putReportInfo.put("uidKey",uidKey);
                        reportRef.child(random).updateChildren(putReportInfo).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Your report has been submitted", Toast.LENGTH_SHORT).show();
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
