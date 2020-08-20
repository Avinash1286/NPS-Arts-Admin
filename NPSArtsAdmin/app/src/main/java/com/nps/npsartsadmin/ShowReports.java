package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
public class ShowReports extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout refreshShowPosts;
    RecyclerView recycleShowPosts;
    DatabaseReference reportRef,postRef,userRef;
    Toolbar reportsTools;
    RelativeLayout noreports,noInt;
    LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reports);
        reportsTools=(Toolbar)findViewById(R.id.showReportsTool);
        reportsTools=(Toolbar)findViewById(R.id.showReportsTool);
        noreports=(RelativeLayout)findViewById(R.id.showNoReports);
        setSupportActionBar(reportsTools);
        noInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBar.setDisplayShowCustomEnabled(true);
        View view=layoutInflater.inflate(R.layout.reviewing_article_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.reviewHeading);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.reviewBack);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        refreshShowPosts=(SwipeRefreshLayout)findViewById(R.id.refreshshowpost);
        refreshShowPosts.setOnRefreshListener(this);
        recycleShowPosts=(RecyclerView)findViewById(R.id.recycleShowReports);
        recycleShowPosts.setHasFixedSize(true);
        recycleShowPosts.setLayoutManager(new LinearLayoutManager(this));
        reportRef= FirebaseDatabase.getInstance().getReference().child("Reports");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        recycleShowPosts.setHasFixedSize(true);
        recycleShowPosts.setLayoutManager(new LinearLayoutManager(this));
        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            reportRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        noreports.setVisibility(View.GONE);
                        if (dataSnapshot.hasChildren()){
                            loadReports();
                            noreports.setVisibility(View.GONE);
                        }
                        else {
                            noreports.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);
                            loadingView.stop();
                        }

                    }
                    else {
                        noreports.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                        loadingView.stop();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            loadingView.setVisibility(View.GONE);
            loadingView.stop();
            noInt.setVisibility(View.VISIBLE);
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
    private void loadReports() {
        FirebaseRecyclerAdapter<ShowReportsModel,ReportsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ShowReportsModel, ReportsViewHolder>
                (ShowReportsModel.class,R.layout.report_layout,ReportsViewHolder.class,reportRef) {
            @Override
            protected void populateViewHolder(ReportsViewHolder reportsViewHolder, final ShowReportsModel showReportsModel, int i) {
                final String reportKey=getRef(i).getKey();
                reportsViewHolder.setMessage(getApplicationContext(),showReportsModel.getReporterName(),showReportsModel.getPostTitle(),showReportsModel.getPostUserName(),showReportsModel.getReason());
                reportsViewHolder.setReporterProLink(getApplicationContext(),showReportsModel.getReporterProLink());
                reportsViewHolder.delectepost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(ShowReports.this);
                        builder.setTitle("Deleting Post");
                        builder.setMessage("Are you sure to delete this post?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postRef.child(showReportsModel.getPostKeyValue()).removeValue();
                                reportRef.child(reportKey).removeValue();
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
                reportsViewHolder.reviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (showReportsModel.getPostType().equals("image")){
                                postRef.child(showReportsModel.getPostKeyValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String uid=dataSnapshot.child("uid").getValue().toString();
                                        final String imageLink=dataSnapshot.child("imageUrl").getValue().toString();
                                         userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 String name=dataSnapshot.child("fullname").getValue().toString();
                                                 Intent intent=new Intent(ShowReports.this,ShowPostImage.class);
                                                 intent.putExtra("name",name);
                                                 intent.putExtra("url",imageLink);
                                                 startActivity(intent);
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                            if (showReportsModel.getPostType().equals("message")){
                               Intent intent=new Intent(ShowReports.this,ShowMessagePost.class);
                               intent.putExtra("postKey",showReportsModel.getPostKeyValue());
                               intent.putExtra("userId",showReportsModel.getUidKey());
                               startActivity(intent);
                            }
                            if (showReportsModel.getPostType().equals("normal")){
                                Intent intent=new Intent(ShowReports.this,PostDetails.class);
                                intent.putExtra("postKey",showReportsModel.getPostKeyValue());
                                intent.putExtra("uidKey",showReportsModel.getUidKey());
                                intent.putExtra("heading",showReportsModel.getPostTitle());
                                startActivity(intent);
                            }
                    }
                });
                reportsViewHolder.declinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(ShowReports.this);
                        builder.setTitle("Decline Report");
                        builder.setMessage("Are you sure to decline this report?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportRef.child(reportKey).removeValue();
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
        recycleShowPosts.setAdapter(firebaseRecyclerAdapter);
        if(refreshShowPosts.isRefreshing()){
            refreshShowPosts.setRefreshing(false);
        }
        loadingView.stop();
        loadingView.setVisibility(View.GONE);

    }

    @Override
    public void onRefresh() {
        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            reportRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        noreports.setVisibility(View.GONE);
                        if (dataSnapshot.hasChildren()){
                            loadReports();
                            noreports.setVisibility(View.GONE);
                        }
                        else {
                            noreports.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);
                            loadingView.stop();
                        }

                    }
                    else {
                        noreports.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                        loadingView.stop();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            if(refreshShowPosts.isRefreshing()){
                refreshShowPosts.setRefreshing(false);
            }
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            loadingView.stop();
            loadingView.setVisibility(View.GONE);
            noInt.setVisibility(View.VISIBLE);
        }
    }

    public static  class ReportsViewHolder extends RecyclerView.ViewHolder{
        View mViewReports;
        ImageView declinButton,reviewButton,delectepost;
        public ReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            mViewReports=itemView;
            declinButton=(ImageView) mViewReports.findViewById(R.id.delcine);
            reviewButton=(ImageView) mViewReports.findViewById(R.id.review);
            delectepost=(ImageView) mViewReports.findViewById(R.id.delectePost);
        }
        public void setMessage(Context context, String reporterName,  String postTitle, String postUserName, String reason) {
            TextView showMessage=(TextView)mViewReports.findViewById(R.id.showReportMessage);
            String messageFormate="<b>"+reporterName+"</b>"+" has reported "+"<b>"+"\""+postTitle+"\""+"</b>"+" article posted by "+"<b>"+postUserName+"</b>"+" because of this given reason "+"<b>"+"\""+reason+"\""+"</b>";
             showMessage.setText(Html.fromHtml(messageFormate));
        }
        public void setReporterProLink(Context context,String reporterProLink) {
            CircularImageView showProImage=(CircularImageView)mViewReports.findViewById(R.id.showProfileInReports);
            Picasso.with(context).load(reporterProLink).placeholder(R.drawable.image_placeholder).into(showProImage);
        }
    }
}
