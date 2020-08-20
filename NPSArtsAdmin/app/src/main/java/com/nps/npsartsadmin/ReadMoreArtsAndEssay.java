package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReadMoreArtsAndEssay extends AppCompatActivity {
    Toolbar readMoreTool;
    TextView showEssay;
    String essay,candidateName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more_arts_and_essay);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        essay=getIntent().getExtras().get("Essay").toString();
        candidateName=getIntent().getExtras().get("CandidateName").toString();
        readMoreTool=(Toolbar)findViewById(R.id.readmoreTool);
        showEssay=(TextView)findViewById(R.id.readMoreEssay);
        showEssay.setTypeface(roboto);
        showEssay.setText(essay);
        setSupportActionBar(readMoreTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.voting_tools,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.readMoreHeading);
        headingPendingPost.setText(candidateName);
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.readMoreBack);
        setBack.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
