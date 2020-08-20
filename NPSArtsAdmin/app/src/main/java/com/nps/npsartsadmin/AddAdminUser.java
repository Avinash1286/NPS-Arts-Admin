package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAdminUser extends AppCompatActivity {
    Toolbar addAdminTool;
    RecyclerView showRecycleNumbers;
    DatabaseReference adminUserRef;
    RelativeLayout noInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin_user);
        addAdminTool=(Toolbar)findViewById(R.id.addAdminUseTool);
        setSupportActionBar(addAdminTool);
        noInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        adminUserRef= FirebaseDatabase.getInstance().getReference().child("AdminNumber");
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
           View view=layoutInflater.inflate(R.layout.add_admin_tool,null);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(view);
        }
        catch (NullPointerException e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        final ImageView onBackAdmin=(ImageView)findViewById(R.id.addUserBack);
        onBackAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView addUserImage=(ImageView)findViewById(R.id.addUser);
        TextView heading=(TextView)findViewById(R.id.addAdminUserHeading);
        heading.setTypeface(roboto);
        showRecycleNumbers=(RecyclerView)findViewById(R.id.recycleAdminUserNumber);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        showRecycleNumbers.setLayoutManager(layoutManager);
        addUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserFragment addUserFragment=new AddUserFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                fragmentTransaction1.add(R.id.addNumberFrag,addUserFragment);
                fragmentTransaction1.addToBackStack(null);
                fragmentTransaction1.commit();
            }
        });

        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            loadData();
        }
        else {
            noInt.setVisibility(View.VISIBLE);
        }
    }
    private void loadData() {
        FirebaseRecyclerAdapter<AdminNumberModel,NumberHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AdminNumberModel, NumberHolder>
                (AdminNumberModel.class,R.layout.admin_number_layout,NumberHolder.class,adminUserRef) {
            @Override
            protected void populateViewHolder(final NumberHolder numberHolder, AdminNumberModel adminNumberModel, int i) {
               final String numberKey=getRef(i).getKey();
                numberHolder.setNumber(adminNumberModel.getNumber());
                numberHolder.deleteNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu=new PopupMenu(getApplicationContext(),numberHolder.deleteNumber);
                        popupMenu.getMenuInflater().inflate(R.menu.deleteoption,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                adminUserRef.child(numberKey).removeValue();
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        showRecycleNumbers.setAdapter(firebaseRecyclerAdapter);


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

    public  static  class NumberHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView number;
        ImageView deleteNumber;
        public NumberHolder(@NonNull View itemView) {
            super(itemView);
        mView=itemView;
        number=(TextView)mView.findViewById(R.id.holdeNumber);
        deleteNumber=(ImageView)mView.findViewById(R.id.deleteNumber);
        }
        public void setNumber(String getNumber){
            number.setText("+977-"+getNumber);
        }

    }

}
