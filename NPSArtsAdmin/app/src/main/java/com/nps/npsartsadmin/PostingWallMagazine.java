package com.nps.npsartsadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class PostingWallMagazine extends AppCompatActivity {

    Toolbar wallToolBar;
    EditText getArt;
    TextInputEditText getAuthorName,getArtTitle;
    TextView getPublishButton;
    ImageView getBackButton;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    long wallCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_wall_magazine);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("WallMagazinesArticles");
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.messstatuscolor));
        }
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        wallToolBar=(Toolbar)findViewById(R.id.wallTool);
        progressDialog=new ProgressDialog(this);
        getAuthorName=(TextInputEditText)findViewById(R.id.getAuthorName);
        getArtTitle=(TextInputEditText)findViewById(R.id.getTopicName);
        getArt=(EditText)findViewById(R.id.containWallmagazineArticles);
        setSupportActionBar(wallToolBar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.wall_post_toll_layout,null);
        actionBar.setCustomView(view);
        getPublishButton=(TextView)findViewById(R.id.publishArts);
        getPublishButton.setTypeface(roboto);
        TextView thisHeading=(TextView)findViewById(R.id.textWallTools);
        thisHeading.setTypeface(roboto);
        getBackButton=(ImageView)findViewById(R.id.onBackWallPost);
        getBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        getPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckValidationAndPublish();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    wallCounter=dataSnapshot.getChildrenCount();
                }
                else {
                    wallCounter=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckValidationAndPublish() {

        String name=getAuthorName.getText().toString();
        String arts=getArt.getText().toString();
        String topics=getArtTitle.getText().toString();
        if (name.isEmpty()){
            getAuthorName.setError("Author name required(*)");
            getAuthorName.requestFocus();
            return;
        }
        else if (arts.isEmpty()){
            getArt.setError("Article is empty(*)");
            getArt.requestFocus();
            return;
        }
        else if (topics.isEmpty()){
            getArtTitle.setError("Topic name required(*)");
            getArtTitle.requestFocus();
            return;
        }
        else {
             progressDialog.setMessage("Uploading");
             progressDialog.setCancelable(false);
             progressDialog.setCanceledOnTouchOutside(false);
             progressDialog.show();
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=dateFormat.format(calendar.getTime());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
            String time=simpleDateFormat.format(calendar.getTime());
            final String random=date+time;
            HashMap putWmArts=new HashMap();
            putWmArts.put("name",name);
            putWmArts.put("topic",topics);
            putWmArts.put("arts",arts);
            putWmArts.put("wallcounter",wallCounter);
            databaseReference.child(random).updateChildren(putWmArts).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(PostingWallMagazine.this, "Uploading done", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        getArtTitle.setText("");
                        getArt.setText("");
                        getAuthorName.setText("");
                    }
                    if (!task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(PostingWallMagazine.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
}
