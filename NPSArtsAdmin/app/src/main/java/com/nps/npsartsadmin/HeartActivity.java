package com.nps.npsartsadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class HeartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

 private    BottomNavigationViewEx bottomNavigationViewEx;
 private Toolbar toolbar;
 private    RecyclerView recycleNotification;
 private    SwipeRefreshLayout swipeRefreshLayoutl;
 private    DatabaseReference likeRef,noteRef;
 private    FirebaseAuth mAuthInNotification;
 private    String currentUserInNotification,postKey;
 private    String uidKey;
 private    TextView notificationHeading;
 private RelativeLayout showNoNotification,showNoInt;
 private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        progressDialog=new ProgressDialog(this);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.heartbottomnav);
        setUpBottomNav();
        showNoInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        showNoNotification=(RelativeLayout)findViewById(R.id.showNoNotification);
        toolbar=(Toolbar)findViewById(R.id.heartTool);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.notification_toolbar_layout,null);
        actionBar.setCustomView(view);
        final ImageView clearAll=(ImageView)findViewById(R.id.clearNotification);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu=new PopupMenu(HeartActivity.this,clearAll);
                popupMenu.getMenuInflater().inflate(R.menu.clearoption,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.clearAll:
                                if (checkForConnctoin()){
                                    progressDialog.setMessage("Clearing your notification");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();
                                    noteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(HeartActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        notificationHeading=(TextView)findViewById(R.id.notificationHeading);
        notificationHeading.setTypeface(roboto);
        recycleNotification=(RecyclerView)findViewById(R.id.recycleheart);
        recycleNotification.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycleNotification.setLayoutManager(layoutManager);
        swipeRefreshLayoutl=(SwipeRefreshLayout)findViewById(R.id.refreshnotification);
        swipeRefreshLayoutl.setOnRefreshListener(this);
        mAuthInNotification=FirebaseAuth.getInstance();
        currentUserInNotification=mAuthInNotification.getCurrentUser().getUid();
        noteRef=FirebaseDatabase.getInstance().getReference().child("Notification").child(currentUserInNotification).child("AllNotification");
    }
    public void showNotification(){
        FirebaseRecyclerAdapter<NotificationModel,NotificationViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<NotificationModel, NotificationViewHolder>
                (NotificationModel.class,R.layout.showficationlayout,NotificationViewHolder.class,noteRef) {
            @Override
            protected void populateViewHolder(NotificationViewHolder viewHolder, NotificationModel model, int position) {
                viewHolder.setProLink(getApplicationContext(),model.getCurrentUserss());
                if (model.getNotificationType().equals("like")){
                    String setNote="<b>"+model.getUsername()+"</b>"+" recently liked your "+"<b>"+model.getHeading()+"</b>"+" article";
                    viewHolder.showNotification.setText(Html.fromHtml(setNote));
                    viewHolder.showNotificationType.setVisibility(View.VISIBLE);
                    viewHolder.showNotificationType.setImageResource(R.drawable.love);
                }
                else if(model.getNotificationType().equals("fav")){
                    String setNote="<b>"+model.getUsername()+"</b>"+" saved your "+"<b>"+model.getHeading()+"</b>"+" article as a favourite";
                    viewHolder.showNotification.setText(Html.fromHtml(setNote));
                    viewHolder.showNotificationType.setVisibility(View.VISIBLE);
                    viewHolder.showNotificationType.setImageResource(R.drawable.lace);

                }
                else {
                    String setNote="<b>"+model.getUsername()+"</b>"+" recently commented on your "+"<b>"+model.getHeading()+"</b>"+" article";
                    viewHolder.showNotification.setText(Html.fromHtml(setNote));
                    viewHolder.showNotificationType.setVisibility(View.VISIBLE);
                    viewHolder.showNotificationType.setImageResource(R.drawable.chat);
                }
                if (swipeRefreshLayoutl.isRefreshing()){
                    swipeRefreshLayoutl.setRefreshing(false);
                }
            }
        };
        recycleNotification.setAdapter(firebaseRecyclerAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (checkForConnctoin()){
            showNoInt.setVisibility(View.GONE);
            noteRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   showNotification();
                   showNoNotification.setVisibility(View.GONE);
               }
               else {
                   showNoNotification.setVisibility(View.VISIBLE);
               }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            showNoInt.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onRefresh() {
        onStart();
    }
    public static class NotificationViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView showNotification;
        CircularImageView showProNotification;
        ImageView showNotificationType;
        DatabaseReference userReff;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
             mView=itemView;
            showProNotification=(CircularImageView) mView.findViewById(R.id.notificationProfile);
            showNotification=(TextView)mView.findViewById(R.id.notificationHolder);
            showNotificationType=(ImageView) mView.findViewById(R.id.notificationTypePicHolder);
            userReff=FirebaseDatabase.getInstance().getReference().child("Users");
        }

        public void setProLink(final Context context,String userIdss) {
            userReff.child(userIdss).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageLink=String.valueOf(dataSnapshot.child("profileLink").getValue());
                    Picasso.with(context).load(imageLink).placeholder(R.drawable.grayback).into(showProNotification);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public void setUpBottomNav(){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
        bottomNavigationViewEx.setTypeface(roboto);
        enableNavigation();
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(3);
        menuItem.setChecked(true);
        menuItem.setIcon(R.drawable.final_nav_notification_on);
    }

    public void enableNavigation() {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(HeartActivity.this, ShowPost.class));
                        overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postsearch:
                        startActivity(new Intent(HeartActivity.this, SearchPeople.class));
                        overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                        finish();
                        break;
                    case R.id.offlineArticles:
                        startActivity(new Intent(HeartActivity.this,MainActivity.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postlikes:
                        break;
                    case R.id.postprofile:
                        startActivity(new Intent(HeartActivity.this, AdminControll.class));
                        overridePendingTransition(R.anim.comment_in, R.anim.comment_in);
                        finish();
                        break;
                }
                return false;
            }
        });
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
