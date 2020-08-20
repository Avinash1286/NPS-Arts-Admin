package com.nps.npsartsadmin;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DigitalComplainBox extends AppCompatActivity {
    private RecyclerView showComment ;
    private String currentUser;
    private DatabaseReference commentRef,userRef;
    private FirebaseAuth commentAuth;
    private String userNameInComment,userProlinkInComment,getComment;
    private  long commentcounter;
    Toolbar digitalTool;
    RelativeLayout noComplains;
    LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_complain_box);
        Typeface aveny=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        showComment=(RecyclerView)findViewById(R.id.commentOnlineRecycle);
        showComment.setHasFixedSize(true);
        digitalTool=(Toolbar)findViewById(R.id.digitalTool);
        setSupportActionBar(digitalTool);
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        noComplains=(RelativeLayout)findViewById(R.id.showNoComp);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.digital_complaintool_bar_layout,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(aveny);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        showComment.setLayoutManager(layoutManager);
        commentRef= FirebaseDatabase.getInstance().getReference().child("DigitalComplains");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        commentAuth=FirebaseAuth.getInstance();
        currentUser=commentAuth.getCurrentUser().getUid();
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    noComplains.setVisibility(View.VISIBLE);
                    loadingView.stop();
                    loadingView.setVisibility(View.GONE);
                }
                else {
                    loadComplain();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadComplain();
    }

    private void loadComplain() {
        FirebaseRecyclerAdapter<DigitalComplainModel,CommentHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<DigitalComplainModel, CommentHolder>
                (DigitalComplainModel.class,R.layout.digital_complain_box_layout,CommentHolder.class,commentRef) {
            @Override
            protected void populateViewHolder(CommentHolder commentHolder, DigitalComplainModel digitalComplainModel, int i) {
                commentHolder.setComplain(getApplicationContext(),digitalComplainModel.getComplain());
            }
        };
          loadingView.stop();
          loadingView.setVisibility(View.GONE);
       showComment.setAdapter(firebaseRecyclerAdapter);

    }

    public static class CommentHolder extends RecyclerView.ViewHolder{
        View mview;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setComplain(Context context,String complain){
            TextView setComplain=(TextView)mview.findViewById(R.id.complainText);
            setComplain.setText(complain);
        }
    }

}
