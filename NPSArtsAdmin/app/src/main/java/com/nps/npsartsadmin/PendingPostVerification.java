package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
public class PendingPostVerification extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference userRef,pendingPost,postRef;
    RecyclerView recyclePending;
     public long postCounter;
     Toolbar toolbarVerification;
     LoadingView loadingView;
     RelativeLayout nopending,noInt;
     ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_post_verification);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshPendingPost);
        toolbarVerification=(Toolbar)findViewById(R.id.verificationTool);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        setSupportActionBar(toolbarVerification);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        progressDialog=new ProgressDialog(this);
        noInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        nopending=(RelativeLayout)findViewById(R.id.showNoPendingPosts);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclePending=(RecyclerView)findViewById(R.id.recyclePendingPosts);
        recyclePending.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclePending.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        pendingPost=FirebaseDatabase.getInstance().getReference().child("NewPendingPosts");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            pendingPost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        if (dataSnapshot.hasChildren()){
                            nopending.setVisibility(View.GONE);
                            DisplayAllUsersPost();
                            noInt.setVisibility(View.GONE);
                        }
                        else {
                            noInt.setVisibility(View.GONE);
                            nopending.setVisibility(View.VISIBLE);
                            loadingView.stop();
                            loadingView.setVisibility(View.GONE);
                        }
                    }
                    else {
                        noInt.setVisibility(View.GONE);
                        nopending.setVisibility(View.VISIBLE);
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
      else {
          loadingView.stop();
          loadingView.setVisibility(View.GONE);
          noInt.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onRefresh() {
        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            pendingPost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){
                        if (dataSnapshot.hasChildren()){
                            nopending.setVisibility(View.GONE);
                            DisplayAllUsersPost();
                            noInt.setVisibility(View.GONE);
                        }
                        else {
                            noInt.setVisibility(View.GONE);
                            nopending.setVisibility(View.VISIBLE);
                            loadingView.stop();
                            loadingView.setVisibility(View.GONE);
                        }
                    }
                    else {
                        noInt.setVisibility(View.GONE);
                        nopending.setVisibility(View.VISIBLE);
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        DatabaseReference pendingRef, userReff;
        TextView dateandTime, userNameClick, verifyPost, deletePost,pendingHeadingHere;
        ImageView showVerified, setphoto;
        CircularImageView profileClick;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            setphoto = (ImageView) mView.findViewById(R.id.pendingshowCreativePost);
            dateandTime = (TextView) mView.findViewById(R.id.pendingdateandtime);
            userNameClick = (TextView) mView.findViewById(R.id.pendinguserNameHere);
            profileClick = (CircularImageView) mView.findViewById(R.id.pendinguserProInMain);
            showVerified = (ImageView) mView.findViewById(R.id.pendingverified);
            verifyPost = (TextView) mView.findViewById(R.id.verifyButton);
            pendingHeadingHere=(TextView)mView.findViewById(R.id.pendinguserHeadingHere);
            deletePost = (TextView) mView.findViewById(R.id.deleteButton);
            pendingRef = FirebaseDatabase.getInstance().getReference().child("PendingPosts");
            userReff = FirebaseDatabase.getInstance().getReference().child("Users");
        }
        public void setUserName(Context context, String userName) {
            Typeface roboto = Typeface.createFromAsset(context.getAssets(), "font/AvenyTMedium.otf");
            userNameClick.setTypeface(roboto);
            userNameClick.setText(userName);
        }
        public void setUserProLink(Context context, String userProLink) {
            Picasso.with(context).load(userProLink).placeholder(R.drawable.profile).into(profileClick);
        }
        public void setHeading(Context context, String heading) {
            Typeface roboto = Typeface.createFromAsset(context.getAssets(), "font/AvenyTMedium.otf");
            pendingHeadingHere.setTypeface(roboto);
            pendingHeadingHere.setText(heading);
        }
        public void setDate(Context context, String time, String date) {
            Typeface roboto = Typeface.createFromAsset(context.getAssets(), "font/FacebookNarrow_A_Rg.ttf");
            dateandTime.setTypeface(roboto);
            dateandTime.setText(time + " " + date);
        }
        public void setPhotos(Context context,String url) {
            Picasso.with(context).load(url).placeholder(R.drawable.image_placeholder).into(setphoto);
        }
        public void setVerifiedLogo(String uid) {
            userReff.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String verifiedValue = dataSnapshot.child("verified").getValue().toString();
                    if (verifiedValue.equals("yes")) {
                        showVerified.setVisibility(View.VISIBLE);
                    } else {
                        showVerified.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    private void DisplayAllUsersPost() {
        FirebaseRecyclerAdapter<AllPostModel, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllPostModel, ViewHolder>
                (AllPostModel.class, R.layout.pending_post_layout, ViewHolder.class, pendingPost) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final AllPostModel model, final int position) {
                final String postKey = getRef(position).getKey();
                viewHolder.setPhotos(getApplicationContext(),model.getImageUrl());
                viewHolder.setDate(getApplicationContext(),model.getTime(),model.getDate());
                viewHolder.setUserName(getApplicationContext(),model.getUserName());
                viewHolder.setUserProLink(getApplicationContext(),model.getUserProLink());
                viewHolder.setVerifiedLogo(model.getUid());
                viewHolder.verifyPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setMessage("Verifying post");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        postRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    postCounter=dataSnapshot.getChildrenCount();
                                }
                                else {
                                    postCounter=0;
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        Calendar calendar=Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                        String date=simpleDateFormat.format(calendar.getTime());
                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm:ss");
                        String time=simpleDateFormat1.format(calendar.getTime());
                        String randomValue=date+time;
                        HashMap putPostInfo=new HashMap();
                        putPostInfo.put("uid",model.getUid());
                        putPostInfo.put("userName",model.getUserName());
                        putPostInfo.put("userProLink",model.getUserProLink());
                        putPostInfo.put("date",model.getDate());
                        putPostInfo.put("time",model.getTime());
                        putPostInfo.put("articles",model.getArticles());
                        putPostInfo.put("backGround",model.getBackGround());
                        putPostInfo.put("fontSize",model.getFontSize());
                        putPostInfo.put("fontColor",model.getFontColor());
                        putPostInfo.put("counter",postCounter);
                        putPostInfo.put("heading",model.getHeading());
                        putPostInfo.put("height",model.getHeight());
                        putPostInfo.put("verifiedPost","no");
                        putPostInfo.put("postType",model.getPostType());
                        putPostInfo.put("message",model.getMessage());
                        putPostInfo.put("imageUrl",model.getImageUrl());
                        postRef.child(randomValue).updateChildren(putPostInfo).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task){
                                if (task.isSuccessful()){
                                    Toast.makeText(PendingPostVerification.this, "Post Verified", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                    progressDialog.dismiss();
                                    pendingPost.child(postKey).removeValue();
                                }
                                else {
                                    progressDialog.dismiss();
                                    String mess=task.getException().toString();
                                    Toast.makeText(PendingPostVerification.this, "Error occurred"+mess, Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }
                        });
                    }
                });
                viewHolder.deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final android.app.AlertDialog.Builder builder=new AlertDialog.Builder(PendingPostVerification.this);
                        builder.setTitle("Deleting Post");
                        builder.setMessage("Are you sure to delete this post?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pendingPost.child(postKey).removeValue();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
            }
        };
        recyclePending.setAdapter(firebaseRecyclerAdapter);
        loadingView.stop();
        loadingView.setVisibility(View.GONE);
          if (swipeRefreshLayout.isRefreshing()){
              swipeRefreshLayout.setRefreshing(false);
          }
    }
    private boolean checkForConnctoin() {
        ConnectivityManager cm=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){

            return true;

        }
        else {
            return false;
        }

    }
}
