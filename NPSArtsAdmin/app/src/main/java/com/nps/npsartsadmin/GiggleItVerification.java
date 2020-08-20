package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class GiggleItVerification extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout refreshManageUser;
    RecyclerView recycleAllUsers;
    DatabaseReference userRef,backRef;
    Boolean verifiedChecker=false;
    Toolbar manageUserTool;
    LoadingView loadingView;
    RelativeLayout showNoInt;
    SearchView searchView;
    String searchText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giggle_it_verification);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        refreshManageUser=(SwipeRefreshLayout)findViewById(R.id.refreshManageUser);
        refreshManageUser.setOnRefreshListener(this);
        manageUserTool=(Toolbar)findViewById(R.id.giggleItTool);
        setSupportActionBar(manageUserTool);
        ActionBar actionBar=getSupportActionBar();
        showNoInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.user_verification_too,null);
        actionBar.setCustomView(view);
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        ImageView setBack=(ImageView)view.findViewById(R.id.manageUserBack);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recycleAllUsers=(RecyclerView)findViewById(R.id.recycleAllUsers);
        recycleAllUsers.setHasFixedSize(true);
        recycleAllUsers.setLayoutManager(new LinearLayoutManager(this));
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        backRef=FirebaseDatabase.getInstance().getReference().child("UserTypeBackUp");
        searchView=(SearchView)findViewById(R.id.startSearching);
        if (checkForConnctoin()){
            showNoInt.setVisibility(View.GONE);
            loadData(searchText);
        }
        else {
            loadingView.setVisibility(View.GONE);
            loadingView.stop();
            showNoInt.setVisibility(View.VISIBLE);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadData(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadData(newText.toLowerCase());
                return true;
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
    private void loadData(String text) {
        Query searchByTag=userRef.orderByChild("tagname").startAt(text).endAt(text+"\uf8ff");
        FirebaseRecyclerAdapter<ManageUserModel,ManageUserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ManageUserModel, ManageUserViewHolder>
                (ManageUserModel.class,R.layout.manage_user_layout,ManageUserViewHolder.class,searchByTag) {
            @Override
            protected void populateViewHolder(final ManageUserViewHolder manageUserViewHolder, final ManageUserModel manageUserModel, int i) {
                final String userKey=getRef(i).getKey();
                manageUserViewHolder.setProfileLink(getApplicationContext(),manageUserModel.getProfileLink());
                manageUserViewHolder.setFullname(getApplicationContext(),manageUserModel.getFullname(),manageUserModel.getUserType());
                manageUserViewHolder.verifiedStatus(userKey);
                manageUserViewHolder.setNumberOfPost(getApplicationContext(),userKey);
                manageUserViewHolder.permissionChecker(userKey);
                manageUserViewHolder.setVPriStatus(userKey);
                manageUserViewHolder.permission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String permissionValue=dataSnapshot.child("permission").getValue().toString();
                                if (permissionValue.equals("yes")){
                                    manageUserViewHolder.permission.setChecked(true);
                                    userRef.child(userKey).child("permission").setValue("no");
                                }
                                else {
                                    manageUserViewHolder.permission.setChecked(false);
                                    ShowWarningBottomSheet showWarningBottomSheet=new ShowWarningBottomSheet();
                                    ShowWarningBottomSheet.userKey=userKey;
                                    showWarningBottomSheet.show(getSupportFragmentManager(),"ShowWarning");
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
                manageUserViewHolder.verifyPrincipal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             if (dataSnapshot.exists()){
                                 if (String.valueOf(dataSnapshot.child(userKey).child("userType").getValue()).equals("Principal")){
                                     backRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                             if (dataSnapshot.exists()){
                                                 if (dataSnapshot.hasChild(userKey)){
                                                     String userTypeBackUp=String.valueOf(dataSnapshot.child(userKey).child("userTypeBackUp").getValue());
                                                     userRef.child(userKey).child("userType").setValue(userTypeBackUp);
                                                     backRef.child(userKey).removeValue();
                                                     manageUserViewHolder.verifyPrincipal.setChecked(false);
                                                 }
                                             }

                                         }
                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) {
                                         }
                                     });
                                 }
                                 else {
                                     String getUserType=String.valueOf(dataSnapshot.child(userKey).child("userType").getValue());
                                     HashMap putInfo=new HashMap();
                                     putInfo.put("userTypeBackUp",getUserType);
                                     backRef.child(userKey).updateChildren(putInfo);
                                     userRef.child(userKey).child("userType").setValue("Principal");
                                     manageUserViewHolder.verifyPrincipal.setChecked(true);
                                 }
                             }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
                manageUserViewHolder.verifyCommadent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (String.valueOf(dataSnapshot.child(userKey).child("userType").getValue()).equals("Commandant")){
                                        backRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(userKey)){
                                                    String userTypeBackUp=String.valueOf(dataSnapshot.child(userKey).child("userTypeBackUp").getValue());
                                                    userRef.child(userKey).child("userType").setValue(userTypeBackUp);
                                                    backRef.child(userKey).removeValue();
                                                    manageUserViewHolder.verifyPrincipal.setChecked(false);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                    else {
                                        String getUserType=String.valueOf(dataSnapshot.child(userKey).child("userType").getValue());
                                        HashMap putInfo=new HashMap();
                                        putInfo.put("userTypeBackUp",getUserType);
                                        backRef.child(userKey).updateChildren(putInfo);
                                        userRef.child(userKey).child("userType").setValue("Commandant");
                                        manageUserViewHolder.verifyPrincipal.setChecked(true);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
                manageUserViewHolder.vefifyVice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (String.valueOf(dataSnapshot.child(userKey).child("userType").getValue()).equals("Vice Principal")){
                                        backRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(userKey)){
                                                    String userTypeBackUp=String.valueOf(dataSnapshot.child(userKey).child("userTypeBackUp").getValue());
                                                    userRef.child(userKey).child("userType").setValue(userTypeBackUp);
                                                    backRef.child(userKey).removeValue();
                                                    manageUserViewHolder.verifyPrincipal.setChecked(false);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                    else {
                                        String getUserType=String.valueOf(dataSnapshot.child(userKey).child("userType").getValue());
                                        HashMap putInfo=new HashMap();
                                        putInfo.put("userTypeBackUp",getUserType);
                                        backRef.child(userKey).updateChildren(putInfo);
                                        userRef.child(userKey).child("userType").setValue("Vice Principal");
                                        manageUserViewHolder.verifyPrincipal.setChecked(true);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
                manageUserViewHolder.showMoreOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if (manageUserViewHolder.optionsHolder.getVisibility()==View.VISIBLE){
                          manageUserViewHolder.optionsHolder.setVisibility(View.GONE);
                      }
                      else {
                          manageUserViewHolder.optionsHolder.setVisibility(View.VISIBLE);
                      }
                    }
                });
                manageUserViewHolder.verifyChairPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (String.valueOf(dataSnapshot.child(userKey).child("userType").getValue()).equals("Chair Person")){
                                        backRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(userKey)){
                                                    String userTypeBackUp=String.valueOf(dataSnapshot.child(userKey).child("userTypeBackUp").getValue());
                                                    userRef.child(userKey).child("userType").setValue(userTypeBackUp);
                                                    backRef.child(userKey).removeValue();
                                                    manageUserViewHolder.verifyPrincipal.setChecked(false);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                    else {
                                        String getUserType=String.valueOf(dataSnapshot.child(userKey).child("userType").getValue());
                                        HashMap putInfo=new HashMap();
                                        putInfo.put("userTypeBackUp",getUserType);
                                        backRef.child(userKey).updateChildren(putInfo);
                                        userRef.child(userKey).child("userType").setValue("Chair Person");
                                        manageUserViewHolder.verifyPrincipal.setChecked(true);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
                manageUserViewHolder.verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String verification=dataSnapshot.child("verified").getValue().toString();
                                if (verification.equals("yes")){
                                    manageUserViewHolder.verify.setChecked(false);
                                    userRef.child(userKey).child("verified").setValue("no");
                                }
                                else {
                                    manageUserViewHolder.verify.setChecked(true);
                                    userRef.child(userKey).child("verified").setValue("yes");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        loadingView.stop();
        loadingView.setVisibility(View.GONE);
        recycleAllUsers.setAdapter(firebaseRecyclerAdapter);
        if(refreshManageUser.isRefreshing()){
            refreshManageUser.setRefreshing(false);
        }

    }

    @Override
    public void onRefresh() {
        if (checkForConnctoin()){
            showNoInt.setVisibility(View.GONE);
            loadData(searchText);
        }
        else {
            loadingView.stop();
            loadingView.setVisibility(View.GONE);
            showNoInt.setVisibility(View.VISIBLE);
            if(refreshManageUser.isRefreshing()){
                refreshManageUser.setRefreshing(false);
            }
        }
    }

    public static class ManageUserViewHolder extends RecyclerView.ViewHolder{
        View mViews;
        TextView setNoOfPost,vefifyHeading,permessionHeading,prncipalHeading,commadentHeading,chairPersonHeading,vicePri;
        CheckBox verify,permission,verifyPrincipal,verifyCommadent,verifyChairPerson,vefifyVice;
         DatabaseReference userReff,postRef,backUpRef;
         ImageView showMoreOptions;
         RelativeLayout optionsHolder;
        public ManageUserViewHolder(@NonNull View itemView) {
            super(itemView);
            mViews=itemView;
            setNoOfPost=(TextView)mViews.findViewById(R.id.holdsNoofPost);
            verify=(CheckBox) mViews.findViewById(R.id.verificationButton);
            permission=(CheckBox) mViews.findViewById(R.id.generalPermissionButton);
            vefifyHeading=(TextView)mViews.findViewById(R.id.verifyHeading);
            permessionHeading=(TextView)mViews.findViewById(R.id.permissionHeading);
            verifyPrincipal=(CheckBox)mViews.findViewById(R.id.verifyPrincipal);
            verifyCommadent=(CheckBox)mViews.findViewById(R.id.verifyCommandent);
            verifyChairPerson=(CheckBox)mViews.findViewById(R.id.verifyChairPerson);
            prncipalHeading=(TextView)mViews.findViewById(R.id.verifyPrincipalHeading);
            commadentHeading=(TextView)mViews.findViewById(R.id.verifyCommandentHeading);
            chairPersonHeading=(TextView)mViews.findViewById(R.id.verifyChairPersonheading);
            vefifyVice=(CheckBox)mViews.findViewById(R.id.verifyVicePri);
            showMoreOptions=(ImageView)mViews.findViewById(R.id.showOptions);
            optionsHolder=(RelativeLayout)mViews.findViewById(R.id.two);
            userReff=FirebaseDatabase.getInstance().getReference().child("Users");
            postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
            backUpRef=FirebaseDatabase.getInstance().getReference().child("UserTypeBackUp");
        }

        public void setVPriStatus(final String userKey){
            userReff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String userType=String.valueOf(dataSnapshot.child(userKey).child("userType").getValue());
                 if (userType.equals("Principal")){
                     verifyChairPerson.setEnabled(false);
                     verifyCommadent.setEnabled(false);
                     vefifyVice.setEnabled(false);
                     verifyPrincipal.setEnabled(true);
                     verifyPrincipal.setChecked(true);
                     verifyChairPerson.setChecked(false);
                     verifyCommadent.setChecked(false);
                     vefifyVice.setChecked(false);
                 }
                  else if (userType.equals("Commandant")){
                       verifyChairPerson.setEnabled(false);
                       verifyCommadent.setEnabled(true);
                       vefifyVice.setEnabled(false);
                       verifyPrincipal.setEnabled(false);
                       verifyCommadent.setEnabled(true);
                       verifyCommadent.setChecked(true);
                     verifyChairPerson.setChecked(false);
                     verifyPrincipal.setChecked(false);
                     vefifyVice.setChecked(false);
                   }
                  else if (userType.equals("Chair Person")){
                       verifyChairPerson.setEnabled(true);
                       verifyCommadent.setEnabled(false);
                       vefifyVice.setEnabled(false);
                       verifyPrincipal.setEnabled(false);
                       verifyChairPerson.setChecked(true);
                       verifyPrincipal.setChecked(false);
                     verifyCommadent.setChecked(false);
                     vefifyVice.setChecked(false);
                   }
                  else if (userType.equals("Vice Principal")){
                       verifyChairPerson.setEnabled(false);
                       verifyCommadent.setEnabled(false);
                       vefifyVice.setEnabled(true);
                       verifyPrincipal.setEnabled(false);
                       vefifyVice.setChecked(true);
                     verifyChairPerson.setChecked(false);
                     verifyCommadent.setChecked(false);
                     verifyPrincipal.setChecked(false);
                   }
                   else {
                     verifyChairPerson.setChecked(false);
                     verifyCommadent.setChecked(false);
                     verifyPrincipal.setChecked(false);
                     vefifyVice.setChecked(false);
                       verifyChairPerson.setEnabled(true);
                       verifyCommadent.setEnabled(true);
                       vefifyVice.setEnabled(true);
                       verifyPrincipal.setEnabled(true);
                   }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        public void setProfileLink(Context context, String profileLink) {
            CircularImageView setPro=(CircularImageView)mViews.findViewById(R.id.holdeProfileInMagageAccount);
            Picasso.with(context).load(profileLink).placeholder(R.drawable.grayback).into(setPro);

        }
        public void setFullname(Context context,String fullname,String userType) {
            TextView setName=(TextView)mViews.findViewById(R.id.setNameInManageAccount);
            setName.setText(fullname+" ("+userType+")");
        }

        public void verifiedStatus(String userKey){
            userReff.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String verifiedValue=dataSnapshot.child("verified").getValue().toString();
                 if (verifiedValue.equals("yes")){
                     vefifyHeading.setText("Verified");
                     verify.setChecked(true);
                 }
                 else {
                     vefifyHeading.setText("Verify");
                     verify.setChecked(false);
                 }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setNumberOfPost(final Context context, String userKey){
            Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
            setNoOfPost.setTypeface(roboto);
            postRef.orderByChild("uid").startAt(userKey).endAt(userKey+"\uf8ff").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     int noOfPosts=(int)dataSnapshot.getChildrenCount();
                     setNoOfPost.setText(noOfPosts+" Posts");
                 }
                 else {
                     setNoOfPost.setText("0 Post");
                 }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void permissionChecker(String userKey){
            userReff.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String permissionValue=dataSnapshot.child("permission").getValue().toString();
                    if (permissionValue.equals("yes")){
                        permessionHeading.setText("Permission Grant");
                        permission.setChecked(true);
                    }
                    else {
                        permessionHeading.setText("No Permission");
                        permission.setChecked(false);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
